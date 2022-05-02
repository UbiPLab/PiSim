package com.pisim.rsu.encryption;

import com.alibaba.fastjson.JSONObject;
import com.pisim.rsu.utils.HttpThread;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.ujmp.core.util.Base64;
import com.pisim.rsu.parameterUtil.IP;
import com.pisim.rsu.parameterUtil.parameter;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.Map;
import java.util.Objects;

public class Initialize extends Thread {
    private String pairingPath;

    public Initialize(String pairingPath) {
        this.pairingPath = pairingPath;
    }

    @Override
    public void run() {
        super.run();
        try {
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() throws InterruptedException, IOException {
        parameter.pairing = PairingFactory.getPairing(pairingPath);
        parameter.G1 = parameter.pairing.getG1();
        parameter.G2 = parameter.pairing.getG2();
        HttpThread httpThread = new HttpThread(IP.taIp + "getparameter", "", true);
        httpThread.start();
        httpThread.join();
        String result = parameter.result;
        JSONObject jsonObject = JSONObject.parseObject(result);
        //获取公共参数
        byte[] byte_temp = Base64.decode((String) Objects.requireNonNull(jsonObject.get("gpk_g")));
        parameter.gpk_g = parameter.G2.newElementFromBytes(byte_temp);
        byte_temp = Base64.decode((String) Objects.requireNonNull(jsonObject.get("gpk_A")));
        parameter.gpk_A = parameter.G2.newElementFromBytes(byte_temp);
        byte_temp = Base64.decode((String) Objects.requireNonNull(jsonObject.get("gpk_B")));
        parameter.gpk_B = parameter.G2.newElementFromBytes(byte_temp);
        byte_temp = Base64.decode((String) Objects.requireNonNull(jsonObject.get("gpk_g2")));
        parameter.gpk_g2 = parameter.G1.newElementFromBytes(byte_temp);
        parameter.MC = (int) jsonObject.get("MC");
        parameter.TA_rsa_pub = (String) jsonObject.get("rsa_pub");
        parameter.ZKPK_rou = jsonObject.getBigInteger("ZKPK_rou");
        parameter.ZKPK_F = jsonObject.getBigInteger("ZKPK_F");
        parameter.ZKPK_g = jsonObject.getBigInteger("ZKPK_g");
        parameter.ZKPK_b = jsonObject.getBigInteger("ZKPK_b");
        parameter.clSign_PK_d1 = jsonObject.getBigInteger("PK_d1");
        parameter.clSign_PK_d2 = jsonObject.getBigInteger("PK_d2");
        parameter.clSign_PK_d3 = jsonObject.getBigInteger("PK_d3");
        parameter.clSign_PK_n = jsonObject.getBigInteger("PK_n");
        JSONObject jsonObject1 = new JSONObject();
        try {
            Map<String,String> RSAPair =  RSA.generateRsaKeyPair();
            jsonObject1.put("PubKey",RSAPair.get("publicKey"));
            jsonObject1.put("address",Inet4Address.getLocalHost());
            parameter.RSU_rsa_pub = RSAPair.get("publicKey");
            parameter.RSU_rsa_pri = RSAPair.get("privateKey");
            HttpThread register = new HttpThread(IP.taIp + "RsuNspRegister", jsonObject1.toJSONString(), true);
            register.start();
            System.out.println("初始化请求参数完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
