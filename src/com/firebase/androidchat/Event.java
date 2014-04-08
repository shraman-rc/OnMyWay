package com.firebase.androidchat;

import java.util.List;
import java.util.Map;

public class Event {

    private String name;
    private String creator;
    private Date date;
    private Map<String, String> attendees;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Event() { }

    Event(String name, String creator, Date date, Map<String, String> attendees) {
        this.name = name;
        this.creator = creator;
        this.date = date;
        this.attendees = attendees;
        
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }
    
    public Date getDate() {
    	return date;
    }
    
    public Map<String, String> getAttendees() {
    	return attendees;
    }
}
