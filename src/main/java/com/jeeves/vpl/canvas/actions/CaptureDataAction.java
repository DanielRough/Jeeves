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
	public final String DESC = "Capture some data from an available sensor";
	public final String NAME = "Capture Data";
	
	@FXML
	private ComboBox<String> cboSensor;

	public CaptureDataAction() {
		this(new FirebaseAction());
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
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		description = DESC;
	}

	@Override
	public String getViewPath() {
		return String.format("/actionCaptureData.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { cboSensor };
	}

	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();
		if (model.getparams().containsKey("selectedSensor")) {
			String sensorName = model.getparams().get("selectedSensor").toString();
			for (Sensor s : sensors) {
				if (s.getname().equals(sensorName))
					cboSensor.setValue(s.getname());
			}
		} 
	}

}
