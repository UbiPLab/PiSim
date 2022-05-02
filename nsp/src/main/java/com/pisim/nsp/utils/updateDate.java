package com.pisim.nsp.utils;

import com.pisim.nsp.parameterUtil.parameter;

import java.math.BigInteger;

public class updateDate {
    public static void updateTe(int te ){
        if (te!= parameter.te){
            parameter.te = (int) (System.currentTimeMillis()/ parameter.cycle);
            for (int i = 0; i < parameter.MC; i++) {
                String temp = "" + te + i;
                BigInteger grlpi_temp = parameter.pairing.getZr().newElement().setFromHash(temp.getBytes(), 0, temp.getBytes().length).toBigInteger();
                BigInteger grlpi = grlpi_temp.modPow(((parameter.ZKPK_F.subtract(BigInteger.ONE)).divide(parameter.ZKPK_rou)), parameter.ZKPK_F);
                parameter.grlpis.add(grlpi);
            }
        }
    }
}
