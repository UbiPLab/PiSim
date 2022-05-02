package com.pisim.rsu.controller;

import com.alibaba.fastjson.JSONObject;
import com.pisim.rsu.encryption.AES;
import com.pisim.rsu.encryption.RSA;
import it.unisa.dia.gas.jpbc.Element;
import org.ujmp.core.util.Base64;
import com.pisim.rsu.parameterUtil.parameter;
import com.pisim.rsu.utils.HttpThread;
import com.pisim.rsu.parameterUtil.IP;

import static com.pisim.rsu.encryption.AES.getStrKeyAES;

public class TraceDriver {
    public static void Trace(Element REi1, Element REi2,String malicious_data,int type) {
        try {
            JSONObject data = new JSONObject();
            data.put("REi1", Base64.encodeBytes(REi1.toBytes()));
            data.put("REi2", Base64.encodeBytes(REi2.toBytes()));
            data.put("type",type);
            data.put("malicious_data",malicious_data);
            JSONObject jsonObject = new JSONObject();
            String AESKey = getStrKeyAES();
            byte[] encData = AES.encryptAES(data.toJSONString().getBytes(), AESKey);
            //用服务器公钥加密AES对称密钥
            String encAESKey = RSA.encrypt(AESKey, parameter.TA_rsa_pub);
            //对加密后的对称密钥签名 并添加入json
            jsonObject.put("sign", RSA.signature(encAESKey, parameter.RSU_rsa_pri));
            jsonObject.put("encData", Base64.encodeBytes(encData));
            jsonObject.put("encAESKey", encAESKey);
            //在登录注册时，这里driver_pub是driver的，请求RSU时，就是device的
            jsonObject.put("driver_pub", parameter.RSU_rsa_pub);
            HttpThread httpThread = new HttpThread(IP.taIp + "TraceDriver", jsonObject.toJSONString(), true);
            httpThread.start();
            jsonObject.clear();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
