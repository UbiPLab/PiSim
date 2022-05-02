package com.example.mygaode.Thread;

import android.content.Context;
import android.os.Environment;
import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.example.mygaode.Object.MyHandler;

import parameter.IP;

import org.ujmp.core.util.Base64;

import java.util.Objects;

import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import parameter.parameter;

public class initializeThread extends Thread {
    private String result;
    private Context context;
    private MyHandler handler;

    public  initializeThread(Context context, MyHandler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            downLoadThread downLoadThread = new downLoadThread(handler, "http://114.55.33.26/config/pairing.properties", Objects.requireNonNull(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).getAbsolutePath(), "pairing.properties");
            downLoadThread.start();
            downLoadThread.join();
            parameter.pairing = PairingFactory.getPairing(Objects.requireNonNull(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).getAbsolutePath() + "/pairing.properties");
            parameter.G1 = parameter.pairing.getG1();
            parameter.G2 = parameter.pairing.getG2();
            HttpThread httpThread = new HttpThread(IP.taIp + "getparameter","",true);
            httpThread.start();
            httpThread.join();
            String result = parameter.result;
            JSONObject jsonObject = JSONObject.parseObject(result);
            //获取公共参数
            byte[] byte_temp = Base64.decode((String) Objects.requireNonNull(jsonObject.get("gpk_g")));
            parameter.gpk_g = parameter.G2.newElementFromBytes(byte_temp).getImmutable();
            byte_temp = Base64.decode((String) Objects.requireNonNull(jsonObject.get("gpk_A")));
            parameter.gpk_A = parameter.G2.newElementFromBytes(byte_temp).getImmutable();
            byte_temp = Base64.decode((String) Objects.requireNonNull(jsonObject.get("gpk_B")));
            parameter.gpk_B = parameter.G2.newElementFromBytes(byte_temp).getImmutable();
            byte_temp = Base64.decode((String) Objects.requireNonNull(jsonObject.get("gpk_g2")));
            parameter.gpk_g2 = parameter.G1.newElementFromBytes(byte_temp).getImmutable();
            parameter.MC = (int) jsonObject.get("MC");
            parameter.TA_rsa_pub = (String) jsonObject.get("rsa_pub");
            parameter.ZKPK_rou = jsonObject.getBigInteger("ZKPK_rou");
            parameter.ZKPK_F = jsonObject.getBigInteger("ZKPK_F");
            parameter.ZKPK_g = jsonObject.getBigInteger("ZKPK_g");
            parameter.ZKPK_b = jsonObject.getBigInteger("ZKPK_b");
            Message message = handler.obtainMessage(1);
            handler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage(2);
            handler.sendMessage(message);
        }
    }
}
