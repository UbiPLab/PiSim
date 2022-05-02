package com.pisim.rsu.encryption;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;


public class Opaak {
    public static Map<String, BigInteger> Map_TA_Cl_Key = new HashMap<>();
    public static Map<String, BigInteger> Map_TA_Cl_Sign = new HashMap<>();
    public static Map<String, BigInteger> Map_ZKPK = new HashMap<>();
    public static Map<String, BigInteger> Map_ZKPK_Params = new HashMap<>();

    public static BigInteger getZKPKParameters(int qlength) {
        long startTime = System.nanoTime();
        Map<String, BigInteger> pq = gen_pq(qlength);
        BigInteger rou = pq.get("rou");
        BigInteger F = pq.get("F");
        BigInteger g;
        SecureRandom secureRandom = new SecureRandom();
        BigInteger gTemp = new BigInteger(10, secureRandom);
        BigInteger b = F.subtract(BigInteger.ONE).divide(rou);
        if (gTemp.modPow(b, F).compareTo(BigInteger.ZERO) != 0) {
            g = gTemp.modPow(b, F);
            if (g.modPow(rou, F).compareTo(BigInteger.ONE) == 0) {
                System.out.println("good");
                Map_ZKPK_Params.put("g", g);
                Map_ZKPK_Params.put("b", b);
            }
        }
        return null;
    }

    //生成p q两个大素数满足 q|(p-1)
    public static Map<String, BigInteger> gen_pq(int qlength) {
        // 声明p q
        BigInteger p, q;
        SecureRandom secureRandom = new SecureRandom();
        // 定义初始的q
        q = new BigInteger(qlength, 100, secureRandom);
        // 定义中间参数
        BigInteger temp_p, s;
        // 定义q不能超过4097位
        //s = BigInteger.TWO.pow(4096);
        s = (BigInteger.ONE.add(BigInteger.ONE)).pow(4096);
        //BigInteger k = BigInteger.TWO;
        BigInteger k = (BigInteger.ONE.add(BigInteger.ONE));
        // k为2^1561位
        k = k.pow(1560);
        while (true) {
            p = q.multiply(k).add(BigInteger.ONE);
            temp_p = p;
            // 如果p是素数了退出
            if (temp_p.isProbablePrime(100))
                break;
            // 如果p超过了s的长度 重新生成一个q
            if (p.compareTo(s) == 1) {
                q = new BigInteger(256, 100, secureRandom);
            }
            // 都不满足 给K加2
            //k = k.add(BigInteger.TWO);
            k = k.add(BigInteger.ONE.add(BigInteger.ONE));
        }
        Map<String, BigInteger> pq = new HashMap<>();
        pq.put("rou", q);
        pq.put("F", p);
        Map_ZKPK_Params.put("rou", q);
        Map_ZKPK_Params.put("F", p);
        return pq;
    }

    /**
     * @param si           对si进行0知识证明
     * @param F            F是要mod的大素数
     * @param grlpi            grlpi
     * @param secureRandom 随机数生成器
     * @return 返回一个Map 内容为要发送给验证放的数据
     */
    public static Map<String, BigInteger> generate_ZKPK(BigInteger si, BigInteger F, BigInteger grlpi, SecureRandom secureRandom) {
        BigInteger M = grlpi.modPow(si, F);
        BigInteger a = new BigInteger(256, secureRandom);
        BigInteger daierta = grlpi.modPow(a, F);
        BigInteger n = new BigInteger(Hash.sha256(grlpi.toString() + M.toString() + daierta.toString()));
        BigInteger a1 = si.multiply(n).add(a);
        Map<String, BigInteger> ZKPK = new HashMap<String, BigInteger>();

        ZKPK.put("grlpi", grlpi);
        ZKPK.put("daierta", daierta);
        ZKPK.put("M", M);
        ZKPK.put("a1", a1);
        return ZKPK;
    }

    /**
     * @param ZKPK 请求方生成的零知识证明
     * @param F    F是要mod的大素数
     * @return 返回验证结果true或false
     */
    public static boolean verify_ZKPK(Map<String, BigInteger> ZKPK, BigInteger F) {
        BigInteger n = new BigInteger(Hash.sha256(ZKPK.get("grlpi").toString() + ZKPK.get("M").toString() + ZKPK.get("daierta").toString()));
        return ((ZKPK.get("M").modPow(n, F).multiply(ZKPK.get("daierta"))).mod(F)).compareTo(ZKPK.get("grlpi").modPow(ZKPK.get("a1"), F)) == 0;
    }
    public static boolean verify_ZKPK(BigInteger grlpi, BigInteger M, BigInteger daierta, BigInteger F, BigInteger a1) {
        BigInteger n = new BigInteger(Hash.sha256(grlpi.toString() + M.toString() + daierta.toString()));
        return ((M.modPow(n, F).multiply(daierta)).mod(F)).compareTo(grlpi.modPow(a1, F)) == 0;
    }

}
