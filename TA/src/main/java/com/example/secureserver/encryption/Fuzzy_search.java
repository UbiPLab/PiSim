package com.example.secureserver.encryption;

import com.example.secureserver.parameterUtil.parameter;
import com.example.secureserver.parameterUtil.parameterLength;
import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;

import java.util.Random;


public class Fuzzy_search {
    public static Matrix V;
    public static Matrix SK1;
    public static Matrix SK2;
    public static Matrix HKP;


    // 生成模糊搜索密钥SK(SK1,SK2,V)
    public static Matrix generateSearchKey() {
        int k1, k2;
        k1 = parameterLength.k1;
        k2 = 1;
        Random random = new Random();
        // 生成SK1和SK2
        SK1 = DenseMatrix.Factory.zeros(k1, k1);
        SK2 = DenseMatrix.Factory.zeros(k1, k1);
        for (int i = 0; i < k1; i++) {
            for (int j = 0; j < k1; j++) {
                SK1.setAsBoolean(random.nextBoolean(), i, j);
                SK2.setAsBoolean(random.nextBoolean(), i, j);
            }
        }
        V = DenseMatrix.Factory.rand(1, k1);
        for (int i = 0; i < k1; i++) {
            V.setAsBoolean(random.nextBoolean(), 0, i);
        }
        HKP = DenseMatrix.Factory.zeros(k2, k1);
        for (int j = 0; j < k1; j++) {
            HKP.setAsBoolean(random.nextBoolean(), 0, j);
        }
        parameter.SK1 = SK1;
        parameter.SK2 = SK2;
        parameter.V = V;
        parameter.HKP = HKP;
        return null;
    }
}
