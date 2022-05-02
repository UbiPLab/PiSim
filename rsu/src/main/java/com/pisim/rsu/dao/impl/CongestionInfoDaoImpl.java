package com.pisim.rsu.dao.impl;

import com.pisim.rsu.bean.CongestionInfo;
import com.pisim.rsu.dao.CongestionInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class CongestionInfoDaoImpl implements CongestionInfoDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 从拥堵信息数据库中查找id为i1到i2的所有报告,一次全查性能浪费太多
     */
    @Override
    @Transactional
    public List<CongestionInfo> getCongestionInfoList() {
        String sql = "select queryindex,indj,`timestamp`,thresholdQuery from congestion";
        RowMapper<CongestionInfo> rowMapper = new BeanPropertyRowMapper<CongestionInfo>(CongestionInfo.class);
        try {
            List<CongestionInfo> congestionInfoList = jdbcTemplate.query(sql, rowMapper);
            if (congestionInfoList.size() > 0) {
                return congestionInfoList;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            return null;
        }
    }

    /**
     * 向拥堵信息数据库中插入拥堵信息
     */
    @Transactional
    @Override
    public boolean insertCongestionInfo(CongestionInfo congestionInfo) {
        String sql = "insert into congestion(queryindex,indj, `timestamp`,thresholdQuery) values(?,?,?,?)";
        try {
            jdbcTemplate.update(sql, congestionInfo.getQueryindex(), congestionInfo.getIndj(), congestionInfo.getTimestamp(), congestionInfo.getThresholdQuery());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 向拥堵信息数据库中批量插入拥堵信息
     */
    @Transactional
    @Override
    public boolean insertCongestionInfo(List<CongestionInfo> congestionInfos) {
        String sql = "insert into congestion(queryindex,indj,`timestamp`,thresholdQuery) values(?,?,?,?)";
        try {
            //向交通拥堵数据库中存储路况信息
            List<Object[]> batchArgs = new ArrayList<>();
            for (int i =0;i<congestionInfos.size();i++){
                batchArgs.add(new Object[]{
                        congestionInfos.get(i).getQueryindex(),
                        congestionInfos.get(i).getIndj(),
                        congestionInfos.get(i).getTimestamp(),
                        congestionInfos.get(i).getThresholdQuery()
                });
            }
            jdbcTemplate.batchUpdate(sql,batchArgs);

//            //向交通拥堵
//            sql = "insert into congestion_tonsp(queryindex,indj,`timestamp`,thresholdQuery) values(?,?,?,?)";
//            batchArgs = new ArrayList<>();
//            for (int i =0;i<congestionInfos.size();i++){
//                batchArgs.add(new Object[]{
//                        congestionInfos.get(i).getQueryindex(),
//                        congestionInfos.get(i).getIndj(),
//                        congestionInfos.get(i).getTimestamp(),
//                        congestionInfos.get(i).getThresholdQuery()
//                });
//            }
            jdbcTemplate.batchUpdate(sql,batchArgs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteCongestionInfo(CongestionInfo congestionInfo) {
        String sql = "delete from congestion where `timestamp` = ?";
        try {
            jdbcTemplate.update(sql, congestionInfo.getTimestamp());
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    //从交通拥堵数据库中删除过期的交通拥堵信息，validTime为过期时间，单位为分钟，可以根据需要进行调整
    @Override
    public boolean deleteOverdueCongestionInfo(double validTime) {
        String sql = "select queryindex,indj,`timestamp` from congestion";
        RowMapper<CongestionInfo> rowMapper = new BeanPropertyRowMapper<CongestionInfo>(CongestionInfo.class);
        try {
            List<CongestionInfo> congestionInfoList = jdbcTemplate.query(sql, rowMapper);
            if (!congestionInfoList.isEmpty()) {
                Timestamp currentTimestamp = new Timestamp(new Date().getTime());
                for (CongestionInfo congestionInfo : congestionInfoList) {
                    long timeDiff = currentTimestamp.getTime() - congestionInfo.getTimestamp().getTime();   //时间戳的差值，单位为毫秒
                    if ((double) timeDiff  > validTime) {
                        deleteCongestionInfo(congestionInfo);
                    }
                }
                System.out.println("删除过期路况信息-----删除过期的路况信息成功！");
                return true;
            } else {
                System.out.println("删除过期路况信息-----数据库中无路况信息！不需要删除");
                return false;
            }
        } catch (DataAccessException e) {
            return false;
        }
    }
}
