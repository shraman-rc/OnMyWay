package com.firebase.androidchat;

import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

public class TabHostActivity extends TabActivity  {
	private static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";
	private String phone_number;
	private String display_name;
	private Firebase usersRef;
	private Firebase friendsRef;
	private GlobalClass global;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_host);
		
		global = (GlobalClass) getApplication();
		
		usersRef = new Firebase(FIREBASE_URL).child("users");
		friendsRef = new Firebase(FIREBASE_URL).child("friends");
		
		// Make sure user has phone number and display name
		setupUser();
		
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
        createdEventsSpec.setContent(new Intent(this, MainActivity.class));
        
        TabSpec settingsSpec = tabHost.newTabSpec("Settings");
        Button settingsView = new Button(this);
        settingsView.setText("Settings");
        settingsSpec.setIndicator(settingsView);
        settingsSpec.setContent(new Intent(this, SelectFriendsActivity.class));

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
		phone_number = prefs.getString("phone_number", null);
		global.phone_number = phone_number;
		display_name = prefs.getString("display_name", null);
		if (display_name != null) {
			storeDisplayName(display_name);
			global.display_name = display_name;
		}
		

		if (phone_number == null) {
			TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			phone_number = tMgr.getLine1Number();
			prefs.edit().putString("phone_number", phone_number).commit();
			global.phone_number = phone_number;
		}
		
		// If display name is not stored in shared prefs, try to get from database
		if (display_name == null) {
			usersRef.child(phone_number).addListenerForSingleValueEvent(new ValueEventListener() {
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
			             getNewDisplayName();
			         }
			     }

			     @Override
			     public void onCancelled() {
			         System.err.println("Listener was cancelled");
			     }
			 });
		}
		
		// Retrieve friends list if exists
		friendsRef.child(phone_number).addListenerForSingleValueEvent(new ValueEventListener() {
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
	
	// Stores display name in shared prefs, global, and database
	private void storeDisplayName(String display_name) {
		this.display_name = display_name;
		
		global.display_name = display_name;
		
		// Store display name in database
        usersRef.child(phone_number).setValue(new User(display_name, phone_number));
        
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
					display_name = input;
		            storeDisplayName(display_name);
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
