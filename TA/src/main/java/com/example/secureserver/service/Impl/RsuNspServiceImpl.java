package com.example.secureserver.service.Impl;

import com.example.secureserver.dao.RsuNspDao;
import com.example.secureserver.service.RsuNspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ujmp.core.objectmatrix.calculation.Unique;

@Service
public class RsuNspServiceImpl implements RsuNspService {
    @Autowired
    private RsuNspDao rsuNspDao;

    @Override
    public boolean insert(String PubKey,String Unique_id){
        return rsuNspDao.insert(PubKey,Unique_id);
    }

    @Override
    public boolean getRsuNspKey(String PubKey) {
        return rsuNspDao.getRsuNspKey(PubKey);
    }
}
