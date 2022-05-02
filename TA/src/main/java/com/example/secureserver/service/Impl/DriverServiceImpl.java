package com.example.secureserver.service.Impl;

import com.example.secureserver.bean.Status;
import com.example.secureserver.bean.Driver;
import com.example.secureserver.dao.DriverDao;
import com.example.secureserver.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverServiceImpl implements DriverService {
    @Autowired
    private DriverDao driverDao;

    @Override
    public List<Driver> findAllDriver() {
        return driverDao.findAllDriver();
    }

    @Override
    public int Drivercount() {
        return driverDao.DriverCount();
    }

    @Override
    public boolean insert(Driver driver) {
        return driverDao.insert(driver);
    }

    @Override
    public Status updatePassword(Driver driver) {
        return driverDao.updatePassword(driver);
    }

    @Override
    public Status updateclsignaturetime(Driver driver) {
        return driverDao.updateclsignaturetime(driver);
    }

    @Override
    public Status delete(Driver driver) {
        return driverDao.delete(driver);
    }

    @Override
    public Driver findDriver(Driver driver) {
        return driverDao.findDriver(driver);
    }
    @Override
    public boolean findDriverByIdNumber(String IdNumber,String IdCar){
        return driverDao.findDriverByIdNumber(IdNumber,IdCar);
    }
}
