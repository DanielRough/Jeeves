package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.Map;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Daniel on 25/05/2016.
 */
@IgnoreExtraProperties

public class FirebaseQuestion extends FirebaseElement implements Serializable{

    public long getquestionType(){
        return questionType;
    }

    public void setquestionType(long type){
    	
    	this.questionType = type;
    }
    public String getquestionText(){
        return questionText;
    }

    public void setquestionText(String text){
    	this.questionText = text;
    }
    public String getassignedVar(){
        return assignedVar;
    }
    
    public void setAssignedVar(String assignedVar){
    	this.assignedVar = assignedVar;
    }

    public void setAssign(boolean assign){
    	params.put("assignToScore", assign);
    }
    public Map<String,Object> getparams(){
        return params;
    }

    public String getimage(){
    	return image;
    }
    
    public void setCondition(Map<String,Object> question){
    	params.put("condition", question);
    }

    public void setOptions(Map<String,Object> options){
    	params.put("options", options);
    }
    private long questionType;
    public String questionText;
    private String assignedVar;
    private String image;
}
