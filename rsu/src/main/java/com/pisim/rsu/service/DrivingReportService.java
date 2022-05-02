package com.pisim.rsu.service;

import com.alibaba.fastjson.JSONObject;
import com.pisim.rsu.bean.DrivingReport;

import java.sql.Timestamp;
import java.util.List;

public interface DrivingReportService {
    List<DrivingReport> getDrivingReportList(boolean flag);
    boolean insertDrivingReport(JSONObject drivingReportJsonString, String pid, Timestamp timestamp);
    boolean deleteDrivingReport(String pid,boolean flag);
    //过滤虚假的驾驶报告
    boolean filterFalseDrivingReport();

    //从交通拥堵数据库中删除过期的驾驶报告，validTime为过期时间，单位为分钟，可以根据需要进行调整
    boolean deleteOverdueDrivingReport(double validTime,boolean flag);
}
