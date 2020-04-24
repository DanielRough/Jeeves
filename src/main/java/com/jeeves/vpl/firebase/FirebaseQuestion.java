package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.Map;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Daniel on 25/05/2016.
 */
@SuppressWarnings("serial")
@IgnoreExtraProperties
public class FirebaseQuestion extends FirebaseElement implements Serializable {

	private String assignedVar;
	private String conditionConstraints;
	private FirebaseQuestion conditionQuestion;
	private String image;
	private String questionId;
	private String questionText;
	private String questionType;
	private boolean isMandatory;
	

	public FirebaseQuestion() {}
	public FirebaseQuestion(String name) {
		this.questionText = name;
	}
	public boolean getisMandatory(){
		return isMandatory;
	}
	
	public void setisMandatory(boolean isMandatory){
		this.isMandatory = isMandatory;
	}

	public String getassignedVar() {
		return assignedVar;
	}

	public String getconditionConstraints() {
		return conditionConstraints;
	}

	public FirebaseQuestion getconditionQuestion() {
		return conditionQuestion;
	}

	public String getimage() {
		return image;
	}


	@Override
	public Map<String, Object> getparams() {
		return params;
	}

	public String getquestionId() {
		return questionId;
	}

	public String getquestionText() {
		return questionText;
	}

	public String getquestionType() {
		return questionType;
	}

	public void setAssign(boolean assign) {
		getparams().put("assignToScore", assign);
	}

	public void setAssignedVar(String assignedVar) {
		this.assignedVar = assignedVar;
	}

	public void setCondition(Map<String, Object> question) {
		params.put("condition", question);
	}

	public void setconditionConstraints(String constraints) {
		this.conditionConstraints = constraints;
	}

	public void setconditionQuestion(FirebaseQuestion q) {
		this.conditionQuestion = q;
	}

	public void setquestionId(String id) {
		this.questionId = id;
	}

	public void setquestionText(String text) {
		this.questionText = text;
	}

	public void setquestionType(String type) {

		this.questionType = type;
	}

}
