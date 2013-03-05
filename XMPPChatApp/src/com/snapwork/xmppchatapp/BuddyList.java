package com.snapwork.xmppchatapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas.VertexMode;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class BuddyList extends Activity {
	ListView listMessages;
	TextView txtView;
	ArrayList<String> buddyList=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		 
		buddyList = getIntent().getStringArrayListExtra("buddyList");
		Log.d("lsit size: ",""+buddyList.size());
		
		listMessages=(ListView)findViewById(R.id.listMessages);
		setListAdapter();
//		LinearLayout ll=new LinearLayout(this);
	//	txtView=new TextView(this);
//		ll.addView(txtView);
		
	}

	private void setListAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.multi_line_list_item,buddyList);
        listMessages.setAdapter(adapter);           
    }
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
*/
}

