package com.jeeves.vpl.canvas.triggers;

import javafx.scene.Node;

public class BeginTrigger extends Trigger{ // NO_UCD (use default)
	
	public static final String NAME = "On beginning study";
	public static final String DESC = "Schedule actions to take place on the patient beginning the study";
//	public BeginTrigger(FirebaseTrigger data) {
//		super(data);
//		addListeners();	}

	@Override
	public String getViewPath() {
		return String.format("/TriggerBegin.fxml", this.getClass().getSimpleName());

	}
	public void fxmlInit(){
		super.fxmlInit();
		name = NAME;
		description = DESC;
	}
	@Override
	public Node[] getWidgets() {
		return new Node[]{};
	}

//	public BeginTrigger() {
//		this(new FirebaseTrigger());
//	}
}
