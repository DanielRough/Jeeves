package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.List;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Daniel on 29/04/2016.
 */
@SuppressWarnings("serial")
@IgnoreExtraProperties
public class FirebaseTrigger extends FirebaseElement implements Serializable {
	private List<FirebaseAction> actions;
	private long clocktype = 0;
	private FirebaseExpression dateFrom;
	private FirebaseExpression dateTo;
	private List<FirebaseExpression> times;
	private List<String> variables;
	private FirebaseExpression timeFrom;
	private FirebaseExpression timeTo;
	private FirebaseExpression location;
	private String triggerId;

	public FirebaseTrigger() {}
	public FirebaseTrigger(String name) {
		this.name.setValue(name);
	}
	public List<String> getvariables(){
		return variables;
	}
	public void setvariables(List<String> variables){
		this.variables = variables;
	}
	public List<FirebaseAction> getactions() {
		return actions;
	}

	public long getclocktype() {
		return clocktype;
	}

	public FirebaseExpression getdateFrom() {
		return dateFrom;
	}

	public FirebaseExpression getlocation() {
		return location;
	}
	public void setlocation(FirebaseExpression location) {
		this.location = location;
	}
	public FirebaseExpression getdateTo() {
		return dateTo;
	}

	public FirebaseExpression gettimeFrom() {
		return timeFrom;
	}

	public List<FirebaseExpression> gettimes(){
		return times;
	}
	
	public FirebaseExpression gettimeTo() {
		return timeTo;
	}

	public String gettriggerId() {
		return triggerId;
	}

	public void setactions(List<FirebaseAction> actions) {
		this.actions = actions;
	}

	public void setdateFrom(FirebaseExpression dateFrom) {
		this.dateFrom = dateFrom;
	}

	public void setdateTo(FirebaseExpression dateTo) {
		this.dateTo = dateTo;
	}

	public void settimeFrom(FirebaseExpression timeFrom) {
		this.timeFrom = timeFrom;
	}

	public void settimes(List<FirebaseExpression> times){
		this.times = times;
	}
	
	public void settimeTo(FirebaseExpression timeTo) {
		this.timeTo = timeTo;
	}

	public void settriggerId(String triggerId) {
		this.triggerId = triggerId;
	}
}
