package com.firebase.androidchat;

public class Location {
	private double latitude;
    private double longitude;
    
    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Location() { }
    
    Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;        
    }
    
    public double getLatitude() {
    	return latitude;
    }
    
    public double getLongitude() {
    	return longitude;
    }
}