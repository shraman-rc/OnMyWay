package com.firebase.androidchat;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String name;
    private String phoneNumber;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private User() { }

    User(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}
