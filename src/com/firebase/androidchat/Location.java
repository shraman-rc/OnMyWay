package com.firebase.androidchat;

public class Location {
	private double latitude;
    private double longitude;
    private long timestamp;
    
    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Location() { }
    
    Location(double latitude, double longitude, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;  
        this.timestamp = timestamp;
    }
    
    public double getLatitude() {
    	return latitude;
    }
    
    public double getLongitude() {
    	return longitude;
    }
    
    public long getTimestamp() {
    	return timestamp;
    }
}