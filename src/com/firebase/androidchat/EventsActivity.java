package com.firebase.androidchat;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.widget.ListView;


public class EventsActivity extends MainActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		final ListView listView = getListView();
		listAdapter = new EventListAdapter(global.userEventsRef.child(global.phone_number), this,
				R.layout.event, this);
		listView.setAdapter(listAdapter);
		
		addPopupListener(listView, R.layout.event_details);
		
		listAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
			}
		});
	}
	
	@Override
	public void onStop() {
		super.onStop();
		listAdapter.cleanup();
	}
}