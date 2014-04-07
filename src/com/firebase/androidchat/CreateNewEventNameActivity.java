package com.firebase.androidchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


public class CreateNewEventNameActivity extends Activity {
	private EditText inputText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_event_name);
		
		findViewById(R.id.next_button).setOnClickListener(
		new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				returnResult();
			}
		});
		

		EditText inputText = (EditText)findViewById(R.id.event_name);
		inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        		if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            		returnResult();
                }
                return true;
            }
        });
	}
	
	public void returnResult() {
		EditText inputText = (EditText)findViewById(R.id.event_name);
		String input = inputText.getText().toString();
		if (!input.equals("")) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra("input", input);
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		}
	}

}
