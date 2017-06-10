package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class FirebaseMain implements Serializable {

	private Map<String, FirebasePrivate> privateData;
	private Map<String, FirebasePublic> publicData;
	
	public void setprivateData(Map<String,FirebasePrivate> privateData){
		this.privateData = privateData;
	}
	public void setpublicData(Map<String,FirebasePublic> publicData){
		this.publicData = publicData;
	}
	
	public Map<String,FirebasePrivate> getprivateData(){
		return privateData;
	}
	public Map<String,FirebasePublic> getpublicData(){
		return publicData;
	}
}
//	private Map<String, FirebaseVariable> systemvars;
