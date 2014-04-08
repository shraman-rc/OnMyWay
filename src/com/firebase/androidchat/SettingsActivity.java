package com.firebase.androidchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends Activity {
	protected GlobalClass global;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		global = (GlobalClass) getApplication();
		addSelectFriendsButton();
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
}
