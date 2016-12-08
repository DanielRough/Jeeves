package com.jeeves.vpl.canvas.triggers;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;

import com.jeeves.vpl.Sensor;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseTrigger;

public class SensorTrigger extends Trigger { // NO_UCD (unused code)
	@FXML protected Button btnLeft;
	@FXML protected Button btnRight;
	@FXML protected ComboBox<String> cboSensor;
	@FXML protected ComboBox<String> cboClassifications;
	@FXML protected ImageView imgBackground;
	@FXML protected ImageView imgSensorImage;
	@FXML protected HBox hboxBox;
	private ExpressionReceiver locReceiver;
	public boolean manualChange = false;
	protected String result = "";

	ChangeListener<String> sensorlistener;
	Popup pop = new Popup();
	public Node[] getWidgets(){
		return new Node[]{cboSensor,cboClassifications};
	}

	public SensorTrigger() {
		this(new FirebaseTrigger());
	}
	public void addListeners(){
		super.addListeners();
		Sensor[] sensors = Sensor.sensors;

		cboSensor.valueProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				if(arg1 != null && arg1.equals(arg2))return;
				for(Sensor s : sensors){
					if(s.getname().equals(arg2)){
						setSelectedSensor(s);
					model.getparams().put("selectedSensor", arg2);
					break;
					}
				}				
			}	
		});

		cboClassifications.valueProperty().addListener(
				(ChangeListener<String>) (arg0, arg1, arg2) -> model.getparams().put("result", arg2));
	}
	@Override
	public String getViewPath() {
		return String.format("/SensorTrigger.fxml", this.getClass().getSimpleName());
	}

	@Override
	public void setData(FirebaseTrigger model){
		super.setData(model);
		this.model = model;
		//Map<String,Object> params = model.getparams();
		String sensorName = null,result = null;
		if(params.containsKey("selectedSensor"))
			sensorName = params.get("selectedSensor").toString();
		else 
			return;
		locReceiver= new ExpressionReceiver(Expression.VAR_NUMERIC);

		Sensor[] sensors = Sensor.sensors;
		for(Sensor s : sensors){
			if(s.getname().equals(sensorName)){
				setSelectedSensor(s);
			//	cboSensor.setValue(sensorName);
			//	imgSensorImage.setImage(new Image(s.getimage()));
			}
		}
		if(params.containsKey("result"))
			result = params.get("result").toString();
		else
			return;
		setResult(result);
	}

	public SensorTrigger(FirebaseTrigger data) {
		super(data);
		name.setValue("SENSOR TRIGGER");
		description = "Execute actions based on externally sensed values";
		cboSensor.setOnMouseClicked(event->manualChange = true);
		styleTextCombo(cboClassifications);
		cboClassifications.getStyleClass().add("choice-box-menu-item");
		Sensor[] sensors = Sensor.sensors;
		for(Sensor s : sensors){
			cboSensor.getItems().add(s.getname());
		}
		addListeners();

	}
	protected void setResult(String result) {
		if(result != null && !result.equals("")){
			this.result = result;
			cboClassifications.setValue(result);
		}
	}


	protected void setSelectedSensor(Sensor sensor) {
		cboSensor.setValue(sensor.getname());
		imgSensorImage.setImage(new Image(sensor.getimage()));
			String[]classifications = (sensor.getvalues());
			cboClassifications.getItems().clear();
			cboClassifications.getItems().addAll(classifications);

			if(model.getparams().get("result") != null)
				cboClassifications.setValue(model.getparams().get("result").toString());
			else if(classifications.length > 0)
					cboClassifications.setValue((String)classifications[0]);
			if(locReceiver == null)
				locReceiver= new ExpressionReceiver(Expression.VAR_NUMERIC);

			if(sensor.getname().equals("Location") && !hboxBox.getChildren().contains(locReceiver)){ //a merciless hack that I'll eventually fix
				hboxBox.getChildren().remove(cboClassifications);
				hboxBox.getChildren().add(locReceiver);
			}
			else if(!sensor.getname().equals("Location") && !hboxBox.getChildren().contains(cboClassifications)){
				hboxBox.getChildren().add(cboClassifications);
				hboxBox.getChildren().remove(locReceiver);
			}
	}

}
