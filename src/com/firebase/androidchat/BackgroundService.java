package com.firebase.androidchat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

public class BackgroundService extends Service {
	private static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";
	private Firebase userPingRef;
	private Firebase eventRef;
	private ChildEventListener userPingListener;
	private String display_name;
	private String phone_number;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Bundle extras = intent.getExtras();
		if(extras != null) {
			phone_number = extras.getString("phone_number");
			display_name = extras.getString("display_name");
			
			userPingRef = new Firebase(FIREBASE_URL).child("userPings").child(phone_number);
			userPingListener = userPingRef.addChildEventListener(new ChildEventListener() {
			    @Override
			    public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
			    	// Notification for "you've been added to this event" goes here
			    }

			    @Override
			    public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
			    	String eventId = snapshot.getName();
			    	getEventName(eventId);
			    }

			    @Override
			    public void onChildRemoved(DataSnapshot snapshot) {

			    }

			    @Override
			    public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

			    }

			    @Override
			    public void onCancelled() {

			    }
			});
			return START_STICKY;
		} else {
			// Terminate the service
			this.stopSelf();
			return START_STICKY;
		}
	}
	
	@Override
	public void onDestroy() {
		userPingRef.removeEventListener(userPingListener); 
	}
	
	// Gets the event name from the event ID and then calls createNotif
	private void getEventName(final String eventId) {
		
		eventRef = new Firebase(FIREBASE_URL).child("events").child(eventId);
		eventRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
	        @Override
	        public void onDataChange(DataSnapshot snapshot) {
	        	postPingNotif(snapshot.getValue().toString(), eventId);
	        }

	        @Override
	        public void onCancelled() {
	            System.err.println("Listener was cancelled");
	        }
	    });;
	}
	
	private void postPingNotif(String eventName, String eventId) {
		Intent openAppIntent = new Intent(this, TabHostActivity.class);
		PendingIntent pOpenAppIntent = PendingIntent.getActivity(this, 0, openAppIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		Intent intentOmw = new Intent("omw");
		intentOmw.putExtra("eventId", eventId);
		intentOmw.putExtra("display_name", display_name);
		intentOmw.putExtra("phone_number", phone_number);
		PendingIntent pIntentOmw = PendingIntent.getBroadcast(this, 0, intentOmw, PendingIntent.FLAG_CANCEL_CURRENT);
		
		Intent intentNo = new Intent("no");
	    intentNo.putExtra("eventId", eventId);
	    intentNo.putExtra("display_name", display_name);
	    intentOmw.putExtra("phone_number", phone_number);
	    PendingIntent pIntentNo = PendingIntent.getBroadcast(this, 0, intentNo, PendingIntent.FLAG_CANCEL_CURRENT);
		
		Notification noti = new Notification.Builder(this)
        .setContentTitle("Event: " + eventName)
        .setContentText("OnMyWay - Event Status Requested!").setSmallIcon(R.drawable.ic_launcher)
        .setContentIntent(pOpenAppIntent)
        .addAction(R.drawable.ic_action_accept, "On my way!", pIntentOmw)
        .addAction(R.drawable.ic_action_cancel, "Not coming.", pIntentNo)
        .setVibrate (new long[] {1,2,3,4,5})
        .setAutoCancel(true).build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    notificationManager.notify(0, noti);
	}

}
