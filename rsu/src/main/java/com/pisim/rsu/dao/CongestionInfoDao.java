package com.pisim.rsu.dao;

import com.alibaba.fastjson.JSONObject;
import com.pisim.rsu.bean.CongestionInfo;

import java.util.List;

public interface CongestionInfoDao {
    List<CongestionInfo> getCongestionInfoList();
    boolean insertCongestionInfo(CongestionInfo congestionInfo);
    boolean deleteCongestionInfo(CongestionInfo congestionInfo);
    boolean insertCongestionInfo(List<CongestionInfo> congestionInfos);
    //从交通拥堵数据库中删除过期的交通拥堵信息，validTime为过期时间，单位为分钟，可以根据需要进行调整
    boolean deleteOverdueCongestionInfo(double validTime);
}
