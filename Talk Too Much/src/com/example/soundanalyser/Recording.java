package com.example.soundanalyser;

public class Recording {
	
	  String date = null;
	  long talk_time = 0;
	  long total_time = 0;
	  int percent = 0;

	  public String getDate(){
		  return date;
	  }
	  
	  public void setDate(String date){
		  this.date = date;
	  }

	  public long getTalkTime() {
	    return talk_time;
	  }

	  public void setTalkTime(long talk_time) {
	    this.talk_time = talk_time;
	  }

	  public long getTotalTime() {
		  return total_time;
	  }
	  
	  public void setTotalTime(long total_time){
		  this.total_time = total_time;
	  }
	  
	  public int getPercent() {
		  return percent;
	  }
	  public void setPercent(int percent) {
		  this.percent = percent;
	  }
}

