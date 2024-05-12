package com.example.pingmesafe.Adapters;

public class Disaster {
    private final String name;
    private final int imageResourceId;
    private double latitude;
    private double longitude;
    private String locationString;

    public Disaster(String name, int imageResourceId) {
        this.name = name;
        this.imageResourceId = imageResourceId;
    }

    public Disaster(String title, double latitude, double longitude, String locationString) {
        this.name = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationString = locationString;
        imageResourceId=0;
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

    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }

    public String getName() {
        return name;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}
