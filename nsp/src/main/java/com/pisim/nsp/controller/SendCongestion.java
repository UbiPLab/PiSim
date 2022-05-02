package com.pisim.nsp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pisim.nsp.bean.CongestionInfo;
import com.pisim.nsp.service.CongestionInfoService;
import com.pisim.nsp.encryption.AES;
import com.pisim.nsp.encryption.RSA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;
import org.ujmp.core.util.Base64;
import com.pisim.nsp.parameterUtil.parameter;

import java.util.List;

import static com.pisim.nsp.encryption.AES.getStrKeyAES;
import static com.pisim.nsp.encryption.Fuzzy_search.Search;
import static com.pisim.nsp.parameterUtil.parameter.NSPNaviCount;
import static com.pisim.nsp.parameterUtil.parameter.NSPNaviCount_temp;
import static com.pisim.nsp.utils.RsuUtil.*;

@RestController
@RequestMapping("/getCongestion")
public class SendCongestion {
    @Autowired
    CongestionInfoService congestionInfoService;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject returnCongestionInfo(@RequestBody JSONObject jsonObject) {
        NSPNaviCount++;
        NSPNaviCount_temp++;
        try {
            String encAESKey = (String) jsonObject.get("encAESKey");
            String xigema = (String) jsonObject.get("sign");
            boolean flag = RSA.verify(encAESKey, xigema, (String) jsonObject.get("RSU_rsa_pub"));
            if (flag) {
                //解密AES密钥
                String AESKey = RSA.decrypt(encAESKey, parameter.NSP_rsa_pri);
                //解密数据内容
                byte[] encData = Base64.decode((String) jsonObject.get("encData"));
                String data_temp = new String(AES.decryptAES(encData, AESKey));
                JSONObject data = JSONObject.parseObject(data_temp);
                //验证通过，计算交通拥堵情况
                short[] congestion = getCongestion(
                        getMatrixArray(data.getJSONArray("Index_EncKiI"), data.getIntValue("count")),
                        getThreshold(data.getJSONArray("threshold")),
                        data.getIntValue("count"));
                //加密并返回
                JSONObject result = new JSONObject();
                AESKey = getStrKeyAES();
                encAESKey = RSA.encrypt(AESKey, (String) jsonObject.get("RSU_rsa_pub"));
                xigema = RSA.signature(encAESKey, parameter.NSP_rsa_pri);
                result.put("encAESKey", encAESKey);
                result.put("sign", xigema);
                JSONObject res_data = new JSONObject();
                res_data.put("congestion", congestion);
                byte[] res_encData = AES.encryptAES(res_data.toJSONString().getBytes(), AESKey);
                result.put("encData", res_encData);
                result.put("result", "success");
                return result;
            } else {
                JSONObject result = new JSONObject();
                result.put("result", "error");
                return result;
            }
        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("result", "error");
            return result;
        }
    }


    public short[] getCongestion(double[][] Index_EncKI, double[] threshold, int count) {
        try {
            System.out.println("查询点的个数" + count / 2);
            parameter.NSPNaviPointCount_temp = count/2;
            short[] congestion = new short[count / 2];
            List<CongestionInfo> congestionInfos = congestionInfoService.getCongestionInfoList();
            Matrix Matrix_Index = DenseMatrix.Factory.importFromArray(Index_EncKI);
            if (congestionInfos != null) {
                for (int i = 0; i < count / 2; i = i + 1) {
                    for (CongestionInfo congestionInfo : congestionInfos) {
                        JSONArray jsonArray = JSON.parseArray(congestionInfo.getQueryindex());
                        double[][] Index_Query = getMatrixArray(jsonArray, 2);
                        double thresholdQuery = congestionInfo.getThresholdQuery();
                        Matrix Matrix_Query = DenseMatrix.Factory.importFromArray(Index_Query);
                        double result = Search(Matrix_Index, Matrix_Query, i * 2);
                        int temp = (int) result;
                        if ((result - temp) > 0.5) {
                            temp++;
                        }
                        if (temp == (int) threshold[i] && temp == (int) thresholdQuery) {
                            System.out.println("当前点索引匹配成功" + i + "***************" + result);
                            congestion[i] = congestionInfo.getIndj();
                            break;
                        }
                    }
                }
                return congestion;
            } else {
                return new short[count / 2];
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new short[count / 2];
        }

    }
}
