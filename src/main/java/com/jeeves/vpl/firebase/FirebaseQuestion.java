package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Daniel on 25/05/2016.
 */
@SuppressWarnings("serial")
@IgnoreExtraProperties
public class FirebaseQuestion extends FirebaseElement implements Serializable {

	private long questionType;
	private String questionText;
	private String assignedVar;
	private String image;
	private long questionId;
	private FirebaseQuestion conditionQuestion;
	private String conditionConstraints;
	
	//Maybe each FirebaseQuestion could have another reference to a FirebaseQuestion that acts as its condition question?
	
	public String getconditionConstraints(){
		return conditionConstraints;
	}
	public void setconditionConstraints(String constraints){
		this.conditionConstraints = constraints;
	}
	public FirebaseQuestion getconditionQuestion(){
		return conditionQuestion;
	}
	public void setconditionQuestion(FirebaseQuestion q){
		this.conditionQuestion = q;
	}
	
	public long getquestionId(){
		return questionId;
	}
	
	public void setquestionId(long id){
		this.questionId = id;
	}
	
	public long getquestionType() {
		return questionType;
	}


	public void setquestionType(long type) {

		this.questionType = type;
	}

	public String getquestionText() {
		return questionText;
	}

	public void setquestionText(String text) {
		this.questionText = text;
	}

	public String getassignedVar() {
		return assignedVar;
	}

	public void setAssignedVar(String assignedVar) {
		this.assignedVar = assignedVar;
	}

	public void setAssign(boolean assign) {
		params.put("assignToScore", assign);
	}

	public Map<String, Object> getparams() {
		return params;
	}

	public String getimage() {
		return image;
	}

	public void setCondition(Map<String, Object> question) {
		params.put("condition", question);
	}

	public void setOptions(Map<String, Object> options) {
		params.put("options", options);
	}
	public Map<String,Object> getOptions(){
		if(params.containsKey("options"))
		return (Map<String,Object>)params.get("options");
		return new HashMap<String,Object>();
	}

}
