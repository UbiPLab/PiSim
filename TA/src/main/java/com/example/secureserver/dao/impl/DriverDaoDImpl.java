package com.example.secureserver.dao.impl;

import com.example.secureserver.bean.Status;
import com.example.secureserver.bean.Driver;
import com.example.secureserver.dao.DriverDao;
import com.example.secureserver.encryption.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCountCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.sql.SQLException;
import java.util.List;

@Repository
public class DriverDaoDImpl implements DriverDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private String statu = "";

    @Override
    @Transactional
    public List<Driver> findAllDriver() {
        try {
            String sql = "select * from driver";
            RowMapper<Driver> rowMapper = new BeanPropertyRowMapper<Driver>(Driver.class);
            return jdbcTemplate.query(sql, rowMapper);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int DriverCount() {
        RowCountCallbackHandler rcch = new RowCountCallbackHandler();
        String sql = "select * from driver";
        jdbcTemplate.query(sql, rcch);
        return rcch.getRowCount();
    }


    @Override
    @Transactional
    public boolean insert(Driver driver) {
        String sql = "insert into driver(username,password,rsa_pubKey,idNumber,idCar,xigema,ni,ni2,Ei1,Ei2) values(?,?,?,?,?,?,?,?,?,?)";
        try {
            jdbcTemplate.update(sql,
                    driver.getUsername(),
                    DigestUtils.md5DigestAsHex(driver.getPassword().getBytes()),
                    driver.getRsa_pubKey(),
                    driver.getIdNumber(),
                    driver.getIdCar(),
                    driver.getXigema(),
                    driver.getNi(),
                    driver.getNi2(),
                    driver.getEi1(),
                    driver.getEi2());
            System.out.println(driver.getIdCar());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public Status updatePassword(Driver driver) {
        String sql = "update driver set password=? where username=?";
        try {
            jdbcTemplate.update(sql, driver.getPassword(), driver.getUsername());// 使用JdbcTemplate访问数据库
        } catch (DataAccessException e) {
            SQLException exception = (SQLException) e.getCause();
            statu = exception.toString();// 通过exception获取ErrorCode、Message等信息
        }
        Status status = new Status();
        status.setStatus(statu);
        return status;
    }

    @Override
    @Transactional
    public Status updateclsignaturetime(Driver driver) {
        String sql = "update driver set clsignaturetime=? where username=? and password = ?";
        try {
            jdbcTemplate.update(sql, driver.getClsignaturetime(), driver.getUsername(), driver.getPassword());// 使用JdbcTemplate访问数据库
        } catch (DataAccessException e) {
            SQLException exception = (SQLException) e.getCause();
            statu = exception.toString();// 通过exception获取ErrorCode、Message等信息
        }
        Status status = new Status();
        status.setStatus(statu);
        return status;
    }

    @Override
    @Transactional
    public Status delete(Driver driver) {
        String sql = "delete from report set password=? where username=?";
        try {
            jdbcTemplate.update(sql, driver.getPassword(), driver.getUsername());// 使用JdbcTemplate访问数据库
        } catch (DataAccessException e) {
            SQLException exception = (SQLException) e.getCause();
            statu = exception.toString();// 通过exception获取ErrorCode、Message等信息
        }
        Status status = new Status();
        status.setStatus(statu);
        return status;
    }

    @Override
    @Transactional
    public Driver findDriver(Driver driver) {
        String sql = "select * from driver where username=? and password=?";
        RowMapper<Driver> rowMapper = new BeanPropertyRowMapper<Driver>(Driver.class);
        try {
            //  jdbcTemplate.queryForObject(sql, new Object[]{driver.getUsername(),driver.getPassword()}, new BeanPropertyRowMapper(Driver.class));// 使用JdbcTemplate访问数据库
            List<Driver> drivers = jdbcTemplate.query(sql, new Object[]{driver.getUsername(), DigestUtils.md5DigestAsHex(driver.getPassword().getBytes()),
            }, rowMapper);
            if (drivers.size() == 1) {
                return drivers.get(0);
            }
        } catch (DataAccessException e) {
            statu = e.getMessage();
        }
        Status status = new Status();
        status.setStatus(statu);
        return null;
    }

    @Override
    @Transactional
    public boolean findDriverByIdNumber(String IdNumber, String IdCar) {
        String sql = "select * from driver where idNumber=? or idCar=?";
        RowMapper<Driver> rowMapper = new BeanPropertyRowMapper<Driver>(Driver.class);
        try {
            //  jdbcTemplate.queryForObject(sql, new Object[]{driver.getUsername(),driver.getPassword()}, new BeanPropertyRowMapper(Driver.class));// 使用JdbcTemplate访问数据库
            List<Driver> drivers = jdbcTemplate.query(sql, new Object[]{IdNumber, IdCar}, rowMapper);
            if (drivers.size() != 0) {
                return true;
            }
        } catch (DataAccessException e) {
            return false;
        }
        return false;
    }
}
