package com.example.secureserver.dao;

import com.example.secureserver.bean.User;

public interface UserDao {
    boolean insert(User user);
    boolean findUser(User user);
    boolean findUser(String username);

}
