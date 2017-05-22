package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Daniel on 26/05/15.
 */
@SuppressWarnings("serial")
// @JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=
// JsonTypeInfo.As.PROPERTY, property="@class")
@IgnoreExtraProperties

public class FirebaseAction extends FirebaseElement implements Serializable {

	private List<FirebaseAction> actions;
	private FirebaseExpression condition;
	private boolean manual;
	private List<FirebaseExpression> vars = new ArrayList<FirebaseExpression>();

	public List<FirebaseAction> getactions() {
		return actions;
	}

	public FirebaseExpression getcondition() {
		return condition;
	}

	// Actions might also have expressions or variables tied to them. I can't be
	// sure yet but probably

	public boolean getmanual() {
		return manual;
	}

	public List<FirebaseExpression> getvars() {
		return vars;
	}

	// Actions might have their own internal actions, as well as a condition

	public void setactions(List<FirebaseAction> actions) {
		this.actions = actions;
	}

	public void setcondition(FirebaseExpression condition) {
		this.condition = condition;
	}

	public void setManual(boolean manual) {
		this.manual = manual;
	}

	public void setvars(List<FirebaseExpression> vars) {
		this.vars = vars;
	}
}
