package com.firebase.androidchat;

import java.util.HashMap;
import java.util.Map;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

public class ButtonClickReceiver  extends BroadcastReceiver {
	private static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";
	private Firebase eventStatusRef;
	private Firebase eventsRef;
	private String display_name;
	private String phone_number;
	private LocationManager locationManager;

	@Override
	public void onReceive(final Context context, Intent intent) {
	    locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		// Remove the notification that was just pressed
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
		
		eventStatusRef = new Firebase(FIREBASE_URL).child("eventStatus");
		eventsRef = new Firebase(FIREBASE_URL).child("events");
		Bundle extras = intent.getExtras();
		if (extras != null) {
			final String eventId = extras.getString("eventId");
			display_name = extras.getString("display_name");
			phone_number = extras.getString("phone_number");
			Firebase userStatusRef = eventStatusRef.child(eventId).child(phone_number);
			if (intent.getAction().equals("omw")) {
				userStatusRef.setValue(new HashMap<String, String>(){{ put("name", display_name); put("status", "On my way!"); }});

				// Find the event location and request location updates
				eventsRef.child(eventId).child("location").addListenerForSingleValueEvent(new ValueEventListener() {
				    @Override
				    public void onDataChange(DataSnapshot snapshot) {
				        Location location = snapshot.getValue(Location.class);
						Intent locationServiceIntent = new Intent(context, LocationService.class);
						locationServiceIntent.putExtra("phone_number", phone_number);
						locationServiceIntent.putExtra("display_name", display_name);
						locationServiceIntent.putExtra("event_id", eventId);
						locationServiceIntent.putExtra("latitude", location.getLatitude());
						locationServiceIntent.putExtra("longitude", location.getLongitude());
						context.startService(locationServiceIntent);
				    }

				    @Override
				    public void onCancelled() {
				        System.err.println("Listener was cancelled");
				    }
				});
				

				
			} else if (intent.getAction().equals("no")) {
				userStatusRef.setValue(new HashMap<String, String>(){{ put("name", display_name); put("status", "Not coming."); }});
			}
		}
	}
}