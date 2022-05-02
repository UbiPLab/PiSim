package com.example.secureserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.secureserver.bean.User;
import com.example.secureserver.service.DriverService;
import com.example.secureserver.service.Malicious_driverService;
import com.example.secureserver.service.UserService;
import com.example.secureserver.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.example.secureserver.parameterUtil.IP.nspIp;
import static com.example.secureserver.parameterUtil.IP.rsuIp;
import static com.example.secureserver.utils.HttpUtils.SendHttpRequest;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:8080","http://39.97.107.100","http://39.101.207.104","http://www.zkwanp.com"})
public class adminlogin {
    @Autowired
    private UserService userService;
    @Autowired
    private DriverService driverService;
    @Autowired
    private Malicious_driverService malicious_driverService;

    private JwtUtils jwtUtils = new JwtUtils();

    //登录接口
    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject adminLogin(@RequestBody JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        User user = new User();
        user.setUsername(jsonObject.getString("username"));
        user.setPassword(jsonObject.getString("password"));
        //判断用户是否存在
        if (userService.findUser(user)) {
            //存在 生成token 返回登录成功
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("username", user.getUsername());
            String token = jwtUtils.createJwt(user.getUsername(), user.getUsername(), dataMap);
            result.put("result", true);
            result.put("token", token);
            return result;
        } else {
            //不存在 返回登录失败
            result.put("result", false);
            result.put("token", "error");
            return result;
        }
    }

    //获取用户数量接口
    @RequestMapping(path = "/TAuserCount", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject TAuserCount(@RequestBody JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        if (checkToken(jsonObject.getString("token"))) {
            result.put("result", true);
            result.put("userCount", driverService.Drivercount());
            result.put("maliciousUserCount", malicious_driverService.Malicious_driver_count());
        } else {
            result.put("result", false);
        }
        return result;
    }

    //获取恶意用户身份信息
    @RequestMapping(path = "/TAMaliciousUserInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject TAMaliciousUserInfo(@RequestBody JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        if (checkToken(jsonObject.getString("token"))) {
            result.put("result", true);
            result.put("maliciousUserInfo", malicious_driverService.findAllMalicious_driver());
        } else {
            result.put("result", false);
        }
        return result;
    }

    //获取恶意用户身份信息
    @RequestMapping(path = "/TAUserInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject TADriverInfo(@RequestBody JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        if (checkToken(jsonObject.getString("token"))) {
            result.put("result", true);
            result.put("UserInfo", driverService.findAllDriver());
        } else {
            result.put("result", false);
        }
        return result;
    }

    //获取RSU信息接口
    @RequestMapping(path = "/RSUInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject RSUInfo(@RequestBody JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        if (checkToken(jsonObject.getString("token"))) {
            String url = rsuIp + "RSUInfo";
            result = SendHttpRequest(url, jsonObject);
            result.put("result", true);
        } else {
            result.put("result", false);
        }
        return result;
    }

    //获取NSP信息接口
    @RequestMapping(path = "/NSPInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject NSPInfo(@RequestBody JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        if (checkToken(jsonObject.getString("token"))) {
            String url = nspIp + "NSPInfo";
            result = SendHttpRequest(url, jsonObject);
            result.put("result", true);
        } else {
            result.put("result", false);
        }
        return result;
    }

    private boolean checkToken(String token) {
        try {
            Claims claims = jwtUtils.parseJwt(token);
            return userService.findUser(claims.getId());
        }catch (Exception e){
            return false;
        }
    }
}
