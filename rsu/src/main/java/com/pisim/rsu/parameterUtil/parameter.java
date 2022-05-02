package com.pisim.rsu.parameterUtil;

import java.math.BigInteger;
import java.util.*;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

public class parameter {
    //缓存线程返回的数据
    public static String result;

    public static Field G1;
    public static Field G2;
    public static Pairing pairing;

    // TA公开参数
    public static Element gpk_g;
    public static Element gpk_g2;
    public static Element gpk_A;
    public static Element gpk_B;
    public static String TA_rsa_pub;
    public static int MC;

    //grlpis
    public static List<BigInteger> grlpis = new ArrayList<>();
    public static List<BigInteger> rlpis = new ArrayList<>();

    //RSU相关参数
    public static String RSU_rsa_pub;
    public static String RSU_rsa_pri;
    public static String NSP_rsa_pub;

    //当前时间
    public static int te;
    public static int origin_te;

    //驾驶报告有效时间 2min
    public static int reportValidTime = 1000 * 60 * 2;
    //路况信息有效时间 10 min
    public static int congestionInfoValidTime = 1000 * 60 * 10;
    //时间跟新频度 2min
    public static int cycle = 1000 * 60 * 2;

    //零知识证明相关参数
    public static BigInteger ZKPK_rou;
    public static BigInteger ZKPK_F;
    public static BigInteger ZKPK_g;
    public static BigInteger ZKPK_b;

    //clSign相关参数
    public static BigInteger clSign_PK_n;
    public static BigInteger clSign_PK_d1;
    public static BigInteger clSign_PK_d2;
    public static BigInteger clSign_PK_d3;
    //SK1 SK2 V 长度
    public static int k1 = 35;

    //RSU
    public static int RsuKeySize = 2048;

    //admin
    public static int RSUNaviCount;
    public static int RSUNaviValidCount;
    public static int RSUReportRequestCount;
    public static int RSUReportRequestValidCount;
    public static int RSUReportValidCount;
    public static int RSUReportMaliciousCount;

    public static int RSUNaviCount_Last;
    public static int RSUNaviValidCount_Last;
    public static int RSUReportRequestCount_Last;
    public static int RSUReportRequestValidCount_Last;
    public static int RSUReportValidCount_Last;

    public static int RSUNaviCount_temp;
    public static int RSUNaviValidCount_temp;
    public static int RSUReportRequestCount_temp;
    public static int RSUReportRequestValidCount_temp;
    public static int RSUReportValidCount_temp;

    public static int RSUNaviPointCount_temp;
}
