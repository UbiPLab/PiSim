package com.pisim.nsp.encryption;

import it.unisa.dia.gas.plaf.jpbc.util.io.Base64;
import com.pisim.nsp.parameterUtil.parameter;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;



public class RSA {
    public RSA() {
    }

    /**
     * @return 返回公私钥和模组成的hashMap，均使用Base64编码
     * @throws Exception
     */
    public static Map<String, String> generateRsaKeyPair() throws Exception {
        // 获取安全随机数
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        // 获取密钥对
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(parameter.RsuKeySize, secureRandom);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //获取公私钥，并转化为BASE64编码，存储到MAP中
        Key publicKey = keyPair.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        String publicKeyString = Base64.encodeBytes(publicKeyBytes);
        Key privateKey = keyPair.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        String privateKeyString = Base64.encodeBytes(privateKeyBytes);
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("publicKey", publicKeyString);
        keyMap.put("privateKey", privateKeyString);
        //获取模数n，并且转化为BASE64编码，存储到MAP中
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        BigInteger n = rsaPublicKey.getModulus();
        byte[] nBytes = n.toByteArray();
        String nString = Base64.encodeBytes(nBytes);
        keyMap.put("modulus", nString);
        return keyMap;
    }

    /**
     * rsa加密
     * @param input     要加密的字符串
     * @param publicKey Base64编码的公钥
     * @return Base64编码后的加密结果字符串
     * @throws Exception
     */
    public static String encrypt(String input, String publicKey) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey1 = keyFactory.generatePublic(keySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey1);
        byte[] temp = input.getBytes();
        String outPut = Base64.encodeBytes(cipher.doFinal(temp));
        return outPut;
    }

    /**
     * rsa解密
     *
     * @param input      要解密的Base64编码的字符串
     * @param privateKey Base64编码的私钥
     * @return Base64编码的解密结果
     * @throws Exception
     */
    public static String decrypt(String input, String privateKey) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey1 = keyFactory.generatePrivate(keySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey1);
        byte[] temp = Base64.decode(input);
        String output = new String(Base64.decode( Base64.encodeBytes(cipher.doFinal(temp))));
        return output;
    }

    /**
     * RSA签名
     *
     * @param input      Base64编码的要签名的字符串
     * @param privateKey Base64编码的私钥
     * @return Base64编码的签名结果
     * @throws Exception
     */
    public static String signature(String input, String privateKey) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey1 = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("SHA256WithRSA");
        signature.initSign(privateKey1);
        signature.update(input.getBytes("UTF-8"));
        byte[] signed = signature.sign();
        String output = Base64.encodeBytes(signed);
        return output;
    }

    /**
     * RSA验证签名
     *
     * @param input     Base64编码的要签名的字符串
     * @param sign      Base64编码的签名结果
     * @param publicKey Base64编码的公钥
     * @return Boolean
     * @throws Exception
     */
    public static boolean verify(String input, String sign, String publicKey) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey1 = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("SHA256WithRSA");
        signature.initVerify(publicKey1);
        signature.update(input.getBytes("UTF-8"));
        boolean verifyFlag = signature.verify(Base64.decode(sign));
        return verifyFlag;
    }
}
