package com.firebase.androidchat;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String name;
    private String phoneNumber;
    private List friends;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private User() { }

    User(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        friends = new ArrayList();
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public List getFriends() {
    	return friends;
    }
}
