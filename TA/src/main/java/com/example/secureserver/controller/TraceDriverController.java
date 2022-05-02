package com.example.secureserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.secureserver.parameterUtil.parameter;
import com.example.secureserver.bean.Driver;
import com.example.secureserver.bean.Malicious_driver;
import com.example.secureserver.service.DriverService;
import com.example.secureserver.service.Malicious_driverService;
import com.example.secureserver.encryption.AES;
import com.example.secureserver.encryption.RSA;
import it.unisa.dia.gas.jpbc.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.ujmp.core.util.Base64;

import java.util.List;

@RestController
@RequestMapping("/TraceDriver")
public class TraceDriverController {
    @Autowired
    private DriverService driverService;
    @Autowired
    private Malicious_driverService malicious_driverService;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject trace(@RequestBody JSONObject jsonObject) throws Exception {
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
                //获取恶意司机的签名
                Element REi1 = parameter.G1.newElementFromBytes(data.getBytes("REi1"));
                Element REi2 = parameter.G1.newElementFromBytes(data.getBytes("REi2"));
                short type = data.getShortValue("type");
                List<Driver> drivers = driverService.findAllDriver();
                Element ni2;
                Driver result = new Driver();
                //TA检索数据库中的每一个ni2，然后和签名比对
                for (Driver driver : drivers) {
                    // ni =  parameter.G1.newElementFromBytes(Base64.decode(driver.getNi())).getImmutable();
                    ni2 = parameter.G2.newElementFromBytes(Base64.decode(driver.getNi2())).getImmutable();
                    Element temp = parameter.pairing.pairing(REi2, parameter.gpk_g).mul(((parameter.pairing.pairing(REi1, parameter.gpk_A)).negate()));
                    Element temp2 = parameter.pairing.pairing(REi1, ni2).getImmutable();
                    if (temp.equals(temp2)) {
                        result.setIdNumber(driver.getIdNumber());
                        result.setIdCar(driver.getIdCar());
                        result.setUsername(driver.getUsername());
                        //获取恶意用户，存储到数据库中
                        Malicious_driver malicious_driver = new Malicious_driver();
                        malicious_driver.setUsername(driver.getUsername());
                        malicious_driver.setIdCar(driver.getIdCar());
                        malicious_driver.setIdNumber(driver.getIdNumber());
                        malicious_driver.setEi1(driver.getEi1());
                        malicious_driver.setEi2(driver.getEi2());
                        malicious_driver.setType(type);
                        malicious_driverService.insert(malicious_driver);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
        return null;
    }
}
