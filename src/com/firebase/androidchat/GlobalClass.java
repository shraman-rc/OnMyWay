package com.firebase.androidchat;

import java.util.HashMap;
import java.util.Map;

import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

public class GlobalClass extends android.app.Application {
	public Map<String, String> friends = new HashMap<String, String>();
	public String phone_number;
	public String display_name;
	
	public static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";
	public Firebase ref;
	public Firebase eventsRef;
	public Firebase createdEventsRef;
	public Firebase userEventsRef;
	public Firebase eventStatusRef;
	public Firebase userPingsRef;
	public Firebase usersRef;
	public Firebase friendsRef;

	
	@Override
    public void onCreate() {
        super.onCreate();
    	ref = new Firebase(FIREBASE_URL);
    	eventsRef = new Firebase(FIREBASE_URL).child("events");
    	createdEventsRef = new Firebase(FIREBASE_URL).child("createdEvents");
    	userEventsRef = new Firebase(FIREBASE_URL).child("userEvents");
    	eventStatusRef = new Firebase(FIREBASE_URL).child("eventStatus");
    	userPingsRef =  new Firebase(FIREBASE_URL).child("userPings");
    	usersRef = new Firebase(FIREBASE_URL).child("users");
    	friendsRef = new Firebase(FIREBASE_URL).child("friends");
    }
}
