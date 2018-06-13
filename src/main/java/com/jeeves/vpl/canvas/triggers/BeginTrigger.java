package com.jeeves.vpl.canvas.triggers;

import com.jeeves.vpl.firebase.FirebaseTrigger;

import javafx.scene.Node;

public class BeginTrigger extends Trigger { // NO_UCD (use default)

	public static final String NAME = "Begin Trigger";

	public BeginTrigger() {
		this(new FirebaseTrigger());
	}

	public BeginTrigger(FirebaseTrigger data) {
		super(data);
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
	}

	@Override
	public String getViewPath() {
		return String.format("/TriggerBegin.fxml", this.getClass().getSimpleName());

	}

}
