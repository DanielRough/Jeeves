package com.jeeves.vpl.firebase;

import java.io.Serializable;

import com.google.firebase.database.IgnoreExtraProperties;

//Class that stores information on a patient's survey entry.
//This includes metadata such as the initiation time, completion time, and what triggered the survey in the first place.
@SuppressWarnings("serial")
@IgnoreExtraProperties
public class FirebaseSurveyEntry extends FirebaseElement implements Serializable{

	private int complete;
	private int initTime;
	private String encodedAnswers;
	private int status;
	private int triggerType;
	private String uid; 
	
	public int getcomplete(){
		return complete;
	}
	public void setcomplete(int complete){
		this.complete = complete;
	}
	public String getencodedAnswers(){
		return encodedAnswers;
	}
	public void setencodedAnswers(String encodedAnswers){
		this.encodedAnswers = encodedAnswers;
	}
	
	public int getstatus(){
		return status;
	}
	public void setstatus(int status){
		this.status = status;
	}
	
	public int gettriggerType(){
		return triggerType;
	}
	public void settriggerType(int triggerType){
		this.triggerType = triggerType;
	}
	
	public String getuid(){
		return uid;
	}
	public void setuid(String uid){
		this.uid = uid;
	}
	
	public int getinitTime(){
		return initTime;
	}
	public void setinitTime(int initTime){
		this.initTime = initTime;
	}
}
