package com.firebase.androidchat;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class TabHostActivity extends TabActivity  {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_host);
		
		TabHost tabHost = getTabHost();
        
        TabSpec eventsSpec = tabHost.newTabSpec("Events");
        Button eventsView = new Button(this);
        eventsView.setText("Events");
        eventsSpec.setIndicator(eventsView);
        eventsSpec.setContent(new Intent(this, EventsActivity.class));
        
        TabSpec createdEventsSpec = tabHost.newTabSpec("Created Events");
        Button createdEventsView = new Button(this);
        createdEventsView.setText("Created Events");
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

}
