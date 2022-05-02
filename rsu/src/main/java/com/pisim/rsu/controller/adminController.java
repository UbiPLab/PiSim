package com.pisim.rsu.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pisim.rsu.bean.DrivingReport;
import com.pisim.rsu.service.DrivingReportService;
import com.pisim.rsu.service.NaviQueryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.pisim.rsu.parameterUtil.parameter.*;


@RestController
@RequestMapping("/RSUInfo")
public class adminController {
    @Autowired
    DrivingReportService drivingReportService;
    @Autowired
    NaviQueryInfoService naviQueryInfoService;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public JSONObject returnRSUInfo() {
        JSONObject result = new JSONObject();
        result.put("RSUNaviCount", RSUNaviCount);
        result.put("RSUNaviValidCount", RSUNaviValidCount);
        result.put("RSUReportRequestCount", RSUReportRequestCount);
        result.put("RSUReportRequestValidCount", RSUReportRequestValidCount);
        result.put("RSUReportValidCount", RSUReportValidCount);
        result.put("RSUNaviCount_Last", RSUNaviCount_Last);
        result.put("RSUNaviValidCount_Last", RSUNaviValidCount_Last);
        result.put("RSUReportRequestCount_Last", RSUReportRequestCount_Last);
        result.put("RSUReportRequestValidCount_Last", RSUReportRequestValidCount_Last);
        result.put("RSUReportValidCount_Last", RSUReportValidCount_Last);
        result.put("RSUReportRequestValidCount_temp", RSUReportRequestValidCount_temp);
        result.put("RSUReportMaliciousCount",RSUReportMaliciousCount);
        result.put("RSUNaviPointCount_temp", RSUNaviPointCount_temp);

        List<DrivingReport> drivingReports = drivingReportService.getDrivingReportList(false);
        JSONArray jsonArray = new JSONArray();
        if (drivingReports != null) {
            result.put("RSUReport", parseStringReport(drivingReports));
        } else {
            result.put("RSUReport", null);
        }
        result.put("RSUNavi", naviQueryInfoService.getNaviQueryInfoList());
        result.put("te", te - origin_te);
        return result;
    }

    private JSONArray parseStringReport(List<DrivingReport> drivingReports) {
        JSONArray jsonArray = new JSONArray();
        for (DrivingReport drivingReport : drivingReports) {
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject_temp = JSONObject.parseObject(drivingReport.getReport_string());
            jsonObject.put("id",drivingReport.getId());
            jsonObject.put("timestamp",drivingReport.getTimestamp());
            jsonObject.put("Query_EncKI",jsonObject_temp.getString("Query_EncKI"));
            jsonObject.put("rlpi",jsonObject_temp.getString("rlpi"));
            jsonObject.put("ci",jsonObject_temp.getString("ci"));
            jsonObject.put("count",jsonObject_temp.getIntValue("count"));
            jsonObject.put("ssi",jsonObject_temp.getString("ssi"));
            jsonObject.put("daierta",jsonObject_temp.getString("daierta"));
            jsonObject.put("pidj",jsonObject_temp.getString("pidj"));
            jsonObject.put("M",jsonObject_temp.getString("M"));
            jsonObject.put("hsvj",jsonObject_temp.getString("hsvj"));
            jsonObject.put("pidjs",jsonObject_temp.getString("pidjs"));
            jsonObject.put("a1",jsonObject_temp.getString("a1"));
            jsonObject.put("thresholdQuery",jsonObject_temp.getIntValue("thresholdQuery"));
            jsonObject.put("xor_hsvjs",jsonObject_temp.getString("xor_hsvjs"));
            jsonObject.put("indj",jsonObject_temp.getString("indj"));
            jsonObject.put("REi2",jsonObject_temp.getString("REi2"));
            jsonObject.put("grlpi",jsonObject_temp.getString("grlpi"));
            jsonObject.put("REi1",jsonObject_temp.getString("REi1"));
            jsonObject.put("rci",jsonObject_temp.getString("rci"));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }


}
