package com.jeeves.vpl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ScheduleItem{
	String wakeTime = null;
	String sleepTime = null;
	String studyDay = "0";
	DateFormat df;
	
	public ScheduleItem(int day, String wake, String sleep) {
		this.wakeTime = wake;
		this.sleepTime = sleep;
		this.studyDay = Integer.toString(day);
		df = new SimpleDateFormat("dd/MM/yy HH:mm");
		df.setTimeZone(TimeZone.getDefault());
		df.setLenient(false);
	}
	public String getWakeTime() {
		Date date = new Date();
		try {
			date.setTime(Long.parseLong(wakeTime));
			return df.format(date);
		}
		catch(NumberFormatException e) {
			return wakeTime;
		}
	}
	public String getSleepTime() {
		Date date = new Date();
		try {
			date.setTime(Long.parseLong(sleepTime));
			return df.format(date);
		}
		catch(NumberFormatException e) {
			return sleepTime;
		}
	}
	public String getStudyDay() {
		return studyDay;
	}
	public void setWakeTime(String wake) {
        try {
            Date date = df.parse(wake);
    		this.wakeTime = Long.toString(date.getTime());
        } catch (ParseException e) {
        	this.wakeTime = wake;
        }
	}
	public void setSleepTime(String sleep) {
        try {
            Date date = df.parse(sleep);
    		this.sleepTime = Long.toString(date.getTime());
        } catch (ParseException e) {
        	this.sleepTime = sleep;
        }	
        }
	public void setStudyDay(String day) {
		this.studyDay = day;
	}
	
}

