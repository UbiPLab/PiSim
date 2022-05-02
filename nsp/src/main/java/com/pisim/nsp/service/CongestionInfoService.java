package com.pisim.nsp.service;

import com.pisim.nsp.bean.CongestionInfo;

import java.util.List;

public interface CongestionInfoService {
    List<CongestionInfo> getCongestionInfoList();
    boolean insertCongestionInfo(List<CongestionInfo> congestionInfos);
    boolean deleteOverdueCongestionInfo(double validTime);
}
