package com.pisim.nsp.dao.impl;

import com.pisim.nsp.bean.CongestionInfo;
import com.pisim.nsp.dao.CongestionInfoDao;
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

    @Override
    @Transactional
    public List<CongestionInfo> getCongestionInfoList() {
        String sql = "select id,queryindex,indj,`timestamp`,thresholdQuery from center_congestion";
        RowMapper<CongestionInfo> rowMapper = new BeanPropertyRowMapper<CongestionInfo>(CongestionInfo.class);
        try {
            List<CongestionInfo> congestionInfos = jdbcTemplate.query(sql, rowMapper);
            if (congestionInfos.size() > 0) {
                return congestionInfos;
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
        String sql = "insert into center_congestion(queryindex,indj,`timestamp`,thresholdQuery) values(?,?,?,?)";
        try {

            jdbcTemplate.update(sql, congestionInfo.getQueryindex(), congestionInfo.getIndj(), congestionInfo.getTimestamp(),congestionInfo.getThresholdQuery());
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
        String sql = "insert into center_congestion(queryindex,indj,`timestamp`,thresholdQuery) values(?,?,?,?)";
        try {
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
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteCongestionInfo(CongestionInfo congestionInfo) {
        String sql = "delete from center_congestion where `timestamp` = ?";
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
        String sql = "select queryindex,indj,`timestamp`,thresholdQuery from center_congestion";
        RowMapper<CongestionInfo> rowMapper = new BeanPropertyRowMapper<CongestionInfo>(CongestionInfo.class);
        try {
            List<CongestionInfo> congestionInfoList = jdbcTemplate.query(sql, rowMapper);
            if (!congestionInfoList.isEmpty()) {
                Timestamp currentTimestamp = new Timestamp(new Date().getTime());
                for (CongestionInfo congestionInfo : congestionInfoList) {
                    long timeDiff = currentTimestamp.getTime() - congestionInfo.getTimestamp().getTime();   //时间戳的差值，单位为毫秒
                    if ((double) timeDiff / (1000 * 60) > validTime) {
                        deleteCongestionInfo(congestionInfo);
                    }
                    //System.out.println((double)timeDiff/(1000*60));
                }
                System.out.println("删除过期的交通拥堵信息成功！");
                return true;
            } else {
                System.out.println("数据库中无可用的交通拥堵信息！");
                return false;
            }
        } catch (DataAccessException e) {
            return false;
        }
    }

}
