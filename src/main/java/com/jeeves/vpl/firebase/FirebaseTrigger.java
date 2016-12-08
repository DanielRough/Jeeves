package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.List;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Daniel on 29/04/2016.
 */
@IgnoreExtraProperties
public class FirebaseTrigger extends FirebaseElement implements Serializable{

    public long getclocktype() {
        return clocktype;
    }


    private long clocktype = 0;


    public List<FirebaseAction> getactions() {
        return actions;
    }

    public void setactions(List<FirebaseAction> actions){
    	this.actions = actions;
    }
    private String triggerId;
    
    public void settriggerId(String triggerId){
    	this.triggerId = triggerId;
    }
    public String gettriggerId(){
    	return triggerId;
    }
    private List<FirebaseAction> actions;
}
