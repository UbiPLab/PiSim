package com.example.mygaode.Thread;

import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.example.mygaode.Object.MyHandler;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import Navi_utils.LocationBean;
import parameter.parameter;

import static Navi_utils.driver_generate.generate_Mj;
import static Navi_utils.driver_generate.generate_report;
import static Navi_utils.driver_generate.testGenerate_Hsvjs;
import static Navi_utils.driver_generate.testGenerate_Pidjs;
import static parameter.IP.rsuIp;
import static parameter.parameter.cycle;

public class submitReportThread extends Thread {
    private LocationBean locationBean; //点序列 每行一个点 第一列为经度，第二列维度
    private MyHandler handler;

    public submitReportThread(LocationBean locationBean) {
        this.locationBean = locationBean;
    }

    public submitReportThread(MyHandler handler, LocationBean locationBean) {
        this.handler = handler;
        this.locationBean = locationBean;
    }

    @Override
    public void run() {
//        testSubmitReport();
        SubmitReport();
    }

    private void SubmitReport() {
        try {
            parameter.rci = parameter.rci + 1;
            //获取rsu公钥
            HttpThread getRsuPubKey = new HttpThread(rsuIp + "getRsuPubKey", false);
            getRsuPubKey.start();
            getRsuPubKey.join();
            parameter.RSU_rsa_pub = JSONObject.parseObject(parameter.result).getString("RSU_rsa_pub");
            //计算时间历元 以两分钟为单位(高德就是两分钟更新一次拥堵库)
//            if ((int) (System.currentTimeMillis() / (cycle)) != parameter.te) {
//                parameter.te = (int) (System.currentTimeMillis() / (cycle));
//                parameter.rci = 0;
//            }
            short indj = 0;
            if (parameter.pidjs.size() > 3) {
                indj = 1;
            }
            if (parameter.pidjs.size() > 10) {
                indj = 2;
            }
            if (parameter.pidjs.size() > 20) {
                indj = 3;
            }
            Map<String, Object> Mj = generate_Mj(indj, parameter.hsvjs.get(0), parameter.pidj, parameter.pidjs,parameter.hsvjs);
            JSONObject jsonObject = generate_report(
                    locationBean, Mj,
                    parameter.SK1, parameter.SK2,
                    parameter.V, parameter.si, parameter.te,
                    parameter.rci, parameter.ZKPK_rou, parameter.ZKPK_F,
                    parameter.Ei1, parameter.Ei2);

            assert jsonObject != null;
            HttpThread httpThread = new HttpThread(rsuIp + "submitReport", jsonObject.toJSONString(), true);
            httpThread.start();
            httpThread.join();
            JSONObject result = JSONObject.parseObject(parameter.result);
            //报告提交成功
            if (result.getString("result").equals("success")) {
                Message message = handler.obtainMessage(12);
                handler.sendMessage(message);
                System.out.println("路况报告提交成功");
            } else {
                System.out.println("路况报告提交失败");
                if (result.getString("result").equals("bad")) {
                    //服务器认为恶意行为
                    Message message = handler.obtainMessage(21);
                    handler.sendMessage(message);
                } else {
                    //报告提交失败
                    Message message = handler.obtainMessage(13);
                    handler.sendMessage(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void testSubmitReport() {
        try {
            //获取rsu公钥
            HttpThread getRsuPubKey = new HttpThread(rsuIp + "getRsuPubKey", false);
            getRsuPubKey.start();
            getRsuPubKey.join();
            parameter.RSU_rsa_pub = JSONObject.parseObject(parameter.result).getString("RSU_rsa_pub");
            //计算时间历元 以两分钟为单位(高德就是两分钟更新一次拥堵库)
//            if ((int) (System.currentTimeMillis() / (cycle)) != parameter.te) {
//                parameter.te = (int) (System.currentTimeMillis() / (cycle));
//                parameter.rci = 0;
//            }
            SecureRandom secureRandom = new SecureRandom();
            List<String> pidjs = testGenerate_Pidjs();
            short indj = (short) secureRandom.nextInt(3);
            List<byte[]> Hsvjs = testGenerate_Hsvjs();
            Map<String, Object> Mj = generate_Mj(indj, Hsvjs.get(0), parameter.pidjss.get(secureRandom.nextInt(100)), pidjs, Hsvjs);
            long start = System.currentTimeMillis();
            JSONObject jsonObject = generate_report(
                    locationBean, Mj,
                    parameter.SK1, parameter.SK2,
                    parameter.V, parameter.si, parameter.te,
                    parameter.rci, parameter.ZKPK_rou, parameter.ZKPK_F,
                    parameter.Ei1, parameter.Ei2);
            long end = System.currentTimeMillis();
            System.out.println(pidjs.size());
            System.out.println("*******************************" + (end - start));
            assert jsonObject != null;
            HttpThread httpThread = new HttpThread(rsuIp + "submitReport", jsonObject.toJSONString(), true);
            httpThread.start();
            httpThread.join();
            JSONObject result = JSONObject.parseObject(parameter.result);
            //报告提交成功
            if (result.getString("result").equals("success")) {
                Message message = handler.obtainMessage(12);
                handler.sendMessage(message);
            } else {
                if (result.getString("result").equals("bad")) {
                    //服务器认为恶意行为
                    Message message = handler.obtainMessage(21);
                    handler.sendMessage(message);
                } else {
                    //报告提交失败
                    Message message = handler.obtainMessage(13);
                    handler.sendMessage(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //报告提交失败
            Message message = handler.obtainMessage(13);
            handler.sendMessage(message);
        }
    }

}
