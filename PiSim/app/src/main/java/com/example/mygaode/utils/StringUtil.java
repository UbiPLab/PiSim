package com.example.mygaode.utils;

import java.math.BigDecimal;

public class StringUtil {
    public static String NumericScaleByFloor(String numberValue, int scale) {
        return new BigDecimal(numberValue).setScale(scale, BigDecimal.ROUND_FLOOR).toString();
    }
}
