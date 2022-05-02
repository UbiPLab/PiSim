package com.example.secureserver.dao.impl;

import com.example.secureserver.bean.RsuNsp;
import com.example.secureserver.dao.RsuNspDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class RsuNspDaoImpl implements RsuNspDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    @Transactional
    public boolean insert(String PubKey,String Unique_id) {
        String sql = "insert into rsu_nsp(pubkey,unique_id) values(?,?)";
        try{
            jdbcTemplate.update(sql,PubKey,Unique_id);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean getRsuNspKey(String PubKey) {
        String sql = "select * from rsu_nsp where pubkey=?";
        RowMapper<RsuNsp> rowMapper = new BeanPropertyRowMapper<RsuNsp>(RsuNsp.class);
        try {
            //  jdbcTemplate.queryForObject(sql, new Object[]{driver.getUsername(),driver.getPassword()}, new BeanPropertyRowMapper(Driver.class));// 使用JdbcTemplate访问数据库
            List<RsuNsp> RSU_NSPs = jdbcTemplate.query(sql, new Object[]{PubKey}, rowMapper);
            if (RSU_NSPs.size() > 0) {
                return true;
            }
        } catch (DataAccessException e) {
            return false;
        }
        return false;
    }
}
