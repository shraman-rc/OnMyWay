package com.firebase.androidchat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

public class EventListAdapter extends FirebaseListAdapter<Event> {

    public EventListAdapter(Query ref, Activity activity, int layout, Context context) {
        super(ref, Event.class, layout, activity);
    }

    @Override
    protected void populateView(final View view, Event event) {
        TextView nameText = (TextView)view.findViewById(R.id.name);
        TextView dateText = (TextView)view.findViewById(R.id.date);
        TextView timeText = (TextView)view.findViewById(R.id.time);
        
        //Getting a well-formatted current time
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Calendar cal = Calendar.getInstance();
    	String dummyTime = dateFormat.format(cal.getTime());
    	int currentTime = Integer.parseInt(dummyTime.substring(5,7) + dummyTime.substring(8,10) + dummyTime.substring(11,13) + dummyTime.substring(14,16));
		 
		//Set Background Colors
		int eventTime = Integer.parseInt(event.getDate().getDateAsString().substring(4));
		if ((eventTime - currentTime < 5) && (eventTime - currentTime >= 0)) {view.setBackgroundColor(0xAAFF8585);
		}else if ((eventTime - currentTime < 30) && (eventTime - currentTime >= 5)) {view.setBackgroundColor(0xAAFFAD33);
		}else if (eventTime - currentTime >=30) {view.setBackgroundColor(0xAAA7FFA7);
		}else {view.setBackgroundColor(0x00000000);}
		 
		/*&& eventTime - currentTime < 2550
        view.getBackground().setAlpha(255-((eventTime-currentTime)/10))*/
		 
		nameText.setText(event.getName());
   	    dateText.setText(event.getDate().getDate());
   	    timeText.setText(event.getDate().getTime());
    }
}
