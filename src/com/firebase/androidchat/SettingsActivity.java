package com.firebase.androidchat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	protected GlobalClass global;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		global = (GlobalClass) getApplication();
		addChangeDisplayNameButton();
		addSelectFriendsButton();
	}
	
	// Change display name button
	protected void addChangeDisplayNameButton() {
		findViewById(R.id.change_display_name_button).setOnClickListener(
		new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getNewDisplayName();
			}
		});
	}
	
	// Select friends button
	protected void addSelectFriendsButton() {
		findViewById(R.id.select_friends_button).setOnClickListener(
		new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(SettingsActivity.this, SelectFriendsActivity.class);
				startActivityForResult(i, 6);
			}
		});
	}
	
	// Same as the one in TabHostActivity
	private void getNewDisplayName() {
		// Prompt for display name 
        final Dialog dialog = new Dialog(SettingsActivity.this);
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
	
	// Also copied from TabHostActivity
	private void storeDisplayName(String display_name) {
		global.display_name = display_name;
		
		// Store display name in database
		global.usersRef.child(global.phone_number).setValue(global.display_name);
        
        // Store display name in shared prefs
		SharedPreferences prefs = getApplication().getSharedPreferences(
				"OnMyWayPrefs", 0);
	    prefs.edit().putString("display_name", display_name).commit();
	}	
}
