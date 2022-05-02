package com.example.secureserver.utils;

import com.example.secureserver.parameterUtil.parameter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtils {
    private String key = parameter.jwtKey; //签名私钥
    private long failureTime = 1000*60*1000;//失效时间

    public String createJwt(String id, String subject, Map<String, Object> map) {
        long exp = System.currentTimeMillis() + failureTime;
        JwtBuilder jwtBuilder = Jwts.builder().setId(id).setSubject(subject).
                setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS256, key);

        //根据map设置claims
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            jwtBuilder.claim(entry.getKey(), entry.getValue());
        }
        jwtBuilder.setExpiration(new Date(exp));
        //创建token
        return jwtBuilder.compact();
    }

    public  Claims parseJwt(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }
}
