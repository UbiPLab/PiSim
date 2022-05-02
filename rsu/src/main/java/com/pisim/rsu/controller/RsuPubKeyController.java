package com.pisim.rsu.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.pisim.rsu.parameterUtil.parameter;


@RestController
@RequestMapping("/getRsuPubKey")
public class RsuPubKeyController {
    @RequestMapping(method = RequestMethod.GET)
    public JSONObject getRsuPubKey(){
        JSONObject data = new JSONObject();
        data.put("RSU_rsa_pub", parameter.RSU_rsa_pub);
        System.out.println("有人向我请求公钥了");
        return data;
    }
}
