package Navi_utils;


import encryption.AES;
import encryption.Hash;
import encryption.RSA;
import it.unisa.dia.gas.jpbc.Element;
import parameter.parameter;

import com.alibaba.fastjson.JSONObject;

import org.ujmp.core.Matrix;
import org.ujmp.core.util.Base64;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

import static encryption.AES.getStrKeyAES;
import static encryption.Fuzzy_search.*;
import static encryption.Opaak.generate_ZKPK;
import static parameter.parameter.*;
import static com.example.mygaode.utils.Util.xor;

public class driver_generate {
    public static JSONObject generate_registerDate(String username, String password, String idNumber, String idCar, String si, String driverPriKey, String driverPubKey, String serverPubKey) {
        try {
            //生成ni 和ni2(ni上多一波浪线)
            BigInteger si_ = new BigInteger(si);
            Element ni = parameter.gpk_g2.pow(si_).getImmutable();
            Element ni2 = parameter.gpk_B.pow(si_).getImmutable();
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            data.put("idNumber", idNumber);
            data.put("idCar", idCar);
            data.put("ni", ni.toBytes());
            data.put("ni2", ni2.toBytes());
            return assembly(data, driverPriKey, driverPubKey, serverPubKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject generate_loginData(String username, String password, String driverPriKey, String driverPubKey, String serverPubKey) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            //对发送的数据用AES加密
            return assembly(data, driverPriKey, driverPubKey, serverPubKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param locationBeans 查询点的经纬度集合
     * @param si_str        司机秘密
     * @param te            时间历元
     * @param rci           请求次数
     * @param rou           零知识证明用ρ
     * @param F             零知识证明用F
     * @param Ei1           TA给司机的签名
     * @param Ei2           TA给司机的签名
     * @return 导航查询NQ
     */
    public static JSONObject generate_Query(List<LocationBean> locationBeans, Matrix SK1, Matrix SK2, Matrix V, String si_str, int te, int rci, BigInteger rou, BigInteger F, Element Ei1, Element Ei2) {
        try {
            SecureRandom secureRandom = new SecureRandom();
            BigInteger si = new BigInteger(si_str);
            //生成请求假名
            String temp = "" + te + rci;
            BigInteger grlpi_temp = pairing.getZr().newElement().setFromHash(temp.getBytes(), 0, temp.getBytes().length).toBigInteger();
            BigInteger grlpi = grlpi_temp.modPow(((F.subtract(BigInteger.ONE)).divide(rou)), F);
            BigInteger rlpi = grlpi.modPow(si, F);
            //生成零知识证明
            Map<String, BigInteger> ZKPK = generate_ZKPK(si, F, grlpi, secureRandom);
            //生成Mi
            double[][] Index_EncKI = generate_IndexEncTable(locationBeans, SK1, SK2, V, true);
            //生成其他数据
            //随机选择r1将(Ei1,Ei2)随机化
            BigInteger r1 = new BigInteger(r1r2_length, secureRandom);
            Element REi1 = Ei1.pow(r1).getImmutable();
            Element REi2 = Ei2.pow(r1).getImmutable();
            //随机选择r2 计算e(E0i1, B)^r2 ← e(Ei1, B)^r1r2
            BigInteger r2 = new BigInteger(r1r2_length, secureRandom);
            // 计算Ei = e(REi1, B)^r2
            Element Ei = pairing.pairing(REi1, gpk_B).pow(r2).getImmutable();
            //计算ci = H(REi1, REi2, e(REi1, B)^r2, Mi)
            //第二个temp 添加了零知识证明
       //     temp = REi1.toString() + REi2.toString() + Arrays.deepToString(Index_EncKI) + rlpi.toString() + rci + Ei.toString();
            temp = REi1.toString() + REi2.toString() + Arrays.deepToString(Index_EncKI) + rlpi.toString() + rci + Ei.toString() + ZKPK.get("daierta") + ZKPK.get("M") + ZKPK.get("a1");


            BigInteger ci = new BigInteger(Hash.sha256(temp));
            //计算ssi = r2 + cisi
            BigInteger ssi = r2.add(ci.multiply(si));
            //建立NQ 并发送给rsu


            //szh7h1zs  szh7h1mz
            JSONObject data = new JSONObject();

            //请求假名部分
            data.put("rlpi", rlpi);
            System.out.println(rlpi.bitLength());
            data.put("grlpi", grlpi);
            data.put("daierta", ZKPK.get("daierta"));
            data.put("M", ZKPK.get("M"));
            data.put("a1", ZKPK.get("a1"));
            data.put("te", te);
            //导航索引向量
            data.put("Index_EncKiI", Index_EncKI);
            data.put("threshold", threshold);
            data.put("count", locationBeans.size() * 2);
            //验证相关信息
            data.put("REi1", Base64.encodeBytes(REi1.toBytes()));
            data.put("rci", rci);
            data.put("REi2", Base64.encodeBytes(REi2.toBytes()));
            data.put("ci", ci);
            data.put("ssi", ssi);
            return assembly(data, driver_rsa_pri, driver_rsa_pub, RSU_rsa_pub);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将数据组装成要发送的json
     *
     * @param data         要发送的数据
     * @param driverPriKey 司机RSU私钥
     * @param driverPubKey 司机RSU公钥
     * @param serverPubKey 服务器公钥
     * @return 加密签名后要发送的数据
     */
    private static JSONObject assembly(JSONObject data, String driverPriKey, String driverPubKey, String serverPubKey) {
        try {
            JSONObject jsonObject = new JSONObject();
            //对发送的数据用AES加密
            String AESKey = getStrKeyAES();
            byte[] encData = AES.encryptAES(data.toJSONString().getBytes(), AESKey);
            //用服务器公钥加密AES对称密钥
            String encAESKey = RSA.encrypt(AESKey, serverPubKey);
            //对加密后的对称密钥签名 并添加入json
            jsonObject.put("sign", RSA.signature(encAESKey, driverPriKey));
            jsonObject.put("encData", Base64.encodeBytes(encData));
            jsonObject.put("encAESKey", encAESKey);
            //在登录注册时，这里driver_pub是driver的，请求RSU时，就是device的
            jsonObject.put("driver_pub", driverPubKey);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @param locationBean 司机当前位置索引
     * @param Mj           司机之间握手生成的集合
     * @param si_str       司机秘密
     * @param te           时间历元
     * @param rci          请求次数
     * @param rou          零知识证明用ρ
     * @param F            零知识证明用F
     * @param Ei1          TA给司机的签名
     * @param Ei2          TA给司机的签名
     * @return 交通报告DR
     */
    public static JSONObject generate_report(LocationBean locationBean, Map<String, Object> Mj, Matrix SK1, Matrix SK2, Matrix V, String si_str, int te, int rci, BigInteger rou, BigInteger F, Element Ei1, Element Ei2) {
        try {
            BigInteger si = new BigInteger(si_str);
            SecureRandom secureRandom = new SecureRandom();
            List<LocationBean> locationBeans = new ArrayList<>();
            locationBeans.add(locationBean);
            //生成请求假名
            String temp = "" + te + rci;
            BigInteger grlpi_temp = pairing.getZr().newElement().setFromHash(temp.getBytes(), 0, temp.getBytes().length).toBigInteger();
            BigInteger grlpi = grlpi_temp.modPow(((F.subtract(BigInteger.ONE)).divide(rou)), F);
            BigInteger rlpi = grlpi.modPow(si, F);
            //生成零知识证明
            Map<String, BigInteger> ZKPK = generate_ZKPK(si, F, grlpi, secureRandom);
            double[][] Query_EncKI = generate_IndexEncTable(locationBeans, SK1, SK2, V, false);
            //生成其他数据
            //随机选择r1将(Ei1,Ei2)随机化
            BigInteger r1 = new BigInteger(r1r2_length, secureRandom);
            Element REi1 = Ei1.pow(r1).getImmutable();
            Element REi2 = Ei2.pow(r1).getImmutable();
            //随机选择r2 计算e(E0i1, B)^r2 ← e(Ei1, B)^r1r2
            BigInteger r2 = new BigInteger(r1r2_length, secureRandom);
            // 计算Ei = e(REi1, B)^r2
            Element Ei = pairing.pairing(REi1, gpk_B).pow(r2).getImmutable();
            //计算ci = H(REi1, REi2, e(REi1, B)^r2, Mi)
            short indj = (short) Mj.get("indj");
            //将要传递的数据序列化
            byte[] xor_hsvjs = (byte[]) Mj.get("xor_hsvjs");
            byte[] hsvj = (byte[]) Mj.get("hsvj");

            //第二个temp添加了零知识证明
//            temp = REi1.toString() + REi2.toString() +
//                    indj + Objects.requireNonNull(Mj.get("pidjs")).toString() + Arrays.toString(xor_hsvjs) + Arrays.toString(hsvj) +
//                    Arrays.deepToString(Query_EncKI) + rci + Ei.toString();
            temp = REi1.toString() + REi2.toString() +
                    indj + Objects.requireNonNull(Mj.get("pidjs")).toString() + Arrays.toString(xor_hsvjs) + Arrays.toString(hsvj) +
                    Arrays.deepToString(Query_EncKI) + rci + Ei.toString() + ZKPK.get("daierta") + ZKPK.get("M") + ZKPK.get("a1");

            BigInteger ci = new BigInteger(Hash.sha256(temp));
            //计算ssi = r2 + cisi
            BigInteger ssi = r2.add(ci.multiply(si));


            JSONObject data = new JSONObject();

            data.put("indj", Mj.get("indj"));
            data.put("pidjs", Mj.get("pidjs"));
            System.out.println(Mj.get("pidjs"));
            data.put("pidj", Mj.get("pidj"));
            data.put("hsvj", Mj.get("hsvj"));
            data.put("xor_hsvjs", Mj.get("xor_hsvjs"));
            //请求假名部分
            data.put("rlpi", rlpi);
            data.put("grlpi", grlpi);
            data.put("daierta", ZKPK.get("daierta"));
            data.put("M", ZKPK.get("M"));
            data.put("a1", ZKPK.get("a1"));
            data.put("te", te);
            //导航索引向量
            data.put("Query_EncKI", Query_EncKI);
            data.put("count", 2);
            data.put("thresholdQuery", thresholdQuery);
            //验证相关信息
            data.put("REi1", Base64.encodeBytes(REi1.toBytes()));
            data.put("rci", rci);
            data.put("REi2", Base64.encodeBytes(REi2.toBytes()));
            data.put("ci", ci);
            data.put("ssi", ssi);
            return assembly(data, driver_rsa_pri, driver_rsa_pub, RSU_rsa_pub);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param locationBeans 经纬度列表
     * @param flag          如果flag==true 就是请求司机的EncIndex 否则是贡献司机EncQuery，贡献司机的经纬度列表只有一项
     * @return Index_EncSK(KI) / Index_EncQu(KI)
     */
    private static double[][] generate_IndexEncTable(List<LocationBean> locationBeans, Matrix SK1, Matrix SK2, Matrix V, boolean flag) {
        double[][] EncKI = new double[(int) V.getColumnCount()][locationBeans.size() * 2];
        threshold = new double[locationBeans.size()];
        double temp = 0;
        if (flag) {
            for (int i = 0; i < locationBeans.size() * 2; i = i + 2) {
                double[] I = BuildIndex(locationBeans.get(i / 2), HKP.toDoubleArray()[0]);
                for (double value : I) {
                    temp = temp + value * value;
                }
                threshold[i / 2] = temp;
                temp = 0;
                Map<String, Matrix> EncIndex = Enc_Index(SK1, SK2, V, I);
                Matrix SK1_I11 = EncIndex.get("SK1_I1");
                Matrix SK2_I22 = EncIndex.get("SK2_I2");
                for (int j = 0; j < V.getColumnCount(); j++) {
                    assert SK1_I11 != null;
                    assert SK2_I22 != null;
                    EncKI[j][i] = SK1_I11.getAsDouble(j, 0);
                    EncKI[j][i + 1] = SK2_I22.getAsDouble(j, 0);
                }
            }
        } else {
            for (int i = 0; i < locationBeans.size() * 2; i = i + 2) {
                Matrix HKP = parameter.HKP;
                Map<String, Matrix> EncIndex = trapDoor(SK1, SK2, V, HKP, locationBeans.get(0));
                Matrix SK1_Q11 = EncIndex.get("SK1_Q1");
                Matrix SK2_Q22 = EncIndex.get("SK2_Q2");
                for (int j = 0; j < V.getColumnCount(); j++) {
                    assert SK1_Q11 != null;
                    assert SK2_Q22 != null;
                    EncKI[j][i] = SK1_Q11.getAsDouble(j, 0);
                    EncKI[j][i + 1] = SK2_Q22.getAsDouble(j, 0);
                }
            }
        }
        return EncKI;
    }


    public static List<String> testGenerate_Pidjs() {
        List<String> pidjs = new ArrayList<>();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 10; i++) {
            pidjs.add(pidjss.get(secureRandom.nextInt(100)));
        }
        return pidjs;
    }

    public static List<byte[]> testGenerate_Hsvjs() {
        SecureRandom secureRandom = new SecureRandom();
        List<byte[]> hsvjs = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            hsvjs.add(parameter.hsvjss.get(secureRandom.nextInt(100)));
        }
        return hsvjs;
    }

    /**
     * @param K  K
     * @param te 时间历元
     * @return 返回自身hsvj
     */
    public static byte[] generate_hsvj(BigInteger K, int te) {
        try {
            byte[] hsvj = Hash.sha128_byte(K.toString() + te);
            hsvjs.add(hsvj);
            return hsvj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param indj  自身位置索引
     * @param hsvj  自身hsvj
     * @param pidjs 收到的pidj合
     * @param hsvjs 收到的hsvj合
     * @return Mj
     */
    public static Map<String, Object> generate_Mj(short indj, byte[] hsvj, byte[][] pidjs, List<byte[]> hsvjs) {
        try {
            Map<String, Object> Mj = new HashMap<>();
            byte[] bytes = new byte[hsvjs.get(0).length];
            //连续异或所有接收到的hsvj
            for (byte[] temp_hsvj : hsvjs) {
                bytes = xor(bytes, temp_hsvj);
            }
            //建立Mj
            Mj.put("indj", indj);
            Mj.put("pidjs", pidjs);
            //自己生成的hsvj
            Mj.put("hsvj", hsvj);
            //接收到的hsvj的异或结果
            Mj.put("xor_hsvjs", bytes);
            return Mj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param indj  自身位置索引
     * @param hsvj  自身hsvj
     * @param pidjs 收到的pidj合
     * @param hsvjs 收到的hsvj合
     * @return Mj
     */
    public static Map<String, Object> generate_Mj(short indj, byte[] hsvj, String pidj, List<String> pidjs, List<byte[]> hsvjs) {
        try {
            Map<String, Object> Mj = new HashMap<>();
            byte[] bytes = new byte[hsvjs.get(0).length];
            //连续异或所有接收到的hsvj
            for (byte[] temp_hsvj : hsvjs) {
                bytes = xor(bytes, temp_hsvj);
            }
            //建立Mj
            Mj.put("indj", indj);
            Mj.put("pidjs", pidjs);

            Mj.put("pidj", pidj);
            //自己生成的hsvj
            Mj.put("hsvj", hsvj);
            //接收到的hsvj的异或结果
            Mj.put("xor_hsvjs", bytes);
            return Mj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
