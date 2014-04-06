package com.firebase.androidchat;

import java.util.List;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

public class CreatedEventListAdapter extends FirebaseListAdapter<String> {
	private static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";
	private Firebase eventsRef = new Firebase(FIREBASE_URL).child("events");
	private Firebase friendsRef = new Firebase(FIREBASE_URL).child("friends");
	private Firebase usersRef = new Firebase(FIREBASE_URL).child("users");
	private Firebase eventStatusRef = new Firebase(FIREBASE_URL).child("eventStatus");
	private Context context;
	private Activity activity;
	private GlobalClass global;
	private String eventId;

    public CreatedEventListAdapter(Query ref, Activity activity, int layout, Context context) {
        super(ref, String.class, layout, activity);
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void populateView(View view, final String eventId) {
        final TextView nameText = (TextView)view.findViewById(R.id.name);
        final TextView dateText = (TextView)view.findViewById(R.id.date);
        final TextView timeText = (TextView)view.findViewById(R.id.time);
        final LinearLayout attendeesList = (LinearLayout) view.findViewById(R.id.attendees);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
   	 
        
        // From the event id, find the event from the events table
        eventsRef.child(eventId).addValueEventListener(new ValueEventListener() {
		     @Override
		     public void onDataChange(DataSnapshot snapshot) {
		    	 Event event = snapshot.getValue(Event.class);
		    	 if (event != null) {
			    	 nameText.setText(event.getName());
			    	 dateText.setText(event.getDate().getDate());
			    	 timeText.setText(event.getDate().getTime());
			    	 
			    	 // Populate the attendees list
			    	 List attendees = event.getAttendees();
			    	 attendeesList.removeAllViews();
			    	 
			    	 
			    	 global = (GlobalClass) activity.getApplication();
			    	 for (Object attendee : attendees) {
			    		// Figure out the attendee from the phone number
			    		 String name = "";
			    		 for (Entry<String, String> entry : global.friends.entrySet()) {
			    		     if (attendee.equals(entry.getValue())) {
			    		            name = entry.getKey();
			    		            break;
			    		     }
			    		 }
			    		 
			    		 final View inflatedView = inflater.inflate(R.layout.rowlayout, null);
	                     TextView attendeeNameText = (TextView) inflatedView.findViewById(R.id.name);
	                     final TextView attendeeStatusText = (TextView) inflatedView.findViewById(R.id.status);
	                     attendeeNameText.setText(name);
	                     
	                     // Determine the status of attendee
	                     eventStatusRef.child(eventId).child(attendee.toString()).addValueEventListener(new ValueEventListener() {
						     @Override
						     public void onDataChange(DataSnapshot snapshot) {
						    	 String status = snapshot.getValue(String.class);

						         if (status != null) {
						        	 attendeeStatusText.setText(status);
						         }
						         attendeesList.addView(inflatedView);
						     }
		
						     @Override
						     public void onCancelled() {
						         System.err.println("Listener was cancelled");
						     }
						 });
	                 }
			 		
			    	 
			    	 
			    	 // Find the creator of the event
			    	 /*usersRef.child(event.getCreator()).addValueEventListener(new ValueEventListener() {
					     @Override
					     public void onDataChange(DataSnapshot snapshot) {
					    	 User user = snapshot.getValue(User.class);
					         if (user != null) {
					        	 ((TextView)theView.findViewById(R.id.creator)).setText(user.getName());
					         }
					     }
	
					     @Override
					     public void onCancelled() {
					         System.err.println("Listener was cancelled");
					     }
					 });*/
		    	 }
		    	 
		     }

		     @Override
		     public void onCancelled() {
		         System.err.println("Listener was cancelled");
		     }
		 });
        
        
        
        
        
        
    }
}
