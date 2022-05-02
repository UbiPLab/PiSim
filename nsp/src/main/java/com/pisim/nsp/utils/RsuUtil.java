package com.pisim.nsp.utils;

import com.alibaba.fastjson.JSONArray;
import com.pisim.nsp.parameterUtil.parameter;


public class RsuUtil {

    //从jsonArray中获取double[][]矩阵
    public static double[][] getMatrixArray(JSONArray jsonArray, int count) {
        int k2 = parameter.k1;
        double[][] temp = new double[k2][count];
        for (int i = 0; i < k2; i++) {
            JSONArray jsonArray1 = (JSONArray) jsonArray.toArray()[i];
            for (int k = 0; k < jsonArray1.size(); k++) {
                temp[i][k] = jsonArray1.getDouble(k);
            }
        }
        return temp;
    }

    //从jsonArray中获取double[]
    public static double[] getThreshold(JSONArray jsonArray) {
        double[] temp = new double[jsonArray.size()];
        for (int i=0 ;i<jsonArray.size();i++){
            temp[i] = jsonArray.getDouble(i);
        }
        return temp;
    }

}
