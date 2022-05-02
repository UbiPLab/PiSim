package com.example.secureserver.encryption;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class cl_signature {
    //生成公私钥
    public static Map<String, BigInteger> generateClSignKeyPair() {
        long startTime = System.nanoTime();
        SecureRandom secureRandom = new SecureRandom();
        //生成两个随机大素数p/q p/q是素数的概率为 1-(1/2)^100
        BigInteger q = new BigInteger(1024, 100, secureRandom);
        BigInteger p = new BigInteger(1024, 100, secureRandom);
        //n = p*q
        BigInteger n = p.multiply(q);
        System.out.println(n.bitLength());
        //二次剩余 x^2=a mod(n) 有解 a就是n的二次剩余 随机选择x 计算n的二次剩余d1 d2 d3
        BigInteger x = new BigInteger(1024, secureRandom);
        BigInteger d1 = x.modPow(BigInteger.ONE.add(BigInteger.ONE), n);
        x = new BigInteger(1024, secureRandom);
        BigInteger d2 = x.modPow(BigInteger.ONE.add(BigInteger.ONE), n);
        x = new BigInteger(1024, secureRandom);
        BigInteger d3 = x.modPow(BigInteger.ONE.add(BigInteger.ONE), n);
        //输出公私钥 q可以用n除一下得到
//        System.out.println("公钥PK:" + "\nn:" + n + "\nd1:" + d1 + "\nd2:" + d2 + "\nd3:" + d3);
//        System.out.println("私钥SK:" + "\np:" + p);
        //将公私钥存入Map
        Map<String, BigInteger> clKeyMap = new HashMap<>();
        clKeyMap.put("PK_n", n);
        clKeyMap.put("PK_d1", d1);
        clKeyMap.put("PK_d2", d2);
        clKeyMap.put("PK_d3", d3);
        clKeyMap.put("SK_p", p);
        long endTime = System.nanoTime();
        //System.out.println("生成CL-signature密钥耗时：" + (endTime - startTime));
        return clKeyMap;
    }

    //签名 (v,e,s) v^e≡d1^m*d2^sd3 mod n
    public static Map<String, BigInteger> signature(String message, BigInteger d1, BigInteger d2, BigInteger d3, BigInteger n, BigInteger p) {
        long startTime = System.nanoTime();
        SecureRandom secureRandom = new SecureRandom();
        // 随机生成素数e e的长度大于消息长度+1
        BigInteger e = new BigInteger(258, 100, secureRandom);
        // 随机生成s s的长度为 ln+le+l l是安全参数 此处随机生成
        int l = secureRandom.nextInt(100);
        //取绝对值
        l = Math.abs(l);
        BigInteger s = new BigInteger(n.bitLength() + e.bitLength() + 5000, secureRandom);
        // 计算v的过程实际上就是rsa n分解攻击的过程
        //计算q
        BigInteger q = n.divide(p);
        // 计算d 基本RSA中(d,n)为私钥 e*d = 1 mod L
        BigInteger L = (p.subtract(BigInteger.ONE)).multiply((q.subtract(BigInteger.ONE)));
        BigInteger d = e.modInverse(L);
        // 利用sha256加密消息
        BigInteger m = new BigInteger(Hash.sha256(message));
        //计算 v = (d1^m*d2^sd3)^d mod n
        BigInteger temp =  ((d1.modPow(m, n).multiply(d2.modPow(s, n))).multiply(d3)).mod(n);
        BigInteger v = temp.modPow(d,n);
        Map<String, BigInteger> signMap = new HashMap<>();
        signMap.put("v", v);
        signMap.put("e", e);
        signMap.put("s", s);
        long endTime = System.nanoTime();
        //System.out.println("生成CL-signature 签名耗时：" + (endTime - startTime));
        return signMap;
    }
    //签名 (v,e,s) v^e≡d1^m*d2^sd3 mod n
    public static Map<String, BigInteger> signature(BigInteger si, BigInteger d1, BigInteger d2, BigInteger d3, BigInteger n, BigInteger p) {
        long startTime = System.nanoTime();
        SecureRandom secureRandom = new SecureRandom();
        // 随机生成素数e e的长度大于消息长度+1
        BigInteger e = new BigInteger(258, 100, secureRandom);
        // 随机生成s s的长度为 ln+le+l l是安全参数 此处随机生成
        int l = secureRandom.nextInt(100);
        //取绝对值
        l = Math.abs(l);
        BigInteger s = new BigInteger(n.bitLength() + e.bitLength() + 5000, secureRandom);
        // 计算v的过程实际上就是rsa n分解攻击的过程
        //计算q
        BigInteger q = n.divide(p);
        // 计算d 基本RSA中(d,n)为私钥 e*d = 1 mod L
        BigInteger L = (p.subtract(BigInteger.ONE)).multiply((q.subtract(BigInteger.ONE)));
        BigInteger d = e.modInverse(L);
        // 利用sha256加密消息
        //计算 v = (d1^si*d2^sd3)^d mod n
        BigInteger temp =  ((d1.modPow(si, n).multiply(d2.modPow(s, n))).multiply(d3)).mod(n);
        BigInteger v = temp.modPow(d,n);
        Map<String, BigInteger> signMap = new HashMap<>();
        signMap.put("v", v);
        signMap.put("e", e);
        signMap.put("s", s);
        long endTime = System.nanoTime();
        //System.out.println("生成CL-signature 签名耗时：" + (endTime - startTime));
        return signMap;
    }

    // 验证签名
    public static boolean verify(String message, BigInteger d1, BigInteger d2, BigInteger d3, BigInteger n, BigInteger v, BigInteger e, BigInteger s) {
        BigInteger m = new BigInteger(Hash.sha256(message));
        if (v.modPow(e, n).compareTo(((d1.modPow(m, n).multiply(d2.modPow(s, n))).multiply(d3)).mod(n)) == 0) {
            if (e.compareTo(BigInteger.ONE.add(BigInteger.ONE).pow(e.bitLength() - 1)) > 0) {
                System.out.println("验证成功");
                return true;
            }
            System.out.println("验证失败");
        }
        System.out.println("验证失败");
        return false;
    }


}
