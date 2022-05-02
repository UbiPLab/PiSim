package com.example.secureserver.encryption;

import com.example.secureserver.parameterUtil.parameterLength;
import com.example.secureserver.utils.ParamsToString;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.ujmp.core.util.Base64;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Properties;

import static com.example.secureserver.encryption.Fuzzy_search.generateSearchKey;
import static com.example.secureserver.encryption.Opaak.gen_pq;
import static com.example.secureserver.encryption.RSA.generateRsaKeyPair;
import static com.example.secureserver.parameterUtil.parameter.*;
import static com.example.secureserver.parameterUtil.parameterLength.gsk_ab_length;
import static com.example.secureserver.parameterUtil.parameterLength.qlength;

public class Initialize {
    public static void getParameterFromFile(String pairingPath, String paramPath) {
        Properties properties = new Properties();
        try {
            //从配置文件生成pairing
            pairing = PairingFactory.getPairing(pairingPath);
            G1 = pairing.getG1();
            G2 = pairing.getG2();
            //从配置文件读取参数
            properties.load(new FileInputStream(new File(paramPath)));
            rsa_pub = properties.getProperty("rsa_pub");
            rsa_pri = properties.getProperty("rsa_pri");
            gsk_a = new BigInteger(properties.getProperty("gsk_a"));
            gsk_b = new BigInteger(properties.getProperty("gsk_b"));
            gpk_g = G2.newElementFromBytes(Base64.decode(properties.getProperty("gpk_g"))).getImmutable();
            gpk_A = G1.newElementFromBytes(Base64.decode(properties.getProperty("gpk_A"))).getImmutable();
            gpk_B = G1.newElementFromBytes(Base64.decode(properties.getProperty("gpk_B"))).getImmutable();
            gpk_g2 = G1.newElementFromBytes(Base64.decode(properties.getProperty("gpk_g2"))).getImmutable();
            ZKPK_rou = new BigInteger(properties.getProperty("ZKPK_rou"));
            ZKPK_F = new BigInteger(properties.getProperty("ZKPK_F"));
            ZKPK_g = new BigInteger(properties.getProperty("ZKPK_g"));
            ZKPK_b = new BigInteger(properties.getProperty("ZKPK_b"));
            MC = Integer.parseInt(properties.getProperty("MC"));
            K = new BigInteger(properties.getProperty("K"));
            SK1 = ParamsToString.StringToSK(properties.getProperty("SK1"), parameterLength.k1,parameterLength.k1);
            SK2 = ParamsToString.StringToSK(properties.getProperty("SK2"),parameterLength.k1,parameterLength.k1);
            V = ParamsToString.StringToSK(properties.getProperty("V"),1,parameterLength.k1);
            HKP = ParamsToString.StringToSK(properties.getProperty("HKP"),1,parameterLength.k1);
            jwtKey = properties.getProperty("jwtKey");
            System.out.println("Info    ----    " + "从配置文件获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error   ----    " + "检测到配置文件出错，重新生成参数");
            param_initialize(pairingPath);
        }
    }

    private static void param_initialize(String pairingPath) {
        try {
            pairing = PairingFactory.getPairing(pairingPath);
            G1 = pairing.getG1();
            G2 = pairing.getG2();
            MC = 5;
            //采用SHA1PRNG的随机算法生成随机数 SHA1PRNG的效率比NativePRNG的效率高
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            //获取组私钥gsk (a,b)来自于正整数群
            gsk_a = new BigInteger(gsk_ab_length, secureRandom);
            gsk_b = new BigInteger(gsk_ab_length, secureRandom);
            //获取组公钥gpk
            gpk_g = G2.newRandomElement().getImmutable();
            gpk_A = gpk_g.pow(gsk_a).getImmutable();
            gpk_B = gpk_g.pow(gsk_b).getImmutable();
            gpk_g2 = G1.newRandomElement().getImmutable();
            // 获取RSA公私钥
            Map<String, String> RSAkeyMap = generateRsaKeyPair();
            rsa_pub = RSAkeyMap.get("publicKey");
            rsa_pri = RSAkeyMap.get("privateKey");
            // 获取SK1 SK2 V HKP
            generateSearchKey();
            //生成零知识证明要用的参数
            Map<String, BigInteger> pq = gen_pq(qlength);
            BigInteger rou = pq.get("rou");
            BigInteger F = pq.get("F");
            ZKPK_rou = rou;
            ZKPK_F = F;
            BigInteger g;
            BigInteger gTemp = new BigInteger(10, secureRandom);
            BigInteger b = F.subtract(BigInteger.ONE).divide(rou);
            if (gTemp.modPow(b, F).compareTo(BigInteger.ZERO) != 0) {
                g = gTemp.modPow(b, F);
                if (g.modPow(rou, F).compareTo(BigInteger.ONE) == 0) {
                    ZKPK_g = g;
                    ZKPK_b = b;
                } else {
                    System.out.println("生成零知识证明参数失败");
                }
            }
            //生成K
            K = new BigInteger(256, secureRandom);
            //将内容存储到文件中
            Properties properties = new Properties();
            properties.setProperty("rsa_pub", rsa_pub);
            properties.setProperty("rsa_pri", rsa_pri);
            properties.setProperty("gsk_a", gsk_a.toString());
            properties.setProperty("gsk_b", gsk_b.toString());
            properties.setProperty("gpk_g", Base64.encodeBytes(gpk_g.toBytes()));
            properties.setProperty("gpk_A", Base64.encodeBytes(gpk_A.toBytes()));
            properties.setProperty("gpk_B", Base64.encodeBytes(gpk_B.toBytes()));
            properties.setProperty("gpk_g2", Base64.encodeBytes(gpk_g2.toBytes()));
            properties.setProperty("ZKPK_rou", ZKPK_rou.toString());
            properties.setProperty("ZKPK_F", ZKPK_F.toString());
            properties.setProperty("ZKPK_g", ZKPK_g.toString());
            properties.setProperty("ZKPK_b", ZKPK_b.toString());
            properties.setProperty("MC", String.valueOf(MC));
            properties.setProperty("K", String.valueOf(K));
            properties.setProperty("SK1",ParamsToString.SKToString(SK1,parameterLength.k1,parameterLength.k1));
            properties.setProperty("SK2",ParamsToString.SKToString(SK2,parameterLength.k1,parameterLength.k1));
            properties.setProperty("V",ParamsToString.SKToString(V,1,parameterLength.k1));
            properties.setProperty("HKP",ParamsToString.SKToString(HKP,1,parameterLength.k1));

            properties.store(new FileOutputStream("./config/params.properties"), "参数");
            System.out.println("Info    ----    " + "参数初始化成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error   ----    " + "参数初始化失败，双线性参数配置文件有误");
        }
    }
}
