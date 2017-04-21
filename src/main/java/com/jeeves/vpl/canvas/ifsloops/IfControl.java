package com.jeeves.vpl.canvas.ifsloops;

import javafx.scene.Node;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;

import static com.jeeves.vpl.Constants.*;
/**
 * This class represents an if statement. If the condition is true, execute the actions within
 * @author Daniel
 *
 */
public class IfControl extends Control { 
	public static final String NAME = "if this is true do that";
	public static final String DESC = "This will execute its contained actions if the contained expression is true";
	public Node[] getWidgets(){
		return new Node[]{exprreceiver,childReceiver};
	}
	
	public void fxmlInit(){
		super.fxmlInit();
		name = NAME;
		description = DESC;
		exprreceiver = new ExpressionReceiver(VAR_BOOLEAN);
		evalbox.getChildren().add(1, exprreceiver);
	}

	@Override
	public String getViewPath() {
		return String.format("/ControlIf.fxml", this.getClass().getSimpleName());
	}

}
