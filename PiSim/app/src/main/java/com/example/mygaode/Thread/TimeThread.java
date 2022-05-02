package com.example.mygaode.Thread;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.amap.api.maps.AMap;
import com.example.mygaode.Object.MyHandler;

import org.ujmp.core.util.Base64;

import java.security.SecureRandom;


import encryption.Hash;
import parameter.parameter;

import static Navi_utils.driver_generate.generate_hsvj;
import static parameter.parameter.cycle;
import static parameter.parameter.te;

@SuppressWarnings("InfiniteLoopStatement")
public class TimeThread extends Thread {
    private MyHandler handler;
    AMap aMap;
    private WifiManager wifiManager;

    public TimeThread(MyHandler handler, AMap aMap, Context context) {
        this.handler = handler;
        this.aMap = aMap;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(
                Context.WIFI_SERVICE);
    }

    @Override
    public void run() {
        super.run();
        try {
            update();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void update() throws InterruptedException {
        SecureRandom secureRandom = new SecureRandom();
        //更新te
        parameter.te = (int) (System.currentTimeMillis() / (cycle));
        System.out.println("当前时间历元是:" + te);
        parameter.rci = -1;
        //更新pidj
        byte[] pidj_byte = Hash.sha128_byte(secureRandom.nextInt()+""+te);
        parameter.pidj = Base64.encodeBytes(pidj_byte);

        //更新hsvj
        parameter.hsvj = generate_hsvj(parameter.K, parameter.te);
        parameter.hsvjs.add(parameter.hsvj);
        int timeTemp;
        while (true) {
            //计算时间历元 以cycle为单位(高德是两分钟更新一次)
            timeTemp = (int) (System.currentTimeMillis() / 1000 - parameter.te * (cycle / 1000));
            System.out.println("相减结果" + timeTemp);
            if (timeTemp >= (cycle / 1000 - 10)) {
                System.out.println("提交时间历元为:" + te + "的路况报告");
                //提交路况报告
                //在时间周期最后5s去提交路况报告
                if (aMap.getMyLocation() != null) {
                    parameter.current_locationBean.setLat(aMap.getMyLocation().getLatitude());
                    parameter.current_locationBean.setLng(aMap.getMyLocation().getLongitude());
                }
                RelyThread relyThread = new RelyThread(handler, parameter.current_locationBean);
                relyThread.start();
                System.out.println(parameter.pidjs);
                relyThread.join();
                //休眠5s
                sleep(1000 * 10);
                //更新te
                parameter.te = (int) (System.currentTimeMillis() / (cycle));
                System.out.println("当前时间历元是:" + te);
                parameter.rci = -1;
                //清除pidjs和hsvjs
                wifiManager.setWifiEnabled(true);
                wifiManager.setWifiEnabled(false);
                parameter.pidjs.clear();
                parameter.hsvjs.clear();

                pidj_byte = Hash.sha128_byte(secureRandom.nextInt()+""+te);
                parameter.pidj = Base64.encodeBytes(pidj_byte);

                //更新hsvj
                parameter.hsvj = generate_hsvj(parameter.K, parameter.te);
                parameter.hsvjs.add(parameter.hsvj);
            } else {
//                if ((timeTemp - 5) > 1) {
//                    System.out.println("休眠" + (cycle/1000 - timeTemp - 5) + "秒");
//                    sleep((cycle/1000 - timeTemp - 5) * 1000);
//                }
                if ((cycle / 1000 - timeTemp - 10) > 0) {
                    System.out.println("休眠" + (cycle / 1000 - timeTemp - 10) + "秒");
                    sleep((cycle / 1000 - timeTemp - 10) * 1000);
                } else {
                    System.out.println("休眠1s");
                    sleep(1000);
                }
            }
        }
    }
}
