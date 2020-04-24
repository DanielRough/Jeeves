package com.jeeves.vpl.canvas.triggers;

import com.jeeves.vpl.firebase.FirebaseTrigger;

public class BeginTrigger extends Trigger { // NO_UCD (use default)


	public BeginTrigger(String name) {
		this(new FirebaseTrigger(name));
	}

	public BeginTrigger(FirebaseTrigger data) {
		super(data);
	}


}
