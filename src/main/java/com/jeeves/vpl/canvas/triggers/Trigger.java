package com.jeeves.vpl.canvas.triggers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import com.jeeves.vpl.ActionHolder;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.actions.Action;
import com.jeeves.vpl.canvas.receivers.ActionReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseTrigger;

public abstract class Trigger extends ViewElement<FirebaseTrigger> implements ActionHolder{
	private ArrayList<Action> actions;// = new ArrayList<Action>();
	protected Map<String,Object> params = new HashMap<String,Object>();
	private ActionReceiver childReceiver;
	private double receiverheight = 0.0;
	public ActionReceiver getMyReceiver(){
		return childReceiver;
	}		private Node root;

	
	public static Trigger create(FirebaseTrigger exprmodel){
		String classname = exprmodel.gettype();

		try {
			return (Trigger)Class.forName(classname).getConstructor(FirebaseTrigger.class).newInstance(exprmodel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Trigger(FirebaseTrigger data){
		super(data,FirebaseTrigger.class);
		this.model = data; 
		if(actions != null)
			actions.forEach(action -> childReceiver.addChild(action, 0, 0));
	}

	public void fxmlInit(){
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		fxmlLoader.setLocation(getClass().getResource(getViewPath()));
		try {
			root = (Node) fxmlLoader.load();
			getChildren().add(root);
			childReceiver = new ActionReceiver();
			getChildren().add(childReceiver);

			double layouty = Math.max(((Pane)root).getPrefHeight(), ((Pane)root).getMinHeight());
			childReceiver.setLayoutY(layouty- 5);
			setPickOnBounds(false);
			((Pane)root).heightProperty().addListener(listen->{double layout = ((Pane)root).getHeight();
			childReceiver.setLayoutY(layout- 5);});
			getChildren().forEach(child->child.setPickOnBounds(false));

		} catch (IOException exception) {
			exception.printStackTrace();
			throw new RuntimeException(exception);
		}
		
	}

	public void addListeners() {
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
		childReceiver.heightProperty().addListener(new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				setPrefHeight(newValue.doubleValue()+80);
			}
			
		});

	}

	@Override
	public ViewElement<FirebaseTrigger> getInstance() {
		return this;
	}

	
	public ArrayList<Action> getActions(){
		return actions;
	}
	protected void setData(FirebaseTrigger model){
		super.setData(model);
		double posX = model.getxPos();
		double posY = model.getyPos();
		Point2D position = new Point2D(posX,posY);
		@SuppressWarnings("unchecked")
		List<FirebaseAction> onReceive = model.getactions();
		this.position = position;
		actions = new ArrayList<Action>();
		if(onReceive == null)return;
		for (FirebaseAction action : onReceive) {
			
			Action actionobj = Action.create(action);
			actionobj.setReceiver(childReceiver);
			actions.add(actionobj);
			actionobj.setActionHolder(this);

		}
	}
	public abstract String getViewPath();


	public void updateActionsHeight(){
		double newreceiverheight = 0.0;
		for(Action a : actions)
			newreceiverheight += a.getInitHeight();
		childReceiver.heightChanged(newreceiverheight-receiverheight);
		receiverheight = newreceiverheight;
	}
}
