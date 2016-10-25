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
public abstract class Control extends Action implements ActionHolder{
	private ArrayList<Action> actions = new ArrayList<Action>();
	protected ActionReceiver childReceiver;
	private Expression condition;
	@FXML
	protected HBox evalbox;
	@FXML
	private Pane pane;
	protected ExpressionReceiver exprreceiver;
	private Label retractedLabel;
	private double receiverheight = 0.0;

	public ActionReceiver getMyReceiver() {
		return childReceiver;
	}
	public Control(FirebaseAction data) {
		super(data);
		setInitHeight(80); // 30 + 50
		if(model.getactions() != null){
			List<FirebaseAction> onReceive = new ArrayList<FirebaseAction>(model.getactions()); //WILL THIS MAKE A COPY
			for (FirebaseAction action : onReceive) {
				Action myaction = Action.create(action);
				myaction.setReceiver(childReceiver);
				actions.add(myaction);
				childReceiver.addElement(myaction); 
				myaction.setActionHolder(this);
			}
		}
		if (model.getcondition() != null) {
			FirebaseExpression condition = model.getcondition();
			if(condition.getisValue()){
				exprreceiver.setText(condition.getvalue().toString());
			}
			else {
				Expression expr = Expression.create(condition);
				exprreceiver.addChild(expr, 0, 0);
			}
		}
		
	}

	public void fxmlInit(){
		super.fxmlInit();
		childReceiver = new ActionReceiver();
		getChildren().add(childReceiver);
		childReceiver.setLayoutY(((Pane) pane).getPrefHeight());

		pane.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				childReceiver.setLayoutY((double) arg2);
				setInitHeight((double) arg2 + receiverheight + 50);
			}
		});
		pane.setPrefHeight(USE_COMPUTED_SIZE);
		childReceiver.setParentAction(this); 
		childReceiver.addStyle("numeric_button");
		retractedLabel = new Label(".....");
		retractedLabel.setPadding(new Insets(5, 5, 5, 5));
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
							((Action)element).setActionHolder(this);});
					this.actions = newActions;
					updateActionsHeight();
				});

		exprreceiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
			if (exprreceiver.getChildElements().get(0) != null) {
				ViewElement child = exprreceiver.getChildElements().get(0);
				setCondition((Expression) child);
				model.setcondition((FirebaseExpression)child.getModel());
			} else{
				setCondition(null);
				model.setcondition(null);
			}
			});
	}

	public void setData(FirebaseAction model) {
		super.setData(model);
		actions = new ArrayList<Action>();
		receiverheight = 0.0;
		
	}

	public void updateActionsHeight() {
		double newreceiverheight = 0.0;
		for (Action a : actions)
			newreceiverheight += a.getInitHeight();
		childReceiver.heightChanged(newreceiverheight - receiverheight);
		setInitHeight((newreceiverheight - receiverheight) + getInitHeight());
		receiverheight = newreceiverheight;
		if (parent != null)
			parent.updateActionsHeight();
	}

	@Override
	public Control getInstance() {
		return this;
	}

	private void setCondition(Expression condition) {
		this.condition = condition;
	}

}