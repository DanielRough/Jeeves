package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.List;

import org.apache.poi.ss.formula.functions.T;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Daniel on 29/04/2016.
 */
@SuppressWarnings("serial")
@IgnoreExtraProperties
public class FirebaseVariable extends FirebaseExpression implements Serializable {
	private boolean isRandom = false;
	private List<String> randomOptions;
	public boolean getisRandom() {
		return isRandom;
	}
	public void setisRandom(boolean isRandom) {
		this.isRandom = isRandom;
	}
	
	public List<String> getrandomOptions(){
		return randomOptions;
	}
	public void setrandomOptions(List<String> options) {
		this.randomOptions = options;
	}
}
