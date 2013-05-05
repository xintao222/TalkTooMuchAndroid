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
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class History extends Activity {
	
	private MySQLiteHelper dbHelper;
	private SimpleCursorAdapter dataAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		dbHelper = new MySQLiteHelper(this);
		dbHelper.open();
		
		Cursor cursor = dbHelper.fetchAllRecordings();
		
		String[] columns = new String[] {
				MySQLiteHelper.KEY_DATE,
				MySQLiteHelper.KEY_TALKTIME,
				MySQLiteHelper.KEY_TOTALTIME
		};
		
		
		
		int[] to = new int [] {
				R.id.date,
				R.id.spoke,
				R.id.recorded,
		};
		
		dataAdapter = new SimpleCursorAdapter(
				this,R.layout.recording_info,
				cursor,
				columns,
				to,
				0);
		
		
		ListView historyList = (ListView) findViewById(R.id.list);
		historyList.setAdapter(dataAdapter);
		dbHelper.close();
		
	}
}