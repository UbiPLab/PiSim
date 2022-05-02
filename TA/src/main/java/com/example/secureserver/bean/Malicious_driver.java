package com.example.secureserver.bean;

public class Malicious_driver {
    private String IdNumber;
    private String IdCar;
    private String username;
    private String Ei1;
    private String Ei2;
    private short type;

    public void setType(short type) {
        this.type = type;
    }

    public short getType() {
        return type;
    }

    public void setEi2(String ei2) {
        Ei2 = ei2;
    }

    public String getEi2() {
        return Ei2;
    }

    public String getEi1() {
        return Ei1;
    }

    public void setEi1(String ei1) {
        Ei1 = ei1;
    }

    public void setIdNumber(String idNumber) {
        IdNumber = idNumber;
    }

    public void setIdCar(String idCar) {
        IdCar = idCar;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getIdNumber() {
        return IdNumber;
    }

    public String getIdCar() {
        return IdCar;
    }
}
