package com.pisim.nsp.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.pisim.nsp.parameterUtil.parameter;


@RestController
@RequestMapping("/getNspPubKey")
public class NspPubKeyController {
    @RequestMapping(method = RequestMethod.GET)
    public JSONObject getRsuPubKey() throws Exception {
        JSONObject data = new JSONObject();
        data.put("NSP_rsa_pub", parameter.NSP_rsa_pub);
        System.out.println("有人向我请求公钥了");
        return data;
    }
}
