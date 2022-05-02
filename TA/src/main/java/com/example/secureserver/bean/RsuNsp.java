package com.example.secureserver.bean;

public class RsuNsp {
    private String pub_key;
    private String Unique_identifier;

    public void setPub_key(String pub_key) {
        this.pub_key = pub_key;
    }

    public String getPub_key() {
        return pub_key;
    }

    public void setUnique_identifier(String unique_identifier) {
        Unique_identifier = unique_identifier;
    }

    public String getUnique_identifier() {
        return Unique_identifier;
    }
}
