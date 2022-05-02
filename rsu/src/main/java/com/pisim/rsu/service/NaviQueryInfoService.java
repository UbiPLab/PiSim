package com.pisim.rsu.service;

import com.pisim.rsu.bean.NaviQuery;

import java.sql.Timestamp;
import java.util.List;

public interface NaviQueryInfoService {
    List<NaviQuery> getNaviQueryInfoList();
    boolean insertNaviQueryInfo(NaviQuery naviQuery);
    boolean deleteNaviQueryInfo(double validTime);
    boolean deleteNaviQueryInfo(int id);
}
