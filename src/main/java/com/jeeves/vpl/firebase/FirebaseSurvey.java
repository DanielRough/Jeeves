package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Daniel on 29/04/2016.
 */
@SuppressWarnings("serial")
@IgnoreExtraProperties
public class FirebaseSurvey extends FirebaseElement implements Serializable {

	@Exclude
	private transient StringProperty titleProperty = new SimpleStringProperty();

	private List<String> answers;
	private String encodedAnswers; //I wonder if this will work
	private String encodedKey;
	private boolean begun; // Has the user begun completing the survey?
	private long expiryTime;
	private String key;
	private List<FirebaseQuestion> questions = new ArrayList<>();
	private long score;
	private boolean fastTransition;
	private String title;
	private long timeAlive;
	private long timeFinished;
	private long timeSent;
	private String surveyId;
	
	@Exclude
	public StringProperty getTitleProperty() {
		return titleProperty;
	}
	public boolean getfastTransition() {
		return fastTransition;
	}
	public void setfastTransition(boolean fast) {
		this.fastTransition = fast;
	}
	public void setencodedAnswers(String encodedAnswers){
		this.encodedAnswers = encodedAnswers;
	}
	public String getencodedAnswers(){
		return encodedAnswers;
	}
	public void setencodedKey(String encodedKey) {
		this.encodedKey = encodedKey;
	}
	public String getencodedKey() {
		return encodedKey;
	}
	public void setsurveyId(String id){
		this.surveyId = id;
	}
	public String getsurveyId(){
		return surveyId;
	}

	public List<String> getanswers() {
		return answers;
	}

	public boolean getbegun() {
		return begun;
	}

	public long getexpiryTime() {
		return expiryTime;
	}

	public String getkey() {
		return key;
	}

	public List<FirebaseQuestion> getquestions() {
		return questions;
	}

	public long getscore() {
		return score;
	}

	public long gettimeAlive() {
		return timeAlive;
	}

	public long gettimeFinished() {
		return timeFinished;
	}

	public long gettimeSent() {
		return timeSent;
	}

	public String gettitle() {
		return titleProperty.get();
	}

	public void setanswers(List<String> answers) {
		this.answers = answers;
	}

	public void setbegun() {
		this.begun = true;
	}

	public void setexpiryTime(long expiryTime) {
		this.expiryTime = expiryTime;
	}

	public void setkey(String key) {
		this.key = key;
	}

	public void setscore(long score) {
		this.score = score;
	}

	public void settimeFinished(long timeFinished) {
		this.timeFinished = timeFinished;
	}

	public void settitle(String title) {
		this.titleProperty.set(title);
		this.title = title;
	}

}
