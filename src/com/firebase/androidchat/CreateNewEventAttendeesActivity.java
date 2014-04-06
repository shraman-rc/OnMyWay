package com.firebase.androidchat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;


public class CreateNewEventAttendeesActivity extends Activity {
	ListView list;
	Button but;
	ItemsAdapter adapter;
	String[] names;
	GlobalClass global;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_event_attendees);
		
        list = (ListView)findViewById(R.id.list);
        but = (Button)findViewById(R.id.but);
        global = (GlobalClass) getApplication();
        names = global.friends.keySet().toArray(new String[global.friends.size()]);
        Arrays.sort(names);
        adapter = new ItemsAdapter(this, names);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {

		   @Override
		   public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		     long arg3) {
		    // TODO Auto-generated method stub
		    CheckedTextView tv = (CheckedTextView)arg1;
		    toggle(tv);
		   }
		         
		  });
        
        but.setOnClickListener(new OnClickListener() {
   
		   @Override
		   public void onClick(View v) {
			   ArrayList<String> attendees = new ArrayList();
		    for(int i = 0;i<list.getChildCount();i++) {
		     View view = list.getChildAt(i);
		     CheckedTextView cv =(CheckedTextView)view.findViewById(R.id.checkList);
		     if(cv.isChecked())
		     {
		      Log.i("listview", cv.getText().toString());
		      String name = names[i];
		      if (global.friends.containsKey(name)) {
		    	  String number = global.friends.get(name);
		    	  attendees.add(number);
		      }
		     }
		    }
		    // Must have at least one attendee
		    if(attendees.size() > 0) {
			    Intent resultIntent = new Intent();
				resultIntent.putStringArrayListExtra("attendees", attendees);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
		    }
		   }
		  });

	}
	
	private class ItemsAdapter extends BaseAdapter {
		  String[] items;

		  public ItemsAdapter(Context context, String[] item) {
		   this.items = item;
		  }

		  // @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		   
		   View v = convertView;
		   if (v == null) {
		    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    v = vi.inflate(R.layout.contact, null);
		   } 
		   CheckedTextView post = (CheckedTextView) v.findViewById(R.id.checkList);
		   post.setText(items[position]);
		   
		   return v;
		  }

		  public int getCount() {
		   return items.length;
		  }

		  public Object getItem(int position) {
		   return position;
		  }

		  public long getItemId(int position) {
		   return position;
		  }
		 }

		 public void toggle(CheckedTextView v)
		 {
		         if (v.isChecked())
		         {
		             v.setChecked(false);
		         }
		         else
		         {
		             v.setChecked(true);
		         }
		 }
	

}
