package com.example.secureserver.parameterUtil;

public class parameterLength {
    //TA 私钥gsk_a gsk_b长度
    public static int gsk_ab_length = 256;

    //SK1 SK2 V的长度
    public static int k1 = 35;


    //ZKPK 生成零知识证明参数时要用的参数 qlength ,在此基础上F在1816位 g1813位
    public static int qlength = 256;

    //服务器签名时随机t的长度
    public static int t_length = 256;

    //RSU
    public static int RsuKeySize = 2048;

}
