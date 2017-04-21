package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.Map;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Daniel on 16/06/16.
 */
@SuppressWarnings("serial")
@IgnoreExtraProperties
public class FirebasePatient implements Serializable {

	private String address;
	private String name;
	private String phoneNo;
	private String email;
	private String currentStudy;
	private Map<String, Object> feedback;
	private Map<String, FirebaseSurvey> incomplete;
	private Map<String, FirebaseSurvey> complete;
	private String uid;

	public String getCurrentStudy() {
		return currentStudy;
	}

	public Map<String, Object> getfeedback() {
		return feedback;
	}

	public void setaddress(String address) {
		this.address = address;
	}

	public void setName(String lastName) {
		this.name = lastName;
	}

	public void setPhoneNo(String phone) {
		this.phoneNo = phone;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getEmail() {
		return email;
	}

	public String getAddress() {
		return address;
	}

	public String getName() {
		return name;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public Map<String, FirebaseSurvey> getincomplete() {
		return incomplete;
	}

	public Map<String, FirebaseSurvey> getcomplete() {
		return complete;
	}
}
