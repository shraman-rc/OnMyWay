package com.firebase.androidchat;

import java.util.Map;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

public class TabHostActivity extends TabActivity  {
	private GlobalClass global;
	private ValueEventListener connectedListener;
	private Dialog connectedDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_host);
		
		global = (GlobalClass) getApplication();
		
		// Make sure user is connected to the database at all times
		connectedDialog = new Dialog(TabHostActivity.this);
        connectedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        connectedDialog.setCancelable(false);
        connectedDialog.setContentView(R.layout.connection_status);
        
		connectedListener = global.ref.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean)dataSnapshot.getValue();
                if (connected) {
                    // Toast.makeText(TabHostActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                    try {
                		connectedDialog.dismiss();
                    } catch (Exception e) {
                    	
                    }
                } else {
                    // Toast.makeText(TabHostActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                	//connectedDialog.onAttachedToWindow
                	connectedDialog.show();
                }
            }

            @Override
            public void onCancelled() {
                // No-op
            }
        });
		
		// Make sure user has phone number and display name
		setupUser();
		
		// Start the background service
		if (global.phone_number != null) {
			Intent serviceIntent = new Intent(this, BackgroundService.class);
			serviceIntent.putExtra("phone_number", global.phone_number);
			serviceIntent.putExtra("display_name", global.display_name);
			startService(serviceIntent);
		}

		TabHost tabHost = getTabHost();
        
        TabSpec eventsSpec = tabHost.newTabSpec("Events");
        Button eventsView = new Button(this);
        eventsView.setText("Events");
        eventsSpec.setIndicator(eventsView);
        eventsSpec.setContent(new Intent(this, EventsActivity.class));
        
        TabSpec createdEventsSpec = tabHost.newTabSpec("Created Events");
        Button createdEventsView = new Button(this);
        createdEventsView.setText("My Events");
        createdEventsSpec.setIndicator(createdEventsView);
        createdEventsSpec.setContent(new Intent(this, CreatedEventsActivity.class));
        
        TabSpec settingsSpec = tabHost.newTabSpec("Settings");
        Button settingsView = new Button(this);
        settingsView.setText("Settings");
        settingsSpec.setIndicator(settingsView);
        settingsSpec.setContent(new Intent(this, SettingsActivity.class));

        tabHost.addTab(eventsSpec);
        tabHost.addTab(createdEventsSpec);
        tabHost.addTab(settingsSpec);
	}
	
	private void setupUser() {
		// Get shared prefs from phone data
		SharedPreferences prefs = getApplication().getSharedPreferences(
				"OnMyWayPrefs", 0);
		// prefs.edit().clear().commit();
		// System.out.println("Current prefs: " + prefs.getAll());
		global.phone_number = prefs.getString("phone_number", null);
		global.display_name = prefs.getString("display_name", null);
		if (global.display_name != null) {
			storeDisplayName(global.display_name);
		} else {
			getNewDisplayName();
		}
		

		if (global.phone_number == null) {
			TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String number = tMgr.getLine1Number();
			number = number.replaceAll("\\D+","");
			global.phone_number = (number.length() < 11) ? "1" + number : number;
			prefs.edit().putString("phone_number", global.phone_number).commit();
		}
		
		// Retrieve friends list if exists
		global.friendsRef.child(global.phone_number).addListenerForSingleValueEvent(new ValueEventListener() {
		     @Override
		     public void onDataChange(DataSnapshot snapshot) {
		    	 GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
		    	 Map<String, String> value = snapshot.getValue(t);
		         if (value != null) {
			         global.friends = value;
		         }
		     }

		     @Override
		     public void onCancelled() {
		         System.err.println("Listener was cancelled");
		     }
		 });

	}
	
	// Stores display name in shared prefs, global, and database
	private void storeDisplayName(String display_name) {
		global.display_name = display_name;
		
		// Store display name in database
		global.usersRef.child(global.phone_number).setValue(global.display_name);
        
        // Store display name in shared prefs
		SharedPreferences prefs = getApplication().getSharedPreferences(
				"OnMyWayPrefs", 0);
	    prefs.edit().putString("display_name", display_name).commit();
	}
	
	private void getNewDisplayName() {
		// Prompt for display name 
        final Dialog dialog = new Dialog(TabHostActivity.this);
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    dialog.setCancelable(false);
		dialog.setContentView(R.layout.display_name);
		dialog.findViewById(R.id.sign_in_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				EditText inputText = (EditText) dialog.findViewById(R.id.display_name);
				String input = inputText.getText().toString();
				if (!input.equals("")) {
					global.display_name = input;
		            storeDisplayName(global.display_name);
		            dialog.dismiss();
				}
			}
		});
		EditText displayNameText = (EditText)dialog.findViewById(R.id.display_name);
		displayNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        		if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
					String input = textView.getText().toString();
					if (!input.equals("")) {
			            storeDisplayName(input);
			            dialog.dismiss();
					}
                }
                return true;
            }
        });
		dialog.show();
	}
}
