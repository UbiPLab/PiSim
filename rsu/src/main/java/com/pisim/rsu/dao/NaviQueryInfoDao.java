package com.pisim.rsu.dao;

import com.pisim.rsu.bean.CongestionInfo;
import com.pisim.rsu.bean.NaviQuery;

import java.sql.Timestamp;
import java.util.List;

public interface NaviQueryInfoDao {
    List<NaviQuery> getNaviQueryInfoList();
    boolean insertNaviQueryInfo(NaviQuery naviQuery);
    boolean deleteNaviQueryInfo(double validTime);
    boolean deleteNaviQueryInfo(int id);

}
