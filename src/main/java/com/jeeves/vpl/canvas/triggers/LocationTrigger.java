package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.VAR_LOCATION;

import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseTrigger;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

public class LocationTrigger extends Trigger { // NO_UCD (unused code)
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
				(ListChangeListener<ViewElement>) listener -> {listener.next(); if(listener.wasRemoved())return; model.setlocation(variableReceiver.getChildModel());});// timeReceiverFrom.getChildElements().get(0).getModel())));			

		 params.put("change", "enters");
		cboClassifications.valueProperty()
				.addListener((ChangeListener<String>) (arg0, arg1, arg2) -> params.put("change", arg2));
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		variableReceiver = new ExpressionReceiver(VAR_LOCATION);
		hboxBox.getChildren().add(variableReceiver);
	}

	@Override
	public String getViewPath() {
		return String.format("/TriggerLocation.fxml", this.getClass().getSimpleName());
	}

	
	@Override
	public void setParentPane(ParentPane parent) {
		super.setParentPane(parent);
		//System.out.println("THEN ME");
		//System.out.println(variableReceiver.getChildExpression());
		if(variableReceiver.getChildExpression()!= null)
			variableReceiver.getChildExpression().setParentPane(parent);

	}
	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		this.model = model;
		// Map<String,Object> params = model.getparams();
		//String result = null;
		setSelectedSensor();
		if(model.getlocation() != null)
			setResult(model.getlocation());
//		if (params.containsKey("result"))
//			variableReceiver.addChild(UserVariable.create((FirebaseExpression)params.get("result")), 0, 0);

//			result = params.get("result").toString();
//		else
//			return;
	//	setResult(result);
		
		if(params.containsKey("change")){
			cboClassifications.setValue(params.get("change").toString());
		}
	}

	protected void setResult(FirebaseExpression result) {
		
		//if (result != null && !result.equals("")) {
			// this.result = result;
			variableReceiver.addChild(UserVariable.create(result), 0, 0);

//				gui.registerVarListener(listener->{
//					listener.next();
//					if(listener.wasAdded()){
//						List<FirebaseVariable> list = (List<FirebaseVariable>) listener.getAddedSubList();
//						if(list.get(0).getname().equals(result)){
//							variableReceiver.addChild(UserVariable.create(list.get(0)),0,0);
//							//System.out.println("FIRST ME");
//						}
//						}
//				});

		}

	//}

	protected void setSelectedSensor() {

		
		cboClassifications.getItems().clear();
		cboClassifications.getItems().addAll("enters","leaves","stays in");

		if (model.getparams().get("changes") != null)
			cboClassifications.setValue(model.getparams().get("changes").toString());
		else 
			cboClassifications.setValue("enters");
		if (variableReceiver == null)
			variableReceiver = new ExpressionReceiver(VAR_LOCATION);

	}

}
