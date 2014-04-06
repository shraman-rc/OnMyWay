package com.firebase.androidchat;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;


public class CreateNewEventDateActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_event_date);
		
		
		
		final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
		
		
        // Add the date picker to the layout
	    Context mContext = getApplicationContext();
	    LinearLayout mainLayout = (LinearLayout) findViewById(R.id.datePickerLayout);
	    DatePicker datePicker = new DatePicker(mContext);
	    datePicker.setCalendarViewShown(false);
	    mainLayout.addView(datePicker);
	    
	    // Create a new LinearLayout
	    /*
	    LinearLayout newLinear = new LinearLayout(mContext);
	    newLinear.setLayoutParams(new LinearLayout.LayoutParams(
	            LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));



	    // first , I add button to the LinearLayout
	    newLinear.addView(test);

	    // Then, I add layout to the inflated layout
	    mainLayout.addView(newLinear);*/
	             
		
		
		
		findViewById(R.id.next_button).setOnClickListener(
		new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*EditText inputText = (EditText) findViewById(R.id.event_name);
				String input = inputText.getText().toString();
				if (!input.equals("")) {
					Intent resultIntent = new Intent();
					resultIntent.putExtra("input", input);
					setResult(Activity.RESULT_OK, resultIntent);
					finish();
				}*/

			}
		});		

	}

}
