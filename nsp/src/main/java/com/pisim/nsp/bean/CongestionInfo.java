package com.pisim.nsp.bean;

import java.sql.Timestamp;

public class CongestionInfo {
    private int id;
    private String queryindex;
    private short indj;
    private Timestamp timestamp;
    private double thresholdQuery;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setIndj(short indj) {
        this.indj = indj;
    }

    public void setQueryindex(String queryindex) {
        this.queryindex = queryindex;
    }

    public short getIndj() {
        return indj;
    }

    public String getQueryindex() {
        return queryindex;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setThresholdQuery(double thresholdQuery) {
        this.thresholdQuery = thresholdQuery;
    }

    public double getThresholdQuery() {
        return thresholdQuery;
    }
}
