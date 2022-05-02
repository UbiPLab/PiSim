package com.example.secureserver.service.Impl;

import com.example.secureserver.bean.User;
import com.example.secureserver.dao.UserDao;
import com.example.secureserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public boolean insert(User user) {
        return userDao.insert(user);
    }

    @Override
    public boolean findUser(User user) {
        return userDao.findUser(user);
    }

    @Override
    public boolean findUser(String username) {
        return userDao.findUser(username);
    }
}
