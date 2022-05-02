package com.pisim.rsu.dao;

import com.alibaba.fastjson.JSONObject;
import com.pisim.rsu.bean.DrivingReport;

import java.sql.Timestamp;
import java.util.List;

public interface DrivingReportDao {
    //取出所有的驾驶报告
    List<DrivingReport> getDrivingReportList(boolean flag);
    //取出部分驾驶报告（idreport在id1-id2范围内，包含边界）
    boolean insertDrivingReport(JSONObject drivingReportJsonString, String pid, Timestamp timestamp);
    boolean deleteDrivingReport(String pid,boolean flag);
    boolean deleteDrivingReport(DrivingReport drivingReport);

    //从交通拥堵数据库中删除过期的驾驶报告，validTime为过期时间，单位为分钟，可以根据需要进行调整
    boolean deleteOverdueDrivingReport(double validTime,boolean flag);
}
