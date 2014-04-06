package com.firebase.androidchat;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

public class CreatedEventListAdapter extends FirebaseListAdapter<String> {
	private static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";
	private Firebase eventsRef = new Firebase(FIREBASE_URL).child("events");
	private Firebase usersRef = new Firebase(FIREBASE_URL).child("users");
	private Context context;

    public CreatedEventListAdapter(Query ref, Activity activity, int layout, Context context) {
        super(ref, String.class, layout, activity);
        this.context = context;
    }

    @Override
    protected void populateView(View view, String eventId) {
        // Map a Chat object to an entry in our listview
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
			    	 for (Object attendee : attendees) {
			    		 // Figure out the attendee from the phone number
			    		 usersRef.child(attendee.toString()).addValueEventListener(new ValueEventListener() {
						     @Override
						     public void onDataChange(DataSnapshot snapshot) {
						    	 User user = snapshot.getValue(User.class);
						         if (user != null) {
						        	 View inflatedView = inflater.inflate(R.layout.rowlayout, null);
				                     TextView attendeeNameText = (TextView) inflatedView.findViewById(R.id.name);
				                     attendeeNameText.setText(user.getName());
				                     attendeesList.addView(inflatedView);
						         }
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