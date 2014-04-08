package com.firebase.androidchat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

public class EventListAdapter extends FirebaseListAdapter<String> {
	public static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";
	private Firebase eventsRef = new Firebase(FIREBASE_URL).child("events");
	private Context context;
	private Activity activity;
	private String eventId;
	private SimpleAdapter adapter;
	

    public EventListAdapter(Query ref, Activity activity, int layout, Context context) {
        super(ref, String.class, layout, activity);
        this.activity = activity;
        this.context = context;
    }

    @Override
    protected void populateView(final View view, final String eventId) {
        final TextView nameText = (TextView)view.findViewById(R.id.name);
        final TextView dateText = (TextView)view.findViewById(R.id.date);
        final TextView timeText = (TextView)view.findViewById(R.id.time);
        
        //Getting a well-formatted current time
    	final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	final Calendar cal = Calendar.getInstance();
    	final String dummyTime = dateFormat.format(cal.getTime());
    	final int currentTime = Integer.parseInt(dummyTime.substring(5,7) + dummyTime.substring(8,10) + dummyTime.substring(11,13) + dummyTime.substring(14,16));
        
        // From the event id, find the event from the events table
        eventsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
		     @Override
		     public void onDataChange(DataSnapshot snapshot) {
		    	 if (snapshot != null) {
		    		 Event event = snapshot.getValue(Event.class);
		    		 
		    		 //Set Background Colors
		    		 int eventTime = Integer.parseInt(event.getDate().getDateAsString().substring(4));
		    		 if ((eventTime - currentTime < 5) && (eventTime - currentTime >= 0)) {view.setBackgroundColor(0xAAFF8585);
		    		 }else if ((eventTime - currentTime < 30) && (eventTime - currentTime >= 5)) {view.setBackgroundColor(0xAAFFAD33);
		    		 }else if (eventTime - currentTime >=30) {view.setBackgroundColor(0xAAA7FFA7);
		    		 }else {view.setBackgroundColor(0x00000000);}
		    		 
		    		 /*&& eventTime - currentTime < 2550
view.getBackground().setAlpha(255-((eventTime-currentTime)/10))*/
		    		 
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
