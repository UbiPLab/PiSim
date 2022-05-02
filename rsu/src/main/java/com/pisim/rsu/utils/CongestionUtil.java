package com.pisim.rsu.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pisim.rsu.bean.CongestionInfo;
import com.pisim.rsu.bean.DrivingReport;
import com.pisim.rsu.service.CongestionInfoService;
import com.pisim.rsu.service.DrivingReportService;
import com.pisim.rsu.encryption.AES;
import com.pisim.rsu.encryption.RSA;
import com.pisim.rsu.parameterUtil.parameter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.pisim.rsu.encryption.AES.getStrKeyAES;
import static com.pisim.rsu.parameterUtil.IP.nspIp;
import static com.pisim.rsu.parameterUtil.parameter.*;

public class CongestionUtil {
    public void generateCongestion(CongestionInfoService congestionInfoService, DrivingReportService drivingReportService) {
        try {
            JSONObject jsonObject;
            CongestionInfo congestionInfo = new CongestionInfo();
            List<CongestionInfo> congestionInfos = new ArrayList<>();
            List<DrivingReport> drivingReportList = drivingReportService.getDrivingReportList(true);
            if (drivingReportList != null) {
                RSUReportValidCount_temp = drivingReportList.size();
                RSUReportValidCount = RSUReportValidCount + RSUReportValidCount_temp;
                RSUReportMaliciousCount = RSUReportRequestValidCount - RSUReportValidCount;
                System.out.println("虚假路况报告数量"+RSUReportMaliciousCount);
                for (DrivingReport drivingReport : drivingReportList) {
                    jsonObject = JSON.parseObject(drivingReport.getReport_string());
                    congestionInfo.setIndj((short) jsonObject.getIntValue("indj"));
                    JSONArray jsonArray = jsonObject.getJSONArray("Query_EncKI");
                    congestionInfo.setQueryindex(jsonArray.toString());
                    congestionInfo.setThresholdQuery(jsonObject.getDouble("thresholdQuery"));
                    //此处获取的是系统时间，当存入mysql中时，mysql采用UTC时间，比系统时间慢8个小时，不过从中读出后timestamp类型会自动进行转换以与系统时间对应（加8小时）
                    Timestamp timestamp = new Timestamp(new Date().getTime());
                    congestionInfo.setTimestamp(timestamp);
                    congestionInfos.add(congestionInfo);
                }
                congestionInfoService.insertCongestionInfo(congestionInfos);
                sendCongestionToNSP(congestionInfos);
            } else {
                System.out.println("生成路况信息-----数据库中无可用的驾驶报告，未生成交通拥堵信息");
                RSUReportMaliciousCount = RSUReportRequestValidCount - RSUReportValidCount;
                System.out.println("虚假路况报告数量"+RSUReportMaliciousCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCongestionToNSP(List<CongestionInfo> congestionInfos) {
        try {
            HttpThread getRsuPubKey = new HttpThread(nspIp + "getNspPubKey", false);
            getRsuPubKey.start();
            getRsuPubKey.join();
            parameter.NSP_rsa_pub = JSONObject.parseObject(parameter.result).getString("NSP_rsa_pub");
            String AESKey = getStrKeyAES();
            String encAESKey = RSA.encrypt(AESKey, parameter.NSP_rsa_pub);
            String xigema = RSA.signature(encAESKey, parameter.RSU_rsa_pri);
            JSONObject result = new JSONObject();
            result.put("encAESKey", encAESKey);
            result.put("sign", xigema);
            result.put("RSU_rsa_pub", parameter.RSU_rsa_pub);
            JSONObject res_data = new JSONObject();
            res_data.put("congestions", congestionInfos);
            byte[] res_encData = AES.encryptAES(res_data.toJSONString().getBytes(), AESKey);
            result.put("encData", res_encData);
            HttpThread httpThread = new HttpThread(nspIp + "sendCongestion", result.toJSONString(), true);
            httpThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
