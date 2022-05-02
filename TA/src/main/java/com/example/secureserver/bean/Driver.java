package com.example.secureserver.bean;

public class Driver{
    private String username;
    private String password;
    private String rsa_pubKey;
    private String idNumber;
    private String idCar;
    private String xigema;
    private String ni;
    private String ni2;
    private String Ei1;
    private String Ei2;
    private int clsignaturetime;


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getIdCar() {
        return idCar;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getXigema() {
        return xigema;
    }

    public String getNi() {
        return ni;
    }

    public String getNi2() {
        return ni2;
    }

    public String getEi1() {
        return Ei1;
    }

    public String getEi2() {
        return Ei2;
    }

    public String getRsa_pubKey() {
        return rsa_pubKey;
    }

    public int getClsignaturetime() {
        return clsignaturetime;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRsa_pubKey(String rsa_pubKey) {
        this.rsa_pubKey = rsa_pubKey;
    }

    public void setIdCar(String idCar) {
        this.idCar = idCar;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setXigema(String xigema) {
        this.xigema = xigema;
    }

    public void setNi(String ni) {
        this.ni = ni;
    }

    public void setNi2(String ni2) {
        this.ni2 = ni2;
    }

    public void setEi1(String ei1) {
        Ei1 = ei1;
    }

    public void setEi2(String ei2) {
        Ei2 = ei2;
    }

    public void setClsignaturetime(int clsignaturetime) {
        this.clsignaturetime = clsignaturetime;
    }
}
