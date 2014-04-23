package com.firebase.androidchat;

import java.util.HashMap;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import com.firebase.client.Firebase;

public class ButtonClickReceiver  extends BroadcastReceiver {
	private static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";
	private Firebase eventStatusRef;
	private String display_name;
	private String phone_number;
	private LocationManager locationManager;

	@Override
	public void onReceive(Context context, Intent intent) {
	    locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		// Remove the notification that was just pressed
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
		
		eventStatusRef = new Firebase(FIREBASE_URL).child("eventStatus");
		Bundle extras = intent.getExtras();
		if (extras != null) {
			String eventId = extras.getString("eventId");
			display_name = extras.getString("display_name");
			phone_number = extras.getString("phone_number");
			Firebase userStatusRef = eventStatusRef.child(eventId).child(phone_number);
			if (intent.getAction().equals("omw")) {
				userStatusRef.setValue(new HashMap<String, String>(){{ put("name", display_name); put("status", "On my way!"); }});

				// Request location
				Intent locationServiceIntent = new Intent(context, LocationService.class);
				locationServiceIntent.putExtra("phone_number", phone_number);
				locationServiceIntent.putExtra("display_name", display_name);
				locationServiceIntent.putExtra("event_id", eventId);
				context.startService(locationServiceIntent);
				
			} else if (intent.getAction().equals("no")) {
				userStatusRef.setValue(new HashMap<String, String>(){{ put("name", display_name); put("status", "Not coming."); }});
			}
		}
	}
}