package com.firebase.androidchat;

import java.util.HashMap;
import java.util.Map;

public class GlobalClass extends android.app.Application {
	public Map<String, String> friends = new HashMap<String, String>();
	public String phone_number;
	public String display_name;
}
