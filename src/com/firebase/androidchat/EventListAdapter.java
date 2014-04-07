package com.firebase.androidchat;

import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

public class EventListAdapter extends FirebaseListAdapter<String> {
	private static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";
	private Firebase eventsRef = new Firebase(FIREBASE_URL).child("events");
	private Firebase friendsRef = new Firebase(FIREBASE_URL).child("friends");
	private Firebase usersRef = new Firebase(FIREBASE_URL).child("users");
	private Firebase eventStatusRef = new Firebase(FIREBASE_URL).child("eventStatus");
	private Firebase userEventsRef = new Firebase(FIREBASE_URL).child("userEvents");
	private Context context;
	private Activity activity;
	private GlobalClass global;
	private String phone_number;
	private String display_name;
	private SimpleAdapter adapter;

    public EventListAdapter(Query ref, Activity activity, int layout, Context context) {
        super(ref, String.class, layout, activity);
        this.context = context;
        this.activity = activity;
        System.out.println("fuck");
		SharedPreferences prefs = activity.getApplication().getSharedPreferences(
				"OnMyWayPrefs", 0);
		phone_number = prefs.getString("phone_number", null);
		display_name = prefs.getString("display_name", null);
    }

	@Override
    protected void populateView(final View view, final String eventID) {

    	final TextView eventText = (TextView)view.findViewById(R.id.name);
    	System.out.println("yolo");
    	// From the phone number, find the event from the events table
        userEventsRef.child(phone_number).addListenerForSingleValueEvent(new ValueEventListener() {
		//List<String> totalEvents =  new ArrayList();
        
        	@Override
		     public void onDataChange(DataSnapshot snapshot) {
		    	 GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
		    	 Map<String, String> values = snapshot.getValue(t);
		    	 System.out.println("Reaches");
		    	 System.out.println(values);
		    	 
		    	 if (values != null) {
		    		 System.out.println("swag");
		        	 for(Entry<String, String> entry : values.entrySet()) {	
		        		 System.out.println("this is debugging");
		        		 System.out.println(eventID);
		        		 System.out.println(entry.getValue());
		        		 if (entry.getValue().equals(eventID)) {
			        			 eventsRef.child(entry.getValue()).addValueEventListener(new ValueEventListener() {
							     @Override
							     public void onDataChange(DataSnapshot snapshot) {
							    	 Event event = snapshot.getValue(Event.class);
							    	 eventText.setText(event.getName());
							    	 System.out.println(event.getName());
			        			 }
							@Override
							public void onCancelled() {
								// TODO Auto-generated method stub
								
							};});
			    		}
		        	 } 
		    	 }
        	}
			@Override
			public void onCancelled() {
				// TODO Auto-generated method stub
				
			}
		});
    }
}
