package com.example.mygaode.utils;

import android.content.Context;

import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapUtils {
    private final static double a = 6378245.0;
    private final static double pi = 3.14159265358979324;
    private final static double ee = 0.00669342162296594323;


    // WGS-84 to GCJ-02
    public static LatLonPoint toGCJ02Point(double latitude, double longitude) {
        LatLonPoint dev = calDev(latitude, longitude);
        double retLat = latitude + dev.getLatitude();
        double retLon = longitude + dev.getLongitude();
        return new LatLonPoint(retLat, retLon);
    }

    // GCJ-02 to WGS-84
    public static LatLonPoint toWGS84Point(double latitude, double longitude) {
        LatLonPoint dev = calDev(latitude, longitude);
        double retLat = latitude - dev.getLatitude();
        double retLon = longitude - dev.getLongitude();
        dev = calDev(retLat, retLon);
        retLat = latitude - dev.getLatitude();
        retLon = longitude - dev.getLongitude();
        return new LatLonPoint(retLat, retLon);
    }

    private static LatLonPoint calDev(double wgLat, double wgLon) {
        if (isOutOfChina(wgLat, wgLon)) {
            return new LatLonPoint(0, 0);
        }
        double dLat = calLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = calLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        return new LatLonPoint(dLat, dLon);
    }

    private static boolean isOutOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    private static double calLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double calLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }




    /**
     * 高德坐标转gps坐标
     *
     * @return 生成可进行离线路径规划的PointList
     */
    public static List<GHPoint> GaodeConvertToGps(List<LatLonPoint> wayToPoint,LatLonPoint mStartPoint,LatLonPoint mEndPoint) {
        List<GHPoint> pointList = new ArrayList<>();
        LatLonPoint temp = ConvertUtils.toWGS84Point(mStartPoint.getLatitude(), mStartPoint.getLongitude());
        GHPoint temp_GHPoint = new GHPoint(temp.getLatitude(), temp.getLongitude());
        pointList.add(temp_GHPoint);
        if (wayToPoint.size() != 0) {
            for (LatLonPoint latLonPoint : wayToPoint) {
                temp = ConvertUtils.toWGS84Point(latLonPoint.getLatitude(), latLonPoint.getLongitude());
                temp_GHPoint = new GHPoint(temp.getLatitude(), temp.getLongitude());
                pointList.add(temp_GHPoint);
            }
        }
        temp = ConvertUtils.toWGS84Point(mEndPoint.getLatitude(), mEndPoint.getLongitude());
        temp_GHPoint = new GHPoint(temp.getLatitude(), temp.getLongitude());
        pointList.add(temp_GHPoint);
        return pointList;
    }
    /**
     * 坐标转化 gps坐标转高德坐标
     *
     * @param pointList 离线算路结果
     * @return 离线算路的gps坐标转高德坐标 经度纬度的方式
     * @throws Exception
     */
    public static double[][] gpsConvertToGaode(Context context,PointList pointList) throws Exception {
     //   PointList pointList = pointListTemp;
        // 一个double二维矩阵保存离线算路数据，每行是两个点，地图划线时候将这两个点相连
        double[][] result = new double[pointList.size()][2];
        CoordinateConverter converter = new CoordinateConverter(context);
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
        DPoint temp = new DPoint();
        DPoint desLatLng;
        for (int i = 0; i < pointList.size() ; i++) {
            //行起点坐标转化
            temp.setLatitude(pointList.get(i).lat);
            temp.setLongitude(pointList.get(i).lon);
            converter.coord(temp);
            desLatLng = converter.convert();
            result[i][0] = desLatLng.getLongitude();
            result[i][1] = desLatLng.getLatitude();
        }
        return result;
    }

    public static double[][] serializationPoint(double[][] pointList,double[] congestionList,int interval){
        double [][] result = new double[pointList.length-1][5];
        for (int i = 0;i<pointList.length-1;i++){
            double temp = (double) i/interval;
            if (temp - (i / interval)>= 0.5){
                result[i][4] = congestionList[i/10+1];
            }else {
                result[i][4] = congestionList[i/interval];
            }
            result[i][0] = pointList[i][0];
            result[i][1] = pointList[i][1];
            result[i][2] = pointList[i+1][0];
            result[i][3] = pointList[i+1][1];
        }
        return result;

    }
    /**
     * 坐标转化 gps坐标转高德坐标
     * @param pointListTemp 离线算路结果
     * @return 离线算路gps左边转高德坐标
     * @throws Exception
     */
//    public static double[][] gpsConvertToGaode( PointList pointListTemp) throws Exception {
//        PointList pointList = pointListTemp;
//        // 一个double二维矩阵保存离线算路数据，每行是两个点，地图划线时候将这两个点相连
//        double[][] result = new double[pointList.size()][4];
//        CoordinateConverter converter = new CoordinateConverter(context);
//        // CoordType.GPS 待转换坐标类型
//        converter.from(CoordinateConverter.CoordType.GPS);
//        DPoint temp = new DPoint();
//        DPoint desLatLng;
//        for (int i = 0; i < pointList.size() - 1; i++) {
//            //行起点坐标转化
//            temp.setLatitude(pointList.get(i).lat);
//            temp.setLongitude(pointList.get(i).lon);
//            converter.coord(temp);
//            desLatLng = converter.convert();
//            result[i][0] = desLatLng.getLongitude();
//            result[i][1] = desLatLng.getLatitude();
//            //行终点坐标转化
//            temp.setLatitude(pointList.get(i + 1).lat);
//            temp.setLongitude(pointList.get(i + 1).lon);
//            converter.coord(temp);
//            desLatLng = converter.convert();
//            result[i][2] = desLatLng.getLongitude();
//            result[i][3] = desLatLng.getLatitude();
//        }
//        return result;
//    }

    /**
     * 读取坐标点
     * return 坐标点序列
     */
    public static List<LatLng> readLatLngs(double[] coords) {
        List<LatLng> points = new ArrayList<LatLng>();
        for (int i = 0; i < coords.length; i += 2) {
            points.add(new LatLng(coords[i + 1], coords[i]));
        }
        return points;
    }
    /**
     * 坐标点数据计算,非离线算路情况下 可用该方法进行算路
     */
    public static double[][] get_coords(String[] coords) {
        double[][] my_coords = new double[coords.length][];
        String[] ArrayStr;
        String[] ArrayDeepStr;
        for (int i = 0; i < coords.length; i++) {
            ArrayStr = coords[i].split(";");
            my_coords[i] = new double[ArrayStr.length * 2];
            for (int k = 0; k < ArrayStr.length * 2; k = k + 2) {
                ArrayDeepStr = ArrayStr[k / 2].split(",");
                my_coords[i][k] = Double.parseDouble(ArrayDeepStr[0]);
                my_coords[i][k + 1] = Double.parseDouble(ArrayDeepStr[1]);
            }
        }
        System.out.println(Arrays.deepToString(my_coords));
        return my_coords;
    }

    public static double[][] get_coords(double[][] coords) {
        double[][] my_coords = new double[coords.length/2][4];
        for (int i = 0; i < coords.length/2; i++) {
            my_coords[i][0] = coords[i][0];
            my_coords[i][1] = coords[i][1];

            my_coords[i][2] = coords[i+1][0];
            my_coords[i][3] = coords[i+1][1];


        }
        System.out.println(Arrays.deepToString(my_coords));
        return my_coords;
    }

}
