package com.example.secureserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.secureserver.bean.Malicious_driver;
import com.example.secureserver.service.Malicious_driverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/getMaliciousDriver")
public class getTraceDriver {
    @Autowired
    private Malicious_driverService malicious_driverService;

    @RequestMapping
    public JSONObject trace() {
        List<Malicious_driver> malicious_drivers = malicious_driverService.findAllMalicious_driver();
        StringBuilder idCar = new StringBuilder();
        StringBuilder idNumber = new StringBuilder();
        JSONObject jsonObject = new JSONObject();
        StringBuilder tempIdCar;
        StringBuilder tempIdNumber;
        for (Malicious_driver maDriver : malicious_drivers) {
            if (maDriver.getIdCar().length() > 6) {
                tempIdCar = new StringBuilder(maDriver.getIdCar());
                idCar.append("~").append(tempIdCar.replace(3, 7, "****").toString());
            } else {
                idCar.append("~").append(maDriver.getIdCar());
            }

            if (maDriver.getIdNumber().length() > 17) {
                tempIdNumber = new StringBuilder(maDriver.getIdNumber());
                idNumber.append("~").append(tempIdNumber.replace(6, 18, "************").toString());
            } else {
                idNumber.append("~").append(maDriver.getIdNumber());
            }
        }
        jsonObject.put("idCar", idCar.toString());
        jsonObject.put("idNumber", idNumber.toString());

        return jsonObject;
    }
}
