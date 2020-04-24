package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 26/05/15.
 */
@SuppressWarnings("serial")

public class FirebaseAction extends FirebaseElement implements Serializable {

	private List<FirebaseAction> actions;
	private FirebaseExpression condition;
	private boolean manual;
	private List<FirebaseExpression> vars = new ArrayList<>();

	public FirebaseAction() {}
	
	public FirebaseAction(String name) {
		this.setname(name);
	}
	public List<FirebaseAction> getactions() {
		return actions;
	}

	public FirebaseExpression getcondition() {
		return condition;
	}

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
