package com.jeeves.vpl.canvas.actions;

import static com.jeeves.vpl.Constants.sensors;

import java.util.Map;

import com.jeeves.vpl.Constants.Sensor;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseDB;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

public class CaptureDataAction extends Action{
	
	@FXML
	private ComboBox<String> cboSensor;
	@FXML
	private ComboBox<String> cboTime;
	public CaptureDataAction(String name) {
		this(new FirebaseAction(name));
	}

	public CaptureDataAction(FirebaseAction data) {
		super(data);
	}

	@Override
	public void addListeners() {
		super.addListeners();
		for (Sensor s : sensors) {
			if(s.isPull()) //Only want to add sensors that we can pull from!
			cboSensor.getItems().add(s.getname());
		}
		//Need to add this manually as it doesn't come under the usual sensors
		cboSensor.getItems().add("Location");
		//cboStartStop.getItems().addAll("start","stop");
		cboTime.getItems().addAll("1 min","10 mins","1 hr","ever");
		cboSensor.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				for (Sensor s : sensors) {
					if (s.getname().equals(arg2))
						cboSensor.setValue(s.getname());
					params.put("selectedSensor", arg2);
				}
				if(arg1 != null)
					FirebaseDB.getOpenProject().getsensors().remove(arg1);
				FirebaseDB.getOpenProject().getsensors().add(arg2);
			}
		});
		cboTime.valueProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				params.put("time", arg2);
			}
		});
		//cboTime.setValue("10 mins");

	}

	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
		if (model.getparams().containsKey("selectedSensor")) {
			String sensorName = model.getparams().get("selectedSensor").toString();
			cboSensor.setValue(sensorName);
		} 
		if(model.getparams().containsKey("time")) {
			cboTime.setValue((String)model.getparams().get("time"));
		}
	}

}
