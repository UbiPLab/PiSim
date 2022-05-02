package encryption;

import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.math.BigInteger;
import java.security.*;

import java.util.HashMap;
import java.util.Map;

import static encryption.cl_signature.generateClSignKeyPair;
import static encryption.cl_signature.signature;

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
        long endTime = System.nanoTime();
        System.out.println("生成零知识证明Opaak的参数耗时：" + (endTime - startTime));
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
        s = (BigInteger.ONE.add(BigInteger.ONE)).pow(4096);
        BigInteger k = BigInteger.ONE.add(BigInteger.ONE);
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
            k = k.add(BigInteger.ONE.add(BigInteger.ONE));
        }
        Map<String, BigInteger> pq = new HashMap<>();
        pq.put("rou", q);
        pq.put("F", p);
        Map_ZKPK_Params.put("rou", q);
        Map_ZKPK_Params.put("F", p);
        return pq;
    }

    // 生成一个零知识证明用的签名
    public static Map<String, BigInteger> generateSign(String uk) {
        // 生成cl-signature中的TA的公私钥 cl_signature中用户没有密钥
        Map_TA_Cl_Key = generateClSignKeyPair();
        //生成签名结果(v,e,s)
        Map_TA_Cl_Sign = signature(uk, Map_TA_Cl_Key.get("PK_d1"), Map_TA_Cl_Key.get("PK_d2"), Map_TA_Cl_Key.get("PK_d3"), Map_TA_Cl_Key.get("PK_n"), Map_TA_Cl_Key.get("SK_p"));
        //用户保存签名结果(v,e,s)
        System.out.println("e:" + Map_TA_Cl_Sign.get("e") + "\ne:" + Map_TA_Cl_Sign.get("v") + "\ne:" + Map_TA_Cl_Sign.get("s"));
        return Map_TA_Cl_Sign;
    }

    // 生成一个零知识证明用的签名
    public static Map<String, BigInteger> generateZKPKSign(BigInteger si, BigInteger PK_d1, BigInteger PK_d2, BigInteger PK_d3, BigInteger PK_n, BigInteger SK_p) {
        //生成签名结果(v,e,s)
        Map_TA_Cl_Sign = signature(si, PK_d1, PK_d2, PK_d3, PK_n, SK_p);
        return Map_TA_Cl_Sign;
    }

    // 生成零知识证明
    public static Map<String, BigInteger> generateZPKP(String input, String uk) {
        Pairing pairing = PairingFactory.getPairing("./config/a.properties");
        // 将uk哈希后转化为大整数
        BigInteger bigInteger_uk = new BigInteger(Hash.sha256(uk));
        BigInteger grnym = pairing.getZr().newElement().setFromHash(input.getBytes(), 0, input.getBytes().length).toBigInteger();
        BigInteger rnym = grnym.modPow(bigInteger_uk.multiply(Map_ZKPK_Params.get("b")), Map_ZKPK_Params.get("F"));
        // 计算A^e并存储到ZKPK中
        Map<String, BigInteger> ZKPK_Map = new HashMap<>();
        BigInteger Ae = Map_TA_Cl_Sign.get("v").modPow(Map_TA_Cl_Sign.get("e"), Map_TA_Cl_Key.get("PK_n"));
        ZKPK_Map.put("Ae", Ae);
        ZKPK_Map.put("s", Map_TA_Cl_Sign.get("s"));
        ZKPK_Map.put("rnym", rnym);
//        ZKPK_Map.put("grnym",grnym);
        Map_ZKPK = ZKPK_Map;
        return ZKPK_Map;
    }

    public static Map<String, BigInteger> generateZPKP(String input, BigInteger si, Field Zr, Map<String, BigInteger> TAtoDriver_ClSign, BigInteger b, BigInteger F, BigInteger PK_n) {
        BigInteger grlpi = Zr.newElement().setFromHash(input.getBytes(), 0, input.getBytes().length).toBigInteger();
        BigInteger rlpi = grlpi.modPow(si.multiply(b), F);

        BigInteger grlpi2 = Zr.newElement().setFromHash("11".getBytes(), 0, "11".getBytes().length).toBigInteger();
        BigInteger rlpi2 = grlpi2.modPow(si.multiply(b), F);
        BigInteger grlpi3 = Zr.newElement().setFromHash("22".getBytes(), 0, "22".getBytes().length).toBigInteger();
        BigInteger rlpi3 = grlpi3.modPow(si.multiply(b), F);

        System.out.println("***" + rlpi3);
        System.out.println(rlpi2);

        // 计算A^e并存储到ZKPK中
        Map<String, BigInteger> ZKPK_Map = new HashMap<>();
        BigInteger Ae = TAtoDriver_ClSign.get("v").modPow(TAtoDriver_ClSign.get("e"), PK_n);
        ZKPK_Map.put("Ae", Ae);
        ZKPK_Map.put("s", TAtoDriver_ClSign.get("s"));
        ZKPK_Map.put("rlpi", rlpi);
        return ZKPK_Map;
    }

    public static boolean verifyZKPK(String input, String uk) {
        BigInteger bigInteger_uk = new BigInteger(Hash.sha256(uk));
        //生成验证用的中间量
        BigInteger ruksvz = ((Map_TA_Cl_Key.get("PK_d1").modPow(bigInteger_uk, Map_TA_Cl_Key.get("PK_n")).multiply(Map_TA_Cl_Key.get("PK_d2").modPow(Map_ZKPK.get("s"), Map_TA_Cl_Key.get("PK_n")))).multiply(Map_TA_Cl_Key.get("PK_d3"))).mod(Map_TA_Cl_Key.get("PK_n"));
        if (Map_ZKPK.get("Ae").mod(Map_TA_Cl_Key.get("PK_n")).compareTo(ruksvz) == 0) {
            Pairing pairing = PairingFactory.getPairing("./config/a.properties");
            BigInteger grnym = pairing.getZr().newElement().setFromHash(input.getBytes(), 0, input.getBytes().length).toBigInteger();
            if (Map_ZKPK.get("rnym").compareTo(grnym.modPow(bigInteger_uk.multiply(Map_ZKPK_Params.get("b")), Map_ZKPK_Params.get("F"))) == 0) {
                System.out.println("零知识证明成功");
            } else {
                System.out.println("证明失败");
            }
        } else {
            System.out.println("证明失败");
        }
        return false;
    }

    public static boolean verifyZKPK(String input, Map<String, BigInteger> Map_ZKPK, BigInteger si, BigInteger PK_d1, BigInteger PK_d2, BigInteger PK_d3, BigInteger PK_n, BigInteger b, BigInteger F) {
        //生成验证用的中间量
        BigInteger ruksvz = ((PK_d1.modPow(si, PK_n).multiply(PK_d2.modPow(Map_ZKPK.get("s"), PK_n))).multiply(PK_d3)).mod(PK_n);
        if (Map_ZKPK.get("Ae").mod(PK_n).compareTo(ruksvz) == 0) {
            Pairing pairing = PairingFactory.getPairing("./config/a.properties");
            BigInteger grlpi = pairing.getZr().newElement().setFromHash(input.getBytes(), 0, input.getBytes().length).toBigInteger();
            if (Map_ZKPK.get("rlpi").compareTo(grlpi.modPow(si.multiply(b), F)) == 0) {
                System.out.println("零知识证明成功");
                return true;
            } else {
                System.out.println("证明失败");
            }
        } else {
            System.out.println("证明失败");
        }
        return false;
    }

    /**
     * @param si           对si进行0知识证明
     * @param F            F是要mod的大素数
     * @param grlpi            grlpi
     * @param secureRandom 随机数生成器
     * @return 返回一个Map 内容为要发送给验证放的数据
     */
    public static Map<String, BigInteger> generate_ZKPK(BigInteger si, BigInteger F,BigInteger grlpi, SecureRandom secureRandom) {
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

//    public static void main(String[] args) {
//        SecureRandom secureRandom = new SecureRandom();
//        BigInteger si = new BigInteger(256, secureRandom);
//        Map<String, BigInteger> pq = gen_pq(256);
//        BigInteger F = pq.get("F");
//        //TestZKPK(si);
////        long startTime = System.nanoTime();
////        getZKPKParameters();
////        String input = "1235646";
////        String uk = "456789";
////        generateSign(uk);
////        generateZPKP(input, uk);
////        verifyZKPK(input, uk);
////        long endTime = System.nanoTime();
////        System.out.println("零知识证明费时：" + (endTime - startTime) / 1000000 + "ms");
//    }
}
