package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.Map;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Daniel on 16/06/16.
 */
//@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
@IgnoreExtraProperties
public class FirebasePatient implements Serializable{

    private String address;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;

    
    public void setAddress(String address){
    	this.address = address;
    }
    public void setFirstName(String firstName){
    	this.firstName = firstName;
    }
    public void setLastName(String lastName){
    	this.lastName = lastName;
    }
    public void setPhone(String phone){
    	this.phone = phone;
    }
    public void setEmail(String email){
    	this.email = email;
    }
    private Map<String,Object> feedback;
    public Map<String,Object> getfeedback(){ return feedback; }
    private Map<String,Map<String,FirebaseSurvey>> incomplete;
    private Map<String,FirebaseSurvey> complete;
    private String uid;
    
    public String getUid(){ return uid;}
    
    public void setUid(String uid){ this.uid = uid;}
    public String getEmail(){ return email; }
    public String getAddress(){
        return address;
    }

    public String getFirstName(){
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }
    public String getPhone(){
        return phone;
    }

    public Map<String,Map<String,FirebaseSurvey>> getincomplete(){
        return incomplete;
    }
    public Map<String,FirebaseSurvey> getcomplete(){
        return complete;
    }
}
