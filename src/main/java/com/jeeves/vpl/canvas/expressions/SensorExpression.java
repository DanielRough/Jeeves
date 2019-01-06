package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.VAR_LOCATION;
import static com.jeeves.vpl.Constants.sensors;

import com.jeeves.vpl.Constants.Sensor;
import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseExpression;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.stage.Popup;

public class SensorExpression extends Expression { // NO_UCD (unused code)
	private static final String RESULT = "result";
	private static final String RETURNS = "returns";
	private static final String SENSOR = "selectedSensor";
	private ComboBox<String> cboClassifications;
	private ComboBox<String> cboSensor;
	private ExpressionReceiver locReceiver;
	private String returnstatus;
	private String sensorname;
	protected String sensorResult = "";
	Popup pop = new Popup();

	public SensorExpression(String name) {
		this(new FirebaseExpression(name));
	}
	@Override
	public void setParentPane(DragPane parent) {
		super.setParentPane(parent);
			if (locReceiver.getChildExpression() != null)
				locReceiver.getChildExpression().setParentPane(parent);
	}
	public SensorExpression(FirebaseExpression data) {
		super(data);

		for (Sensor s : sensors) {
			if(s.isPull()) { //Only want to add sensors that we can pull from!
			cboSensor.getItems().add(s.getname());
			}
		}
		cboSensor.valueProperty().addListener((arg0, arg1, arg2) -> {
				for (Sensor s : sensors) {
					if (s.getname().equals(arg2))
						setSelectedSensor(s);

					params.put(SENSOR, arg2);
					//Here we also want to change the necessary sensors of our project. There's no easy way to do this I don't think

				}
				if(arg1 != null)
					FirebaseDB.getInstance().getOpenProject().getsensors().remove(arg1);
				if(!FirebaseDB.getInstance().getOpenProject().getsensors().contains(arg2))
					FirebaseDB.getInstance().getOpenProject().getsensors().add(arg2);
		});
		locReceiver.getChildElements().addListener(
				(ListChangeListener<ViewElement>) listener -> {listener.next(); if(listener.wasRemoved())return; 

				params.put(RESULT, locReceiver.getChildModel().getname());

				});	
		
		cboClassifications.valueProperty()
				.addListener((ChangeListener<String>) (arg0, arg1, arg2) -> 
				params.put(RESULT, arg2));

				addListeners();

	}

	public String getReturnStatus() {
		return returnstatus;
	}

	public String getSensorName() {
		return sensorname;
	}


	@Override
	public void setData(FirebaseExpression model) {
		super.setData(model);
		updatePane();
		if (model.getparams().containsKey(SENSOR)) {
			String sensorName = model.getparams().get(SENSOR).toString();
			for (Sensor s : sensors) {
				if (s.getname().equals(sensorName))
					setSelectedSensor(s);
			}
		} else {
			return;
		}
		if (model.getparams().containsKey(RESULT)) {
			String result = model.getparams().get(RESULT).toString();
			setResult(result);
		}
	}

	public void setReturnStatus(String returnstatus) {
		this.returnstatus = returnstatus;
	}

	public void setSensorName(String sensorname) {
		this.sensorname = sensorname;
	}

	@Override
	public void setup() {
		operand.setText(RETURNS);

	}

	@Override
	public void updatePane() {
		cboSensor = new ComboBox<>();
		cboClassifications = new ComboBox<>();
		locReceiver = new ExpressionReceiver(VAR_LOCATION);

		setup();
		box.getChildren().clear();
		box.getChildren().addAll(cboSensor, operand, cboClassifications);
		box.setPadding(new Insets(0, 4, 0, 4));

	}

	public void updateReturnVals() {
		if (!cboSensor.getValue().equals("Location"))
			return;
		cboClassifications.getItems().clear();
		if (!params.get(RETURNS).equals(""))
			cboClassifications.setValue(params.get(RETURNS).toString());

	}

	protected void setResult(String result) {
		if (result != null && !result.equals("")) {
			this.sensorResult = result;
			cboClassifications.setValue(result);
		}
	}

	protected void setSelectedSensor(Sensor sensor) {
		String[] classifications = (sensor.getvalues());
		cboSensor.setValue(sensor.getname());
		cboClassifications.getItems().clear();
		cboClassifications.getItems().addAll(classifications);

		if (model.getparams().get(RESULT) != null)
			cboClassifications.setValue(model.getparams().get(RESULT).toString());
		else if (classifications.length > 0)
			cboClassifications.setValue(classifications[0]);
		if (sensor.getname().equals("Location")) { // a merciless hack that I'll
													// eventually fix
			box.getChildren().remove(cboClassifications);
			box.getChildren().add(locReceiver);
		} else {
			box.getChildren().remove(locReceiver);
			box.getChildren().remove(cboClassifications);
			box.getChildren().add(cboClassifications);
		}
	}

}
