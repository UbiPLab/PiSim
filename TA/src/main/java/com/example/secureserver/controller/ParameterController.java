package com.example.secureserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.secureserver.encryption.Hash;
import com.example.secureserver.parameterUtil.parameter;
import org.ujmp.core.util.Base64;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.secureserver.parameterUtil.parameter.*;

@RestController
@RequestMapping("/getparameter")
public class ParameterController {
    @RequestMapping
    public JSONObject getTAParameter() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("gpk_g", Base64.encodeBytes(parameter.gpk_g.toBytes()));
            jsonObject.put("gpk_A", Base64.encodeBytes(parameter.gpk_A.toBytes()));
            jsonObject.put("gpk_B", Base64.encodeBytes(parameter.gpk_B.toBytes()));
            jsonObject.put("gpk_g2", Base64.encodeBytes(parameter.gpk_g2.toBytes()));
            jsonObject.put("rsa_pub",parameter.rsa_pub);
            jsonObject.put("ZKPK_rou", ZKPK_rou);
            jsonObject.put("ZKPK_F", ZKPK_F);
            jsonObject.put("ZKPK_g", ZKPK_g);
            jsonObject.put("ZKPK_b", ZKPK_b);
            jsonObject.put("MC", parameter.MC);
//            jsonObject.put("PK_d1", clSign_PK_d1);
//            jsonObject.put("PK_d2", clSign_PK_d2);
//            jsonObject.put("PK_d3", clSign_PK_d3);
//            jsonObject.put("PK_n", clSign_PK_n);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}