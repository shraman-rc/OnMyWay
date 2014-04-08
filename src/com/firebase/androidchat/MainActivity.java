package com.firebase.androidchat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
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
	
	// Create new event button
	protected void addNewEventButton() {
		findViewById(R.id.create_event_button).setOnClickListener(
		new View.OnClickListener() {
			@Override
			public void onClick(View view) {
		         Intent i = new Intent(MainActivity.this, CreateNewEventNameActivity.class);    
		         startActivityForResult(i, 2);
			}
		});
	}
	
	// Open popup for attendee status listener
	protected void addPopupListener(ListView listView, final int layout) {
		listView.setOnItemClickListener(new OnItemClickListener() {
		   @Override
		   public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		     long arg3) {

			    dialog.setContentView(layout);
			    
		        final TextView nameText = (TextView)dialog.findViewById(R.id.name);
		        final TextView dateText = (TextView)dialog.findViewById(R.id.date);
		        final TextView timeText = (TextView)dialog.findViewById(R.id.time);
		        		        
		        // From the event id, find the event from the events table
		        final String eventId = listAdapter.getItem(arg2).toString();
		        global.eventsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
				     @Override
				     public void onDataChange(DataSnapshot snapshot) {
				    	 Event event = snapshot.getValue(Event.class);
				    	 if (event != null) {
					    	 nameText.setText(event.getName());
					    	 dateText.setText(event.getDate().getDate());
					    	 timeText.setText(event.getDate().getTime());
					    	 
					    	 // Listen to event status and remove listener when the dialog is closed
					    	 final ValueEventListener statusListener = global.eventStatusRef.child(eventId).addValueEventListener(new ValueEventListener() {
							     @Override
							     public void onDataChange(DataSnapshot snapshot) {
							    	 GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
							    	 Map<String, String> values = snapshot.getValue(t);
							         if (values != null) {
							        	 // Convert to list of maps
							        	 global = (GlobalClass) getApplication();
							        	 List<Map<String, String>> statuses = new ArrayList();
							        	 for(Entry<String, String> entry : values.entrySet()) {
							        		 Map<String, String> newEntry = new HashMap<String, String>();
							        		 
							        		 // Figure out the attendee name
							        		 String name = "";
								    		for (Entry<String, String> entry2 : global.friends.entrySet()) {
								    		    if (entry.getKey().equals(entry2.getValue())) {
								    		           name = entry2.getKey();
								    		           break;
								    		    }
								    		}
							        		 newEntry.put("name", name);
							        		 newEntry.put("status", entry.getValue());
							        		 statuses.add(newEntry);
							        	 }
							        	 
							        	 final List<Map<String, String>> finalStatuses = statuses;
							        	 
							        	 // Get the event
								    	 global.eventsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
										     @Override
										     public void onDataChange(DataSnapshot snapshot) {
										    	 Event event = snapshot.getValue(Event.class);
										    	 if (event != null) {
										    		// Populate the attendees list
												    final List attendees = event.getAttendees();
												    
											        // Add listener to the ping button
											        final Button pingButton = (Button)dialog.findViewById(R.id.ping_button);
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
												    
											    	global = (GlobalClass) getApplication();
											    	for (Object attendee : attendees) {
											    		// Figure out the attendee name from the phone number
											    		String name = "";
											    		for (Entry<String, String> entry : global.friends.entrySet()) {
											    		    if (attendee.equals(entry.getValue())) {
											    		           name = entry.getKey();
											    		           break;
											    		    }
											    		}

											    		ListView listView = (ListView) dialog.findViewById(R.id.attendees);
											            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,
											            		  finalStatuses, 
											            	      R.layout.rowlayout, 
											            	      new String[] {"name", "status"}, 
											            	      new int[] {R.id.name, R.id.status});
											     		listView.setAdapter(adapter);  
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
				     }

				     @Override
				     public void onCancelled() {
				         System.err.println("Listener was cancelled");
				     }
				});
		    
		   }
		         
		});
	}
	
	// Ping all
	protected void pingAll(String eventId, List attendees) {
		Calendar c = Calendar.getInstance(); 
		int seconds = c.get(Calendar.SECOND);
		for(Object attendee : attendees) {
			// Update ping table
			
			global.userPingsRef.child(attendee.toString()).child(eventId).setValue(seconds);
		}
	}

	// Remove from event list and return removed event
	protected void creatorRemoveEvent(final String eventId) {
		
		// Remove event from this user's created events
		global.createdEventsRef.child(global.phone_number).child(eventId).removeValue();
		
		// Remove entry from event status
		global.eventStatusRef.child(eventId).removeValue();
		
		global.eventsRef.addChildEventListener(new ChildEventListener() {
		    @Override
		    public void onChildAdded(DataSnapshot snapshot, String previousChildName) {

		    }

		    @Override
		    public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

		    }

		    @Override
		    public void onChildRemoved(DataSnapshot snapshot) {
		    	Event event = snapshot.getValue(Event.class);
		    	removeEventAttendees(eventId, event);
		    }

		    @Override
		    public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

		    }

		    @Override
		    public void onCancelled() {

		    }
		});	
		global.eventsRef.child(eventId).removeValue();
	}
	
	protected void removeEventAttendees(String eventId, Event event) {
		if (event != null) {
			for(Object attendee : event.getAttendees()) {
				global.userEventsRef.child(attendee.toString()).child(eventId).removeValue();
			}
			
			for(Object attendee : event.getAttendees()) {
				global.userPingsRef.child(attendee.toString()).child(eventId).removeValue();
			}
		}
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
