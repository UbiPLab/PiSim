package com.pisim.rsu.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.pisim.rsu.bean.CongestionInfo;
import com.pisim.rsu.dao.CongestionInfoDao;
import com.pisim.rsu.service.CongestionInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CongestionInfoServiceImpl implements CongestionInfoService {
    @Autowired
    private CongestionInfoDao congestionInfoDao;

    @Override
    public List<CongestionInfo> getCongestionInfoList() {
        return congestionInfoDao.getCongestionInfoList();
    }

    @Override
    public boolean insertCongestionInfo(CongestionInfo congestionInfo) {
        return congestionInfoDao.insertCongestionInfo(congestionInfo);
    }

    @Override
    public boolean insertCongestionInfo(List<CongestionInfo> congestionInfos) {
        return congestionInfoDao.insertCongestionInfo(congestionInfos);
    }


    @Override
    public boolean deleteCongestionInfo(CongestionInfo congestionInfo) {
        return congestionInfoDao.deleteCongestionInfo(congestionInfo);
    }

    //从交通拥堵数据库中删除过期的交通拥堵信息，validTime为过期时间，单位为分钟，可以根据需要进行调整
    @Override
    public boolean deleteOverdueCongestionInfo(double validTime) {
        return congestionInfoDao.deleteOverdueCongestionInfo(validTime);
    }

}
