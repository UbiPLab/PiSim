package com.example.secureserver.utils;

import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;

public class ParamsToString {


    public static String SKToString(Matrix SK, int row, int cloumn) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < cloumn; j++) {
                if (SK.getAsBoolean(i, j)) {
                    temp.append("1");
                } else {
                    temp.append("0");
                }
            }
        }
        return temp.toString();
    }

    public static Matrix StringToSK(String SK_str, int row, int cloumn) {
        Matrix SK_temp = DenseMatrix.Factory.zeros(row, cloumn);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < cloumn; j++) {
                if (SK_str.charAt(i * cloumn + j) == '1') {
                    SK_temp.setAsBoolean(true, i, j);
                } else {
                    SK_temp.setAsBoolean(false, i, j);
                }
            }
        }
        return SK_temp;
    }
}
