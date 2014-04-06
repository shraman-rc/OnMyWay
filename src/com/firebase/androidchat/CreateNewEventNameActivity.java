package com.firebase.androidchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class CreateNewEventNameActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_event_name);
		findViewById(R.id.next_button).setOnClickListener(
		new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				EditText inputText = (EditText) findViewById(R.id.event_name);
				String input = inputText.getText().toString();
				if (!input.equals("")) {
					Intent resultIntent = new Intent();
					resultIntent.putExtra("input", input);
					setResult(Activity.RESULT_OK, resultIntent);
					finish();
				}

			}
		});
	}

}
