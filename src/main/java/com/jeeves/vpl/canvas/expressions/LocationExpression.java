package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.*;

import java.util.List;


import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.stage.Popup;

import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseVariable;

public class LocationExpression extends Expression { // NO_UCD (unused code)
	public static final String DESC = "Returns true if the specified sensor returns a particular result";
	public static final String NAME = "Location";
	public boolean manualChange = false;
	private ExpressionReceiver locReceiver;
	protected String result = "";
	Popup pop = new Popup();

	public LocationExpression() {
		this(new FirebaseExpression());
	}
	@Override
	public void setParentPane(ParentPane parent) {
		super.setParentPane(parent);
			if (locReceiver.getChildExpression() != null)
				locReceiver.getChildExpression().setParentPane(parent);
			locReceiver.getChildElements().addListener(
					(ListChangeListener<ViewElement>) listener -> {listener.next(); if(listener.wasRemoved())return; 
					locReceiver.getChildExpression().setParentPane(parent);

					});// timeReceiverFrom.getChildElements().get(0).getModel())));		

	}
	public LocationExpression(FirebaseExpression data) {
		super(data);

		locReceiver.getChildElements().addListener(
				(ListChangeListener<ViewElement>) listener -> {listener.next(); if(listener.wasRemoved())return; 
				//model.getparams().put("result", locReceiver.getChildModel().getname());
				params.put("result", locReceiver.getChildModel().getname());

				});// timeReceiverFrom.getChildElements().get(0).getModel())));		

		params.put("selectedSensor", "Location");

	}


	@Override
	public Node[] getWidgets() {
		return new Node[] {};
	}

	@Override
	public void setData(FirebaseExpression model) {
		super.setData(model);
		updatePane();

		if (model.getparams().containsKey("result")) {
			String result = model.getparams().get("result").toString();
			setResult(result);
		}
	}


	@Override
	public void setup() {
		name = NAME;
		description = DESC;
		this.varType = VAR_BOOLEAN;
		operand.setText("user is at");
		box.getStyleClass().add(this.varType);

	}

	@Override
	public void updatePane() {

		locReceiver = new ExpressionReceiver(VAR_LOCATION);

		setup();
		box.getChildren().clear();
		box.getChildren().addAll(operand,locReceiver);
		box.setPadding(new Insets(0, 4, 0, 4));

	}

	public void updateReturnVals() {
		

	}

	protected void setResult(String result) {
		if (result != null && !result.equals("")) {
				gui.registerVarListener(listener->{
					listener.next();
					if(listener.wasAdded()){
						List<FirebaseVariable> list = (List<FirebaseVariable>) listener.getAddedSubList();
						if(list.get(0).getname().equals(result))
							locReceiver.addChild(UserVariable.create(list.get(0)),0,0);

					}
				});
				
			this.result = result;
		}
	}

	protected void setSelectedSensor() {
		box.getChildren().add(locReceiver);

	}

}
