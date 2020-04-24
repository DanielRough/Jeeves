package com.jeeves.vpl.canvas.actions;

import com.jeeves.vpl.firebase.FirebaseAction;

public class ScheduleAction extends Action { // NO_UCD (unused code)

	public ScheduleAction(String name) {
		this(new FirebaseAction(name));
	}

	public ScheduleAction(FirebaseAction data) {
		super(data);
	}

	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
	}

}