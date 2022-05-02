package com.example.secureserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.secureserver.service.DriverService;
import com.example.secureserver.service.RsuNspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/RsuNspRegister")
public class RsuNspRegisterController {
    @Autowired
    private RsuNspService rsuNspService;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject register(@RequestBody JSONObject jsonObject)  {
        try {
            String PubKey = (String) jsonObject.get("PubKey");
            String Unique_id = (String) jsonObject.get("address");
            rsuNspService.insert(PubKey,Unique_id);
            jsonObject.put("result", "good");
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("result", "error");
            return jsonObject;
        }
    }
}
