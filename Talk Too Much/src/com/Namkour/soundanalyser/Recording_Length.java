package com.Namkour.soundanalyser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.Namkour.soundanalyser.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

public class Recording_Length extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recording__length);
		
		final ListView listview = (ListView) findViewById(R.id.list);
	    String[] values = new String[] { "10 mins", "20 mins", "30 mins",
	        "45 mins", "60 mins", "90 mins", "120 mins"};

	    final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < values.length; ++i) {
	      list.add(values[i]);
	    }
	    final StableArrayAdapter adapter = new StableArrayAdapter(this,
	        android.R.layout.simple_list_item_1, list);
	    listview.setAdapter(adapter);
	    
	    listview.setOnItemClickListener(new OnItemClickListener() {
	    	  public void onItemClick(AdapterView<?> parent, View view,
	    	    int position, long id) {
	    		//get info about list item selected
	    		int timeValue=10;
	    		if(position==0)
	    		{
	    			timeValue=10;
	    		}
	    		if(position==1)
	    		{
	    			timeValue=20;
	    		}
	    		if(position==2)
	    		{
	    			timeValue=30;
	    		}
	    		if(position==3)
	    		{
	    			timeValue=45;
	    		}
	    		if(position==4)
	    		{
	    			timeValue=60;
	    		}
	    		if(position==5)
	    		{
	    			timeValue=90;
	    		}
	    		if(position==6)
	    		{
	    			timeValue=120;
	    		}
	    		
	    		if(com.Namkour.soundanalyser.SoundAnalyserActivity.isRecordingStarted == false)
	    		{
	    			com.Namkour.soundanalyser.SoundAnalyserActivity.recordLength=timeValue;
	    			com.Namkour.soundanalyser.SoundAnalyserActivity.isRecordingStarted = true;
	    			Intent i = new Intent(Recording_Length.this, SoundAnalyserActivity.class);
	    	        startActivity(i);
	    		}
	    	  }
	    	}); 
	    
	  }

	  private class StableArrayAdapter extends ArrayAdapter<String> {

	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	      super(context, textViewResourceId, objects);
	      for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
	    }

}

}
