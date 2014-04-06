package com.firebase.androidchat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

public class MainActivity extends ListActivity {

	// TODO: change this to your own Firebase URL
	private static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";

	private String phone_number;
	private String display_name;
	private String[] contacts = new String[] {
	        "Action", "Adventure", "Animation", "Children", "Comedy", "Documentary", "Drama",
	        "Foreign", "History", "Independent", "Romance", "Sci-Fi", "Television", "Thriller"
	    };;
	private Firebase eventsRef;
	private Firebase usersRef;
	private Firebase createdEventsRef;
	private Firebase userEventsRef;
	private Firebase friendsRef;
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
		
		// Make sure user has phone number and display name
		setupUser();
		setTitle(((display_name == null) ? phone_number : display_name) + "'s Created Events");

		// Create new event button
		findViewById(R.id.create_event_button).setOnClickListener(
		new View.OnClickListener() {
			@Override
			public void onClick(View view) {
		         //Intent i = new Intent(MainActivity.this, CreateNewEventAttendeesActivity.class);    
		         //startActivityForResult(i, 5);
				Intent i = new Intent(MainActivity.this, SelectFriendsActivity.class);
				startActivityForResult(i, 50);
			}
		});
		
        // This is the chat box at the bottom of the screen
		EditText inputText = (EditText) findViewById(R.id.messageInput);
		inputText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView,
							int actionId, KeyEvent keyEvent) {
						if (actionId == EditorInfo.IME_NULL
								&& keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
							createEvent();
						}
						return true;
					}
				});

		// This is also for the chat box
		findViewById(R.id.sendButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						createEvent();
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

		GlobalClass global = (GlobalClass) getApplication();
		System.out.println(global.friends);
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
		    	
		    }
	    }
		
	}
	
	// Stores display name in shared prefs and database
	private void storeDisplayName(String display_name) {
		this.display_name = display_name;
		
		// Store display name in database
        // usersRef.child(phone_number).setValue(display_name);
        usersRef.child(phone_number).setValue(new User(display_name, phone_number));
        
        // Update the app title bar
        setTitle(((display_name == null) ? phone_number : display_name) + "'s Created Events");

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
				R.layout.event, this);
		listView.setAdapter(createdEventListAdapter);
		createdEventListAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				// listView.setSelection(chatListAdapter.getCount() - 1);
			}
		});

		// Firebase code to indicate connection status
		connectedListener = eventsRef.getRoot().child(".info/connected")
				.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						boolean connected = (Boolean) dataSnapshot.getValue();
						if (connected) {
							Toast.makeText(MainActivity.this,
									"Connected to Firebase", Toast.LENGTH_SHORT)
									.show();
						} else {
							Toast.makeText(MainActivity.this,
									"Disconnected from Firebase",
									Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onCancelled() {
						// No-op
					}
				});
	}

	@Override
	public void onStop() {
		super.onStop();
		eventsRef.getRoot().child(".info/connected")
				.removeEventListener(connectedListener);
		usersRef.getRoot().child(".info/connected")
		.removeEventListener(connectedListener);
		createdEventListAdapter.cleanup();
	}

	
	private void createEvent() {
		EditText inputText = (EditText) findViewById(R.id.messageInput);
		String input = inputText.getText().toString();
		if (!input.equals("")) {
			Calendar c = Calendar.getInstance(); 
			int seconds = c.get(Calendar.SECOND);
			
			Date curr = new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(Calendar.HOUR), c.get(Calendar.MINUTE));
			// Date curr = new Date(2015,1,2,3,4);
			List list = new ArrayList();
			list.add("15555215554");
			list.add("14255039234");
			Event event = new Event(input, phone_number, curr, list);
			
			// Add event to event list
			Firebase newEventRef = eventsRef.push();
			// Prioritize by date (getDateAsString) so that earlier events show up at the top
			newEventRef.setValue(event, event.getDate().getDateAsString());
			
			// Add event to user's created events
			createdEventsRef.child(phone_number).push().setValue(newEventRef.getName(), event.getDate().getDateAsString());
			inputText.setText("");
		}
	}
}
