package com.example.mygaode.Thread;

import android.os.Message;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.mygaode.Object.MyHandler;

import org.ujmp.core.util.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Navi_utils.LocationBean;
import encryption.AES;
import encryption.RSA;
import parameter.parameter;

import static Navi_utils.driver_generate.generate_Query;
import static parameter.IP.rsuIp;
import static parameter.parameter.cycle;


public class queryCongestionThread extends Thread {
    private double[][] pointList; //点序列 每行一个点 第一列为经度，第二列维度
    private short interval;
    private MyHandler handler;

    public queryCongestionThread(MyHandler handler, double[][] pointList, short interval) {
        this.pointList = pointList;
        this.interval = interval;
        this.handler = handler;
    }


    @Override
    public void run() {
        try {
            parameter.rci = parameter.rci + 1;
            //获取rsu公钥
            HttpThread getRsuPubKey = new HttpThread(rsuIp + "getRsuPubKey", false);
            getRsuPubKey.start();
            getRsuPubKey.join();
            parameter.RSU_rsa_pub = JSONObject.parseObject(parameter.result).getString("RSU_rsa_pub");
            //获取选点结果
            List<LocationBean> locationBeans = selectPoint();
            //计算时间历元 以两分钟为单位(高德地图便是两分钟更新一次拥堵库)
//            if ((int) (System.currentTimeMillis() / (cycle)) != parameter.te) {
//                parameter.te = (int) (System.currentTimeMillis() / (cycle));
//                parameter.rci = 0;
//            }
            System.out.println("请求点的数量" + locationBeans.size());
            JSONObject jsonObject = generate_Query(locationBeans,
                    parameter.SK1, parameter.SK2, parameter.V,
                    parameter.si, parameter.te, parameter.rci,
                    parameter.ZKPK_rou, parameter.ZKPK_F,
                    parameter.Ei1.getImmutable(), parameter.Ei2.getImmutable());
            assert jsonObject != null;
            parameter.result = null;
            HttpThread httpThread = new HttpThread(rsuIp + "getCongestion", jsonObject.toJSONString(), true);
            httpThread.start();
            httpThread.join();
            JSONObject result = JSONObject.parseObject(parameter.result);
            if (result.getString("result").equals("success")) {
                parameter.draw_result = true;
                Message message = handler.obtainMessage(10);
                handler.sendMessage(message);
                String encAESKey = (String) result.get("encAESKey");
                String xigema = (String) result.get("sign");
                assert encAESKey != null;
                boolean flag = RSA.verify(encAESKey, xigema, parameter.RSU_rsa_pub);
                if (flag) {
                    //解密AES密钥
                    String AESKey = RSA.decrypt(encAESKey, parameter.driver_rsa_pri);
                    byte[] encData = Base64.decode((String) Objects.requireNonNull(result.get("encData")));
                    //解密接收到的数据
                    String data_temp = new String(AES.decryptAES(encData, AESKey));
                    JSONObject data = JSONObject.parseObject(data_temp);
                    JSONArray jsonArray = data.getJSONArray("congestion");
                    short[] congestionList = new short[jsonArray.size()];
                    for (int i = 0; i < jsonArray.size(); i++) {
                        congestionList[i] = jsonArray.getShort(i);
                    }
                    parameter.congestionList = congestionList;
                }
            } else {
                parameter.draw_result = false;
                if (result.getString("result").equals("bad")) {
                    Message message = handler.obtainMessage(21);
                    handler.sendMessage(message);
                    parameter.congestionList = new short[pointList.length / interval + 2];
                } else {
                    Message message = handler.obtainMessage(11);
                    handler.sendMessage(message);
                    parameter.congestionList = new short[pointList.length / interval + 2];
                }
            }
        } catch (Exception e) {
            parameter.congestionList = new short[pointList.length / interval + 2];
        }
    }

    /**
     * @return 得到选点结果
     */
    private List<LocationBean> selectPoint() {
        List<LocationBean> selectResult = new ArrayList<>();
//        selectResult.add(new LocationBean(pointList[0][0], pointList[0][1]));
        //   selectResult.add(new LocationBean(pointList[0][2], pointList[0][3]));
        for (int i = 0; i < pointList.length / interval; i++) {
            selectResult.add(new LocationBean(pointList[i][0], pointList[i][1]));
            //       selectResult.add(new LocationBean(pointList[i][2], pointList[i][3]));
        }
//        selectResult.add(new LocationBean(pointList[pointList.length - 1][0], pointList[pointList.length - 1][1]));
        selectResult.add(new LocationBean(pointList[pointList.length - 1][2], pointList[pointList.length - 1][3]));

        return selectResult;
    }


}
