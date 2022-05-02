package com.example.secureserver.service;

import com.example.secureserver.bean.User;

public interface UserService {
    boolean insert(User user);
    boolean findUser(User user);
    boolean findUser(String username);
}
