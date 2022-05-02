package com.example.mygaode.utils;

import com.graphhopper.util.PointList;

import java.util.Arrays;

public class Util {
    public static byte[] xor(byte[] Byte1_temp, byte[] Byte2_temp) {
        byte[] Byte_temp = new byte[Byte1_temp.length];
        for (int i = 0; i < Byte1_temp.length; i++) {
            Byte_temp[i] = (byte) (Byte1_temp[i] ^ Byte2_temp[i]);
        }
        return Byte_temp;
    }

    public static byte[] xorStr(String str1, String str2) {
        byte[] Byte1_temp;
        byte[] Byte2_temp;
        if (str1.getBytes().length > str1.getBytes().length) {
            Byte1_temp = str1.getBytes();
            Byte2_temp = new byte[Byte1_temp.length];
            System.arraycopy(str2.getBytes(), 0, Byte2_temp, 0, str2.getBytes().length);
        } else {
            Byte2_temp = str2.getBytes();
            Byte1_temp = new byte[Byte2_temp.length];
            System.arraycopy(str1.getBytes(), 0, Byte1_temp, 0, str1.getBytes().length);
        }
        byte[] Byte_temp = new byte[Byte1_temp.length];
        for (int i = 0; i < Byte1_temp.length; i++) {
            Byte_temp[i] = (byte) (Byte1_temp[i] | Byte2_temp[i]);
        }
        return Byte_temp;
    }

    /**
     * 从字符串生成路径点序列
     * @param Points_str 保存路径点序列的字符串
     * @return 生成的路径点序列
     */
    public static PointList convertPointList(String Points_str){
        Points_str = Points_str.replace(")","");
        Points_str = Points_str.replace("(","");
        String[] temp1 = Points_str.split(",");
        PointList pointList = new PointList();
        for (int i=0;i<temp1.length;i = i + 2){
            pointList.add(Double.parseDouble(temp1[i]),Double.parseDouble(temp1[i+1]));
        }
        return pointList;
    }


}