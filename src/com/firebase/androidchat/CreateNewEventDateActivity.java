package com.firebase.androidchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;


public class CreateNewEventDateActivity extends Activity {
	private DatePicker datePicker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_event_date);

        // Add the date picker to the layout
	    Context mContext = getApplicationContext();
	    LinearLayout mainLayout = (LinearLayout) findViewById(R.id.datePickerLayout);
	    
	    datePicker = new DatePicker(mContext);
	    datePicker.setCalendarViewShown(false);
	    datePicker.setBackgroundColor(-16777216);
	    
	    mainLayout.addView(datePicker);

		findViewById(R.id.next_button).setOnClickListener(
		new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra("year", datePicker.getYear());
				resultIntent.putExtra("month", datePicker.getMonth());
				resultIntent.putExtra("day", datePicker.getDayOfMonth());
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		});		

	}

}
