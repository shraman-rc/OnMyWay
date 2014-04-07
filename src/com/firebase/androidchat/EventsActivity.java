package com.firebase.androidchat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

public class EventsActivity extends ListActivity {

	// TODO: change this to your own Firebase URL
	private static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";
	
	private GlobalClass global;
	private Firebase userEventsRef = new Firebase(FIREBASE_URL).child("userEvents");
	private Firebase ownEvents;
	
	private String phone_number;
	private String display_name;
	
	private EventListAdapter adapter;
	
	protected Map<String, String> events = new HashMap<String, String>();
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		//get preferences
		SharedPreferences prefs = getApplication().getSharedPreferences(
				"OnMyWayPrefs", 0);
		TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		phone_number = tMgr.getLine1Number();
		prefs.edit().putString("phone_number", phone_number).commit();
		System.out.println("prefs are " + prefs.getAll());
		phone_number = prefs.getString("phone_number", null);
		System.out.println("This is fucking up my shit" + phone_number);
		display_name = prefs.getString("display_name", null);
	}
	
	protected void onStart() {
		super.onStart();
		final ListView listView = getListView();
		System.out.println("fuck");
		System.out.println("this is" + phone_number);
		System.out.println(userEventsRef.child(phone_number));
		EventListAdapter eventListAdapter = new EventListAdapter(userEventsRef.child(phone_number), this,
				R.layout.created_event, this);
		listView.setAdapter(eventListAdapter);
	}
}