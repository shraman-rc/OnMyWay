package com.firebase.androidchat;

import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;


public class CreatedEventsActivity extends MainActivity {
	
	// Used with new event creation
	private String new_event_name;
	private int new_event_year;
	private int new_event_month;
	private int new_event_day;
	private int new_event_hour;
	private int new_event_minute;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_created_events);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		addNewEventButton();
		
		final ListView listView = getListView();
		listAdapter = new EventListAdapter(global.createdEventsRef.child(global.phone_number), this,
				R.layout.event, this);
		listView.setAdapter(listAdapter);
		
		addDeleteListener(listView);
		addPopupListener(listView, R.layout.event_details_creator);
		
		listAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
			}
		});
	}
	
	@Override
	public void onStop() {
		super.onStop();
		listAdapter.cleanup();
	}
	
    // Creator delete event feature
	protected void addDeleteListener(ListView listView) {
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int pos, long arg3) {
 				final String eventId = listAdapter.getItem(pos).toString();
 				
 				TextView nameText = (TextView) arg1.findViewById(R.id.name);
 				final String name = nameText.getText().toString();
            	
            	final Dialog dialog = new Dialog(CreatedEventsActivity.this);
 			    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
 				dialog.setContentView(R.layout.dialog);
 				TextView text = (TextView) dialog.findViewById(R.id.message);
 				text.setText("Remove " + name + "?");
 				Button buttonOkay = (Button) dialog.findViewById(R.id.okay_button);
 				buttonOkay.setOnClickListener(new OnClickListener() {
 					@Override
 					public void onClick(View v) {
 						creatorRemoveEvent(eventId);
 						dialog.dismiss();
 					}
 				});
 				Button buttonCancel = (Button) dialog.findViewById(R.id.cancel_button);
 				buttonCancel.setOnClickListener(new OnClickListener() {
 					@Override
 					public void onClick(View v) {
 						dialog.dismiss();
 					}
 				});
 				dialog.show();
 				return true;
            }
        });
	}
	
	// Activity results for creating new event
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Created event name
		if (requestCode == 2) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
		        if(extras != null) {
		        	new_event_name = extras.getString("input");
		            Intent i = new Intent(CreatedEventsActivity.this, CreateNewEventDateActivity.class);    
   		            startActivityForResult(i, 3);
		        }
		    }
	    }
		
		// Created event date
		if (requestCode == 3) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
		        if(extras != null) {
		        	new_event_year = extras.getInt("year");
		        	new_event_month = extras.getInt("month");
		        	new_event_day = extras.getInt("day");
		            Intent i = new Intent(CreatedEventsActivity.this, CreateNewEventTimeActivity.class);    
   		            startActivityForResult(i, 4);
		        }
		    }
	    }
		
		// Created event time
		if (requestCode == 4) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
		        if(extras != null) {
		        	new_event_hour = extras.getInt("hour");
		        	new_event_minute = extras.getInt("minute");
		            Intent i = new Intent(CreatedEventsActivity.this, CreateNewEventAttendeesActivity.class);    
   		            startActivityForResult(i, 5);
		        }
		    }
	    }
		
		// Created event attendees
		if (requestCode == 5) {
			if (resultCode == RESULT_OK) {
				createEvent();
		    }
	    }		
	}
	
	protected void createEvent() {
		Date date = new Date(new_event_year, new_event_month, new_event_day, new_event_hour, new_event_minute);
		
		// Get new event ID
		Firebase newEventRef = global.eventsRef.push();
		
		// Allocate ping entries and add to user event and event status lists
		for(String attendee : global.attendees.keySet()) {
			global.userPingsRef.child(attendee).child(newEventRef.getName()).setValue("0");
		}
		
		for(String attendee : global.attendees.keySet()) {
			global.userEventsRef.child(attendee).child(newEventRef.getName()).setValue(newEventRef.getName(), date.getDateAsString()); // Prioritize by date (getDateAsString) so that earlier events show up at the top
		}

		for(final String attendee : global.attendees.keySet()) {
			global.eventStatusRef.child(newEventRef.getName()).child(attendee).setValue(new HashMap<String, String>(){{ put("name", global.attendees.get(attendee)); put("status", "?"); }});
		}
		
		// Add self to attendees and create event
        global.attendees.put(global.phone_number, global.display_name);
        Event event = new Event(new_event_name, global.phone_number, date, global.attendees);
		// Add to events
		newEventRef.setValue(event, date.getDateAsString());
		// Add event to user's created events
		global.createdEventsRef.child(global.phone_number).child(newEventRef.getName()).setValue(newEventRef.getName(), date.getDateAsString());
		// Designate status as creator
		global.eventStatusRef.child(newEventRef.getName()).child(global.phone_number).setValue(new HashMap<String, String>(){{ put("name", global.display_name); put("status", "Creator"); }});
		
	}
}