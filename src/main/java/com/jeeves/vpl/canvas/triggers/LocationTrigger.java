package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.VAR_LOCATION;
import static com.jeeves.vpl.Constants.bluetoothSensor;
import static com.jeeves.vpl.Constants.wifiSensor;
import static com.jeeves.vpl.Constants.sensors;

import java.util.List;

import com.jeeves.vpl.Constants.Sensor;
import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseTrigger;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;

public class LocationTrigger extends Trigger { // NO_UCD (unused code)
	public static final String DESC = "Schedule actions to take place when a phone sensor returns a particular result";
	public static final String NAME = "Location Trigger";
	private ExpressionReceiver variableReceiver;
	
	@FXML
	protected HBox hboxBox;
	@FXML
	protected ComboBox<String> cboClassifications;
	
	ChangeListener<String> sensorlistener;

	public LocationTrigger() {
		this(new FirebaseTrigger());
	}

	public LocationTrigger(FirebaseTrigger data) {
		super(data);

	}

	@Override
	public void addListeners() {
		super.addListeners();


		variableReceiver.getChildElements().addListener(
				(ListChangeListener<ViewElement>) listener -> {listener.next(); if(listener.wasRemoved())return; params.put("result", variableReceiver.getChildModel().getname());});// timeReceiverFrom.getChildElements().get(0).getModel())));			

		cboClassifications.valueProperty()
				.addListener((ChangeListener<String>) (arg0, arg1, arg2) -> params.put("result", arg2));
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		description = DESC;
		variableReceiver = new ExpressionReceiver(VAR_LOCATION);
		hboxBox.getChildren().add(variableReceiver);
	}

	@Override
	public String getViewPath() {
		return String.format("/TriggerLocation.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] {cboClassifications };
	}
	
	@Override
	public void setParentPane(ParentPane parent) {
		super.setParentPane(parent);
		System.out.println("THEN ME");
		if(variableReceiver.getChildExpression()!= null)
			variableReceiver.getChildExpression().setParentPane(parent);

	}
	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		this.model = model;
		// Map<String,Object> params = model.getparams();
		String result = null;
		setSelectedSensor();
		if (params.containsKey("result"))
			result = params.get("result").toString();
		else
			return;
		setResult(result);
	}

	protected void setResult(String result) {
		
		if (result != null && !result.equals("")) {
			// this.result = result;
				gui.registerVarListener(listener->{
					listener.next();
					if(listener.wasAdded()){
						List<FirebaseVariable> list = (List<FirebaseVariable>) listener.getAddedSubList();
						if(list.get(0).getname().equals(result)){
							variableReceiver.addChild(UserVariable.create(list.get(0)),0,0);
							System.out.println("FIRST ME");
						}
						}
				});

		}
		
	}

	protected void setSelectedSensor() {

		
		cboClassifications.getItems().clear();
		cboClassifications.getItems().addAll("enters","leaves","stays in");

		if (model.getparams().get("result") != null)
			cboClassifications.setValue(model.getparams().get("result").toString());
		else 
			cboClassifications.setValue("enters");
		if (variableReceiver == null)
			variableReceiver = new ExpressionReceiver(VAR_LOCATION);

	}

}
