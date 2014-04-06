package com.firebase.androidchat;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.Firebase;

public class SelectFriendsActivity extends ListActivity{

	private static final int PICK_CONTACT_REQUEST = 1;
	private Map<String, String> friends = new TreeMap<>();
	private ArrayAdapter<String> adapter;
	
	private static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_friends);
		
		// Retrieve friends list from global class
		GlobalClass global = (GlobalClass) getApplication();
		if(global.friends != null) {
			this.friends = global.friends;
		}
    	
		// Return button
		findViewById(R.id.return_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	GlobalClass global = (GlobalClass) getApplication();
            	global.friends = friends;
            	Firebase friendsRef = new Firebase(FIREBASE_URL).child("friends");
            	friendsRef.child(global.phone_number).setValue(friends);
				finish();
            }
        });
        
		// Add friend button
		findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	pickContact();
            }
        });
		
		final ListView listView = getListView();
		adapter = new ArrayAdapter<String>(this,
		        R.layout.rowlayout, R.id.name, new ArrayList<String>(friends.keySet()));
		setListAdapter(adapter);  
	}

	// Android's stuff to pick a contact from the phone
	private void pickContact() {
	    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
	    pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
	    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
	}
	
	//Getting the results from picking a singular contact
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
	    if (requestCode == PICK_CONTACT_REQUEST) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	            Uri contactUri = data.getData();
	            String[] projection = {Phone.NUMBER, Phone.DISPLAY_NAME};
	            Cursor cursor = getContentResolver()
	                    .query(contactUri, projection, null, null, null);
	            cursor.moveToFirst();

	            int column = cursor.getColumnIndex(Phone.DISPLAY_NAME);
	            String name = cursor.getString(column);
	            
	            column = cursor.getColumnIndex(Phone.NUMBER);
	            String number = cursor.getString(column);
	            
	            // TreeMap automatically sorts contacts in alphabetical order
                friends.put(name, number);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	    		        R.layout.rowlayout, R.id.name, new ArrayList<String>(friends.keySet()));
	    		setListAdapter(adapter);
                // adapter.notifyDataSetChanged();
	        }
	    }
	}
}
