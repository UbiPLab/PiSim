package com.example.secureserver.dao.impl;

import com.example.secureserver.bean.Malicious_driver;
import com.example.secureserver.dao.Malicious_driverDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Repository
public class Malicious_driverDaoImpl implements Malicious_driverDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<Malicious_driver> findAllMalicious_driver() {
        String sql = "select * from maliciousdriver";
        RowMapper<Malicious_driver> rowMapper = new BeanPropertyRowMapper<Malicious_driver>(Malicious_driver.class);
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    @Transactional
    public int Malicious_driver_count() {
        RowCountCallbackHandler rcch = new RowCountCallbackHandler();
        String sql = "select * from maliciousdriver";
        jdbcTemplate.query(sql, rcch);
        return rcch.getRowCount();
    }

    @Override
    @Transactional
    public boolean insert(Malicious_driver malicious_driver) {
//        String sql = "insert into maliciousdriver(username,idcar,idnumber,ei1,ei2,`type`) values(?,?,?,?,?,?)";
        try {
//            jdbcTemplate.update(sql, malicious_driver.getUsername(),
//                    malicious_driver.getIdCar(),
//                    malicious_driver.getIdNumber(),
//                    malicious_driver.getEi1(),
//                    malicious_driver.getEi2(),
//                    malicious_driver.getType());
//            return true;
            String procedure = "{call insert_event(?,?,?,?,?,?)}";
            jdbcTemplate.execute(procedure, new CallableStatementCallback() {
                @Override
                public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                    cs.setString(1, malicious_driver.getUsername());
                    cs.setString(2, malicious_driver.getIdCar());
                    cs.setString(3, malicious_driver.getIdNumber());
                    cs.setString(4, malicious_driver.getEi1());
                    cs.setString(5, malicious_driver.getEi2());
                    cs.setInt(6, malicious_driver.getType());
                    cs.execute();
                    return cs;
                }
            });
            return true;
        } catch (
                Exception e) {
            return false;
        }
    }
}
