package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Daniel on 10/06/15.
 */
@SuppressWarnings("serial")
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class", defaultImpl = FirebaseExpression.class)
@IgnoreExtraProperties
public class FirebaseExpression extends FirebaseElement implements Serializable {
	private long index;
	private boolean isCustom;
	private boolean isValue;
	private String value;
	private List<FirebaseExpression> variables = new ArrayList<>();
	private String vartype;
	private long timeCreated;
	
	public FirebaseExpression() {}
	
	public FirebaseExpression(String name) {
		this.setname(name);
	}
	public void settimeCreated(long time){
		this.timeCreated = time;
		
	}
	
	public long gettimeCreated(){
		return timeCreated;
	}
	
	public long getindex() {
		return index;
	}

	public boolean getisCustom() {
		return isCustom;
	}

	public boolean getisValue() {
		return isValue;
	}

	public String getvalue() {
		return value;
	}

	public List<FirebaseExpression> getvariables() {
		return variables;
	}

	public String getvartype() {
		return vartype;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public void setisCustom(boolean isCustom) {
		this.isCustom = isCustom;
	}

	public void setIsValue(boolean isValue) {
		this.isValue = isValue;
	}

	public void setvalue(String value) {
		this.value = value;
	}

	public void setVariables(List<FirebaseExpression> variables) {
		this.variables = variables;
	}

	public void setVartype(String vartype) {
		this.vartype = vartype;
	}
}
