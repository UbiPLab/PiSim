package com.example.secureserver.service.Impl;

import com.example.secureserver.bean.Malicious_driver;
import com.example.secureserver.dao.DriverDao;
import com.example.secureserver.dao.Malicious_driverDao;
import com.example.secureserver.service.Malicious_driverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Malicious_driverServiceImpl implements Malicious_driverService {
    @Autowired
    private Malicious_driverDao malicious_driverDao;

    @Override
    public List<Malicious_driver> findAllMalicious_driver() {
        return malicious_driverDao.findAllMalicious_driver();
    }

    @Override
    public boolean insert(Malicious_driver malicious_driver) {
        return malicious_driverDao.insert(malicious_driver);
    }

    @Override
    public int Malicious_driver_count() {
        return malicious_driverDao.Malicious_driver_count();
    }
}
