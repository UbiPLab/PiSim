package com.example.secureserver.parameterUtil;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import org.ujmp.core.Matrix;

import java.math.BigInteger;
import java.security.SecureRandom;

public class parameter {
    public static Field G1;
    public static Field G2;
    public static Pairing pairing;

    // TA公开参数
    public static BigInteger gsk_a;
    public static BigInteger gsk_b;
    public static Element gpk_g;
    public static Element gpk_g2;
    public static Element gpk_A;
    public static Element gpk_B;
    public static String rsa_pub;
    public static String rsa_pri;
    public static int MC;

    //模糊搜索相关参数
    public static Matrix HKP;
    public static Matrix SK1;
    public static Matrix SK2;
    public static Matrix V;

    public static BigInteger K;

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
    public static BigInteger clSign_SK_p;

    public static SecureRandom secureRandom = new SecureRandom();

    public static String jwtKey;
}
