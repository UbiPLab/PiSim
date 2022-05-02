package com.example.mygaode.Thread;


import com.alibaba.fastjson.JSONObject;
import com.example.mygaode.Object.Malicious;

import parameter.IP;

import java.util.List;

import parameter.parameter;

public class GetMaliciousDriver extends Thread {
    private List<Malicious> maliciousList;

    public GetMaliciousDriver(List<Malicious> maliciousList) {
        this.maliciousList = maliciousList;
    }

    @Override
    public void run() {
        try {
            HttpThread httpThread = new HttpThread(IP.taIp + "getMaliciousDriver", "", true);
            httpThread.start();
            httpThread.join();
            JSONObject jsonObject = JSONObject.parseObject(parameter.result);
            String[] idCars = jsonObject.getString("idCar").split("~");
            String[] idNumber = jsonObject.getString("idNumber").split("~");
            maliciousList.clear();
            for (int i = 0; i < idCars.length; i++) {
                Malicious malicious = new Malicious(idCars[i], idNumber[i]);
                maliciousList.add(malicious);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
