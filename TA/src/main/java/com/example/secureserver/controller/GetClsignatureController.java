package com.example.secureserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.secureserver.parameterUtil.parameter;
import com.example.secureserver.bean.Driver;
import com.example.secureserver.service.DriverService;
import com.example.secureserver.encryption.AES;
import com.example.secureserver.encryption.RSA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.ujmp.core.util.Base64;

import java.math.BigInteger;
import java.util.Map;

import static com.example.secureserver.encryption.cl_signature.signature;
import static com.example.secureserver.encryption.AES.getStrKeyAES;

@RestController
@RequestMapping("/getClSignature")
public class GetClsignatureController {
    @Autowired
    private DriverService driverService;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject GetClsignaturr(@RequestBody JSONObject jsonObject) {
        try {
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
                String username = data.getString("username");
                String password = data.getString("password");
                Driver driver = new Driver();
                driver.setUsername(username);
                driver.setPassword(password);
                driver = driverService.findDriver(driver);
                int time = (int) (System.currentTimeMillis() - driver.getClsignaturetime());
                if (time > 1) {
                    BigInteger message = data.getBigInteger("message");
                    Map<String, BigInteger> signature = signature(message.toString(), parameter.clSign_PK_d1, parameter.clSign_PK_d2, parameter.clSign_PK_d3, parameter.clSign_PK_n, parameter.clSign_SK_p);
                    BigInteger v = signature.get("v");
                    BigInteger e = signature.get("e");
                    BigInteger s = signature.get("s");
                    data.clear();
                    data.put("v", v);
                    data.put("e", e);
                    data.put("s", s);
                    data.put("message", message);
                    driver.setClsignaturetime((int) (System.currentTimeMillis() / (1000 * 60 * 60 * 24)));
                    driverService.updateclsignaturetime(driver);
                }
                //反馈用户
                JSONObject result = new JSONObject();
                AESKey = getStrKeyAES();
                encAESKey = RSA.encrypt(AESKey, (String) jsonObject.get("driver_pub"));
                xigema = RSA.signature(encAESKey, parameter.rsa_pri);
                result.put("encAESKey", encAESKey);
                result.put("sign", xigema);
                byte[] res_encData = AES.encryptAES(data.toJSONString().getBytes(), AESKey);
                result.put("encData", res_encData);
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
