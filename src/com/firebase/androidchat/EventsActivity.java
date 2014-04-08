package com.firebase.androidchat;

import com.firebase.client.ValueEventListener;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.widget.ListView;


public class EventsActivity extends MainActivity {
	private ValueEventListener connectedListener;
	private EventListAdapter eventListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
	}
	
	@Override
	public void onStart() {
		super.onStart();
		final ListView listView = getListView();
		eventListAdapter = new EventListAdapter(userEventsRef.child(phone_number), this,
				R.layout.event, this);
		listView.setAdapter(eventListAdapter);
		
		addDeleteListener(listView);
		addPopupListener(listView);
		
		eventListAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
			}
		});
	}
	
	@Override
	public void onStop() {
		super.onStop();
		eventListAdapter.cleanup();
	}
}