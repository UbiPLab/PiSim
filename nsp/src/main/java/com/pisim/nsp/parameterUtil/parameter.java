package com.pisim.nsp.parameterUtil;

import com.alibaba.fastjson.JSONObject;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import org.ujmp.core.Matrix;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class parameter {
    //缓存线程返回的数据
    public static String result;

    //当前时间
    public static int te;
    //数据有效时间
    public static int validTime = 4;
    //时间更新频度
    public static int cycle =  1000*60*2;
    public static Pairing pairing;
    public static int MC;
    //grlpis
    public static List<BigInteger> grlpis = new ArrayList<>();
    //RSU相关参数
    public static String NSP_rsa_pub;
    public static String NSP_rsa_pri;
    //零知识证明相关参数
    public static BigInteger ZKPK_rou;
    public static BigInteger ZKPK_F;
    //索引向量的长度
    public static int k1 = 35;
    //RSU
    public static int RsuKeySize = 2048;


    public static int NSPtrafficInfoCount;
    public static int NSPNaviCount;
    public static int NSPtrafficInfoCount_Last;
    public static int NSPNaviCount_Last;
    public static int NSPtrafficInfoCount_temp;
    public static int NSPNaviCount_temp;
    public static int NSPNaviPointCount_temp;



}
