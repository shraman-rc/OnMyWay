package com.firebase.androidchat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.GenericTypeIndicator;
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
	private SimpleAdapter adapter;

    public CreatedEventListAdapter(Query ref, Activity activity, int layout, Context context) {
        super(ref, String.class, layout, activity);
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void populateView(final View view, final String eventId) {
        final TextView nameText = (TextView)view.findViewById(R.id.name);
        final TextView dateText = (TextView)view.findViewById(R.id.date);
        final TextView timeText = (TextView)view.findViewById(R.id.time);
        
        // From the event id, find the event from the events table
        eventsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
		     @Override
		     public void onDataChange(DataSnapshot snapshot) {
		    	 Event event = snapshot.getValue(Event.class);
		    	 if (event != null) {
			    	 nameText.setText(event.getName());
			    	 dateText.setText(event.getDate().getDate());
			    	 timeText.setText(event.getDate().getTime());
		    	 }
		     }

		     @Override
		     public void onCancelled() {
		         System.err.println("Listener was cancelled");
		     }
		});
    }
}
