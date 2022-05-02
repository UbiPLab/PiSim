package com.example.secureserver.service;

import com.example.secureserver.bean.Status;
import com.example.secureserver.bean.Driver;

import java.util.List;

public interface DriverService {
    List<Driver> findAllDriver();
    int Drivercount();
    boolean insert(Driver driver);

    Status updatePassword(Driver driver);

    Status updateclsignaturetime(Driver driver);

    Status delete(Driver driver);

    Driver findDriver(Driver driver);
    boolean findDriverByIdNumber(String IdNumber,String IdCar);
}
