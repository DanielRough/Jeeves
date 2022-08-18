package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Daniel on 16/06/16.
 */
@SuppressWarnings("serial")
@IgnoreExtraProperties
public class FirebasePatient implements Serializable {

	private String userinfo;
	private String address;
	private Map<String, FirebaseSurvey> complete;
	private String currentStudy;
	private String email;
	private Map<String, Object> feedback;
	private Map<String, FirebaseSurvey> incomplete;
	private String name;
	private String phoneNo;
	private String uid;
	private String date;
	private long signuptime;
	
	public long getsignuptime() {
		return signuptime;
	}
	public void setsignuptime(long signuptime) {
		this.signuptime = signuptime;
	}
	
	private List<String> schedule;
	private boolean hasSchedule;
	
	public void setschedule(List<String> schedule) {
		this.schedule = schedule;
	}
	public List<String> getschedule(){
		return schedule;
	}
	public void sethasSchedule(boolean hasSchedule) {
		this.hasSchedule = hasSchedule;
	}
	public boolean gethasSchedule() {
		return hasSchedule;
	}
	
	public String getAddress() {
		return address;
	}

	
	public void setuserinfo(String userinfo){
		this.userinfo = userinfo;
	}
	public String getuserinfo(){
		return userinfo;
	}
	public Map<String, FirebaseSurvey> getcomplete() {
		return complete;
	}
	
	public void setcomplete(Map<String, FirebaseSurvey> complete) {
		this.complete = complete;
	}

	public String getCurrentStudy() {
		return currentStudy;
	}

	public String getDate(){
		return date;
	}
	public void setDate(String date){
		this.date = date;
	}
	public String getEmail() {
		return email;
	}

	public void setfeedback(Map<String,Object> feedback) {
		this.feedback = feedback;
	}
	public Map<String, Object> getfeedback() {
		return feedback;
	}

	public Map<String, FirebaseSurvey> getincomplete() {
		return incomplete;
	}

	public void setincomplete(Map<String, FirebaseSurvey> incomplete) {
		this.incomplete = incomplete;
	}
	public String getName() {
		return name;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public String getUid() {
		return uid;
	}

	public void setaddress(String address) {
		this.address = address;
	}

	public void setEmail(String email) {
		this.email = email;
	}

//	public void setScreenName(String name){
//		this.screenName = name;
//	}
//	public String getScreenName(){
//		return screenName;
//	}
	public void setName(String lastName) {
		this.name = lastName;
	}

	public void setPhoneNo(String phone) {
		this.phoneNo = phone;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	@Override
	public boolean equals(Object other){
		if(!(other instanceof FirebasePatient))
			return false;
		if(((FirebasePatient)other).getuserinfo() == null)
			return false;
		if(((FirebasePatient)other).getuserinfo().equals(this.getuserinfo()))
			return true;
		return false;
	}
}
