package com.example.mygaode.Thread;

import com.example.mygaode.Object.MyHandler;

import Navi_utils.LocationBean;

public class RelyThread extends Thread {
    private double[][] pointList; //点序列 每行一个点 第一列为经度，第二列维度
    private short interval;
    private String result;
    private MyHandler handler;
    private boolean tag = false;
    LocationBean locationBean;


    //发起请求
    public RelyThread(MyHandler handler, double[][] pointList, short interval) {
        this.pointList = pointList;
        this.interval = interval;
        this.handler = handler;
        this.tag = true;
    }

    //提交报告
    public RelyThread(LocationBean locationBean) {
        this.locationBean = locationBean;
        this.tag = false;
    }

    //提交报告
    public RelyThread(MyHandler handler, LocationBean locationBean) {
        this.locationBean = locationBean;
        this.handler = handler;
        this.tag = false;
    }

    @Override
    public void run() {
        if (tag) {
            queryCongestionThread queryCongestionThread = new queryCongestionThread(handler, pointList, (short) 10);
            queryCongestionThread.start();
        } else {
            submitReportThread submitReportThread = new submitReportThread(handler,locationBean);
            submitReportThread.start();
        }
    }
}
