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

	private String assignedVar;
	private String conditionConstraints;
	private FirebaseQuestion conditionQuestion;
	private String image;
	private long questionId;
	private String questionText;
	private long questionType;

	// Maybe each FirebaseQuestion could have another reference to a
	// FirebaseQuestion that acts as its condition question?

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

	public Map<String, Object> getOptions() {
		if (params.containsKey("options"))
			return (Map<String, Object>) params.get("options");
		return new HashMap<String, Object>();
	}

	@Override
	public Map<String, Object> getparams() {
		return params;
	}

	public long getquestionId() {
		return questionId;
	}

	public String getquestionText() {
		return questionText;
	}

	public long getquestionType() {
		return questionType;
	}

	public void setAssign(boolean assign) {
		params.put("assignToScore", assign);
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

	public void setOptions(Map<String, Object> options) {
		params.put("options", options);
	}

	public void setquestionId(long id) {
		this.questionId = id;
	}

	public void setquestionText(String text) {
		this.questionText = text;
	}

	public void setquestionType(long type) {

		this.questionType = type;
	}

}
