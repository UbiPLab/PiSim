package com.example.secureserver.dao.impl;

import com.example.secureserver.bean.Driver;
import com.example.secureserver.bean.User;
import com.example.secureserver.dao.UserDao;
import com.example.secureserver.encryption.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public boolean insert(User user) {
        String sql = "insert into users(username,password) values (?,?)";
        try {
            jdbcTemplate.update(sql,
                    user.getUsername(),
                    Hash.SHA_256(user.getPassword()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean findUser(User user) {
        String sql = "select * from users where username=? and password=?";
        try {
            RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
            List<User> users = jdbcTemplate.query(sql,
                    new Object[]{user.getUsername(), Hash.SHA_256(user.getPassword()),
                    }, rowMapper);
            return users.size() == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean findUser(String username) {
        String sql = "select * from users where username=?";
        try {
            RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
            List<User> users = jdbcTemplate.query(sql,
                    new Object[]{username
                    }, rowMapper);
            return users.size() == 1;
        } catch (Exception e) {
            return false;
        }
    }
}
