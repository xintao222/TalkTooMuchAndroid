package com.example.soundanalyser;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class History extends Activity {
	
	private MySQLiteHelper dbHelper;
	private SimpleCursorAdapter dataAdapter;
	private ListView historyList;
	TextView recordedTotal;
	TextView spokeTotal;
	TextView percentTotal;
	Button clear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		clear = (Button) findViewById(R.id.clearButton);
		
		recordedTotal = (TextView) findViewById(R.id.recorded);
		spokeTotal = (TextView) findViewById(R.id.spoke);
		percentTotal = (TextView) findViewById(R.id.percent);
		
		//reset summary
				percentTotal.setText("0%");
				recordedTotal.setText("0 min, 0 sec");
				spokeTotal.setText("0 min, 0 sec");
		
		dbHelper = new MySQLiteHelper(this);
		dbHelper.open();
		
		Cursor cursor = dbHelper.fetchAllRecordings();
		
		if(cursor.getCount()>0)
		{
			
			clear.setVisibility(0); //make visible 
		
		String [] columns = new String[] {
				MySQLiteHelper.KEY_DATE,
				MySQLiteHelper.KEY_SPOKE,
				MySQLiteHelper.KEY_RECORDED,
				MySQLiteHelper.KEY_PERCENT
		};
		
		
		
		int [] to = new int [] {
				R.id.date,
				R.id.spoke,
				R.id.recorded,
				R.id.percent
		};
		
		dataAdapter = new SimpleCursorAdapter(
				this,R.layout.recording_info,
				cursor,
				columns,
				to,
				0);
		
		
		historyList = (ListView) findViewById(R.id.list);
		historyList.setAdapter(dataAdapter);
		summaryCalc();
		}
	}
	
	public void historyClear(View v)
	{
		dbHelper = new MySQLiteHelper(this);
		
		new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Clear History")
        .setMessage("Are you sure you want to delete all previous recordings?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        	
    		dbHelper.open();
    		Cursor cursor = dbHelper.fetchAllRecordings();
    		
    		if(cursor.getCount()>0)
    		{
    		dbHelper.deleteAllRecordings();
    		historyList.setVisibility(4);
    		historyList.setAdapter(dataAdapter);
    		
    		//reset summary
    		percentTotal.setText("0%");
    		recordedTotal.setText("0 min, 0 sec");
    		spokeTotal.setText("0 min, 0 sec");
    		}
    		
    		dbHelper.close(); 
    		
    		clear.setVisibility(4); //make clear button invisible
        }

    })
    .setNegativeButton("No", null)
    .show();
		
	}
	
	public void summaryCalc()
	{
		dbHelper = new MySQLiteHelper(this);
		dbHelper.open();
		Cursor cursor = dbHelper.fetchAllRecordings();
		
		cursor.moveToFirst();
		long totalRecorded=0;
		long totalSpoke=0;
		//summary record
		while(!cursor.isLast())
		{
			totalRecorded = totalRecorded+cursor.getLong(5);
			cursor.moveToNext();
		}
		cursor.moveToLast();
		totalRecorded= totalRecorded+cursor.getLong(5);
		
		//summary spoke
		cursor.moveToFirst();
		
		while(!cursor.isLast())
		{
			totalSpoke = totalSpoke+cursor.getLong(3);
			cursor.moveToNext();
		}
		cursor.moveToLast();
		totalSpoke = totalSpoke+cursor.getLong(3);
		
		//summary percent
		double temp_percent = (double) totalSpoke / totalRecorded;
		temp_percent = temp_percent*100;
		int percent = (int) temp_percent;
		
		//talk total
		int talk_mins = (int) Math.floor(totalRecorded/60);
		double talk_temp2 = (double)totalRecorded/60 - talk_mins;
		int talk_secs = (int) (talk_temp2*60);
		
		recordedTotal.setText(talk_mins+" min, "+talk_secs+" sec");
		
		//record total
		int total_mins = (int) Math.floor(totalSpoke/60);
		double total_temp2 = (double)totalSpoke/60 - total_mins;
		int total_secs = (int) (total_temp2*60);
		
		spokeTotal.setText(total_mins+" min, "+total_secs+" sec");
		
		//record percent
		
		percentTotal.setText(percent+"%");
		dbHelper.close();
	}
}