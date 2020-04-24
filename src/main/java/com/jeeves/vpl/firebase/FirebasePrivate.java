package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class FirebasePrivate implements Serializable {

	private Map<String, FirebasePatient> patients;
	private Map<String, FirebaseProject> projects;
	private Map<String, String> tokens;

	public Map<String, FirebasePatient> getpatients() {
		return patients;
	}

	public Map<String, FirebaseProject> getprojects() {
		return projects;
	}

	public Map<String, String> gettokens() {
		return tokens;
	}

	public void setPatients(Map<String, FirebasePatient> patients) {
		this.patients = patients;
	}

	public void setProjects(Map<String, FirebaseProject> projects) {
		this.projects = projects;
	}

	public void settokens(Map<String, String> tokens) {
		this.tokens = tokens;
	}

}
