package com.firebase.androidchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

public class MainActivity extends ListActivity {
    private static final String FIREBASE_URL = "https://cefbbpiir8y.firebaseio-demo.com/";
	private String phone_number;
	private String display_name;
	private Firebase eventsRef = new Firebase(FIREBASE_URL).child("events");
	private Firebase createdEventsRef = new Firebase(FIREBASE_URL).child("createdEvents");
	private Firebase userEventsRef = new Firebase(FIREBASE_URL).child("userEvents");
	private Firebase eventStatusRef = new Firebase(FIREBASE_URL).child("eventStatus");
	private ValueEventListener connectedListener;
	private CreatedEventListAdapter createdEventListAdapter;
	private GlobalClass global;
	
	// Used with new event creation
	private String new_event_name;
	private int new_event_year;
	private int new_event_month;
	private int new_event_day;
	private int new_event_hour;
	private int new_event_minute;
	private List<String> new_event_attendees;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		global = (GlobalClass) getApplication();
		phone_number = global.phone_number;
		display_name = global.display_name;
		
		setContentView(R.layout.activity_main);
		addNewEventButton();
		addSelectFriendsButton();
	}
	
	

	@Override
	public void onStart() {
		super.onStart();
		final ListView listView = getListView();
		createdEventListAdapter = new CreatedEventListAdapter(createdEventsRef.child(phone_number), this,
				R.layout.created_event, this);
		listView.setAdapter(createdEventListAdapter);
		
		addDeleteListener(listView);
		addPopupListener(listView);
		
		createdEventListAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
			}
		});
	}
	
	
	
	@Override
	public void onStop() {
		super.onStop();
		createdEventListAdapter.cleanup();
	}
	
	// Activity results for creating new event
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Created event name
		if (requestCode == 2) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
		        if(extras != null) {
		        	new_event_name = extras.getString("input");
		            Intent i = new Intent(MainActivity.this, CreateNewEventDateActivity.class);    
   		            startActivityForResult(i, 3);
		        }
		    }
	    }
		
		// Created event date
		if (requestCode == 3) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
		        if(extras != null) {
		        	new_event_year = extras.getInt("year");
		        	new_event_month = extras.getInt("month");
		        	new_event_day = extras.getInt("day");
		            Intent i = new Intent(MainActivity.this, CreateNewEventTimeActivity.class);    
   		            startActivityForResult(i, 4);
		        }
		    }
	    }
		
		// Created event time
		if (requestCode == 4) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
		        if(extras != null) {
		        	new_event_hour = extras.getInt("hour");
		        	new_event_minute = extras.getInt("minute");
		            Intent i = new Intent(MainActivity.this, CreateNewEventAttendeesActivity.class);    
   		            startActivityForResult(i, 5);
		        }
		    }
	    }
		
		// Created event attendees
		if (requestCode == 5) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
		        if(extras != null) {
		        	new_event_attendees = extras.getStringArrayList("attendees");
		        	createEvent();
		        }
		    }
	    }		
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
	
	// Select friends button
	protected void addSelectFriendsButton() {
		findViewById(R.id.select_friends_button).setOnClickListener(
		new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this, SelectFriendsActivity.class);
				startActivityForResult(i, 6);
			}
		});
	}
	
	// Delete event feature
	protected void addDeleteListener(ListView listView) {
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int pos, long arg3) {
 				final String eventId = getItem(pos).toString();
 				
 				TextView nameText = (TextView) arg1.findViewById(R.id.name);
 				final String name = nameText.getText().toString();
            	
            	final Dialog dialog = new Dialog(MainActivity.this);
 			    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
 				dialog.setContentView(R.layout.dialog);
 				TextView text = (TextView) dialog.findViewById(R.id.message);
 				text.setText("Remove " + name + "?");
 				Button buttonOkay = (Button) dialog.findViewById(R.id.okay_button);
 				buttonOkay.setOnClickListener(new OnClickListener() {
 					@Override
 					public void onClick(View v) {
 						removeEvent(eventId);
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
	
	// Open popup for attendee status listener
	protected void addPopupListener(ListView listView) {
		listView.setOnItemClickListener(new OnItemClickListener() {
		   @Override
		   public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		     long arg3) {

			    final Dialog dialog = new Dialog(MainActivity.this);
			    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			    dialog.setContentView(R.layout.created_event_details);
			    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			    
		        final TextView nameText = (TextView)dialog.findViewById(R.id.name);
		        final TextView dateText = (TextView)dialog.findViewById(R.id.date);
		        final TextView timeText = (TextView)dialog.findViewById(R.id.time);
		        
		        // From the event id, find the event from the events table
		        final String eventId = createdEventListAdapter.getItem(arg2).toString();
		        eventsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
				     @Override
				     public void onDataChange(DataSnapshot snapshot) {
				    	 Event event = snapshot.getValue(Event.class);
				    	 if (event != null) {
					    	 nameText.setText(event.getName());
					    	 dateText.setText(event.getDate().getDate());
					    	 timeText.setText(event.getDate().getTime());
					    	 
					    	 // Listen to event status and remove listener when the dialog is closed
					    	 final ValueEventListener statusListener = eventStatusRef.child(eventId).addValueEventListener(new ValueEventListener() {
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
								    	 eventsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
										     @Override
										     public void onDataChange(DataSnapshot snapshot) {
										    	 Event event = snapshot.getValue(Event.class);
										    	 if (event != null) {
										    		// Populate the attendees list
												    List attendees = event.getAttendees();
												    
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
							    	eventStatusRef.removeEventListener(statusListener);
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

	protected void createEvent() {
		Date date = new Date(new_event_year, new_event_month, new_event_day, new_event_hour, new_event_minute);
		Event event = new Event(new_event_name, phone_number, date, new_event_attendees);
		
		// Add event to event list
		Firebase newEventRef = eventsRef.push();
		
		// Prioritize by date (getDateAsString) so that earlier events show up at the top
		newEventRef.setValue(event, event.getDate().getDateAsString());
		
		// Add event to user's created events
		createdEventsRef.child(phone_number).push().setValue(newEventRef.getName(), event.getDate().getDateAsString());
		
		// Add event to invitees' lists
		for(String attendee : new_event_attendees) {
			userEventsRef.child(attendee).child(newEventRef.getName()).setValue("0", event.getDate().getDateAsString());
		}
		
		// Allocate event status entry
		for(String attendee : new_event_attendees) {
			eventStatusRef.child(newEventRef.getName()).child(attendee).setValue("?");
		}
	}
	
	// Remove from event list and return removed event
	protected void removeEvent(final String eventId) {
		eventsRef.addChildEventListener(new ChildEventListener() {
		    @Override
		    public void onChildAdded(DataSnapshot snapshot, String previousChildName) {

		    }

		    @Override
		    public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

		    }

		    @Override
		    public void onChildRemoved(DataSnapshot snapshot) {
		    	Event event = snapshot.getValue(Event.class);
		    	removeEventDependencies(eventId, event);
		    }

		    @Override
		    public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

		    }

		    @Override
		    public void onCancelled() {

		    }
		});	
		eventsRef.child(eventId).removeValue();
	}
	
	protected void removeEventDependencies(String eventId, Event event) {
		if (event != null) {
			// Remove event from this user's created events
			createdEventsRef.child(phone_number).child(eventId).removeValue();
			
			// Remove from invitees' lists
			for(Object attendee : event.getAttendees()) {
				userEventsRef.child(attendee.toString()).child(eventId).removeValue();
			}
			
			// Remove entry from event status
			eventStatusRef.child(eventId).removeValue();
		}
	}
	
	protected Object getItem(int position) {
		return createdEventListAdapter.getItem(position);
	}
	
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
