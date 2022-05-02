package encryption;

import Navi_utils.LocationBean;
import ch.hsr.geohash.GeoHash;
import parameter.parameter;

import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;

import java.util.*;

public class Fuzzy_search {
    private static Matrix HKP;
    private static Matrix SK1_I1;
    private static Matrix SK2_I2;
    private static Matrix SK1_Q1;
    private static Matrix SK2_Q2;

    //对位置生成索引向量
    public static double[] BuildIndex(LocationBean locationBean, double[] key) {
        GeoHash geoHash = GeoHash.withCharacterPrecision(locationBean.getLat(),locationBean.getLng(),7);
        String temp_byte = geoHash.toBinaryString();
        //        for (int j = 0; j < temp.length(); j++) {
//            if (Character.isDigit(temp.charAt(j))) {
//                result[(int)temp.charAt(j) - 48] =result[temp.charAt(j) - 48] + 10;
////                result[(int)temp.charAt(j) - 48] = 10;
//            } else {
//                result[(int) temp.charAt(j) - 88] = result[(int)temp.charAt(j) - 88] + 10;
////                result[(int)temp.charAt(j) - 88] = 10;
//            }
//        }
  //      System.out.println(Arrays.toString(result));
        return Hash.encryption_index(temp_byte,key);
    }

    //通过S将I分割成I`和I``，然后用SK1和SK2加密
    //新版本加密方式
    public static Map<String, Matrix> Enc_Index(Matrix SK1, Matrix SK2, Matrix V, double[] I_temp) {
        double[] I = new double[parameter.k1];
        for (int i = 0; i < I.length; i++) {
            I[i] = I_temp[i];
        }
        double[] I1 = new double[I.length];
        double[] I2 = new double[I.length];
        Random random = new Random();
        for (int i = 0; i < V.getColumnCount(); i++) {
            if (V.getAsBoolean(0, i)) {
                //I`[t] = I``[t] =I[t]
                I1[i] = I2[i] = I[i];
            } else {
                //生成100以内的随机数
                double temp = random.nextInt(100);
                // 这里直接整数处以2等整数了，存在0.5情况导致信息丢失
                I1[i] = I[i] / 2 + temp;
                I2[i] = I[i] / 2 - temp;
            }
        }
        Matrix SK1_I1 = SK1.transpose().mtimes(DenseMatrix.Factory.importFromArray(I1).transpose());
        Matrix SK2_I2 = SK2.transpose().mtimes(DenseMatrix.Factory.importFromArray(I2).transpose());
        // 将索引加密结果保存到EncIndex
        Map<String, Matrix> EncIndex = new HashMap<>();
        EncIndex.put("SK1_I1", SK1_I1);
        EncIndex.put("SK2_I2", SK2_I2);
        return EncIndex;
    }

    // 对查询向量进行加密
    //新版本加密方式
    public static Map<String, Matrix> Enc_Query(Matrix SK1, Matrix SK2, Matrix V, double[] Q) {
        double[] Q1 = new double[Q.length];
        double[] Q2 = new double[Q.length];
        Random random = new Random();
        for (int i = 0; i < V.getColumnCount(); i++) {
            if (V.getAsBoolean(0, i)) {
                //生成100以内的随机数
                double temp = random.nextInt(100);
                // 这里直接整数处以2等整数了，存在0.5情况导致信息丢失
                Q1[i] = Q[i] / 2 + temp;
                Q2[i] = Q[i] / 2 - temp;
            } else {
                Q1[i] = Q2[i] = Q[i];
            }
        }
        //对查询向量进行加密
        Matrix SK1_Q1 = SK1.inv().mtimes(DenseMatrix.Factory.importFromArray(Q1).transpose());
        Matrix SK2_Q2 = SK2.inv().mtimes(DenseMatrix.Factory.importFromArray(Q2).transpose());
        // 将索引加密结果保存到EncIndex
        Map<String, Matrix> EncQuery = new HashMap<>();
        EncQuery.put("SK1_Q1", SK1_Q1);
        EncQuery.put("SK2_Q2", SK2_Q2);
        return EncQuery;
    }

    // 对查询语句生成陷门
    // 新版本生成方式
    public static Map<String, Matrix> trapDoor(Matrix SK1, Matrix SK2, Matrix V, Matrix HKP, LocationBean locationBean) {
        double[] vector = BuildIndex(locationBean,HKP.toDoubleArray()[0]);
        double temp = 0;
        for (double value : vector) {
            temp = temp + value * value;
        }
        parameter.thresholdQuery = temp;
        //   System.out.println(Arrays.toString(vector));
        Map<String, Matrix> EncSK;
        EncSK = Enc_Query(SK1, SK2, V, vector);
        return EncSK;
    }

    //进行查询，计算安全索引向量Encsk(I)和陷门函数EncSK(Q)之间的内积
    public static void Search(Map<String, Matrix> EncIndex, Map<String, Matrix> EncSK) {
        Matrix eee = (EncIndex.get("SK1_I1").transpose()).mtimes(EncSK.get("SK1_Q1")).plus((EncIndex.get("SK2_I2").transpose()).mtimes(EncSK.get("SK2_Q2")));
        System.out.println("求得内积结果：" + eee.getAsLong(0, 0));
    }

    /**
     * @param locationBeans 经纬度列表
     * @param flag          如果flag==true 就是请求司机的EncIndex 否则是贡献司机EncQuery，贡献司机的经纬度列表只有一项
     * @return Index_EncSK(KI) / Index_EncQu(KI)
     */
    public static double[][] generate_IndexEncTable(List<LocationBean> locationBeans, Matrix SK1, Matrix SK2, Matrix V, boolean flag) {
        double[][] EncKI = new double[(int) V.getColumnCount()][locationBeans.size() * 2];
        if (flag) {
            for (int i = 0; i < locationBeans.size() * 2; i = i + 2) {
                double[] I = BuildIndex(locationBeans.get(i / 2), HKP.toDoubleArray()[0]);
                Map<String, Matrix> EncIndex = Enc_Index(SK1, SK2, V, I);
                Matrix SK1_I11 = EncIndex.get("SK1_I1");
                Matrix SK2_I22 = EncIndex.get("SK2_I2");
                SK1_I1 = SK1_I11;
                SK2_I2 = SK2_I22;
                for (int j = 0; j < V.getColumnCount(); j++) {
                    EncKI[j][i] = SK1_I11.getAsDouble(j, 0);
                    EncKI[j][i + 1] = SK2_I22.getAsDouble(j, 0);
                }
            }
        } else {
            for (int i = 0; i < locationBeans.size() * 2; i = i + 2) {
                Map<String, Matrix> EncIndex = trapDoor(SK1, SK2, V, HKP, locationBeans.get(0));
                Matrix SK1_Q11 = EncIndex.get("SK1_Q1");
                Matrix SK2_Q22 = EncIndex.get("SK2_Q2");
                SK1_Q1 = SK1_Q11;
                SK2_Q2 = SK2_Q22;
                for (int j = 0; j < V.getColumnCount(); j++) {
                    EncKI[j][i] = SK1_Q11.getAsDouble(j, 0);
                    EncKI[j][i + 1] = SK2_Q22.getAsDouble(j, 0);
                }
            }
        }
        return EncKI;
    }
}