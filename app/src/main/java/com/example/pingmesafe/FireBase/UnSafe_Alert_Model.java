package com.example.pingmesafe.FireBase;

public class UnSafe_Alert_Model {
    double latitude, longitude;
    String name, alertMessage, deviceName, currentTime;

    public UnSafe_Alert_Model(double latitude, double longitude, String name, String alertMessage, String deviceName, String currentTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.alertMessage = alertMessage;
        this.deviceName = deviceName;
        this.currentTime = currentTime;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        alertMessage = alertMessage;
    }
}
