package com.example.secureserver.bean;

public class LatLonPoint {
    private double a;
    private double b;
    private double c;
    public double getLongitude() {
        return this.b;
    }

    public void setLongitude(double var1) {
        this.b = var1;
    }

    public double getLatitude() {
        return this.a;
    }

    public void setLatitude(double var1) {
        this.a = var1;
    }
    public double getSpeed(){
        return this.c;
    }

    public void setSpeed(double c) {
        this.c = c;
    }
}
