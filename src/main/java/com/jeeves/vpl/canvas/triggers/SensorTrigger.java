package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.VAR_LOCATION;
import static com.jeeves.vpl.Constants.sensors;

import java.util.List;

import com.jeeves.vpl.Constants.Sensor;
import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseTrigger;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;

public class SensorTrigger extends Trigger { // NO_UCD (unused code)
	private ExpressionReceiver variableReceiver;
	
	@FXML
	protected Button btnLeft;
	@FXML
	protected Button btnRight;
	@FXML
	protected ComboBox<String> cboClassifications;
	@FXML
	protected ComboBox<String> cboSensor;
	@FXML
	protected HBox hboxBox;
	@FXML
	protected ImageView imgBackground;
	@FXML
	protected ImageView imgSensorImage;
	Popup pop = new Popup();
	ChangeListener<String> sensorlistener;

	public SensorTrigger(String name) {
		this(new FirebaseTrigger(name));
	}

	public SensorTrigger(FirebaseTrigger data) {
		super(data);

	}

	{
		for (Sensor s : sensors) {
			cboSensor.getItems().add(s.getname());
		}

	}
	@Override
	public void addListeners() {
		super.addListeners();

		cboSensor.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (arg1 != null && arg1.equals(arg2))
					return;
				for (Sensor s : sensors) {
					if (s.getname().equals(arg2)) {
						setSelectedSensor(s);
						params.put("selectedSensor", arg2);
						break;
					}
				}
			}
		});
		variableReceiver = new ExpressionReceiver(VAR_LOCATION);

		variableReceiver.getChildElements().addListener(
				(ListChangeListener<ViewElement>) listener -> {listener.next(); if(listener.wasRemoved())return; params.put("result", variableReceiver.getChildModel().getname());});// timeReceiverFrom.getChildElements().get(0).getModel())));			

		cboClassifications.valueProperty()
				.addListener((ChangeListener<String>) (arg0, arg1, arg2) -> params.put("result", arg2));
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
		String sensorName = null, result = null;
		if (params.containsKey("selectedSensor"))
			sensorName = params.get("selectedSensor").toString();
		else
			return;
		for (Sensor s : sensors) {
			if (s.getname().equals(sensorName)) {
				setSelectedSensor(s);
			}
		}
		if (params.containsKey("result"))
			result = params.get("result").toString();
		else
			return;
		setResult(result);
	}

	protected void setResult(String result) {
		
		if (result != null && !result.equals("")) {
			cboClassifications.setValue(result);
		}
		
	}

	protected void setSelectedSensor(Sensor sensor) {
		cboSensor.setValue(sensor.getname());
		imgSensorImage.setImage(new Image(sensor.getimage()));
		String[] classifications = (sensor.getvalues());
		cboClassifications.getItems().clear();
		cboClassifications.getItems().addAll(classifications);

		if (model.getparams().get("result") != null)
			cboClassifications.setValue(model.getparams().get("result").toString());
		else if (classifications.length > 0)
			cboClassifications.setValue(classifications[0]);
		if (variableReceiver == null)
			variableReceiver = new ExpressionReceiver(VAR_LOCATION);
		
		//IF our sensor has no possible classification values then it's one we have to drag a variable into
		if(sensor.getvalues().length == 0){
			hboxBox.getChildren().remove(cboClassifications);
			variableReceiver.setReceiveType(sensor.getname());
			hboxBox.getChildren().add(variableReceiver);
			
		}
		else{
			hboxBox.getChildren().remove(cboClassifications);
			hboxBox.getChildren().add(cboClassifications);
			hboxBox.getChildren().remove(variableReceiver);
		}
	}

}
