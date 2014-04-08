package com.firebase.androidchat;

import android.app.Dialog;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


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
		
		addDeleteListener(listView);
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
	
	// Remove me from this event
	protected void addDeleteListener(ListView listView) {
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int pos, long arg3) {
 				final String eventId = listAdapter.getItem(pos).toString();
 				
 				TextView nameText = (TextView) arg1.findViewById(R.id.name);
 				final String name = nameText.getText().toString();
            	
            	final Dialog dialog = new Dialog(EventsActivity.this);
 			    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
 				dialog.setContentView(R.layout.dialog);
 				TextView text = (TextView) dialog.findViewById(R.id.message);
 				text.setText("Remove " + name + "?");
 				Button buttonOkay = (Button) dialog.findViewById(R.id.okay_button);
 				buttonOkay.setOnClickListener(new OnClickListener() {
 					@Override
 					public void onClick(View v) {
 						removeAttendeeFromEvent(eventId, global.phone_number);
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
}