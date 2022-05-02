package com.example.mygaode.Thread;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.example.mygaode.Object.MyHandler;
import parameter.IP;

import org.ujmp.core.util.Base64;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Objects;

import encryption.AES;
import encryption.RSA;
import parameter.parameter;

import static encryption.AES.getStrKeyAES;

public class getClSignatureThread extends Thread {
    private String username;
    private String password;
    private MyHandler handler;

    public getClSignatureThread(MyHandler handler, String username, String password) {
        this.handler = handler;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run() {
        getClsignature();
    }

    private void getClsignature() {
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            SecureRandom secureRandom = new SecureRandom();
            BigInteger random = new BigInteger(256, secureRandom);
            data.put("message", random);
            JSONObject jsonObject = new JSONObject();
            //对发送的数据用AES加密
            String AESKey = getStrKeyAES();
            byte[] encData = AES.encryptAES(data.toJSONString().getBytes(), AESKey);
            //用服务器公钥加密AES对称密钥
            String encAESKey = RSA.encrypt(AESKey, parameter.TA_rsa_pub);
            //对加密后的对称密钥签名 并添加入json
            jsonObject.put("sign", RSA.signature(encAESKey, parameter.driver_rsa_pri));
            jsonObject.put("encData", Base64.encodeBytes(encData));
            jsonObject.put("encAESKey", encAESKey);
            //在登录注册时，这里driver_pub是driver的
            jsonObject.put("driver_pub", parameter.driver_rsa_pub);
            HttpThread httpThread = new HttpThread(IP.taIp + "getClSignature", jsonObject.toJSONString(), true);
            httpThread.start();
            httpThread.join();
            jsonObject = JSONObject.parseObject(parameter.result);
            encAESKey = (String) jsonObject.get("encAESKey");
            String xigema = (String) jsonObject.get("sign");
            assert encAESKey != null;
            boolean flag = RSA.verify(encAESKey, xigema, parameter.TA_rsa_pub);
            if (flag) {
                //解密AES密钥
                AESKey = RSA.decrypt(encAESKey, parameter.driver_rsa_pri);
                encData = Base64.decode((String) Objects.requireNonNull(jsonObject.get("encData")));
                //解密接收到的数据
                String data_temp = new String(AES.decryptAES(encData, AESKey));
                data = JSONObject.parseObject(data_temp);
                parameter.e = data.getBigInteger("e");
                parameter.v = data.getBigInteger("v");
                parameter.s = data.getBigInteger("s");
                parameter.message = data.getBigInteger("message");
                Message message = handler.obtainMessage(9);
                handler.sendMessage(message);
                SharedPreferences sharedPreferences = handler.context.getSharedPreferences("clSignature", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("e", parameter.e.toString());
                editor.putString("v", parameter.v.toString());
                editor.putString("s", parameter.s.toString());
                editor.putString("message", parameter.message.toString());
                editor.apply();
            } else {
                Message message = handler.obtainMessage(10);
                handler.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage(10);
            handler.sendMessage(message);
        }
    }
}
