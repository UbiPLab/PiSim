package com.pisim.rsu.utils;


import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.pisim.rsu.parameterUtil.parameter;


/**
 * 测试http的get、post的线程
 *
 * @author xiaoyf
 */
public class HttpThread extends Thread {
    private final static int CONNECT_TIMEOUT = 100000*6;
    private final static int READ_TIMEOUT = 100000*6;
    private final static int WRITE_TIMEOUT = 100000*6;
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
            .build();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String url;
    private String param;
    private boolean tag;//true post方式；false get方式

    public HttpThread(String url, String param, boolean tag) {
        super();
        this.url = url;
        this.param = param;
        this.tag = tag;
    }

    public HttpThread(String url, boolean tag) {
        super();
        this.url = url;
        this.tag = tag;
    }

    private void doGet() {
        try {
            Request request = new Request.Builder().url(url).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                parameter.result = Objects.requireNonNull(response.body()).string();
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doPost(String json) {
        try {
            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                parameter.result = Objects.requireNonNull(response.body()).string();
            } else {
                System.out.println("连接失败"+"连接超时了");
                throw new IOException("Unexpected code " + response);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        if (tag) {
            doPost(param);
        } else {
            doGet();
        }
    }

}