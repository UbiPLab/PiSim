package com.pisim.rsu.dao.impl;

import com.pisim.rsu.bean.NaviQuery;
import com.pisim.rsu.dao.NaviQueryInfoDao;
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
public class NaviQueryInfoDaoImpl implements NaviQueryInfoDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<NaviQuery> getNaviQueryInfoList() {
        String sql = "select * from navi_query_history";
        RowMapper<NaviQuery> rowMapper = new BeanPropertyRowMapper<NaviQuery>(NaviQuery.class);
        try {
            List<NaviQuery> naviQueryList = jdbcTemplate.query(sql, rowMapper);
            if (naviQueryList.size() > 0) {
                return naviQueryList;
            } else {
                return null;
            }
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean insertNaviQueryInfo(NaviQuery naviQuery) {
        String sql = "insert into navi_query_history(REi1,REi2,Index_EncKiI,grlpi,rlpi,daierta,M,a1,count,timestamp) values(?,?,?,?,?,?,?,?,?,?)";
        try {
            if (naviQuery.getIndex_EncKiI().length() >= 5120) {
                jdbcTemplate.update(sql,
                        naviQuery.getREi1(),
                        naviQuery.getREi2(),
                        naviQuery.getIndex_EncKiI().substring(0, 5120),
                        naviQuery.getGrlpi(),
                        naviQuery.getRlpi(),
                        naviQuery.getDaierta(),
                        naviQuery.getM(),
                        naviQuery.getA1(),
                        naviQuery.getCount(),
                        naviQuery.getTimestamp());
                return true;
            } else {
                jdbcTemplate.update(sql,
                        naviQuery.getREi1(),
                        naviQuery.getREi2(),
                        naviQuery.getIndex_EncKiI(),
                        naviQuery.getGrlpi(),
                        naviQuery.getRlpi(),
                        naviQuery.getDaierta(),
                        naviQuery.getM(),
                        naviQuery.getA1(),
                        naviQuery.getCount(),
                        naviQuery.getTimestamp());
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteNaviQueryInfo(double validTime) {
        String sql = "select id,timestamp from navi_query_history";
        RowMapper<NaviQuery> rowMapper = new BeanPropertyRowMapper<NaviQuery>(NaviQuery.class);
        try {
            List<NaviQuery> naviQueryList = jdbcTemplate.query(sql, rowMapper);
            if (!naviQueryList.isEmpty()) {
                Timestamp currentTimestamp = new Timestamp(new Date().getTime());
                for (NaviQuery naviQuery : naviQueryList) {
                    long timeDiff = currentTimestamp.getTime() - naviQuery.getTimestamp().getTime();    //时间戳的差值，单位为毫秒
                    if ((double) timeDiff > validTime) {
                        deleteNaviQueryInfo(naviQuery.getId());
                    }
                }
                System.out.println("删除过期导航查询记录-----删除过期导航查询记录成功！");
                return true;
            } else {
                System.out.println("删除过期导航查询记录-----数据库中无导航查询记录！不需要删除");
                return false;
            }
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Override
    public boolean deleteNaviQueryInfo(int id) {
        String sql = "delete from navi_query_history where id = ?";
        try {
            jdbcTemplate.update(sql, id);// 使用JdbcTemplate访问数据库
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }
}

