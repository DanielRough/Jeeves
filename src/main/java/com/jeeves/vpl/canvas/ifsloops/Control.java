package com.jeeves.vpl.canvas.ifsloops;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import com.jeeves.vpl.ActionHolder;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.canvas.actions.Action;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.receivers.ActionReceiver;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseExpression;

/**
 * Class which represents a Control structure, such as a for loop, if-statement
 * or do-while loop. These contain actions, and are actions themselves
 * 
 * @author Daniel
 *
 */
@SuppressWarnings("rawtypes")
public abstract class Control extends Action implements ActionHolder{
	private ArrayList<Action> actions = new ArrayList<Action>();
	protected ActionReceiver childReceiver;
	@FXML
	protected HBox evalbox;
	@FXML
	private Pane pane;
	protected ExpressionReceiver exprreceiver;

	public ActionReceiver getMyReceiver() {
		return childReceiver;
	}
	public Control(FirebaseAction data) {
		super(data);
	}

	public Control(){
		super();
	}
	public void fxmlInit(){
		super.fxmlInit();
		this.type = ElementType.CTRL_ACTION;
		childReceiver = new ActionReceiver();
		getChildren().add(childReceiver);
		childReceiver.setLayoutY(((Pane) pane).getPrefHeight());

		pane.setPrefHeight(USE_COMPUTED_SIZE);
		childReceiver.getBrackets().getStyleClass().clear();
		childReceiver.getBrackets().getStyleClass().add("if_control");
		setPickOnBounds(false);
	}
	
	protected void addListeners() {
		super.addListeners();
		childReceiver.getChildElements().addListener(
				(ListChangeListener<ViewElement>) arg0 -> {
					ArrayList<Action> newActions = new ArrayList<Action>();
					if(model.getactions() == null)
						model.setactions(new ArrayList<FirebaseAction>());
					model.getactions().clear();
					childReceiver.getChildElements().forEach(
							element ->{newActions.add((Action) element);
							model.getactions().add((FirebaseAction)element.getModel());
							});
					this.actions = newActions;
				});
	}

	public void setData(FirebaseAction model) {
		super.setData(model);
		actions = new ArrayList<Action>();
			List<FirebaseAction> onReceive = new ArrayList<FirebaseAction>(model.getactions()); //WILL THIS MAKE A COPY
			for (FirebaseAction action : onReceive) {
				Action myaction = Action.create(action);
				actions.add(myaction);
				childReceiver.addChild(myaction,0,0); 
			}
			FirebaseExpression condition = model.getcondition();
				Expression expr = Expression.create(condition);
				exprreceiver.addChild(expr, 0, 0);
	}

	@Override
	public Control getInstance() {
		return this;
	}

}