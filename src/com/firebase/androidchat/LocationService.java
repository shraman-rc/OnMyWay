package com.firebase.androidchat;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.firebase.client.Firebase;


public class LocationService extends Service implements LocationListener {
	private String phone_number;
	private String display_name;
	private List<String> activeEvents;
	private List<Double> latitudes;
	private List<Double> longitudes;
	private NotificationManager notificationManager;
	private LocationManager locationManager;
	private Criteria criteria;
	private String provider;
	private Location location;
	public final static int REQUEST_LOCATION_UPDATE_TIMER = 400;
	public final static int REQUEST_LOCATION_UPDATE_MINDISTANCE_METER = 1; // 500;
	
	private static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";
	
    public class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
    	notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // Display a notification about us starting.  We put an icon in the status bar.
        //showNotification();
    	activeEvents = new ArrayList<String>();
    	latitudes = new ArrayList<Double>();
    	longitudes = new ArrayList<Double>();
    }


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Bundle extras = intent.getExtras();
		if(extras != null) {
			phone_number = extras.getString("phone_number");
			display_name = extras.getString("display_name");
			activeEvents.add(extras.getString("event_id"));
			latitudes.add(extras.getDouble("latitude"));
			longitudes.add(extras.getDouble("longitude"));
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			criteria = new Criteria();
			provider = locationManager.getBestProvider(criteria, true);

		    location = locationManager.getLastKnownLocation(provider);
		    if (location != null) {
		    	onLocationChanged(location);
		    }
		    
		    locationManager.requestLocationUpdates(provider,
		    		REQUEST_LOCATION_UPDATE_TIMER,
		    		REQUEST_LOCATION_UPDATE_MINDISTANCE_METER,
		    		this);
		    return START_NOT_STICKY;
		} else {
			// Terminate the service
			this.stopSelf();
			return START_NOT_STICKY;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Toast.makeText(this, "Location updated", Toast.LENGTH_SHORT).show();
		double lat = location.getLatitude();
		double lng = location.getLongitude();
		Firebase userLocationRef = new Firebase(FIREBASE_URL).child("userLocations").child(phone_number);
		userLocationRef.setValue(new com.firebase.androidchat.Location(lat, lng, System.currentTimeMillis()));
		// we need to implement no repeating of event ids
		for (int i = 0; i < activeEvents.size(); ++i) {
			Firebase userStatusRef = new Firebase(FIREBASE_URL).child("eventStatus").child(activeEvents.get(i)).child(phone_number);
			float[] results = new float[1];
			Location.distanceBetween(latitudes.get(i), longitudes.get(i), lat, lng, results);
			final float distance = results[0];
			userStatusRef.setValue(new HashMap<String, String>(){{ put("name", display_name); put("status", "On my way! (" + distance + " meters)"); }});
		}
	}

  	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	    // TODO Auto-generated method stub
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
	}
}