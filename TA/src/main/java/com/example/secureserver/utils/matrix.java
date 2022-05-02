package com.example.secureserver.utils;

import java.text.DecimalFormat;

public class matrix {
    public static double[][] mathDeterminantCalculation(double[][] value)
            throws Exception {
        // 当矩阵的行数大于2时
        for (int i = 0; i < value.length; i++) {
            // 检查数组对角线位置的数值是否是0，如果是零则对该数组进行调换，查找到一行不为0的进行调换
            if (value[i][i] == 0) {
                value = changeDeterminantNoZero(value, i, i);
            }
            for (int j = 0; j < i; j++) {
                // 让开始处理的行的首位为0处理为三角形式
                // 如果要处理的列为0则和自己调换一下位置，这样就省去了计算
                if (value[i][j] == 0) {
                    continue;
                }
                // 如果要是要处理的行是0则和上面的一行进行调换
                if (value[j][j] == 0) {
                    double[] temp = value[i];
                    value[i] = value[i - 1];
                    value[i - 1] = temp;
                    continue;
                }
                double ratio = -(value[i][j] / value[j][j]);
                value[i] = addValue(value[i], value[j], ratio);
            }
        }
        return value;
    }
    /**
     * 检查矩阵是否可逆
     *
     * @param value
     *            要检查的矩阵
     * @return 矩阵的行列式的值
     * @throws Exception
     *             抛出的异常
     */
    public static double mathDeterminantCalculationValue(double[][] value)
            throws Exception {
        value = mathDeterminantCalculation(value);
        DecimalFormat df = new DecimalFormat(".##");
        return Double.parseDouble(df.format(mathValue(value, 1)));
    }

    /**
     * 计算行列式的结果
     *
     * @param value
     * @return
     */
    public static double mathValue(double[][] value, double result)
            throws Exception {
        for (int i = 0; i < value.length; i++) {
            // 如果对角线上有一个值为0则全部为0，直接返回结果
            if (value[i][i] == 0) {
                return 0;
            }
            result *= value[i][i];
        }
        return result;
    }

    /***
     * 将i行之前的每一行乘以一个系数，使得从i行的第i列之前的数字置换为0
     *
     * @param currentRow
     *            当前要处理的行
     * @param frontRow
     *            i行之前的遍历的行
     * @param ratio
     *            要乘以的系数
     * @return 将i行i列之前数字置换为0后的新的行
     */
    public static double[] addValue(double[] currentRow, double[] frontRow,
                                    double ratio) throws Exception {
        for (int i = 0; i < currentRow.length; i++) {
            currentRow[i] += frontRow[i] * ratio;
        }
        return currentRow;
    }

    /**
     * 指定列的位置是否为0，查找第一个不为0的位置的行进行位置调换，如果没有则返回原来的值
     *
     * @param determinant
     *            需要处理的行列式
     * @param line
     *            要调换的行
     * @param row
     *            要判断的列
     */
    public static double[][] changeDeterminantNoZero(double[][] determinant,
                                                     int line, int row) throws Exception {
        for (int j = line; j < determinant.length; j++) {
            // 进行行调换
            if (determinant[j][row] != 0) {
                double[] temp = determinant[line];
                determinant[line] = determinant[j];
                determinant[j] = temp;
                return determinant;
            }
        }
        return determinant;
    }

    /**
     * 将矩阵转化为单位阵和矩阵的拼接
     *
     * @param inverseResult
     *            需要转换的矩阵
     */
    public static void changeMatixToUnit(double[][] inverseResult) {
        //将行和列都需要倒着进行运算
        for (int i = inverseResult.length - 1; i > -1; i--) {
            for (int j =  inverseResult.length -1; j > i-1; j--) {
                // 将需要修改的阶梯矩阵的前部分转换成为单位矩阵
                if (j  == i
                        && inverseResult[i][j] != 0) {
                    double temp = inverseResult[i][j];
                    // 当需要转换为单位阵的部分对角线上元素不为0的时候，整行除以这个数
                    for (int j2 = 0; j2 < inverseResult[i].length; j2++) {
                        inverseResult[i][j2] = inverseResult[i][j2] / temp;
                    }
                } else {
                    // 如果不是对角线上的元素时，找下面的那行乘以一个系数然后和当前行的元素相加，变为0
                    for (int j2 = 1; j2 < inverseResult.length-j+2; j2++) {
                        if (inverseResult[i+j2][j] != 0) {
                            double temp = inverseResult[i][j]
                                    / inverseResult[i+j2][j];
                            for (int k = 0; k < inverseResult[i].length; k++) {
                                inverseResult[i][k] -= (inverseResult[i+j2][k] * temp);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
