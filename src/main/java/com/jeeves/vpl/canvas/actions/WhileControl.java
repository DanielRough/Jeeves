package com.jeeves.vpl.canvas.actions;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;

import static com.jeeves.vpl.Constants.*;

/**
 * This class represents an if statement. If the condition is true, execute the
 * actions within
 * 
 * @author Daniel
 *
 */
public class WhileControl extends Control {
	public static final String NAME = "While Condition";

	public WhileControl(String name) {
		this(new FirebaseAction(name));
	}

	public WhileControl(FirebaseAction data) {
		super(data);
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		exprreceiver = new ExpressionReceiver(VAR_BOOLEAN);
		evalbox.getChildren().add(1, exprreceiver);
	}


}
