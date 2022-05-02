package com.pisim.rsu.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.pisim.rsu.bean.CongestionInfo;
import com.pisim.rsu.bean.DrivingReport;
import com.pisim.rsu.dao.DrivingReportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Repository
public class DrivingReportDaoImpl implements DrivingReportDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<DrivingReport> getDrivingReportList(boolean flag) {
        String sql = "select report_string, pidj from report";
        if (!flag) {
            sql = "select id,report_string,pidj,timestamp from report_history";
        }
        RowMapper<DrivingReport> rowMapper = new BeanPropertyRowMapper<DrivingReport>(DrivingReport.class);
        try {
            List<DrivingReport> drivingReportList = jdbcTemplate.query(sql, rowMapper);
            if (drivingReportList.size() > 0) {
                return drivingReportList;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public boolean insertDrivingReport(JSONObject drivingReportJsonString, String pidj, Timestamp timestamp) {
        String sql = "insert into report (report_string,pidj,`timestamp`) values(?,?,?)";
        try {
            jdbcTemplate.update(sql, drivingReportJsonString.toJSONString(), pidj, timestamp);
            System.out.println("存储路况报告");
            sql = "insert into report_history (report_string,pidj,`timestamp`) values(?,?,?)";
            jdbcTemplate.update(sql, drivingReportJsonString.toJSONString(), pidj, timestamp);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteDrivingReport(String pid,boolean flag) {
        String sql = "delete from report where pidj = ?";
        if (!flag){
            sql = "delete from report_history where pidj = ?";
        }
        try {
            jdbcTemplate.update(sql, pid);// 使用JdbcTemplate访问数据库
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Override
    public boolean deleteDrivingReport(DrivingReport drivingReport) {
        String sql = "delete from report where timestamp = ?";
        try {
            jdbcTemplate.update(sql, drivingReport.getTimestamp());
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Override
    public boolean deleteOverdueDrivingReport(double validTime,boolean flag) {
        String sql = "select report_string,pidj,timestamp from report";
        if (!flag){
            sql = "select report_string,pidj,timestamp from report_history";
        }
        RowMapper<DrivingReport> rowMapper = new BeanPropertyRowMapper<DrivingReport>(DrivingReport.class);
        try {
            List<DrivingReport> drivingReportList = jdbcTemplate.query(sql, rowMapper);
            if (!drivingReportList.isEmpty()) {
                Timestamp currentTimestamp = new Timestamp(new Date().getTime());
                for (DrivingReport drivingReport : drivingReportList) {
                    long timeDiff = currentTimestamp.getTime() - drivingReport.getTimestamp().getTime();    //时间戳的差值，单位为毫秒
                    if ((double) timeDiff > validTime) {
                        deleteDrivingReport(drivingReport.getPidj(),flag);
                    }
                }
                System.out.println("删除过期路况报告-----删除过期的路况报告成功！");
                return true;
            } else {
                System.out.println("删除过期路况报告-----数据库中无路况报告！不需要删除");
                return false;
            }
        } catch (DataAccessException e) {
            return false;
        }
    }
}

