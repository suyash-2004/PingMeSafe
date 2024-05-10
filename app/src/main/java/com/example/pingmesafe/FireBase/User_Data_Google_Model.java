package com.example.pingmesafe.FireBase;

import android.net.Uri;

public class User_Data_Google_Model {

    String fname, lname,  email,  dob,  number;
    double latitude, longitude;
    String selectedProfileImageUrl;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public User_Data_Google_Model(String fname, String lname, String email, double latitude, double longitude, Uri selected_profile_image) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.selectedProfileImageUrl = selected_profile_image.toString(); // Convert Uri to string
    }

    public String getSelectedProfileImageUrl() {
        return selectedProfileImageUrl;
    }

    public void setSelectedProfileImageUrl(String selectedProfileImageUrl) {
        this.selectedProfileImageUrl = selectedProfileImageUrl;
    }
}