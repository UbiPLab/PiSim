package com.example.mygaode.Thread;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.mygaode.Object.MyHandler;

import org.apache.commons.lang.RandomStringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.List;

import parameter.parameter;

public class ReportThread extends Thread {

    private Context context;
    private WifiManager wifiManager;
    private MyHandler handler;

    public ReportThread(Context context, MyHandler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {

        boolean flag = true;
        SecureRandom secureRandom = new SecureRandom();
        String ssid;
//        Calendar calendar = Calendar.getInstance();
//        Date firstTime = calendar.getTime();
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                //获取定位信息
//                AMapLocation aMapLocation = new AMapLocation("");
//                mAMapLocationListener.onLocationChanged(aMapLocation);
//                //提交报告
//                RelyThread relyThread = new RelyThread(handler,parameter.current_locationBean);
//                relyThread.start();
//            }
//        }, firstTime, cycle);

        while (true) {
            //打开热点 供其他司机搜索
            ssid = "1" + parameter.pidj;
//            if (flag) {
////                parameter.te = (int) (System.currentTimeMillis() / (cycle));
////                byte[] pidj_byte = Hash.sha128_byte("" + secureRandom.nextInt() + "" + te);
////                parameter.pidj = Base64.encodeBytes(pidj_byte);
////                parameter.pidjs.add(parameter.pidj);
//                //1表示广播的为pidj
//                ssid = "1" + parameter.pidj;
//                flag = !flag;
//            } else {
////                parameter.hsvj = generate_hsvj(parameter.K, parameter.te);
////                if (!parameter.hsvjs.contains(parameter.hsvjs)) {
////                    parameter.hsvjs.add(parameter.hsvj);
////                }
//                //2表示广播的为hsvj
//                ssid = "2" + Base64.encodeBytes(parameter.hsvj);
//                flag = !flag;
//            }
            String password = RandomStringUtils.randomAlphanumeric(32);
            createWiFiAP(createWifiInfo2(ssid, password), true);
            SecureRandom random = new SecureRandom();
            //开启热点一段时间
            try {
                sleep(parameter.frequency + random.nextInt(1000 * 10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //关闭热点 搜索其他司机
            createWiFiAP(createWifiInfo2(ssid, password), false);
            //对周边进行扫描
            startScan();
        }
    }

    private void startScan() {
        wifiManager.setWifiEnabled(true);
        wifiManager.startScan();
        try {
            sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //得到扫描结果
        List<ScanResult> results = wifiManager.getScanResults();
        if (!results.isEmpty()) {
            for (ScanResult result : results) {
                String temp = result.SSID;
                boolean flag;
                System.out.println(temp);
                //1表示广播的为pidj
                if (temp.charAt(0) == '1') {
                    flag = true;
                } else {
                    flag = false;
                }
                System.out.println(temp);
                temp = temp.substring(1);
                String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
                Boolean isLegal = temp.matches(base64Pattern);
                if (isLegal) {
                    if (flag) {
                        parameter.pidjs.add(temp);
                        System.out.println("成功加入集合" + temp);
                    } else {
                        //     parameter.hsvjs.add(Base64.decode(temp));
                    }
                }

            }
        }
        wifiManager.setWifiEnabled(false);
    }

    /**
     * 配置热点信息
     *
     * @param ssid     ssid
     * @param password 热点密码
     * @return 配置结果
     */
    private static WifiConfiguration createWifiInfo2(String ssid,
                                                     String password) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        config.SSID = ssid;
        config.preSharedKey = password;
        config.allowedAuthAlgorithms
                .set(4);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        return config;
    }

    /**
     * 创建WiFi热点
     *
     * @param config       WiFi配置信息
     * @param paramBoolean true为开启WiFi热点，false为关闭
     * @return 返回开启成功状态，true为成功，false为失败
     */
    private boolean createWiFiAP(WifiConfiguration config, boolean paramBoolean) {
        // 开启热点前，如果WiFi可用，先关闭WiFi
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(
                Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
        String TAG = "info   ----     ";
        // Log.i(TAG, "into startWifiAp（） 启动一个Wifi 热点！");
        boolean ret = false;
        try {
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            ret = (Boolean) method.invoke(wifiManager, config, paramBoolean);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.d(TAG, "stratWifiAp() IllegalArgumentException e");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "stratWifiAp() IllegalAccessException e");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Log.d(TAG, "stratWifiAp() InvocationTargetException e");
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d(TAG, "stratWifiAp() SecurityException e");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.d(TAG, "stratWifiAp() NoSuchMethodException e");
        }
        //     Log.i(TAG, "out startWifiAp（） 启动一个Wifi 热点！");
        return ret;
    }
}
