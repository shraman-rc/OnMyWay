package com.firebase.androidchat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

public class MainActivity extends ListActivity {
	protected GlobalClass global;
	protected EventListAdapter listAdapter;
	protected Dialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		global = (GlobalClass) getApplication();
		dialog = new Dialog(MainActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	// Open popup for attendee status listener
	protected void addPopupListener(final ListView listView, final int layout) {
		listView.setOnItemClickListener(new OnItemClickListener() {
		   @Override
		   public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		     long arg3) {

			     dialog.setContentView(layout);
			    
		         TextView nameText = (TextView)dialog.findViewById(R.id.name);
		         TextView dateText = (TextView)dialog.findViewById(R.id.date);
		         TextView timeText = (TextView)dialog.findViewById(R.id.time);
		        		        
		         final Event event = (Event) listAdapter.getItem(arg2);
		    	 nameText.setText(event.getName());
		    	 dateText.setText(event.getDate().getDate());
		    	 timeText.setText(event.getDate().getTime());
		    	 
		    	 // Listen to event status and remove listener when the dialog is closed
		    	 final String eventId = listAdapter.getIdOfItem(arg2);
		    	 final ValueEventListener statusListener = global.eventStatusRef.child(eventId).addValueEventListener(new ValueEventListener() {
				     @Override
				     public void onDataChange(DataSnapshot snapshot) {
				    	 GenericTypeIndicator<Map<String,Map<String, String>>> t = new GenericTypeIndicator<Map<String,Map<String, String>>>() {};
				    	 final Map<String, Map<String, String>> statusMap = snapshot.getValue(t);
				    	 if (statusMap != null) {
				    		 final List<Map<String, String>> statuses = new ArrayList<>(statusMap.values());
				        	 // Get the event attendees
					    	 global.eventsRef.child(eventId).child("attendees").addListenerForSingleValueEvent(new ValueEventListener() {
							     @Override
							     public void onDataChange(DataSnapshot snapshot) {
							    	 GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
							    	 final Map<String, String> attendees = snapshot.getValue(t);
							    	 if (attendees != null) {
								        // Set the adapter
							    		ListView listView = (ListView) dialog.findViewById(R.id.attendees);
							            final SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, statuses, R.layout.rowlayout, 
							            	      new String[] {"name", "status"}, new int[] {R.id.name, R.id.status});
							     		listView.setAdapter(adapter);
							     		
							     		// Add listener for ping button and deletion of attendees if user is creator
							     		if (event.getCreator().equals(global.phone_number)) {
								    		final Button pingButton = (Button)dialog.findViewById(R.id.ping_button);
								    		addPingButtonListener(pingButton, eventId, attendees);
							     			addAttendeeDeleteListener(listView, adapter, statusMap, eventId);
							     		}
							    	 }
							     }

							     @Override
							     public void onCancelled() {
							         System.err.println("Listener was cancelled");
							     }
							});
				         }
				     }

				     @Override
				     public void onCancelled() {
				         System.err.println("Listener was cancelled");
				     }
				 });
		    	 
				 dialog.setOnDismissListener(new OnDismissListener() {
				    @Override
				    public void onDismiss(DialogInterface dialogInterface) {
				    	global.eventStatusRef.removeEventListener(statusListener);
				    }
			     });
		    	 
		    	 dialog.show();		    
		   }
		         
		});
	}
	
	// Add ping button listener
	protected void addPingButtonListener(final Button pingButton, final String eventId, final Map<String, String> attendees) {
		if (pingButton != null) {
        	pingButton.setOnClickListener(
    		new View.OnClickListener() {
    			@Override
    			public void onClick(View view) {
    				pingButton.setEnabled(false);
    				pingButton.setText("Pings Sent!");
    		        pingAll(eventId, attendees);
    			}
    		});
        }
	}
	
	// Ping all
	protected void pingAll(String eventId, Map<String, String> attendees) {
		Calendar c = Calendar.getInstance(); 
		int seconds = c.get(Calendar.SECOND);
		for(Object attendee : attendees.keySet()) {
			// Don't ping the creator
			if (!attendee.toString().equals(global.phone_number)) {
				global.userPingsRef.child(attendee.toString()).child(eventId).setValue(System.currentTimeMillis());
			}
		}
	}
	
	// Add listener for deletion of attendees
	protected void addAttendeeDeleteListener(ListView listView, final SimpleAdapter adapter, final Map<String, Map<String, String>> statusMap, final String eventId) {
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int pos, long arg3) {
            	final Map<String, String> entry = (Map<String, String>) adapter.getItem(pos);
 				
 				TextView nameText = (TextView) arg1.findViewById(R.id.name);
 				final String name = nameText.getText().toString();
            	
 				String number = "";
            	// Find the user phone number
				for (Entry<String, Map<String, String>> e : statusMap.entrySet()) {
					if (e.getValue().get("name").equals(name)) {
						number = e.getKey();
					}
				}
				final String finalNumber = number;
            	
            	// Creator cannot delete self
				if (!number.equals(global.phone_number)) {
					final Dialog dialog = new Dialog(MainActivity.this);
	 			    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	 				dialog.setContentView(R.layout.dialog);
	 				TextView text = (TextView) dialog.findViewById(R.id.message);
	 				text.setText("Remove " + name + "?");
	 				Button buttonOkay = (Button) dialog.findViewById(R.id.okay_button);
	 				buttonOkay.setOnClickListener(new OnClickListener() {
	 					@Override
	 					public void onClick(View v) {
	 						removeAttendeeFromEvent(eventId, finalNumber);
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
				}
				return true;
            }
        });
	}

	// Remove from event list and return removed event
	protected void creatorRemoveEvent(final String eventId) {
		global.createdEventsRef.child(global.phone_number).child(eventId).removeValue();
		global.eventStatusRef.child(eventId).removeValue();
		global.eventsRef.child(eventId).child("attendees").addListenerForSingleValueEvent(new ValueEventListener() {
		     @Override
		     public void onDataChange(DataSnapshot snapshot) {
		    	 GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
		    	 final Map<String, String> attendees = snapshot.getValue(t);
		    	 if (attendees != null) {
		    		 removeEventAttendees(eventId, attendees);
		    		 global.eventsRef.child(eventId).removeValue();
		    	 }
		     }

		     @Override
		     public void onCancelled() {
		         System.err.println("Listener was cancelled");
		     }
		});

		
		
	}
	
	protected void removeEventAttendees(String eventId, Map<String, String> attendees) {
		if (attendees != null) {
			for(String attendee : attendees.keySet()) {
				global.userEventsRef.child(attendee).child(eventId).removeValue();
			}
			
			for(String attendee : attendees.keySet()) {
				global.userPingsRef.child(attendee).child(eventId).removeValue();
			}
		}
	}
	
	protected void removeAttendeeFromEvent(String eventId, String attendee) {
		global.eventStatusRef.child(eventId).child(attendee).removeValue();
		global.eventsRef.child(eventId).child("attendees").child(attendee).removeValue();
		global.userEventsRef.child(attendee).child(eventId).removeValue();
		global.userPingsRef.child(attendee).child(eventId).removeValue();
	}
	
	/*protected Object getItem(EventListAdapter listAdapter, int position) {
		return listAdapter.getItem(position);
	}*/
	
	// This code sets the height of the ListView dynamically
    // http://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
    /*public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }*/
}
