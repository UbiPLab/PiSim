package com.example.secureserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.secureserver.parameterUtil.parameter;
import com.example.secureserver.bean.*;
import com.example.secureserver.service.DriverService;
import com.example.secureserver.encryption.AES;
import com.example.secureserver.encryption.RSA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.ujmp.core.util.Base64;

import static com.example.secureserver.encryption.AES.getStrKeyAES;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private DriverService driverService;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject login(@RequestBody JSONObject jsonObject) throws Exception {
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

            //获取用户信息
            String username = (String) data.get("username");
            String password = (String) data.get("password");

            driver.setUsername(username);
            driver.setPassword(password);
            driver.setXigema(xigema);
            Driver driver_result = driverService.findDriver(driver);
            if (!(driver_result == null)) {
                //用户存在
                //反馈用户
                JSONObject result = new JSONObject();
                AESKey = getStrKeyAES();
                encAESKey = RSA.encrypt(AESKey, (String) jsonObject.get("driver_pub"));
                xigema = RSA.signature(encAESKey, parameter.rsa_pri);
                result.put("encAESKey", encAESKey);
                result.put("sign", xigema);
                JSONObject res_data = new JSONObject();
                res_data.put("rsa_pubKey", driver_result.getRsa_pubKey());
                res_data.put("Ei1", Base64.decode(driver_result.getEi1()));
                res_data.put("Ei2", Base64.decode(driver_result.getEi2()));
                res_data.put("SK1_array", parameter.SK1.toDoubleArray());
                res_data.put("SK2_array", parameter.SK2.toDoubleArray());
                res_data.put("HKP_array", parameter.HKP.toDoubleArray());
                res_data.put("V_array", parameter.V.toDoubleArray());
                res_data.put("K", parameter.K);
                byte[] res_encData = AES.encryptAES(res_data.toJSONString().getBytes(), AESKey);
                result.put("encData", res_encData);
                result.put("result","success");
                return result;
            }else {
                JSONObject result = new JSONObject();
                result.put("result","error");
                return result;
            }
        } else {
            return null;
        }
    }
}
