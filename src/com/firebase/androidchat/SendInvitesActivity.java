package com.firebase.androidchat;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SendInvitesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_invites);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_invites, menu);
		return true;
	}

}
