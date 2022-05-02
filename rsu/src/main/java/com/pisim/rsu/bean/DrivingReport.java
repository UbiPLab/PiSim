package com.pisim.rsu.bean;

import java.sql.Timestamp;

public class DrivingReport {
    private int id;
    private String report_string;
    private String pidj;
    private Timestamp timestamp;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getReport_string() {
        return report_string;
    }

    public void setReport_string(String report_string) {
        this.report_string = report_string;
    }

    public void setPidj(String pidj) {
        this.pidj = pidj;
    }

    public String getPidj() {
        return pidj;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
