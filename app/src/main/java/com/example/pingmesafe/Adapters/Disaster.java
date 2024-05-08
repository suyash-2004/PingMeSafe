package com.example.pingmesafe.Adapters;

public class Disaster {
    private final String name;
    private final int imageResourceId;

    public Disaster(String name, int imageResourceId) {
        this.name = name;
        this.imageResourceId = imageResourceId;
    }

    public String getName() {
        return name;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}
