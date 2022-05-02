package com.pisim.rsu.service.Impl;

import com.pisim.rsu.bean.NaviQuery;
import com.pisim.rsu.dao.NaviQueryInfoDao;
import com.pisim.rsu.service.NaviQueryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;

@Service
public class NaviQueryInfoServiceImpl implements NaviQueryInfoService {
    @Autowired
    NaviQueryInfoDao naviQueryInfoDao;


    @Override
    public List<NaviQuery> getNaviQueryInfoList() {
        return naviQueryInfoDao.getNaviQueryInfoList();
    }

    @Override
    public boolean insertNaviQueryInfo(NaviQuery naviQuery) {
        return naviQueryInfoDao.insertNaviQueryInfo(naviQuery);
    }

    @Override
    public boolean deleteNaviQueryInfo(double validTime) {
        return naviQueryInfoDao.deleteNaviQueryInfo(validTime);
    }

    @Override
    public boolean deleteNaviQueryInfo(int id) {
        return naviQueryInfoDao.deleteNaviQueryInfo(id);
    }
}
