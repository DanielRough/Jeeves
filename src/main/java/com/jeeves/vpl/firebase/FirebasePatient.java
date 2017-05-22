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
	private Map<String, FirebaseSurvey> complete;
	private String currentStudy;
	private String email;
	private Map<String, Object> feedback;
	private Map<String, FirebaseSurvey> incomplete;
	private String name;
	private String phoneNo;
	private String uid;

	public String getAddress() {
		return address;
	}

	public Map<String, FirebaseSurvey> getcomplete() {
		return complete;
	}

	public String getCurrentStudy() {
		return currentStudy;
	}

	public String getEmail() {
		return email;
	}

	public Map<String, Object> getfeedback() {
		return feedback;
	}

	public Map<String, FirebaseSurvey> getincomplete() {
		return incomplete;
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

	public void setName(String lastName) {
		this.name = lastName;
	}

	public void setPhoneNo(String phone) {
		this.phoneNo = phone;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
}
