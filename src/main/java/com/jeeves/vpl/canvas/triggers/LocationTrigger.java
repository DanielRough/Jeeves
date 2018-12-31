package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.VAR_LOCATION;

import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseTrigger;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

public class LocationTrigger extends Trigger { 
	public static final String CHANGE = "change";
	public static final String NAME = "Location Trigger";
	private ExpressionReceiver variableReceiver;
	@FXML
	protected HBox hboxBox;
	@FXML
	protected ComboBox<String> cboClassifications;
	
	ChangeListener<String> sensorlistener;

	public LocationTrigger(String name) {
		this(new FirebaseTrigger(name));
	}

	public LocationTrigger(FirebaseTrigger data) {
		super(data);

	}

	@Override
	public void addListeners() {
		super.addListeners();
		variableReceiver = new ExpressionReceiver(VAR_LOCATION);
		hboxBox.getChildren().add(variableReceiver);	

		variableReceiver.getChildElements().addListener(
				(ListChangeListener<ViewElement>) listener -> {
					listener.next(); 
					if(listener.wasRemoved()) {
						return; 
					}
				model.setlocation(variableReceiver.getChildModel());});		
		cboClassifications.valueProperty()
				.addListener((ChangeListener<String>) (arg0, arg1, arg2) -> params.put(CHANGE, arg2));
	}

	
	@Override
	public void setParentPane(DragPane parent) {
		super.setParentPane(parent);
		if(variableReceiver.getChildExpression()!= null)
			variableReceiver.getChildExpression().setParentPane(parent);

	}
	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		this.model = model;
		setSelectedSensor();
		if(model.getlocation() != null)
			setResult(model.getlocation());
		if(params.containsKey(CHANGE)){
			cboClassifications.setValue(params.get(CHANGE).toString());
		}
	}

	protected void setResult(FirebaseExpression result) {
			variableReceiver.addChild(UserVariable.create(result), 0, 0);
		}


	protected void setSelectedSensor() {

		
		cboClassifications.getItems().clear();
		cboClassifications.getItems().addAll("enters","leaves","stays in");

		if (model.getparams().get(CHANGE) != null)
			cboClassifications.setValue(model.getparams().get(CHANGE).toString());
		else 
			cboClassifications.setValue("enters");
		if (variableReceiver == null)
			variableReceiver = new ExpressionReceiver(VAR_LOCATION);

	}

}
