package com.example.secureserver.controller;

import com.example.secureserver.parameterUtil.parameter;
import com.example.secureserver.bean.*;
import com.example.secureserver.service.DriverService;
import com.example.secureserver.encryption.AES;
import com.example.secureserver.encryption.RSA;
import it.unisa.dia.gas.jpbc.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.ujmp.core.util.Base64;
import com.alibaba.fastjson.*;

import java.math.BigInteger;

import static com.example.secureserver.parameterUtil.parameterLength.t_length;
import static com.example.secureserver.encryption.AES.getStrKeyAES;

@RestController
@RequestMapping("/register")
public class RegisteController {

    @Autowired
    private DriverService driverService;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject register(@RequestBody JSONObject jsonObject) {
        try {
            return query_register(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject query_register(JSONObject jsonObject) throws Exception {
        Driver driver = new Driver();
        String encAESKey = (String) jsonObject.get("encAESKey");
        // 用户签名σ
        String xigema = (String) jsonObject.get("sign");
        boolean flag = RSA.verify(encAESKey, xigema, (String) jsonObject.get("driver_pub"));
        if (flag) {
            //解密AES密钥
            String AESKey = RSA.decrypt(encAESKey, parameter.rsa_pri);
            //解密数据内容
            byte[] encData = Base64.decode((String) jsonObject.get("encData"));
            String data_temp = new String(AES.decryptAES(encData, AESKey));
            JSONObject data = JSONObject.parseObject(data_temp);
            // 获取双线性映射元素ni和ni2
            Element ni = parameter.G1.newElementFromBytes(data.getBytes("ni"));
            Element ni2 = parameter.G2.newElementFromBytes(data.getBytes("ni2"));
            if (parameter.pairing.pairing(ni, parameter.gpk_B).isEqual(parameter.pairing.pairing(parameter.gpk_g2, ni2))) {
                //获取用户信息
                String username = (String) data.get("username");
                String password = (String) data.get("password");
                String idNumber = (String) data.get("idNumber");
                String idCar = (String) data.get("idCar");

                //获取随机t
                BigInteger t = new BigInteger(t_length, parameter.secureRandom);
                //TA_driver.t = t;
                //生成Eird (Ei1,Ei2)
                Element Ei1 = parameter.gpk_g2.pow(t).getImmutable();
                Element Ei2 = (((parameter.gpk_g2.pow(parameter.gsk_a)).mul(ni.pow(parameter.gsk_b))).pow(t)).getImmutable();
                //向数据库中添加用户
                driver.setUsername(username);
                driver.setPassword(password);
                driver.setRsa_pubKey((String) jsonObject.get("driver_pub"));
                driver.setIdCar(idCar);
                driver.setIdNumber(idNumber);
                driver.setXigema(xigema);
                driver.setNi(Base64.encodeBytes(ni.toBytes()));
                driver.setNi2(Base64.encodeBytes(ni2.toBytes()));
                driver.setEi1(Base64.encodeBytes(Ei1.toBytes()));
                driver.setEi2(Base64.encodeBytes(Ei2.toBytes()));
                driver.setClsignaturetime((int) (System.currentTimeMillis() / (1000 * 60 * 60 * 24)));
                if (driverService.insert(driver)) {
                    //反馈用户
                    JSONObject result = new JSONObject();
                    AESKey = getStrKeyAES();
                    encAESKey = RSA.encrypt(AESKey, (String) jsonObject.get("driver_pub"));
                    xigema = RSA.signature(encAESKey, parameter.rsa_pri);
                    result.put("encAESKey", encAESKey);
                    result.put("sign", xigema);
                    JSONObject res_data = new JSONObject();
                    res_data.put("Ei1", Ei1.toBytes());
                    res_data.put("Ei2", Ei2.toBytes());
                    res_data.put("SK1_array", parameter.SK1.toDoubleArray());
                    res_data.put("SK2_array", parameter.SK2.toDoubleArray());
                    res_data.put("HKP_array", parameter.HKP.toDoubleArray());
                    res_data.put("V_array", parameter.V.toDoubleArray());
                    res_data.put("K", parameter.K);
                    byte[] res_encData = AES.encryptAES(res_data.toJSONString().getBytes(), AESKey);
                    result.put("encData", res_encData);
                    result.put("result", "success");
                    return result;
                } else {
                    JSONObject result = new JSONObject();
                    result.put("result", "error");
                    return result;
                }
            } else {
                JSONObject result = new JSONObject();
                result.put("result", "error");
                return result;
            }
        } else {
            JSONObject result = new JSONObject();
            result.put("result", "error");
            return result;
        }
    }

}
