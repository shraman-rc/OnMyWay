package com.firebase.androidchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
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
        eventsRef.child(eventId).addValueEventListener(new ValueEventListener() {
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
        
        // Listen for attendee status
        eventStatusRef.child(eventId).addValueEventListener(new ValueEventListener() {
		     @Override
		     public void onDataChange(DataSnapshot snapshot) {
		    	 GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
		    	 Map<String, String> values = snapshot.getValue(t);
		         if (values != null) {
		        	 // Convert to list of maps
		        	 global = (GlobalClass) activity.getApplication();
		        	 List<Map<String, String>> statuses = new ArrayList();
		        	 for(Entry<String, String> entry : values.entrySet()) {
		        		 Map<String, String> newEntry = new HashMap<String, String>();
		        		 
		        		 // Figure out the attendee name
		        		 String name = "";
			    		for (Entry<String, String> entry2 : global.friends.entrySet()) {
			    		    if (entry.getKey().equals(entry2.getValue())) {
			    		           name = entry2.getKey();
			    		           break;
			    		    }
			    		}
		        		 newEntry.put("name", name);
		        		 newEntry.put("status", entry.getValue());
		        		 statuses.add(newEntry);
		        	 }
		        	 
		        	 final List<Map<String, String>> finalStatuses = statuses;
		        	 
		        	 // Get the event
			    	 eventsRef.child(eventId).addValueEventListener(new ValueEventListener() {
					     @Override
					     public void onDataChange(DataSnapshot snapshot) {
					    	 Event event = snapshot.getValue(Event.class);
					    	 if (event != null) {
					    		// Populate the attendees list
							    List attendees = event.getAttendees();
							    
						    	global = (GlobalClass) activity.getApplication();
						    	for (Object attendee : attendees) {
						    		// Figure out the attendee name from the phone number
						    		String name = "";
						    		for (Entry<String, String> entry : global.friends.entrySet()) {
						    		    if (attendee.equals(entry.getValue())) {
						    		           name = entry.getKey();
						    		           break;
						    		    }
						    		}

						    		System.out.println(finalStatuses);
						    		ListView listView = (ListView) view.findViewById(R.id.attendees);
						    		setListViewHeightBasedOnChildren(listView);
						    		listView.setOnTouchListener(new OnTouchListener() {
						    		    // Setting on Touch Listener for handling the touch inside ScrollView
						    		    @Override
						    		    public boolean onTouch(View v, MotionEvent event) {
						    		    // Disallow the touch request for parent scroll on touch of child view
						    		    v.getParent().requestDisallowInterceptTouchEvent(true);
						    		    return false;
						    		    }
						    		});
						            adapter = new SimpleAdapter(context,
						            		  finalStatuses, 
						            	      R.layout.rowlayout, 
						            	      new String[] {"name", "status"}, 
						            	      new int[] {R.id.name, R.id.status});
						     		listView.setAdapter(adapter);  
				                }
					    	 } 
					     }

					     @Override
					     public void onCancelled() {
					         System.err.println("Listener was cancelled");
					     }
					});
		         }
		     }

		     @Override
		     public void onCancelled() {
		         System.err.println("Listener was cancelled");
		     }
		 });
    }
    
    // This code sets the height of the ListView dynamically
    // http://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
