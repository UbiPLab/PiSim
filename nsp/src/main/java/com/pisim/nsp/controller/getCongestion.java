package com.pisim.nsp.controller;

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
import org.ujmp.core.util.Base64;
import com.pisim.nsp.parameterUtil.parameter;

import java.util.ArrayList;
import java.util.List;

import static com.pisim.nsp.parameterUtil.parameter.*;


@RestController
@RequestMapping("/sendCongestion")
public class getCongestion {
    @Autowired
    CongestionInfoService congestionInfoService;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject returnCongestionInfo(@RequestBody JSONObject jsonObject) {
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
                List<CongestionInfo> congestionInfos = new ArrayList<>();
                //获取拥堵信息
                JSONArray jsonArray_temp = data.getJSONArray("congestions");
                CongestionInfo congestionInfo_temp = new CongestionInfo();
                for (int i = 0; i < jsonArray_temp.size(); i++) {
                    congestionInfo_temp.setIndj(jsonArray_temp.getJSONObject(i).getShort("indj"));
                    congestionInfo_temp.setQueryindex(jsonArray_temp.getJSONObject(i).getJSONArray("queryindex").toString());
                    congestionInfo_temp.setTimestamp(jsonArray_temp.getJSONObject(i).getTimestamp("timestamp"));
                    congestionInfo_temp.setThresholdQuery(jsonArray_temp.getJSONObject(i).getDouble("thresholdQuery"));
                    congestionInfos.add(congestionInfo_temp);
                }
                //拥堵信息入库
                congestionInfoService.insertCongestionInfo(congestionInfos);
                NSPtrafficInfoCount = NSPtrafficInfoCount + congestionInfos.size();
                NSPtrafficInfoCount_temp = NSPtrafficInfoCount_temp + congestionInfos.size();
            }
            JSONObject result = new JSONObject();
            result.put("result", "success");
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
