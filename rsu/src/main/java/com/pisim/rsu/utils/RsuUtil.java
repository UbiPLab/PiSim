package com.pisim.rsu.utils;

import com.alibaba.fastjson.JSONArray;
import com.pisim.rsu.encryption.Hash;
import it.unisa.dia.gas.jpbc.Element;
import com.pisim.rsu.parameterUtil.parameter;

import java.math.BigInteger;

public class RsuUtil {
    public static boolean verifyRequest(Element REi1, Element REi2, String Mij, BigInteger ci, BigInteger ssi, double[][] Index_EncKiI, BigInteger rlpi, int rci,String temp_ZKPK) {
        //计算Ei = e(REi1, A)^ci · e(REi2, g))^ci · e(REi1, B)^ssi
        Element Ei = (parameter.pairing.pairing(REi1, parameter.gpk_A).pow(ci)).mul(
                (parameter.pairing.pairing(REi2, parameter.gpk_g).pow(ci)).negate()
        ).mul(parameter.pairing.pairing(REi1, parameter.gpk_B).pow(ssi));
        // 计算cii 与司机发来的ci比较
        String temp = Mij + Ei.toString() + temp_ZKPK;
        BigInteger cii = new BigInteger(Hash.sha256(temp));
        if (cii.compareTo(ci) == 0) {
            System.out.println("验证rdi导航查询或cdi提交报告请求成功");
            return true;
        } else {
            System.out.println("验证rdi导航查询或cdi提交报告请求失败----身份错误");
            return false;
        }
    }

    //从jsonArray中获取double[][]矩阵
    public static double[][] getMatrixArray(JSONArray jsonArray, int count) {
        double[][] temp = new double[jsonArray.size()][count];
        for (int i = 0; i < jsonArray.size(); i++) {
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

    //将一维数组还原成二维
    public static byte[][] getPidjs(byte[] pidj_temp) {
        byte[][] pidj = new byte[pidj_temp.length / 32][32];
        byte[] temp = new byte[32];
        for (int i = 0; i < pidj_temp.length / 32; i++) {
//            System.arraycopy(pidj_temp, i * 32, temp, 0, 32);
//            pidj[i] = temp;
            for (int j = 0; j < 32; j++) {
                pidj[i][j] = pidj_temp[i * 32 + j];
            }
        }
        return pidj;
    }

    //从数据库读取的字符串中获取long[][]型的Query_Index
    public static long[][] getQuery_Index(JSONArray jsonArray) {
        try {
            long[][] Query_Index = new long[2][parameter.k1];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < parameter.k1; j++) {
                    Query_Index[i][j] = jsonArray.getJSONArray(i).getBigDecimal(j).longValue();
                }
            }
            return Query_Index;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
