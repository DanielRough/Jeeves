package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class FirebaseMain implements Serializable {

	private Map<String, FirebasePatient> patients;
	private Map<String, FirebaseProject> projects;
	private Map<String, FirebaseVariable> systemvars;

	public Map<String, FirebasePatient> getpatients() {
		return patients;
	}

	public Map<String, FirebaseProject> getprojects() {
		return projects;
	}

	public Map<String, FirebaseVariable> getsystemvars() {
		return systemvars;
	}

	public void setPatients(Map<String, FirebasePatient> patients) {
		this.patients = patients;
	}

	public void setProjects(Map<String, FirebaseProject> projects) {
		this.projects = projects;
	}

	public void setSystemvars(Map<String, FirebaseVariable> systemvars) {
		this.systemvars = systemvars;
	}

}
