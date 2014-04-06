package com.firebase.androidchat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;



public class Date {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private String date;
    private String time;
    private String dateAsString;
    

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Date() { }

    Date(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        
        // Format 5/12 as May 12
        java.util.Date d = new java.util.Date(year, month, day, hour, minute);
        DateFormat dateFormat = new SimpleDateFormat("MMMMMMMMM d");
        this.date = dateFormat.format(d);
        
        dateFormat = new SimpleDateFormat("h:mm a");
        this.time = dateFormat.format(d);
        
        // Format date as long number
        dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
    	this.dateAsString = dateFormat.format(d);
        
    }

    public int getYear() {
    	return year;
    }
    
    public int getMonth() {
    	return month;
    }
    
    public int getDay() {
    	return day;
    }
    
    public int getHour() {
    	return hour;
    }
    
    public int getMinute() {
    	return minute;
    }
    
    public String getDate() {
		return date;
    }

    public String getTime() {
		return time;
    }
    
    public String getDateAsString() {
		return dateAsString;
    }
}
