package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.VAR_LOCATION;

import java.util.List;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.stage.Popup;

public class LocationExpression extends Expression { // NO_UCD (unused code)
	private static final String RESULT = "result";
	private ExpressionReceiver locReceiver;
	protected String locationResult = "";
	Popup pop = new Popup();

	public LocationExpression(String name) {
		this(new FirebaseExpression(name));
	}
	@Override
	public void setParentPane(DragPane parent) {
		super.setParentPane(parent);
			if (locReceiver.getChildExpression() != null)
				locReceiver.getChildExpression().setParentPane(parent);
			locReceiver.getChildElements().addListener(
					(ListChangeListener<ViewElement>) listener -> {listener.next(); if(listener.wasRemoved())return; 
					locReceiver.getChildExpression().setParentPane(parent);

					});
	}
	public LocationExpression(FirebaseExpression data) {
		super(data);

		locReceiver.getChildElements().addListener(
				(ListChangeListener<ViewElement>) listener -> {listener.next(); if(listener.wasRemoved())return; 
				params.put(RESULT, locReceiver.getChildModel().getname());

				});	

		params.put("selectedSensor", "Location");

	}



	@Override
	public void setData(FirebaseExpression model) {
		super.setData(model);
		updatePane();

		if (model.getparams().containsKey(RESULT)) {
			String locResult = model.getparams().get(RESULT).toString();
			setResult(locResult);
		}
	}


	@Override
	public void setup() {
		operand.setText("user is at");

	}

	@Override
	public void updatePane() {
		super.updatePane();
		locReceiver = new ExpressionReceiver(VAR_LOCATION);
		box.getChildren().clear();
		box.getChildren().addAll(operand,locReceiver);
		box.setPadding(new Insets(0, 4, 0, 4));

	}

	protected void setResult(String result) {
		if (result != null && !result.equals("")) {
			Constants.getOpenProject().registerVarListener(listener->{
					listener.next();
					if(listener.wasAdded()){
						@SuppressWarnings("unchecked")
						List<FirebaseVariable> list = (List<FirebaseVariable>) listener.getAddedSubList();
						if(list.get(0).getname().equals(result))
							locReceiver.addChild(UserVariable.create(list.get(0)),0,0);

					}
				});
				
			this.locationResult = result;
		}
	}

	protected void setSelectedSensor() {
		box.getChildren().add(locReceiver);

	}

}
