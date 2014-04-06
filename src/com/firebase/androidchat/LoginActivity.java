package com.firebase.androidchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		findViewById(R.id.sign_in_button).setOnClickListener(
		new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				EditText inputText = (EditText) findViewById(R.id.editText1);
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
