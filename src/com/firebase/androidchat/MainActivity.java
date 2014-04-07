package com.firebase.androidchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

public class MainActivity extends ListActivity {

	// TODO: change this to your own Firebase URL
	private static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";

	private String phone_number;
	private String display_name;
	private Firebase eventsRef;
	private Firebase usersRef;
	private Firebase createdEventsRef;
	private Firebase userEventsRef;
	private Firebase friendsRef;
	private Firebase eventStatusRef;
	private ValueEventListener connectedListener;
	private CreatedEventListAdapter createdEventListAdapter;
	private GlobalClass global;
	
	// Used with new event creation
	private String new_event_name;
	private int new_event_year;
	private int new_event_month;
	private int new_event_day;
	private int new_event_hour;
	private int new_event_minute;
	private List<String> new_event_attendees;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		global = (GlobalClass) getApplication();

		// Setup our Firebase ref
		eventsRef = new Firebase(FIREBASE_URL).child("events");
		usersRef = new Firebase(FIREBASE_URL).child("users");
		createdEventsRef = new Firebase(FIREBASE_URL).child("createdEvents");
		userEventsRef = new Firebase(FIREBASE_URL).child("userEvents");
		friendsRef = new Firebase(FIREBASE_URL).child("friends");
		eventStatusRef = new Firebase(FIREBASE_URL).child("eventStatus");
		
		// Make sure user has phone number and display name
		setupUser();
		
		// Create new event button
		findViewById(R.id.create_event_button).setOnClickListener(
		new View.OnClickListener() {
			@Override
			public void onClick(View view) {
		         Intent i = new Intent(MainActivity.this, CreateNewEventNameActivity.class);    
		         startActivityForResult(i, 2);
			}
		});
		
		// Select friends button
		findViewById(R.id.select_friends_button).setOnClickListener(
		new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this, SelectFriendsActivity.class);
				startActivityForResult(i, 6);
			}
		});
	}
	
	private void setupUser() {
		// Get shared prefs from phone data
		SharedPreferences prefs = getApplication().getSharedPreferences(
				"OnMyWayPrefs", 0);
		// prefs.edit().clear().commit();
		// System.out.println("Current prefs: " + prefs.getAll());
		phone_number = prefs.getString("phone_number", null);
		global.phone_number = phone_number;
		display_name = prefs.getString("display_name", null);
		if (display_name != null) {
			storeDisplayName(display_name);
		}
		

		if (phone_number == null) {
			TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			phone_number = tMgr.getLine1Number();
			prefs.edit().putString("phone_number", phone_number).commit();
			global.phone_number = phone_number;
		}
		// If display name is not stored in shared prefs, try to get from database
		if (display_name == null) {
			usersRef.child(phone_number).addValueEventListener(new ValueEventListener() {
			     @Override
			     public void onDataChange(DataSnapshot snapshot) {
			         Object value = snapshot.getValue();
			         if (value != null) {
			        	 User user = snapshot.getValue(User.class);
				         if (user != null) {
				        	 display_name = user.getName();
				        	 storeDisplayName(display_name);
				         }
			         } else {
			        	// If display name was not in database, call LoginActivity
				         Intent i = new Intent(MainActivity.this, LoginActivity.class);    
				         startActivityForResult(i, 1);
			         }
			     }

			     @Override
			     public void onCancelled() {
			         System.err.println("Listener was cancelled");
			     }
			 });
		}
		
		// Retrieve friends list if exists
		friendsRef.child(phone_number).addValueEventListener(new ValueEventListener() {
		     @Override
		     public void onDataChange(DataSnapshot snapshot) {
		    	 GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
		    	 Map<String, String> value = snapshot.getValue(t);
		         if (value != null) {
			         GlobalClass global = (GlobalClass) getApplication();
			         global.friends = value;
		         }
		     }

		     @Override
		     public void onCancelled() {
		         System.err.println("Listener was cancelled");
		     }
		 });

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// Display name
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
		    	Bundle extras = data.getExtras();
		        if(extras != null) {
		            display_name = extras.getString("input");
		            storeDisplayName(display_name);
		        }
		    }
	    }
		
		// Created event name
		if (requestCode == 2) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
		        if(extras != null) {
		        	new_event_name = extras.getString("input");
		            Intent i = new Intent(MainActivity.this, CreateNewEventDateActivity.class);    
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
		            Intent i = new Intent(MainActivity.this, CreateNewEventTimeActivity.class);    
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
		            Intent i = new Intent(MainActivity.this, CreateNewEventAttendeesActivity.class);    
   		            startActivityForResult(i, 5);
		        }
		    }
	    }
		
		// Created event attendees
		if (requestCode == 5) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
		        if(extras != null) {
		        	new_event_attendees = extras.getStringArrayList("attendees");
		        	createEvent();
		        }
		    }
	    }
		
	}
	
	// Stores display name in shared prefs and database
	private void storeDisplayName(String display_name) {
		this.display_name = display_name;
		
		// Store display name in database
        // usersRef.child(phone_number).setValue(display_name);
        usersRef.child(phone_number).setValue(new User(display_name, phone_number));
        
        // Store display name in shared prefs
		SharedPreferences prefs = getApplication().getSharedPreferences(
				"OnMyWayPrefs", 0);
	    prefs.edit().putString("display_name", display_name).commit();
	}

	@Override
	public void onStart() {
		super.onStart();
		final ListView listView = getListView();
		createdEventListAdapter = new CreatedEventListAdapter(createdEventsRef.child(phone_number), this,
				R.layout.created_event, this);
		listView.setAdapter(createdEventListAdapter);
		
		// Delete event
		/*listView.setOnItemClickListener(new OnItemClickListener() {
			   @Override
			   public void OnItemLongClickListener(AdapterView<?> arg0, View arg1, int arg2,
			     long arg3) {
				   
			   }
		});		*/
		
		// Open popup
		listView.setOnItemClickListener(new OnItemClickListener() {
		   @Override
		   public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		     long arg3) {

			    final Dialog dialog = new Dialog(MainActivity.this);
			    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.created_event_details);
				
		        final TextView nameText = (TextView)dialog.findViewById(R.id.name);
		        final TextView dateText = (TextView)dialog.findViewById(R.id.date);
		        final TextView timeText = (TextView)dialog.findViewById(R.id.time);
		        
		        // From the event id, find the event from the events table
		        final String eventId = createdEventListAdapter.getItem(arg2).toString();
		        eventsRef.child(eventId).addValueEventListener(new ValueEventListener() {
				     @Override
				     public void onDataChange(DataSnapshot snapshot) {
				    	 Event event = snapshot.getValue(Event.class);
				    	 if (event != null) {
					    	 nameText.setText(event.getName());
					    	 dateText.setText(event.getDate().getDate());
					    	 timeText.setText(event.getDate().getTime());
					    	 eventStatusRef.child(eventId).addValueEventListener(new ValueEventListener() {
							     @Override
							     public void onDataChange(DataSnapshot snapshot) {
							    	 GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
							    	 Map<String, String> values = snapshot.getValue(t);
							         if (values != null) {
							        	 // Convert to list of maps
							        	 global = (GlobalClass) getApplication();
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
												    
											    	global = (GlobalClass) getApplication();
											    	for (Object attendee : attendees) {
											    		// Figure out the attendee name from the phone number
											    		String name = "";
											    		for (Entry<String, String> entry : global.friends.entrySet()) {
											    		    if (attendee.equals(entry.getValue())) {
											    		           name = entry.getKey();
											    		           break;
											    		    }
											    		}

											    		ListView listView = (ListView) dialog.findViewById(R.id.attendees);
											            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,
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
					    	 
					    	 dialog.show();
				    	 }
				     }

				     @Override
				     public void onCancelled() {
				         System.err.println("Listener was cancelled");
				     }
				});
		    
		   }
		         
		  });
		createdEventListAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				// listView.setSelection(chatListAdapter.getCount() - 1);
			}
		});
	}

	@Override
	public void onStop() {
		super.onStop();
		createdEventListAdapter.cleanup();
	}

	
	private void createEvent() {
		Date date = new Date(new_event_year, new_event_month, new_event_day, new_event_hour, new_event_minute);
		Event event = new Event(new_event_name, phone_number, date, new_event_attendees);
		
		// Add event to event list
		Firebase newEventRef = eventsRef.push();
		
		// Prioritize by date (getDateAsString) so that earlier events show up at the top
		newEventRef.setValue(event, event.getDate().getDateAsString());
		
		// Add event to user's created events
		createdEventsRef.child(phone_number).push().setValue(newEventRef.getName(), event.getDate().getDateAsString());
		
		// Add event to invitees' lists
		for(String attendee : new_event_attendees) {
			userEventsRef.child(attendee).child(newEventRef.getName()).setValue("0", event.getDate().getDateAsString());
		}
		
		// Allocate event status entry
		for(String attendee : new_event_attendees) {
			eventStatusRef.child(newEventRef.getName()).child(attendee).setValue("?");
		}
	}
	
	// This code sets the height of the ListView dynamically
    // http://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
    /*public static void setListViewHeightBasedOnChildren(ListView listView) {
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
    }*/
}
