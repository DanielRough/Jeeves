package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class FirebasePublic implements Serializable {
	private Map<String, FirebaseProject> projects;
	
	public void setprojects(Map<String, FirebaseProject> projects){
		this.projects = projects;
	}
	
	public Map<String, FirebaseProject> getprojects(){
		return projects;
	}
}