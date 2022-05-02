package com.pisim.nsp.encryption;

import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Hash {
    public static String sha256(String bigInteger) {
        long startTime = System.nanoTime();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            //生成待加密字符串
            String input = bigInteger;//.toString();
            //执行加密
            for (int i = 0; i < 1000; i++) {
                messageDigest.update(input.getBytes());
            }
            //获取并输出加密结果
            BigInteger out = new BigInteger(1, messageDigest.digest());
            long endTime = System.nanoTime();
           // System.out.println("哈希算法耗时:"+(endTime - startTime));
            //返回加密结果
            //System.out.println("哈希结果"+out.toString());
            return out.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
    public static byte[] sha256_byte(String input) {
        long startTime = System.nanoTime();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            // 调用digest方法，进行加密操作
            byte[] cipherBytes = messageDigest.digest(input.getBytes());
            return cipherBytes;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Hmac sha 256
    public static String HMACSHA256(String data, byte[] key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key, "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toLowerCase();
    }

}
