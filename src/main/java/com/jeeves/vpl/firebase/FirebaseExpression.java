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
@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class",defaultImpl = FirebaseExpression.class)
@IgnoreExtraProperties
public class FirebaseExpression extends FirebaseElement implements Serializable{
	 private List<FirebaseExpression> variables = new ArrayList<FirebaseExpression>();
	    private String vartype;
	    private String value;
	    private long index;
	    private boolean isValue;
	    private boolean isCustom;

  

    public List<FirebaseExpression> getvariables() {
        return variables;
    }

    public String getvartype() {
        return vartype;
    }
   
    public String getvalue(){return value; }

   
	public void setVariables(List<FirebaseExpression> variables) {
		this.variables = variables;
	}
	public void setVartype(String vartype) {
		this.vartype = vartype;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setIndex(long index) {
		this.index = index;
	}
	public void setIsValue(boolean isValue) {
		this.isValue = isValue;
	}

    public long getindex(){
        return index;
    }

    public boolean getisValue(){
        return isValue;
    }

    public boolean getisCustom(){
        return isCustom;
    }

    public void setisCustom(boolean isCustom){
    	this.isCustom = isCustom;
    }
}
