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
	private long clocktype = 0;
	private String triggerId;
	private List<FirebaseAction> actions;

	public long getclocktype() {
		return clocktype;
	}

	public List<FirebaseAction> getactions() {
		return actions;
	}

	public void setactions(List<FirebaseAction> actions) {
		this.actions = actions;
	}

	public void settriggerId(String triggerId) {
		this.triggerId = triggerId;
	}

	public String gettriggerId() {
		return triggerId;
	}
}
