package com.pisim.rsu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pisim.rsu.bean.CongestionInfo;
import com.pisim.rsu.bean.NaviQuery;
import com.pisim.rsu.dao.NaviQueryInfoDao;
import com.pisim.rsu.service.CongestionInfoService;
import com.pisim.rsu.encryption.AES;
import com.pisim.rsu.encryption.RSA;
import com.pisim.rsu.service.NaviQueryInfoService;
import it.unisa.dia.gas.jpbc.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;
import org.ujmp.core.util.Base64;
import com.pisim.rsu.parameterUtil.parameter;
import com.pisim.rsu.utils.HttpThread;
import com.pisim.rsu.parameterUtil.IP;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

import static com.pisim.rsu.controller.TraceDriver.Trace;
import static com.pisim.rsu.encryption.AES.getStrKeyAES;
import static com.pisim.rsu.encryption.Fuzzy_search.Search;
import static com.pisim.rsu.encryption.Opaak.verify_ZKPK;
import static com.pisim.rsu.parameterUtil.parameter.*;
import static com.pisim.rsu.utils.RsuUtil.*;
import static com.pisim.rsu.parameterUtil.IP.nspIp;

@RestController
@RequestMapping("/getCongestion")
public class SendCongestion {

    @Autowired
    CongestionInfoService congestionInfoService;

    @Autowired
    NaviQueryInfoService naviQueryInfoService;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject returnCongestionInfo(@RequestBody JSONObject jsonObject) {
        RSUNaviCount++;
        RSUNaviCount_temp++;
        try {
            String encAESKey = (String) jsonObject.get("encAESKey");
            String xigema = (String) jsonObject.get("sign");
            boolean flag = RSA.verify(encAESKey, xigema, (String) jsonObject.get("driver_pub"));
            if (flag) {
                //??????AES??????
                String AESKey = RSA.decrypt(encAESKey, parameter.RSU_rsa_pri);
                //??????????????????
                byte[] encData = Base64.decode((String) jsonObject.get("encData"));
                String data_temp = new String(AES.decryptAES(encData, AESKey));
                JSONObject data = JSONObject.parseObject(data_temp);
                switch (check(data)) {
                    case 1: {
                        Element REi1 = parameter.G1.newElementFromBytes(data.getBytes("REi1"));
                        Element REi2 = parameter.G1.newElementFromBytes(data.getBytes("REi2"));
                        //????????????????????????????????????  ????????????????????????
                        Trace(REi1, REi2, data.toJSONString(),0);
                        JSONObject result = new JSONObject();
                        result.put("result", "bad");
                        return result;
                    }
                    case 2: {
                        //???????????????????????????????????????
                        RSUNaviValidCount++;
                        RSUNaviValidCount_temp++;
                        //????????????????????????
                        NaviQuery naviQuery = new NaviQuery();
                        naviQuery.setREi1(data.getString("REi1"));
                        naviQuery.setREi2(data.getString("REi2"));
                        naviQuery.setIndex_EncKiI(data.getString("Index_EncKiI"));
                        naviQuery.setCount("" + data.getIntValue("count")/2);
                        naviQuery.setGrlpi(data.getString("grlpi"));
                        naviQuery.setRlpi(data.getString("rlpi"));
                        naviQuery.setDaierta(data.getString("daierta"));
                        naviQuery.setA1(data.getString("a1"));
                        naviQuery.setM(data.getString("M"));
                        Timestamp timestamp = new Timestamp(new Date().getTime());
                        naviQuery.setTimestamp(timestamp);
                        naviQueryInfoService.insertNaviQueryInfo(naviQuery);

                        long start = System.currentTimeMillis();
                        short[] congestion = getCongestion(
                                getMatrixArray(data.getJSONArray("Index_EncKiI"), data.getIntValue("count")),
                                getThreshold(data.getJSONArray("threshold")),
                                data.getIntValue("count"));
//                      System.out.println(Arrays.toString(congestion));
                        long end = System.currentTimeMillis();
                        System.out.println("RSU??????????????????????????????:" + (end - start));

                        //???????????????
                        JSONObject result = new JSONObject();
                        AESKey = getStrKeyAES();
                        encAESKey = RSA.encrypt(AESKey, (String) jsonObject.get("driver_pub"));
                        xigema = RSA.signature(encAESKey, parameter.RSU_rsa_pri);
                        result.put("encAESKey", encAESKey);
                        result.put("sign", xigema);
                        JSONObject res_data = new JSONObject();
                        res_data.put("congestion", congestion);
                        byte[] res_encData = AES.encryptAES(res_data.toJSONString().getBytes(), AESKey);
                        result.put("encData", res_encData);
                        result.put("result", "success");
                        return result;
                    }
                    default: {
                        JSONObject result = new JSONObject();
                        result.put("result", "error");
                        return result;
                    }
                }

            }
        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("result", "error");
            return result;
        }
        JSONObject result = new JSONObject();
        result.put("result", "error");
        return result;
    }

    //???????????????
    private int check(JSONObject data) {
        //??????????????????????????????
        if (!parameter.grlpis.contains(data.getBigInteger("grlpi"))) {
            //????????????
            System.out.println("??????rdi???????????????cdi????????????????????????----??????????????????");
            return 1;
        }
        if (parameter.rlpis.contains(data.getBigInteger("rlpi"))) {
            //????????????
            System.out.println("??????rdi???????????????cdi????????????????????????----????????????????????????");
            return 1;
        }
        parameter.rlpis.add(data.getBigInteger("rlpi"));
        //?????????????????????
        boolean flag = verify_ZKPK(data.getBigInteger("grlpi"), data.getBigInteger("M"), data.getBigInteger("daierta"), parameter.ZKPK_F, data.getBigInteger("a1"));
        if (flag) {
            if (pairingCheck(data)) {
                //????????????
                return 2;
            } else {
                //????????????
                System.out.println("??????rdi???????????????cdi????????????????????????----?????????????????????");
                return 0;
            }
        } else {
            //????????????
            return 0;
        }

    }

    public boolean pairingCheck(JSONObject data) {
        Element REi1 = parameter.G1.newElementFromBytes(data.getBytes("REi1"));
        Element REi2 = parameter.G1.newElementFromBytes(data.getBytes("REi2"));
        BigInteger ci = data.getBigInteger("ci");
        BigInteger ssi = data.getBigInteger("ssi");
        double[][] Index_EncKiI = getMatrixArray(data.getJSONArray("Index_EncKiI"), data.getIntValue("count"));
        BigInteger rlpi = data.getBigInteger("rlpi");
        int rci = data.getIntValue("rci");
        String Mj_str = REi1.toString() + REi2.toString() + Arrays.deepToString(Index_EncKiI) + rlpi.toString() + rci;
        //????????????????????????
        String temp_ZKPK = data.getString("daierta") + data.getString("M") + data.getString("a1");
        return verifyRequest(REi1, REi2, Mj_str, ci, ssi, Index_EncKiI, rlpi, rci, temp_ZKPK);
    }

    public short[] getCongestion(double[][] Index_EncKI, double[] threshold, int count) {
        try {
            System.out.println("??????????????????" + count / 2);
            RSUNaviPointCount_temp = count / 2;
            short[] congestion = new short[count / 2];
            List<Integer> record = new ArrayList<>();
            List<CongestionInfo> congestionInfos = congestionInfoService.getCongestionInfoList();
            if (congestionInfos != null) {
                boolean flag = false;
                Matrix Matrix_Index = DenseMatrix.Factory.importFromArray(Index_EncKI);
                for (int i = 0; i < count / 2; i = i + 1) {
                    for (CongestionInfo congestionInfo : congestionInfos) {
                        JSONArray jsonArray = JSON.parseArray(congestionInfo.getQueryindex());
                        double ThresholdQuery = congestionInfo.getThresholdQuery();
                        double[][] Index_Query = getMatrixArray(jsonArray, 2);
                        Matrix Matrix_Query = DenseMatrix.Factory.importFromArray(Index_Query);
                        double result = Search(Matrix_Index, Matrix_Query, i * 2);
                        int temp = (int) result;
                        if ((result - temp) > 0.5) {
                            temp++;
                        }
                        if (temp == (int) threshold[i] && temp == (int) ThresholdQuery) {
                            System.out.println("???????????????????????????" + i + "***************" + temp);
                            congestion[i] = congestionInfo.getIndj();
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        record.add(i);
                        congestion[i] = 0;
                    }
                    flag = false;
                }
                //???????????????NSP?????????
                double[][] findFromTa = new double[Index_EncKI.length][record.size() * 2];
                for (int i = 0; i < record.size() * 2; i = i + 2) {
                    for (int k = 0; k < Index_EncKI.length; k++) {
                        findFromTa[k][i] = Index_EncKI[k][record.get(i / 2)];
                        findFromTa[k][i + 1] = Index_EncKI[k][record.get(i / 2) + 1];
                    }
                }
                double[] threshold_temp = new double[record.size()];
                for (int i = 0; i < record.size(); i++) {
                    threshold_temp[i] = threshold[record.get(i)];
                }

                //???NSP????????????
                JSONArray jsonArray = SearchFromNsp(findFromTa, threshold_temp);
                //??????NSP??????????????????RSU?????????????????????
                if (jsonArray != null) {
                    int i = 0;
                    for (Integer integer : record) {
                        congestion[integer] = jsonArray.getShort(i);
                        i++;
                    }
                }
                //????????????????????????
                System.out.println("???NSP??????????????????:" + Arrays.toString(congestion));
                return congestion;
            } else {
                //?????????????????????????????????NSP????????????
                JSONArray jsonArray = SearchFromNsp(Index_EncKI, threshold);
                for (int i = 0; i < jsonArray.size(); i++) {
                    congestion[i] = jsonArray.getShort(i);
                }
                return congestion;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new short[count];
        }

    }

    //???NSP????????????
    private JSONArray SearchFromNsp(double[][] findFromTa, double[] threshold) {
        try {
            JSONObject data = new JSONObject();
            data.put("Index_EncKiI", findFromTa);
            data.put("count", threshold.length * 2);
            data.put("threshold", threshold);
            String AESKey = getStrKeyAES();
            byte[] encData = AES.encryptAES(data.toJSONString().getBytes(), AESKey);
            HttpThread getRsuPubKey = new HttpThread(nspIp + "getNspPubKey", false);
            getRsuPubKey.start();
            getRsuPubKey.join();
            parameter.NSP_rsa_pub = JSONObject.parseObject(parameter.result).getString("NSP_rsa_pub");
            String encAESKey = RSA.encrypt(AESKey, parameter.NSP_rsa_pub);
            JSONObject jsonObject = new JSONObject();
            //????????????????????????????????? ????????????json
            jsonObject.put("sign", RSA.signature(encAESKey, parameter.RSU_rsa_pri));
            jsonObject.put("encData", Base64.encodeBytes(encData));
            jsonObject.put("encAESKey", encAESKey);
            jsonObject.put("RSU_rsa_pub", parameter.RSU_rsa_pub);
            HttpThread httpThread = new HttpThread(IP.nspIp + "getCongestion", jsonObject.toJSONString(), true);
            httpThread.start();
            httpThread.join();
            //?????????????????????
            JSONObject result = JSONObject.parseObject(parameter.result);
            if ("success".equals(result.getString("result"))) {
                encAESKey = (String) result.get("encAESKey");
                String xigema = (String) result.get("sign");
                assert encAESKey != null;
                boolean flag = RSA.verify(encAESKey, xigema, parameter.NSP_rsa_pub);
                if (flag) {
                    //??????AES??????
                    AESKey = RSA.decrypt(encAESKey, parameter.RSU_rsa_pri);
                    encData = Base64.decode((String) Objects.requireNonNull(result.get("encData")));
                    //????????????????????????
                    String data_temp = new String(AES.decryptAES(encData, AESKey));
                    data = JSONObject.parseObject(data_temp);
                    return data.getJSONArray("congestion");
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
