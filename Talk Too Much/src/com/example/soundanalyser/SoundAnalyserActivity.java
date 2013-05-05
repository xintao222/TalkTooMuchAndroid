package com.example.soundanalyser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


//Ian
//Michael
public class SoundAnalyserActivity extends Activity {
	
	private MySQLiteHelper dbHelper;
	private SimpleCursorAdapter dataAdapter;
	
	private MediaRecorder recorder;
	private Timer timer;
	private Handler handler;
	private SeekBar seekBar;
	private ProgressBar progressBar;
	private ProgressBar loudnessGBar;
	private ProgressBar loudnessRBar;

	public int teacherSeconds = 0;
	public int totalSeconds = -1;

	private static final int MAX_THRESHOLD = 32767;
	// private static final int DEF_THRESHOLD = 10000;
	private static final int DEFAULT_CLASS_TIME = 90;
	private static final int INIT_CLASS_TIME = 10;
	private int currSeekbar;// = (int) (DEF_THRESHOLD * 100 / MAX_THRESHOLD);
	private int teacherVoiceThreshold;

	private long currentAmplitude;
	private int classtime;
	public static int recordLength; //to be determined by user

	private TextView progressMaxView;
	private TextView progressBarTitleView;
	private TextView thresholdBarTitleView;
	private TextView thresholdView;

	private int talkMinutes = 0;
	private int talkSeconds = 0;
	private int totMinutes = 0;
	private int totSeconds = 0;
	private TextView talktimeTextView;
	private TextView totaltimeTextView;
	private int talkPercent = 0;
	private TextView talkPercentTextView;

	private boolean isRecordingStopped;
	public static boolean isRecordingStarted;
	private boolean isRecordingPaused;

	private Button startBtn;
	private Button endBtn;
	private Button pauseResumeBtn;


	private static final String TAG = "SoundAnalyser";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound_analyser);
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		currSeekbar = prefs.getInt("seekBarPreference", 30);
		teacherVoiceThreshold = (int) (currSeekbar * MAX_THRESHOLD / 100);
		Log.i(TAG, "Threshold set to " + currSeekbar + "("
				+ teacherVoiceThreshold + ")");

		classtime = Integer.parseInt(prefs.getString("classTimeLength", "\""
				+ DEFAULT_CLASS_TIME + "\""));
		Log.i(TAG, "Class time set to " + classtime);

		handler = new Handler();

		progressMaxView = (TextView) findViewById(R.id.maxProgressBar);
		progressBarTitleView = (TextView) findViewById(R.id.progressBarTitle);
		thresholdBarTitleView = (TextView) findViewById(R.id.thresholdBarTitle);

		seekBar = (SeekBar) findViewById(R.id.thresholdSeekBar);
		seekBar.setMax(100);
		seekBar.setProgress(currSeekbar);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				currSeekbar = seekBar.getProgress();
				teacherVoiceThreshold = (int) (currSeekbar * MAX_THRESHOLD / 100);
				/*
				 * Toast.makeText( getBaseContext(), "Threshold changed to " +
				 * currSeekbar + "(" + teacherVoiceThreshold + ")",
				 * Toast.LENGTH_LONG).show();
				 */

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { // TODO
																// Auto-generated
																// method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) { // TODO Auto-generated method stub

			}
			
		});

		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar.setMax(recordLength * 60);// (DEFAULT_CLASS_TIME*60);
		loudnessGBar = (ProgressBar) findViewById(R.id.progressBarG);
		loudnessGBar.setProgressDrawable(getResources().getDrawable(
				R.drawable.green_progress));
		loudnessGBar.setMax(100);
		loudnessRBar = (ProgressBar) findViewById(R.id.progressBarR);
		loudnessRBar.setProgressDrawable(getResources().getDrawable(
				R.drawable.red_progress));
		loudnessRBar.setMax(100);

		talktimeTextView = (TextView) findViewById(R.id.TalkTimetextView);
		totaltimeTextView = (TextView) findViewById(R.id.TotalTimetextView);
		talkPercentTextView = (TextView) findViewById(R.id.TalkPercenttextView);

		startBtn = (Button) findViewById(R.id.startButton);
		endBtn = (Button) findViewById(R.id.endButton);
		pauseResumeBtn = (Button) findViewById(R.id.pauseButton);
		

		isRecordingStopped = false;
		isRecordingPaused = false;
		
		//data
		dbHelper = new MySQLiteHelper(this);
		dbHelper.open();
		dbHelper.insertARecording();
	
		if(isRecordingStarted)
		{
				progressMaxView.setText(Integer.toString(recordLength) + " min");
				progressBar.setMax(recordLength * 60);
				progressBar.setProgress(0);// (DEFAULT_CLASS_TIME*60);
				progressBar.setSecondaryProgress(0);
				loudnessGBar.setMax(100);
				loudnessGBar.setProgress(0);
				loudnessRBar.setMax(100);
				loudnessRBar.setProgress(0);
				startBtn.setVisibility(4); //4 = invisible
				endBtn.setVisibility(0); //0= visible
				pauseResumeBtn.setVisibility(0);
				teacherSeconds = 0;
				totalSeconds = -1;
				talkMinutes = 0;
				talkSeconds = 0;
				totMinutes = 0;
				totSeconds = 0;
				talkPercent = 0;
				talktimeTextView.setText("Speaking time: " + talkMinutes + " minutes "
						+ talkSeconds + " seconds");
				totaltimeTextView.setText("Total time: " + totMinutes + " minutes "
						+ totSeconds + " seconds");
				talkPercentTextView.setText("Talk time percentage: " + talkPercent
						+ "%");
				talkPercentTextView.setVisibility(0);
				talktimeTextView.setVisibility(0);
				totaltimeTextView.setVisibility(0);

				isRecordingStopped = false;
				isRecordingPaused = false;

				recorder = new MediaRecorder();
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				recorder.setOutputFile("/dev/null");

				timer = new Timer();
				try {
					recorder.prepare();
				} catch (IOException e) {
					Log.e("SOUNDRECORDER_ERROR", "Failed to record!!!");
				}

				recorder.start();
				timer.scheduleAtFixedRate(new RecorderTask(), 0, 1000);

			}
	}
		@Override
	public void onResume() {
		super.onResume();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		/*
		 * currSeekbar = prefs.getInt("seekBarPreference", 30);
		 * teacherVoiceThreshold = (int) (currSeekbar * MAX_THRESHOLD / 100);
		 * Log.i(TAG, "Threshold set to " + currSeekbar + "(" +
		 * teacherVoiceThreshold + ")");
		 */
		classtime = Integer.parseInt(prefs.getString("classTimeLength", "\""
				+ DEFAULT_CLASS_TIME + "\""));
		Log.i(TAG, "Class time set to " + classtime);
	}
	
	
//this is the function where recording time will be implemented!
	public void SetRecordTime(View v) {
		Intent i = new Intent(SoundAnalyserActivity.this, Recording_Length.class);
        startActivity(i);
	}
	
	public void pauseResumeFunction(View v)
	{
		if (isRecordingStarted) {
			if (isRecordingPaused) {
				isRecordingPaused = false;
				resumeRecording();
			} else {
				isRecordingPaused = true;
				pauseRecording();
			}
		}
	}

	private void pauseRecording() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				pauseResumeBtn.setText("Resume");
				loudnessGBar.setProgress(100);
				loudnessRBar.setProgress(100);
				loudnessGBar.setProgress(0);
				loudnessRBar.setProgress(0);
			}
		});
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
		if (timer != null) {
			timer.cancel();
		}
	}

	private void resumeRecording() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				pauseResumeBtn.setText("Pause");
				loudnessGBar.setProgress(100);
				loudnessRBar.setProgress(100);
				loudnessGBar.setProgress(0);
				loudnessRBar.setProgress(0);
			}
		});

		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile("/dev/null");

		timer = new Timer();
		try {
			recorder.prepare();
		} catch (IOException e) {
			Log.e("SOUNDRECORDER_ERROR", "Failed to record!!!");
		}

		recorder.start();
		timer.scheduleAtFixedRate(new RecorderTask(), 0, 1000);
	}

	public void StopRecording(View v) throws IOException{
		isRecordingStarted = false;
		isRecordingStopped = true;
		isRecordingPaused = false;
		stopRecording();
	}
	
	//this is where history will be implemented!
	private void stopRecording() throws IOException {
		//write to history
		Date currentDate = new Date(System.currentTimeMillis());
		String Date1 = currentDate.toString();
		long talk_time = talkMinutes*60 + talkSeconds;
		long total_time = totMinutes*60 + totSeconds;
		dbHelper.open();
		dbHelper.createRecording(Date1, talk_time, total_time);
		dbHelper.close();
		
		handler.post(new Runnable() {

			@Override
			public void run() {
				startBtn.setVisibility(0); //visible = 0
				endBtn.setVisibility(4); //invisible = 4
				pauseResumeBtn.setVisibility(4);
				loudnessGBar.setProgress(100);
				loudnessRBar.setProgress(100);
				loudnessGBar.setProgress(0);
				loudnessRBar.setProgress(0);
			}
		});
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
		if (timer != null) {
			timer.cancel();
		}
	}
	
	//go to history page if requested by user
	public void HistoryExecute(View v){
		Intent i = new Intent(SoundAnalyserActivity.this, History.class);
        startActivity(i);
	}

	private class RecorderTask extends TimerTask {
		public void run() {
			if ((recorder != null) || (isRecordingStopped == false)) {
				totalSeconds++;

				currentAmplitude = recorder.getMaxAmplitude();
				Log.v("MicInfoService", "amplitude: " + currentAmplitude);

				handler.post(new Runnable() {

					@Override
					public void run() {
						progressBar.setSecondaryProgress(totalSeconds);
						totMinutes = totalSeconds / 60;
						totSeconds = totalSeconds % 60;
						totaltimeTextView.setText("Total time: " + totMinutes
								+ " minutes " + totSeconds + " seconds");
						if (currentAmplitude > teacherVoiceThreshold) {
							loudnessRBar.setVisibility(4);
							loudnessGBar
									.setProgress((int) (currentAmplitude * 100 / MAX_THRESHOLD));
							loudnessGBar.setVisibility(0);
						} else {
							loudnessGBar.setVisibility(4);
							loudnessRBar
									.setProgress((int) (currentAmplitude * 100 / MAX_THRESHOLD));
							loudnessRBar.setVisibility(0);
						}
					}
				});

				if (currentAmplitude > teacherVoiceThreshold) {
					teacherSeconds++;
					handler.post(new Runnable() {

						@Override
						public void run() {
							progressBar.setProgress(teacherSeconds);// ((DEFAULT_CLASS_TIME*60)
																	// -
																	// (int)totalSeconds);
							talkMinutes = teacherSeconds / 60;
							talkSeconds = teacherSeconds % 60;
							talktimeTextView.setText("Speaking time: " + talkMinutes
									+ " minutes " + talkSeconds + " seconds");
						}
					});
				}

				talkPercent = (int) ((float) teacherSeconds
						/ (float) totalSeconds * 100);
				handler.post(new Runnable() {

					@Override
					public void run() {
						talkPercentTextView.setText("Talk time percentage: "
								+ talkPercent + "%");
					}
				});

				/*
				 * totalSeconds++;
				 * 
				 * currentAmplitude = recorder.getMaxAmplitude();
				 * Log.v("MicInfoService", "amplitude: " + currentAmplitude); if
				 * (currentAmplitude > teacherVoiceThreshold) {
				 * teacherSeconds++; } talkPercent = (int) ((float)
				 * teacherSeconds / (float) totalSeconds * 100);
				 * handler.post(new Runnable() {
				 * 
				 * @Override public void run() { if (currentAmplitude >
				 * teacherVoiceThreshold) { loudnessRBar.setVisibility(4);
				 * loudnessGBar .setProgress((int) (currentAmplitude * 100 /
				 * MAX_THRESHOLD)); loudnessGBar.setVisibility(0); } else {
				 * loudnessGBar.setVisibility(4); loudnessRBar
				 * .setProgress((int) (currentAmplitude * 100 / MAX_THRESHOLD));
				 * loudnessRBar.setVisibility(0); }
				 * 
				 * progressBar.setProgress(teacherSeconds);//
				 * ((DEFAULT_CLASS_TIME*60) // - // (int)totalSeconds);
				 * talkMinutes = teacherSeconds / 60; talkSeconds =
				 * teacherSeconds % 60; talktimeTextView.setText("Talk time " +
				 * talkMinutes + " minutes " + talkSeconds + " seconds");
				 * 
				 * progressBar.setSecondaryProgress(totalSeconds); totMinutes =
				 * totalSeconds / 60; totSeconds = totalSeconds % 60;
				 * totaltimeTextView.setText("Total time " + totMinutes +
				 * " minutes " + totSeconds + " seconds");
				 * 
				 * talkPercentTextView.setText("Talk time percentage " +
				 * talkPercent + "%");
				 * 
				 * } });
				 */
				if (totalSeconds >= (classtime * 60)) {
					isRecordingStopped = true;
					handler.post(new Runnable() {

						@Override
						public void run() {
							try {
								stopRecording();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});

				} else {
					if (totalSeconds == (recordLength * 60)) {
						recordLength += 10;
						handler.post(new Runnable() {

							@Override
							public void run() {
								progressBar.setMax(recordLength * 60);// ((DEFAULT_CLASS_TIME*60)
																			// -
																			// (int)totalSeconds);
								progressBar.setProgress(teacherSeconds - 1);
								progressBar.setProgress(teacherSeconds);
								progressBar
										.setSecondaryProgress(totalSeconds - 1);
								progressBar.setSecondaryProgress(totalSeconds);
								progressMaxView.setText(Integer
										.toString(recordLength) + " min");
							}
						});
					}
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_sound_analyser, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Log.i(TAG, "settings");
			Intent prefs = new Intent(this, PreferencesActivity.class);
			startActivity(prefs);
			break;
		}
		return true;
	}
}


