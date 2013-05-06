package com.Namkour.soundanalyser;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper {

  public static final String KEY_ROWID = "_id";
  public static final String KEY_DATE = "date";
  public static final String KEY_TALKTIME = "talk_time";
  public static final String KEY_SPOKE = "spoke_string";
  public static final String KEY_TOTALTIME = "total_time";
  public static final String KEY_RECORDED = "recorded_string";
  public static final String KEY_PERCENT = "percent";
  
  
  private static final String TAG = "RecordingsDbAdapter";
  private DatabaseHelper mDbHelper;
  private SQLiteDatabase mDb;

  private static final String DATABASE_NAME = "Recordings.db";
  private static final String SQLITE_TABLE = "Recording";
  private static final int DATABASE_VERSION = 1;
  
  private final Context mCtx;

  // Database creation sql statement
  private static final String DATABASE_CREATE = "CREATE TABLE if not exists "
      + SQLITE_TABLE + " (" + 
	  KEY_ROWID + " integer PRIMARY KEY autoincrement," + 
      KEY_DATE  + "," + 
	  KEY_TALKTIME + "," +
      KEY_SPOKE + "," +
      KEY_TOTALTIME + ","+
      KEY_RECORDED + ","+
	  KEY_PERCENT + ","+
	  " UNIQUE (" + KEY_ROWID +"));";

  private static class DatabaseHelper extends SQLiteOpenHelper {
	  
	  DatabaseHelper (Context context) {
		  super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.w(TAG, DATABASE_CREATE);
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w(TAG, "Upgrading database from version " +oldVersion + "to "+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
		onCreate(db);
	}
  }

  public MySQLiteHelper(Context ctx){
	  this.mCtx = ctx;
  }
  
  public MySQLiteHelper open() throws SQLException{
	  mDbHelper = new DatabaseHelper(mCtx);
	  mDb = mDbHelper.getWritableDatabase();
	  return this;
  }
  
  public void close() {
	  if (mDbHelper != null) {
		  mDbHelper.close();
	  }
  }
  
  public long createRecording (String date, long talk_time, long total_time, int percent, String spoke_string, String recorded_string)
  {
	  ContentValues initialValues = new ContentValues();
	  initialValues.put(KEY_DATE, date);
	  initialValues.put(KEY_TALKTIME, talk_time);
	  initialValues.put(KEY_SPOKE, spoke_string);
	  initialValues.put(KEY_TOTALTIME, total_time);
	  initialValues.put(KEY_RECORDED, recorded_string);
	  initialValues.put(KEY_PERCENT, percent);
	  
	  return mDb.insert(SQLITE_TABLE, null, initialValues);
  }
  
  public boolean deleteAllRecordings() {
	  int doneDelete = 0;
	  doneDelete = mDb.delete(SQLITE_TABLE, null, null);
	  Log.w(TAG, Integer.toString(doneDelete));
	  return doneDelete > 0;
  }
  
  public Cursor fetchRecordingsByDate(String inputText) throws SQLException {
	  Log.w(TAG, inputText);
	  Cursor mCursor = null;
	  if(inputText == null || inputText.length() ==0) {
		  mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID, KEY_DATE, KEY_TALKTIME, KEY_SPOKE, KEY_TOTALTIME, KEY_RECORDED, KEY_PERCENT}, null,null,null,null,null);
		  
	  }
	  else{
		  mCursor = mDb.query(true, SQLITE_TABLE, new String[] {KEY_ROWID, KEY_DATE, KEY_TALKTIME, KEY_SPOKE, KEY_TOTALTIME, KEY_RECORDED, KEY_PERCENT}, KEY_DATE + " like '%" + inputText + "%'", null, null, null, null, null);
		  
	  }
	  if(mCursor != null) {
		  mCursor.moveToFirst();
	  }
	  return mCursor;
  }
  
  public Cursor fetchAllRecordings() {
	  
	  Cursor mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID, KEY_DATE, KEY_TALKTIME, KEY_SPOKE, KEY_TOTALTIME, KEY_RECORDED, KEY_PERCENT}, null, null, null, null, null);
	  
	  if(mCursor != null) {
		  mCursor.moveToFirst();
	  }
	  return mCursor;
  }

} 
