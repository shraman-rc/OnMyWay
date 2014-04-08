package com.firebase.androidchat;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.firebase.client.Firebase;

public class ButtonClickReceiver  extends BroadcastReceiver {
	private static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";
	private Firebase eventStatusRef;
	private String phone_number;

	@Override
	public void onReceive(Context context, Intent intent) {
	    //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
		eventStatusRef = new Firebase(FIREBASE_URL).child("eventStatus");
		Bundle extras = intent.getExtras();
		if (extras != null) {
			String eventId = extras.getString("eventId");
			phone_number = extras.getString("phone_number");
			Firebase userStatusRef = eventStatusRef.child(eventId).child(phone_number);;
			if (intent.getAction().equals("omw")) {
				userStatusRef.setValue("On my way!");
				/* Request location updates */
				/*Intent locationRequest = new Intent (context, LocationService.class);
				context.startService(locationRequest);*/
			}
			else {
				userStatusRef.setValue("Not coming.");
			}
		}
	}
}