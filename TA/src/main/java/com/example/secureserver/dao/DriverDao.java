package com.example.secureserver.dao;

import com.example.secureserver.bean.Status;
import com.example.secureserver.bean.Driver;

import java.util.List;

public interface DriverDao {
    List<Driver> findAllDriver();

    int DriverCount();
    boolean insert(Driver driver);

    Status updatePassword(Driver driver);

    Status updateclsignaturetime(Driver driver);

    Status delete(Driver driver);

    Driver findDriver(Driver driver);
    boolean findDriverByIdNumber(String IdNumber,String IdCar);




}
