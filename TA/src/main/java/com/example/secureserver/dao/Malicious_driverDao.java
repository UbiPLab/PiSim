package com.example.secureserver.dao;

import com.example.secureserver.bean.Malicious_driver;

import java.util.List;

public interface Malicious_driverDao {
    List<Malicious_driver> findAllMalicious_driver();
    boolean insert(Malicious_driver malicious_driver);
    int Malicious_driver_count();
}
