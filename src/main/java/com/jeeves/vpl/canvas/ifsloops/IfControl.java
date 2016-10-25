package com.jeeves.vpl.canvas.ifsloops;

import javafx.scene.Node;

import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;

/**
 * This class represents an if statement. If the condition is true, execute the actions within
 * @author Daniel
 *
 */
public class IfControl extends Control { // NO_UCD (unused code)

	public IfControl(FirebaseAction data) {
		super(data);
		name.setValue("IF CONDITION");
		description = "Executes the given actions if the given expression is true";

		addListeners();
	}
	public Node[] getWidgets(){
		return new Node[]{exprreceiver,childReceiver};
	}
	
	public void fxmlInit(){
		super.fxmlInit();
		exprreceiver = new ExpressionReceiver(Expression.VAR_BOOLEAN);
		evalbox.getChildren().add(1, exprreceiver);
	}
	public IfControl(){
		this(new FirebaseAction());
	}

	public void setData(FirebaseAction model){
		super.setData(model);

	}

	@Override
	public String getViewPath() {
		return String.format("/controlIf.fxml", this.getClass().getSimpleName());
	}

}
