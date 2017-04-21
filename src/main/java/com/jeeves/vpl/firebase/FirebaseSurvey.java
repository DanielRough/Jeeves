package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Daniel on 29/04/2016.
 */
@SuppressWarnings("serial")
@IgnoreExtraProperties
public class FirebaseSurvey extends FirebaseElement implements Serializable {

	private long timeAlive;
	private long expiryTime;
	private long timeSent;
	private long timeFinished;
	private long score;
	private List<FirebaseQuestion> questions = new ArrayList<FirebaseQuestion>();
	private boolean begun; // Has the user begun completing the survey?
	private List<Map<String, String>> answers;
	private String key;

	public FirebaseSurvey() {

	}

	public long getexpiryTime() {
		return expiryTime;
	}

	public void setexpiryTime(long expiryTime) {
		this.expiryTime = expiryTime;
	}

	public List<FirebaseQuestion> getquestions() {
		return questions;
	}

	public List<Map<String, String>> getanswers() {
		return answers;
	}

	public long gettimeSent() {
		return timeSent;
	}

	public long gettimeAlive() {
		return timeAlive;
	}

	public long gettimeFinished() {
		return timeFinished;
	}

	public long getscore() {
		return score;
	}

	public void setscore(long score) {
		this.score = score;
	}

	public void setkey(String key) {
		this.key = key;
	}

	public String getkey() {
		return key;
	}

	public void setanswers(List<Map<String, String>> answers) {
		this.answers = answers;
	}

	public void settimeFinished(long timeFinished) {
		this.timeFinished = timeFinished;
	}

	public boolean getbegun() {
		return begun;
	}

	public void setbegun() {
		this.begun = true;
	}

}
