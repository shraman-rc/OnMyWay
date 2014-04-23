package com.firebase.androidchat;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


public class CreateNewEventLocationActivity extends Activity {
	private EditText inputText;
	private int MAX_SEARCH_RESULTS = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_event_location);
		
		findViewById(R.id.search_button).setOnClickListener(
		new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				searchForLocation();
			}
		});
		

		EditText inputText = (EditText)findViewById(R.id.event_address);
		inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        		if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
        			searchForLocation();
                }
                return true;
            }
        });
	}
	
	public void searchForLocation() {
		EditText inputText = (EditText)findViewById(R.id.event_address);
		String input = inputText.getText().toString();
		if (!input.equals("")) {
			// Hide the keyboard
			InputMethodManager inputManager = (InputMethodManager) this
		            .getSystemService(Context.INPUT_METHOD_SERVICE);
		    View v=this.getCurrentFocus();
		    inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			
			Geocoder geoCoder = new Geocoder(this);
			try {
				List<Address> results = geoCoder.getFromLocationName(input, MAX_SEARCH_RESULTS);
				if (results != null && results.size() > 0) {
					final Address address = results.get(0);
					
			        // Print address to screen
				    Context mContext = getApplicationContext();
				    LinearLayout addressLayout = (LinearLayout) findViewById(R.id.address);
				    addressLayout.removeAllViews();

					int n = address.getMaxAddressLineIndex();
					for (int i = 0; i < n; ++i) {
						System.out.println(address.getAddressLine(i));
					    TextView textView = new TextView(mContext);
					    textView.setText(address.getAddressLine(i)); 
					    textView.setGravity(Gravity.CENTER_HORIZONTAL);
					    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
					    addressLayout.addView(textView);
					}
					   
					// Add next button
					Button nextButton = new Button(mContext);
					nextButton.setText("Next");
					LayoutParams params = new LayoutParams(
					        LayoutParams.MATCH_PARENT,      
					        LayoutParams.WRAP_CONTENT
					);
					params.setMargins(0, 200, 0, 0);
					nextButton.setLayoutParams(params);
					nextButton.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							returnResult(address);
						}
					});
					
					addressLayout.addView(nextButton);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public void returnResult(Address address) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("input", address);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

}
