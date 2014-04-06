package com.firebase.androidchat;

import java.util.List;

public class Event {

    private String name;
    private String creator;
    private Date date;
    private List attendees;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Event() { }

    Event(String name, String creator, Date date, List attendees) {
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
    
    public List getAttendees() {
    	return attendees;
    }
}
