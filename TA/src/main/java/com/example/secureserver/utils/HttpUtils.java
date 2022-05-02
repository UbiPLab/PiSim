package com.example.secureserver.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class HttpUtils {
    public static JSONObject SendHttpRequest(String url, JSONObject jsonObject) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> apiResponse = restTemplate.postForEntity(
                url, jsonObject, String.class
        );
        JSONObject result = new JSONObject();
        if (apiResponse.getStatusCodeValue() == 200) {
            result = JSON.parseObject(apiResponse.getBody());
            return result;
        }else {
            result.put("result","error");
            return result;
        }

    }
}
