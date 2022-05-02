package com.pisim.rsu.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pisim.rsu.bean.DrivingReport;
import com.pisim.rsu.dao.DrivingReportDao;
import com.pisim.rsu.encryption.Hash;
import com.pisim.rsu.parameterUtil.parameter;
import com.pisim.rsu.service.CongestionInfoService;
import com.pisim.rsu.service.DrivingReportService;
import it.unisa.dia.gas.jpbc.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pisim.rsu.utils.Graph;
import org.ujmp.core.util.Base64;

import java.sql.Timestamp;
import java.util.*;

import static com.pisim.rsu.controller.TraceDriver.Trace;
import static com.pisim.rsu.parameterUtil.parameter.*;

@Service
public class DrivingReportServiceImpl implements DrivingReportService {
    @Autowired
    private DrivingReportDao drivingReportDao;
    @Autowired
    CongestionInfoService congestionInfoService;
    @Autowired
    DrivingReportService drivingReportService;

    @Override
    public List<DrivingReport> getDrivingReportList(boolean flag) {
        return drivingReportDao.getDrivingReportList(flag);
    }

    @Override
    public boolean insertDrivingReport(JSONObject drivingReportJsonString, String pid, Timestamp timestamp) {
        return drivingReportDao.insertDrivingReport(drivingReportJsonString, pid, timestamp);
    }

    @Override
    public boolean deleteDrivingReport(String pid, boolean flag) {
        return drivingReportDao.deleteDrivingReport(pid, flag);
    }

    @Override
    public boolean filterFalseDrivingReport() {
        List<DrivingReport> drivingReportList = drivingReportDao.getDrivingReportList(true);
        if (drivingReportList != null) {
//            RSUReportRequestValidCount = RSUReportRequestValidCount + drivingReportList.size();
//            RSUReportRequestValidCount_temp =  drivingReportList.size();
            Graph graph = constructGraph(drivingReportList);


//            double trustSeed = 40;
//            graph.setVertexTrustValue(trustSeed, trustVertexList);

//            给指定的司机分配信任值（非随机分配）方法调用范例
//
//          1.给指定的单个司机（给出具体的pid）分配信任值
            byte[] pidj_byte = Hash.sha128_byte("123456" + (te - 1));
            String pid_temp = Base64.encodeBytes(pidj_byte);
//            String pid_temp = "mXBZ4SDYSy7s52vJJDU5qMQRTF4=";
            double trustValue = 40;
            graph.setVertexTrustValueByPid(pid_temp, trustValue);

            //2.给指定的司机列表（给出对应的pid集合）分配信任值（平均分配）
            //List<String> pidList = new ArrayList<String>();
            //pidList.add("111111111");
            //pidList.add("222222222");
            //pidList.add("333333333");
            //double testTrustSeed = 9;
            //graph.setTrustValueByPidList(pidList, testTrustSeed);


            graph.updateVertexEdgesListMap();

            //过滤图中的恶意节点
            graph.filterMaliciousVertex();

            List<Graph.Vertex> honestDriverVertexList = graph.getVertexList();
            List<String> honestDriverPidList = new ArrayList<>();   //过滤后的诚实节点（贡献司机的pid）的集合
            for (Graph.Vertex vertex : honestDriverVertexList) {
                if (!honestDriverPidList.contains(vertex.getId())) {
                    honestDriverPidList.add(vertex.getId());
                }
            }

            List<String> maliciousDriverPidList = new ArrayList<>();    //恶意节点的pid的集合
            for (DrivingReport drivingReport : drivingReportList) {
                if (!honestDriverPidList.contains(drivingReport.getPidj())) {
                    JSONObject jsonObject = JSONObject.parseObject(drivingReport.getReport_string());
                    Element REi1 = parameter.G1.newElementFromBytes(jsonObject.getBytes("REi1"));
                    Element REi2 = parameter.G1.newElementFromBytes(jsonObject.getBytes("REi2"));
                    Trace(REi1,REi2,jsonObject.getString("pidjs"),1);
                    maliciousDriverPidList.add(drivingReport.getPidj());
                }
            }

//            RSUReportValidCount_temp = RSUReportRequestValidCount_temp - maliciousDriverPidList.size();

            //从数据库中删除恶意节点对应的驾驶报告
            for (String pid : maliciousDriverPidList) {
                deleteDrivingReport(pid, true);
            }

            System.out.println("过滤虚假报告----虚假报告过滤成功！");
//            getCongestion getCongestion = new getCongestion();
//            getCongestion.generateCongestion(congestionInfoService, drivingReportService);

        } else {
            System.out.println("过滤虚假报告----无可用的路况报告，不需要进行过滤！");
        }

        return true;
    }

    @Override
    public boolean deleteOverdueDrivingReport(double validTime, boolean flag) {
        return drivingReportDao.deleteOverdueDrivingReport(validTime, flag);
    }


    //根据驾驶报告集构建加权邻近图
    public static Graph constructGraph(List<DrivingReport> drivingReportList) {
        //构建的加权图
        Graph graph = new Graph(new ArrayList<Graph.Vertex>(), new HashMap<Graph.Vertex, List<Graph.Edge>>());
        List<Graph.Vertex> vertexList = new ArrayList<>();  //节点集
        Map<Graph.Vertex, List<Graph.Edge>> VertexEdgesListMap = new HashMap<>();  //图的每个顶点对应的无向边集

        //List<String> pidListOfAllDrivers = new ArrayList<>();   //驾驶报告集中包含的所有司机的pid集合
        //for (DrivingReport drivingReport :drivingReportList) {
        //    String cdPid = drivingReport.getPidj(); //发送驾驶报告的司机的pid
        //    JSONObject jsonObject = JSON.parseObject(drivingReport.getReport_string());
        //    //与发送驾驶报告的司机成功握手的司机的pid集合
        //    List<String> pidOfHandshakeWithCdList = JSON.parseArray(jsonObject.getJSONArray("pidjs").toJSONString(), String.class);
        //    //如果该发送司机的pid不在其中，则将其加入
        //    if (!pidListOfAllDrivers.contains(cdPid)) {
        //        pidListOfAllDrivers.add(cdPid);
        //    }
        //    //将pidOfHandshakeWithCdList中不在pidListOfAllDrivers的司机的pid也加入其中
        //    for (String pid : pidOfHandshakeWithCdList) {
        //        if (!pidListOfAllDrivers.contains(pid)) {
        //            pidListOfAllDrivers.add(pid);
        //        }
        //    }
        //}

        //驾驶报告集中贡献司机的pid的集合
        List<String> cdPidList = new ArrayList<>();
        for (DrivingReport drivingReport : drivingReportList) {
            //若为同一司机多次发送的驾驶报告，则只将其pid加入集合一次
            String drivingReportPid = drivingReport.getPidj();
            if (!(cdPidList.contains(drivingReportPid))) {
                cdPidList.add(drivingReportPid);
            }
        }

        //每份驾驶报告中贡献司机的pid和与其成功成功握手的司机的pid集合的映射
        Map<String, List<String>> pidMap = new HashMap<>();
        for (DrivingReport drivingReport : drivingReportList) {
            JSONObject drivingReportJsonObject = JSON.parseObject(drivingReport.getReport_string());
            JSONArray pidListJsonArray = drivingReportJsonObject.getJSONArray("pidjs");
            List<String> pidList = JSON.parseArray(pidListJsonArray.toJSONString(), String.class);
            //如果为同一个贡献司机发送的多份驾驶报告，则将其合并
            String cdPid = (String) drivingReportJsonObject.get("pidj");
            if (pidMap.containsKey(cdPid)) {
                List<String> oldPidList = pidMap.get(cdPid);
                oldPidList.addAll(pidList);
            } else {
                pidMap.put(cdPid, pidList);
            }
        }


        Map<String, List<String>> newPidMap = new HashMap<>();
        newPidMap.putAll(pidMap);   //深拷贝原来的pidMap?
        for (Map.Entry<String, List<String>> map : pidMap.entrySet()) {
            String cdPid = map.getKey();
            List<String> pidList = map.getValue();
            for (String pid : pidList) {
                if (!cdPidList.contains(pid)) {
                    cdPidList.add(pid);
                    List<String> newPidList = new ArrayList<>();
                    newPidList.add(cdPid);
                    newPidMap.put(pid, newPidList);
                }
            }
        }

        //构建节点集
        for (String pid : cdPidList) {
            Graph.Vertex vertex = new Graph.Vertex(pid);
            vertexList.add(vertex);
        }

        //构建图的每个顶点对应的无向边集
        for (Map.Entry<String, List<String>> map : newPidMap.entrySet()) {
            Graph.Vertex oneVertex = new Graph.Vertex(map.getKey());    //发送驾驶报告的贡献司机对应的节点
            List<Graph.Edge> vertexEdgeList = new ArrayList<>();    //节点对应的无向边集（邻接表）
            //遍历每个节点对应的握手成功的pid集
            List<String> pidList = map.getValue();
            for (String pid : pidList) {
                Graph.Vertex anotherVertex = new Graph.Vertex(pid);
                Graph.Edge edge = new Graph.Edge(oneVertex, anotherVertex, 1); //构造边
                //如果该边已存在，则将该边对应的权重加1
                if (vertexEdgeList.contains(edge)) {
                    int index = vertexEdgeList.indexOf(edge);
                    int weight = vertexEdgeList.get(index).getWeight();
                    weight = weight + 1;
                    edge.setWeight(weight);
                    vertexEdgeList.set(index, edge);
                }
                //否则，将该边加入对应的无向边集中
                else {
                    vertexEdgeList.add(edge);
                }
            }
            VertexEdgesListMap.put(oneVertex, vertexEdgeList);
        }

        //解决节点存储的边一样（权重不同）时造成的图结构的不一致
        //Map<Graph.Vertex, List<Graph.Edge>> oldVertexEdgesListMap = new HashMap<>();
        //oldVertexEdgesListMap.putAll(VertexEdgesListMap);   //深拷贝？
        for (Map.Entry<Graph.Vertex, List<Graph.Edge>> map : VertexEdgesListMap.entrySet()) {
            Map<Graph.Vertex, List<Graph.Edge>> VertexEdgesListMapCopy = new HashMap<>();
            VertexEdgesListMapCopy.putAll(VertexEdgesListMap);  //深拷贝？
            Graph.Vertex vertex = map.getKey();
            List<Graph.Edge> edgeList = map.getValue();

            VertexEdgesListMapCopy.remove(vertex);  //去除当前节点及边列表后图中剩余的节点与边列表
            for (Map.Entry<Graph.Vertex, List<Graph.Edge>> remainMap : VertexEdgesListMapCopy.entrySet()) {
                Graph.Vertex remainVertex = remainMap.getKey();
                List<Graph.Edge> remainEdgeList = remainMap.getValue();

                for (Graph.Edge edge : edgeList) {
                    //如果有相同的边，则更新对应的权重值
                    if (remainEdgeList.contains(edge)) {
                        int remainIndex = remainEdgeList.indexOf(edge);
                        int remainWeight = remainEdgeList.get(remainIndex).getWeight();
                        int oldWeight = edge.getWeight();
                        edge.setWeight(oldWeight + remainWeight);
                        Graph.Edge remainEdge = remainEdgeList.get(remainIndex);
                        remainEdge.setWeight(remainWeight + oldWeight);
                    }
                }
            }
            break;
        }

        graph.setVertexList(vertexList);
        graph.setVertexEdgesListMap(VertexEdgesListMap);

        return graph;
    }

}
