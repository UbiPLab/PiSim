package com.pisim.nsp.controller;

import com.alibaba.fastjson.JSONObject;
import com.pisim.nsp.service.CongestionInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.pisim.nsp.parameterUtil.parameter.*;


@RestController
@RequestMapping("/NSPInfo")
public class adminController {
    @Autowired
    CongestionInfoService congestionInfoService;
    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject returnCongestionInfo() {
        JSONObject result = new JSONObject();
        result.put("NSPtrafficInfoCount", NSPtrafficInfoCount);
        result.put("NSPNaviCount", NSPNaviCount);
        result.put("NSPtrafficInfoCount_Last", NSPtrafficInfoCount_Last);
        result.put("NSPNaviCount_Last", NSPNaviCount_Last);
        result.put("NSPNaviPointCount_temp",NSPNaviPointCount_temp);
        result.put("NSPReceiveCongest",congestionInfoService.getCongestionInfoList());
        return result;
    }
}
