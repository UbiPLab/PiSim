package com.pisim.rsu.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pisim.rsu.bean.CongestionInfo;
import com.pisim.rsu.service.CongestionInfoService;
import com.pisim.rsu.service.DrivingReportService;
import com.pisim.rsu.encryption.AES;
import com.pisim.rsu.encryption.RSA;
import it.unisa.dia.gas.jpbc.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.ujmp.core.util.Base64;
import com.pisim.rsu.parameterUtil.parameter;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;


import static com.pisim.rsu.controller.TraceDriver.Trace;
import static com.pisim.rsu.encryption.Opaak.verify_ZKPK;
import static com.pisim.rsu.parameterUtil.parameter.*;
import static com.pisim.rsu.utils.RsuUtil.*;
import static com.pisim.rsu.utils.updateDate.updateTe;

@RestController
@RequestMapping("/submitReport")
public class ReceiveReport {
    @Autowired
    CongestionInfoService congestionInfoService;
    @Autowired
    DrivingReportService drivingReportService;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject returnCongestionInfo(@RequestBody JSONObject jsonObject) {
        RSUReportRequestCount++;
        RSUReportRequestCount_temp++;
        try {
            String encAESKey = (String) jsonObject.get("encAESKey");
            String xigema = (String) jsonObject.get("sign");
            boolean flag = RSA.verify(encAESKey, xigema, (String) jsonObject.get("driver_pub"));
            if (flag) {
                //解密AES密钥
                String AESKey = RSA.decrypt(encAESKey, parameter.RSU_rsa_pri);
                //解密数据内容
                byte[] encData = Base64.decode((String) jsonObject.get("encData"));
                String data_temp = new String(AES.decryptAES(encData, AESKey));
                JSONObject data = JSONObject.parseObject(data_temp);
                switch (check(data)) {
                    case 1: {
                        Element REi1 = parameter.G1.newElementFromBytes(data.getBytes("REi1"));
                        Element REi2 = parameter.G1.newElementFromBytes(data.getBytes("REi2"));
                        //验证失败提交恶意司机追踪  返回恶意司机身份
                        Trace(REi1, REi2, data.toJSONString(),0);
                        JSONObject result = new JSONObject();
                        result.put("result", "bad");
                        return result;
                    }
                    case 2: {
                        //验证通过，接收交通拥堵情况
//                        CongestionInfo congestionInfo = new CongestionInfo();
//                        congestionInfo.setIndj((short) data.getIntValue("indj"));
//                        JSONArray jsonArray = data.getJSONArray("Query_EncKI");
//                        congestionInfo.setQueryindex(jsonArray.toString());
//                        congestionInfo.setThresholdQuery(data.getDouble("thresholdQuery"));
//                        //设定时间戳
                        Timestamp timestamp = new Timestamp(new Date().getTime());
//                        congestionInfo.setTimestamp(timestamp);
//                        //将交通拥堵状况存储到数据库中（表congestion）
//                        congestionInfoService.insertCongestionInfo(congestionInfo);
                        //驾驶报告存储到数据库（表report）
                        int temp_pidjs_size = data.getJSONArray("pidjs").size();
                        int temp_indj = data.getIntValue("indj");
                        boolean flag_temp = false;
                        if (temp_pidjs_size <= 3 && temp_indj == 0) {
                            flag_temp = true;
                        }
                        if (temp_pidjs_size > 3 && temp_pidjs_size <= 10 && temp_indj == 1) {
                            flag_temp = true;
                        }
                        if (temp_pidjs_size > 10 && temp_pidjs_size <= 20 && temp_indj == 2) {
                            flag_temp = true;
                        }
                        if (temp_pidjs_size > 20 && temp_indj == 3) {
                            flag_temp = true;
                        }
                        if (flag_temp) {
                            drivingReportService.insertDrivingReport(data, data.getString("pidj"), timestamp);
                            RSUReportRequestValidCount_temp++;
                            RSUReportRequestValidCount++;
                            JSONObject result = new JSONObject();
                            result.put("result", "success");
                            return result;
                        } else {
                            JSONObject result = new JSONObject();
                            result.put("result", "error");
                            return result;
                        }
                    }
                    default: {
                        JSONObject result = new JSONObject();
                        result.put("result", "error");
                        return result;
                    }
                }
            }
        } catch (
                Exception e) {
            JSONObject result = new JSONObject();
            result.put("result", "error");
            return result;
        }

        JSONObject result = new JSONObject();
        result.put("result", "error");
        return result;
    }

    //合法性校验
    private int check(JSONObject data) {
        //验证请求次数是否异常
        if (!parameter.grlpis.contains(data.getBigInteger("grlpi"))) {
            System.out.println("验证rdi导航查询或cdi提交报告请求失败----限制假名错误");
            return 1;
        }
        if (parameter.rlpis.contains(data.getBigInteger("rlpi"))) {
            System.out.println("验证rdi导航查询或cdi提交报告请求失败----请求假名重复使用");
            return 1;
        } else {
            parameter.rlpis.add(data.getBigInteger("rlpi"));
        }
        //验证零知识证明
        boolean flag = verify_ZKPK(data.getBigInteger("grlpi"), data.getBigInteger("M"), data.getBigInteger("daierta"), parameter.ZKPK_F, data.getBigInteger("a1"));
        if (flag) {
            if (pairingCheck(data)) {
                //正常请求
                return 2;
            } else {
                System.out.println("验证rdi导航查询或cdi提交报告请求失败----零知识证明错误");
                return 0;
            }
        } else {
            //身份无效
            return 0;
        }

    }

    public boolean pairingCheck(JSONObject data) {
        Element REi1 = parameter.G1.newElementFromBytes(data.getBytes("REi1"));
        Element REi2 = parameter.G1.newElementFromBytes(data.getBytes("REi2"));
        BigInteger ci = data.getBigInteger("ci");
        BigInteger ssi = data.getBigInteger("ssi");
        double[][] Query_EncKI = getMatrixArray(data.getJSONArray("Query_EncKI"), data.getIntValue("count"));
        BigInteger rlpi = data.getBigInteger("rlpi");
        int rci = data.getIntValue("rci");
        short indj = data.getShort("indj");
        List<String> pidjs = new ArrayList<>();
        JSONArray temp = data.getJSONArray("pidjs");
        for (Object o : temp) {
            pidjs.add((String) o);
        }
        byte[] xor_hsvjs = data.getBytes("xor_hsvjs");
        byte[] hsvj = data.getBytes("hsvj");
        String Mj_str = REi1.toString() + REi2.toString() + indj + pidjs.toString() + Arrays.toString(xor_hsvjs) + Arrays.toString(hsvj) + Arrays.deepToString(Query_EncKI) + rci;
        //零知识证明字符串
        String temp_ZKPK = data.getString("daierta") + data.getString("M") + data.getString("a1");
        return verifyRequest(REi1, REi2, Mj_str, ci, ssi, Query_EncKI, rlpi, rci, temp_ZKPK);
    }

}