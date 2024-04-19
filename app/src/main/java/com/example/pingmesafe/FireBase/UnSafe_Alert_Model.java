package com.example.pingmesafe.FireBase;

public class UnSafe_Alert_Model {
    double latitude, longitude;
    String name,AlertMessage;

    public UnSafe_Alert_Model(double latitude, double longitude, String name, String alertMessage) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        AlertMessage = alertMessage;
    }

    public UnSafe_Alert_Model(double latitude, double longitude, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public UnSafe_Alert_Model() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlertMessage() {
        return AlertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        AlertMessage = alertMessage;
    }
}
