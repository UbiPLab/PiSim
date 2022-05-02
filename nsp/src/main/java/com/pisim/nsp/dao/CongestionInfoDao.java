package com.pisim.nsp.dao;

import com.alibaba.fastjson.JSONObject;
import com.pisim.nsp.bean.CongestionInfo;

import java.util.ArrayList;
import java.util.List;

public interface CongestionInfoDao {
    List<CongestionInfo> getCongestionInfoList();

    boolean insertCongestionInfo(CongestionInfo congestionInfo);
    boolean insertCongestionInfo(List<CongestionInfo> congestionInfos);
    boolean deleteCongestionInfo(CongestionInfo congestionInfo);

    //从交通拥堵数据库中删除过期的交通拥堵信息，validTime为过期时间，单位为分钟，可以根据需要进行调整
    boolean deleteOverdueCongestionInfo(double validTime);

}
