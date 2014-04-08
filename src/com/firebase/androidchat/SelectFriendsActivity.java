package com.firebase.androidchat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class SelectFriendsActivity extends ListActivity{

	private static final int PICK_CONTACT_REQUEST = 1;
	private Map<String, String> friends = new HashMap<>();
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
        refreshList();
		
		// Delete friend
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int pos, long arg3) {
            	TextView nameText = (TextView) arg1.findViewById(R.id.name);
 				final String name = nameText.getText().toString();
            	
            	final Dialog dialog = new Dialog(SelectFriendsActivity.this);
 			    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
 				dialog.setContentView(R.layout.dialog);
 				TextView text = (TextView) dialog.findViewById(R.id.message);
 				text.setText("Remove " + name + "?");
 				Button buttonOkay = (Button) dialog.findViewById(R.id.okay_button);
 				buttonOkay.setOnClickListener(new OnClickListener() {
 					@Override
 					public void onClick(View v) {
 						removeFriend(name);
 						refreshList();
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

	private void pickContact() {
	    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
	    pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
	    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
	}
	
	private boolean removeFriend(String name) {
		return friends.remove(name) != null;
	}
	
	private void refreshList() {
		String[] names = friends.keySet().toArray(new String[friends.size()]);
        Arrays.sort(names);
        adapter = new ArrayAdapter<String>(this,
		        R.layout.rowlayout, R.id.name, names);
		setListAdapter(adapter);
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
	            
	           // Format the number properly
		        number = number.replaceAll("\\D+","");
		    	number = (number.length() < 11) ? "1" + number : number;
	            
                friends.put(name, number);
                refreshList();
	    		
	        }
	    }
	}
}
