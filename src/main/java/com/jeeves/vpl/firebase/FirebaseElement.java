package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.firebase.database.IgnoreExtraProperties;

@SuppressWarnings("serial")
@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
@IgnoreExtraProperties
public class FirebaseElement implements Serializable{

    private String description;
    protected Map<String,Object> params = new HashMap<String,Object>();
    private String type;
    private long xPos;
    private long yPos;
	public StringProperty name = new SimpleStringProperty();
	
    public String getdescription() {
        return description;
    }

    public void setdescription(String description){
    	this.description = description;
    }
    public String getname() {
        return name.get();
    }
    public void setname(String name){
    	this.name.setValue(name);
    }
    public Map<String, Object> getparams() {
        return params;
    }
    public void setparams(Map<String,Object> params){
    	this.params = params;
    }

    public String gettype() {
        return type;
    }
    public void settype(String type){
    	this.type = type;
    }
    public long getxPos() {
        return xPos;
    }

    public void setxPos(long xPos){
    	this.xPos = xPos;
    }
  
    public long getyPos() {
        return yPos;
    }
    public void setyPos(long yPos){
    	this.yPos = yPos;
    }


    @Override 
    public String toString(){
    	return name.get();
    }
}
