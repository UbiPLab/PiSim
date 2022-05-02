package com.pisim.rsu.bean;


import java.sql.Timestamp;

public class CongestionInfo {
    private String queryindex;
    private short indj;
    private Timestamp timestamp;
    private double thresholdQuery;

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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setThresholdQuery(double thresholdQuery) {
        this.thresholdQuery = thresholdQuery;
    }

    public double getThresholdQuery() {
        return thresholdQuery;
    }
}
