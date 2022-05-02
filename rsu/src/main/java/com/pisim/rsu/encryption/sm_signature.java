package com.pisim.rsu.encryption;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

//用对双消息的签名做个示例

public class sm_signature {
    public static Field G1;
    public static Field G2;
    public static Pairing pairing;

    //密钥生成
    public static Map<String, Object> generate_key(){
        SecureRandom secureRandom = new SecureRandom();
        BigInteger x = new BigInteger(256,secureRandom);
        BigInteger y1 = new BigInteger(256,secureRandom);
        BigInteger y2 = new BigInteger(256,secureRandom);
        pairing = PairingFactory.getPairing("./config/a.properties");
        G1 = pairing.getG1();
        G2 = pairing.getG2();
        Element g = G2.newRandomElement();
        Element X = g.pow(x);
        Element Y1 = g.pow(y1);
        Element Y2 = g.pow(y2);
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("pk_g",g);
        keyMap.put("pk_X",X);
        keyMap.put("pk_Y1",Y1);
        keyMap.put("pk_Y2",Y2);
        keyMap.put("sk_x",x);
        keyMap.put("sk_y1",y1);
        keyMap.put("sk_y2",y2);
        return keyMap;
    }
    public static Map<String, Object> smSignature(BigInteger m1, BigInteger m2, BigInteger x, BigInteger y1, BigInteger y2){
        Map<String, Object> signMap = new HashMap<>();
        Element h = G1.newRandomElement();
        signMap.put("h",h);
        BigInteger temp = m1.multiply(y1).add(m2.multiply(y2));
        signMap.put("h1",h.pow(x.add(temp)));
        return signMap;
    }
    public static boolean smVerify(BigInteger m1, BigInteger m2, Map<String, Object> signMap, Element X, Element Y1, Element Y2, Element g){
        Element temp1 = pairing.pairing((Element) signMap.get("h"),X.mul(Y1.pow(m1)).mul(Y2.pow(m2)));
        Element temp2 = pairing.pairing((Element)signMap.get("h1"),g);
        if (temp1.isEqual(temp2)){
            return true;
        }else {
            return false;
        }
    }


}
