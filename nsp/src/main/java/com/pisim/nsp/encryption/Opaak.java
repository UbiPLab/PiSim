package com.pisim.nsp.encryption;

import java.math.BigInteger;
import java.util.Map;


public class Opaak {
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
