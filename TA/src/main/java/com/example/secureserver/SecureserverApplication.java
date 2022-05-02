package com.example.secureserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import static com.example.secureserver.encryption.Initialize.getParameterFromFile;

@SpringBootApplication
public class SecureserverApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
//            String pairingPath = "./config/pairing.properties";
//        String paramPath = "./config/params.properties";
        String pairingPath = args[0];
        String paramPath = args[1];
        //参数初始化
        getParameterFromFile(pairingPath, paramPath);
        SpringApplication.run(SecureserverApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SecureserverApplication.class);
    }
}

