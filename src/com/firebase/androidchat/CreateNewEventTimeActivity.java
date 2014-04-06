package com.firebase.androidchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TimePicker;


public class CreateNewEventTimeActivity extends Activity {
private TimePicker timePicker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_event_time);
        // Add the time picker to the layout
	    Context mContext = getApplicationContext();
	    LinearLayout mainLayout = (LinearLayout) findViewById(R.id.timePickerLayout);
	    
	    timePicker = new TimePicker(mContext);
	    timePicker.setBackgroundColor(-16777216);
	    
	    mainLayout.addView(timePicker);

		findViewById(R.id.next_button).setOnClickListener(
		new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra("hour", timePicker.getCurrentHour());
				resultIntent.putExtra("minute", timePicker.getCurrentMinute());
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		});		

	}

}
