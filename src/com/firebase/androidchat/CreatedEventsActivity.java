package com.firebase.androidchat;

import java.util.HashMap;

import android.app.Dialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
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
	private double new_event_latitude;
	private double new_event_longitude;
	
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
	
	
	// Create new event button
	protected void addNewEventButton() {
		findViewById(R.id.create_event_button).setOnClickListener(
		new View.OnClickListener() {
			@Override
			public void onClick(View view) {
		         Intent i = new Intent(CreatedEventsActivity.this, CreateNewEventNameActivity.class);    
		         startActivityForResult(i, 2);
			}
		});
	}
	
    // Creator delete event feature
	protected void addDeleteListener(ListView listView) {
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int pos, long arg3) {
 				Event event = (Event) listAdapter.getItem(pos);
 				final String eventId = listAdapter.getIdOfItem(pos);
 				
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
		            Intent i = new Intent(CreatedEventsActivity.this, CreateNewEventLocationActivity.class);    
		            // Bundle options = ActivityOptions.makeCustomAnimation(CreatedEventsActivity.this, R.anim.right_slide_in, R.anim.right_slide_out).toBundle();
		            // startActivityForResult(i, 3, options);
		            startActivityForResult(i, 3);
		        }
		    }
	    }
		
		// Created event location
		if (requestCode == 3) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
		        if(extras != null) {
		        	new_event_latitude = extras.getDouble("latitude");
		        	new_event_longitude = extras.getDouble("longitude");
		            Intent i = new Intent(CreatedEventsActivity.this, CreateNewEventDateActivity.class);    
		            startActivityForResult(i, 4);
		        }
		    }
	    }
		
		// Created event date
		if (requestCode == 4) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
		        if(extras != null) {
		        	new_event_year = extras.getInt("year");
		        	new_event_month = extras.getInt("month");
		        	new_event_day = extras.getInt("day");
		            Intent i = new Intent(CreatedEventsActivity.this, CreateNewEventTimeActivity.class);    
		            startActivityForResult(i, 5);
		        }
		    }
	    }
		
		// Created event time
		if (requestCode == 5) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
		        if(extras != null) {
		        	new_event_hour = extras.getInt("hour");
		        	new_event_minute = extras.getInt("minute");
		            Intent i = new Intent(CreatedEventsActivity.this, CreateNewEventAttendeesActivity.class);    
		            startActivityForResult(i, 6);
		        }
		    }
	    }
		
		// Created event attendees
		if (requestCode == 6) {
			if (resultCode == RESULT_OK) {
				createEvent();
		    }
	    }		
	}
	
	protected void createEvent() {
		Date date = new Date(new_event_year, new_event_month, new_event_day, new_event_hour, new_event_minute);
		Location location = new Location(new_event_latitude, new_event_longitude, System.currentTimeMillis());
		
		// Get new event ID
		Firebase newEventRef = global.eventsRef.push();
		
		// Add self to attendees and create event
        global.attendees.put(global.phone_number, global.display_name);
        Event event = new Event(new_event_name, global.phone_number, date, location, global.attendees);
		
		// Allocate ping entries and add to user event and event status lists
		for(String attendee : global.attendees.keySet()) {
			// Creator doesn't need a ping entry
			if (!attendee.equals(global.phone_number)) {
				global.userPingsRef.child(attendee).child(newEventRef.getName()).setValue(0);
			}
		}
		
		for(String attendee : global.attendees.keySet()) {
			// Don't add event to creator's own events
			if (!attendee.equals(global.phone_number)) {
				global.userEventsRef.child(attendee).child(newEventRef.getName()).setValue(event, date.getDateAsString()); // Prioritize by date (getDateAsString) so that earlier events show up at the top
			}
		}

		for(final String attendee : global.attendees.keySet()) {
			if (!attendee.equals(global.phone_number)) {
				global.eventStatusRef.child(newEventRef.getName()).child(attendee).setValue(new HashMap<String, String>(){{ put("name", global.attendees.get(attendee)); put("status", "?"); }});
			} else {
				global.eventStatusRef.child(newEventRef.getName()).child(global.phone_number).setValue(new HashMap<String, String>(){{ put("name", global.display_name); put("status", "Creator"); }});
			}
			
		}

		// Add event to user's created events
		global.createdEventsRef.child(global.phone_number).child(newEventRef.getName()).setValue(event, date.getDateAsString());
		
		// Add to events
		newEventRef.setValue(event, date.getDateAsString());		
		
	}
}