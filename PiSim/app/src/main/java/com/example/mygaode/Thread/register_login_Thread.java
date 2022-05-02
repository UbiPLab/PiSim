package com.example.mygaode.Thread;

import android.os.Message;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mygaode.Object.MyHandler;

import encryption.Hash;
import parameter.IP;

import org.ujmp.core.DenseMatrix;
import org.ujmp.core.util.Base64;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Objects;

import encryption.AES;
import encryption.RSA;
import parameter.parameter;

import static Navi_utils.driver_generate.generate_hsvj;
import static Navi_utils.driver_generate.generate_loginData;
import static Navi_utils.driver_generate.generate_registerDate;

public class register_login_Thread extends Thread {
    private String username;
    private String password;
    private String driverPrivateKey;
    private String driverPubKey;
    private String idNumber;
    private String idCar;
    private String si;
    private MyHandler handler;
    private boolean tag;//tag为true则为登录 否则为注册


    public register_login_Thread(MyHandler handler, String username, String password, String idNumber, String idCar, String si, String driverPriKey, String driverPubKey) {
        parameter.username = this.username = username;
        parameter.password = this.password = password;
        this.driverPrivateKey = driverPriKey;
        this.driverPubKey = driverPubKey;
        this.idNumber = idNumber;
        this.idCar = idCar;
        this.si = si;//司机秘密si
        this.handler = handler;
        this.tag = false;
    }

    public register_login_Thread(MyHandler handler, String username, String password, String driverPriKey, String driverPubKey) {
        this.username = username;
        this.password = password;
        this.driverPrivateKey = driverPriKey;
        this.driverPubKey = driverPubKey;
        this.handler = handler;
        this.tag = true;
    }

    @Override
    public void run() {
        if (this.tag) {
            login();
        } else {
            register();
        }
    }

    private void login() {
        try {
            if (!parameter.TA_rsa_pub.isEmpty()) {
                JSONObject reqJson = generate_loginData(username, password, driverPrivateKey, driverPubKey, parameter.TA_rsa_pub);
                assert reqJson != null;
                long startTime = System.currentTimeMillis();
                HttpThread httpThread = new HttpThread(IP.taIp + "login", reqJson.toJSONString(), true);
                httpThread.start();
                httpThread.join();
                long endTime = System.currentTimeMillis();
                System.out.println("登录耗时:" + (endTime - startTime));
                JSONObject jsonObject = JSONObject.parseObject(parameter.result);
                if (!jsonObject.isEmpty() && jsonObject.getString("result").equals("success")) {
                    String encAESKey = (String) jsonObject.get("encAESKey");
                    String xigema = (String) jsonObject.get("sign");
                    assert encAESKey != null;
                    boolean flag = RSA.verify(encAESKey, xigema, parameter.TA_rsa_pub);
                    if (flag) {
                        //登录成功
                        Message message = handler.obtainMessage(6);
                        handler.sendMessage(message);
                        //解密AES密钥
                        String AESKey = RSA.decrypt(encAESKey, driverPrivateKey);
                        byte[] encData = Base64.decode((String) Objects.requireNonNull(jsonObject.get("encData")));
                        //解密接收到的数据
                        String data_temp = new String(AES.decryptAES(encData, AESKey));
                        JSONObject data = JSONObject.parseObject(data_temp);
                        parameter.Ei1 = parameter.G1.newElementFromBytes(data.getBytes("Ei1")).getImmutable();
                        parameter.Ei2 = parameter.G1.newElementFromBytes(data.getBytes("Ei2")).getImmutable();
                        parameter.SK1 = DenseMatrix.Factory.importFromArray(getMatrixArray(data.getJSONArray("SK1_array")));
                        parameter.SK2 = DenseMatrix.Factory.importFromArray(getMatrixArray(data.getJSONArray("SK2_array")));
                        parameter.HKP = DenseMatrix.Factory.importFromArray(getMatrixArray(data.getJSONArray("HKP_array")));
                        parameter.V = DenseMatrix.Factory.importFromArray(getVectorArray((JSONArray) data.getJSONArray("V_array").toArray()[0]));
                        parameter.K = data.getBigInteger("K");
                        int te = (int) (System.currentTimeMillis() / (1000 * 2 * 60));
                        parameter.hsvj = generate_hsvj(parameter.K,te);
                        parameter.hsvjs.add(parameter.hsvj);
                        SecureRandom secureRandom = new SecureRandom();
                        byte[] pidj_byte = Hash.sha128_byte("" + secureRandom.nextInt() +""+ te);
                        parameter.pidj = Base64.encodeBytes(pidj_byte);
                    }else {
                        //登录失败
                        Message message = handler.obtainMessage(7);
                        handler.sendMessage(message);
                    }
                } else {
                    //登录失败
                    Message message = handler.obtainMessage(7);
                    handler.sendMessage(message);
                }
            } else {
                //没有服务器公钥 登录失败
                Message message = handler.obtainMessage(22);
                handler.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //没有服务器公钥 登录失败
            Message message = handler.obtainMessage(26);
            handler.sendMessage(message);
        }
    }

    private void register() {
        try {
            if (!parameter.TA_rsa_pub.isEmpty()) {
                JSONObject reqJson = generate_registerDate(username, password, idNumber, idCar, si, driverPrivateKey, driverPubKey, parameter.TA_rsa_pub);
                assert reqJson != null;
                HttpThread httpThread = new HttpThread(IP.taIp + "register", reqJson.toJSONString(), true);
                httpThread.start();
                httpThread.join();
                //结果处理
                JSONObject jsonObject = JSONObject.parseObject(parameter.result);
                if (jsonObject.getString("result").equals("success")) {
                    String encAESKey = (String) jsonObject.get("encAESKey");
                    String xigema = (String) jsonObject.get("sign");
                    assert encAESKey != null;
                    boolean flag = RSA.verify(encAESKey, xigema, parameter.TA_rsa_pub);
                    if (flag) {
                        //注册成功
                        Message message = handler.obtainMessage(4);
                        handler.sendMessage(message);
                        //解密AES密钥
                        String AESKey = RSA.decrypt(encAESKey, driverPrivateKey);
                        byte[] encData = Base64.decode((String) Objects.requireNonNull(jsonObject.get("encData")));
                        //解密接收到的数据
                        String data_temp = new String(AES.decryptAES(encData, AESKey));
                        JSONObject data = JSONObject.parseObject(data_temp);
                        parameter.Ei1 = parameter.G1.newElementFromBytes(data.getBytes("Ei1")).getImmutable();
                        parameter.Ei2 = parameter.G1.newElementFromBytes(data.getBytes("Ei2")).getImmutable();
                        parameter.SK1 = DenseMatrix.Factory.importFromArray(getMatrixArray(data.getJSONArray("SK1_array")));
                        parameter.SK2 = DenseMatrix.Factory.importFromArray(getMatrixArray(data.getJSONArray("SK2_array")));
                        parameter.HKP = DenseMatrix.Factory.importFromArray(getMatrixArray(data.getJSONArray("HKP_array")));
                        parameter.V = DenseMatrix.Factory.importFromArray(getVectorArray((JSONArray) data.getJSONArray("V_array").toArray()[0]));
                        parameter.K = data.getBigInteger("K");
                        int te = (int) (System.currentTimeMillis() / (1000 * 2 * 60));
                        parameter.hsvj = generate_hsvj(parameter.K,te);
                        parameter.hsvjs.add(parameter.hsvj);
                        SecureRandom secureRandom = new SecureRandom();
                        byte[] pidj_byte = Hash.sha128_byte("" + secureRandom.nextInt() +""+ te);
                        parameter.pidj = Base64.encodeBytes(pidj_byte);
                    }
                } else {
                    Message message = handler.obtainMessage(5);
                    handler.sendMessage(message);
                }
            } else {
                //没有服务器公钥 登录失败
                Message message = handler.obtainMessage(22);
                handler.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage(26);
            handler.sendMessage(message);
        }
    }

    private double[][] getMatrixArray(JSONArray jsonArray) {
        int k1 = jsonArray.size();
        int k2 = jsonArray.getJSONArray(0).size();
        double[][] temp = new double[k1][k2];
        for (int i = 0; i < k1; i++) {
            JSONArray jsonArray1 = (JSONArray) jsonArray.toArray()[i];
            for (int k = 0; k < k2; k++) {
                temp[i][k] = jsonArray1.getDouble(k);
            }
        }
        return temp;
    }

    private double[] getVectorArray(JSONArray jsonArray) {
        int k1 = parameter.k1;
        double[] temp = new double[k1];
        for (int i = 0; i < k1; i++) {
            temp[i] = jsonArray.getDouble(i);
        }
        return temp;
    }
}
