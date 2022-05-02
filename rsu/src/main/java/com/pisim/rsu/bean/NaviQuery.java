package com.pisim.rsu.bean;

import java.sql.Timestamp;

public class NaviQuery {
    private int id;
    private String REi1;
    private String REi2;
    private String Index_EncKiI;
    private String grlpi;
    private String rlpi;
    private String daierta;
    private String M;
    private String a1;
    private String count;
    private Timestamp timestamp;

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getA1() {
        return a1;
    }

    public String getCount() {
        return count;
    }

    public String getDaierta() {
        return daierta;
    }

    public String getGrlpi() {
        return grlpi;
    }

    public String getIndex_EncKiI() {
        return Index_EncKiI;
    }

    public String getM() {
        return M;
    }

    public String getREi1() {
        return REi1;
    }

    public String getREi2() {
        return REi2;
    }

    public String getRlpi() {
        return rlpi;
    }

    public void setA1(String a1) {
        this.a1 = a1;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setDaierta(String daierta) {
        this.daierta = daierta;
    }

    public void setGrlpi(String grlpi) {
        this.grlpi = grlpi;
    }

    public void setIndex_EncKiI(String index_EncKiI) {
        Index_EncKiI = index_EncKiI;
    }

    public void setM(String m) {
        M = m;
    }

    public void setREi1(String REi1) {
        this.REi1 = REi1;
    }

    public void setREi2(String REi2) {
        this.REi2 = REi2;
    }

    public void setRlpi(String rlpi) {
        this.rlpi = rlpi;
    }

}
