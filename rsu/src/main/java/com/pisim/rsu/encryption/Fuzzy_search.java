package com.pisim.rsu.encryption;

import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation;

public class Fuzzy_search {

    //进行查询，计算安全索引向量Encsk(I)和陷门函数EncSK(Q)之间的内积
    public static double Search(Matrix matrixI, Matrix matrixQ,int i) {
        Matrix SK1_I1 = matrixI.selectColumns(Calculation.Ret.NEW, i);
        Matrix SK2_I2 = matrixI.selectColumns(Calculation.Ret.NEW, i+1);
        Matrix SK1_Q1 = matrixQ.selectColumns(Calculation.Ret.NEW, 0);
        Matrix SK2_Q2 = matrixQ.selectColumns(Calculation.Ret.NEW, 1);
        Matrix result = ((SK1_I1.transpose()).mtimes(SK1_Q1)).plus(((SK2_I2).transpose()).mtimes(SK2_Q2));
        return result.getAsDouble(0,0);
    }
}
