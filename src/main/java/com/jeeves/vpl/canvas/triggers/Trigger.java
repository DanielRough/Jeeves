package com.jeeves.vpl.canvas.triggers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jeeves.vpl.ActionHolder;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.actions.Action;
import com.jeeves.vpl.canvas.receivers.ActionReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseTrigger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public abstract class Trigger extends ViewElement<FirebaseTrigger> implements ActionHolder{
	private ArrayList<Action> actions;// = new ArrayList<Action>();
	//protected Map<String,Object> params = new HashMap<String,Object>();
	protected ObservableMap<String,Object> params = FXCollections.observableHashMap();
	private ActionReceiver childReceiver;
	private double receiverheight = 0.0;
	public static final String[] triggerNames = {"com.jeeves.vpl.canvas.triggers.ButtonTrigger",
			"com.jeeves.vpl.canvas.triggers.ClockTriggerInterval",
			"com.jeeves.vpl.canvas.triggers.ClockTriggerRandom",
			"com.jeeves.vpl.canvas.triggers.ClockTriggerSetTimes",
			"com.jeeves.vpl.canvas.triggers.SensorTrigger",
			"com.jeeves.vpl.canvas.triggers.SurveyTrigger"
	};
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
	
	protected static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
	public Trigger(FirebaseTrigger data){
		super(data,FirebaseTrigger.class);
		this.model = data; 
		if(model.gettriggerId() == null)
			model.settriggerId(getSaltString());
		
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
		if(params != null)
		params.addListener(new MapChangeListener<String, Object>(){

			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change) {
				model.settriggerId(getSaltString()); //Again, update, must reset 
				System.out.println("CHANGED PARAMS");
			}
			
		});
		childReceiver.getChildElements().addListener(
				(ListChangeListener<ViewElement>) arg0 -> {
					model.settriggerId(getSaltString()); //Need to update ID if actions change
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
		params = FXCollections.observableMap(model.getparams());
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
