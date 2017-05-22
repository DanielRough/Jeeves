package com.jeeves.vpl.canvas.triggers;

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

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseTrigger;

import static com.jeeves.vpl.Constants.*;

public class SensorTrigger extends Trigger { // NO_UCD (unused code)
	public static final String DESC = "Schedule actions to take place when a phone sensor returns a particular result";
	public static final String NAME = "On sensor result";
	private ExpressionReceiver locReceiver;
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

	public SensorTrigger() {
		this(new FirebaseTrigger());
	}

	public SensorTrigger(FirebaseTrigger data) {
		super(data);

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

		cboClassifications.valueProperty()
				.addListener((ChangeListener<String>) (arg0, arg1, arg2) -> params.put("result", arg2));
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		description = DESC;
		for (Sensor s : sensors) {
			cboSensor.getItems().add(s.getname());
		}
	}

	@Override
	public String getViewPath() {
		return String.format("/TriggerSensor.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { cboSensor, cboClassifications };
	}

	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		this.model = model;
		// Map<String,Object> params = model.getparams();
		String sensorName = null, result = null;
		if (params.containsKey("selectedSensor"))
			sensorName = params.get("selectedSensor").toString();
		else
			return;
		locReceiver = new ExpressionReceiver(VAR_LOCATION);
		for (Sensor s : sensors) {
			if (s.getname().equals(sensorName)) {
				setSelectedSensor(s);
				// cboSensor.setValue(sensorName);
				// imgSensorImage.setImage(new Image(s.getimage()));
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
			// this.result = result;
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
		if (locReceiver == null)
			locReceiver = new ExpressionReceiver(VAR_LOCATION);

		if (sensor.getname().equals("Location") && !hboxBox.getChildren().contains(locReceiver)) { // a
																									// merciless
																									// hack
																									// that
																									// I'll
																									// eventually
																									// fix
			hboxBox.getChildren().remove(cboClassifications);
			hboxBox.getChildren().add(locReceiver);
		} else if (!sensor.getname().equals("Location") && !hboxBox.getChildren().contains(cboClassifications)) {
			hboxBox.getChildren().add(cboClassifications);
			hboxBox.getChildren().remove(locReceiver);
		}
	}

}
