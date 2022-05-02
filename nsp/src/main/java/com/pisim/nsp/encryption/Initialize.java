package com.pisim.nsp.encryption;

import com.alibaba.fastjson.JSONObject;
import com.pisim.nsp.parameterUtil.IP;
import com.pisim.nsp.parameterUtil.parameter;
import com.pisim.nsp.utils.HttpThread;

import java.net.Inet4Address;
import java.util.Map;

public class Initialize extends Thread {
    @Override
    public void run() {
        try {
            initilize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initilize(){
        JSONObject jsonObject1 = new JSONObject();
        try {
            Map<String,String> RSAPair =  RSA.generateRsaKeyPair();
            jsonObject1.put("PubKey",RSAPair.get("publicKey"));
            jsonObject1.put("address", Inet4Address.getLocalHost());
            parameter.NSP_rsa_pub = RSAPair.get("publicKey");
            parameter.NSP_rsa_pri = RSAPair.get("privateKey");
            HttpThread register = new HttpThread(IP.taIp + "RsuNspRegister", jsonObject1.toJSONString(), true);
            register.start();
            System.out.println("NSP初始化完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
