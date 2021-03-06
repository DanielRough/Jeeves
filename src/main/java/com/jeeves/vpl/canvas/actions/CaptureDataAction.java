package com.jeeves.vpl.canvas.actions;

import static com.jeeves.vpl.Constants.sensors;
import static com.jeeves.vpl.Constants.SENSOR;
import com.jeeves.vpl.Constants.Sensor;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseDB;
import javafx.fxml.FXML;
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
			if(s.isPull()) { //Only want to add sensors that we can pull from!
				cboSensor.getItems().add(s.getname());
			}
		}
		//Need to add this manually as it doesn't come under the usual sensors
		cboSensor.getItems().add("Location");

		cboTime.getItems().addAll("1 min","10 mins","1 hr","ever");
		cboSensor.valueProperty().addListener((arg0,arg1,arg2)-> {
			for (Sensor s : sensors) {
				if (s.getname().equals(arg2))
					cboSensor.setValue(s.getname());
				params.put(SENSOR, arg2);
			}
			if(arg1 != null)
				FirebaseDB.getInstance().getOpenProject().getsensors().remove(arg1);
			if(!FirebaseDB.getInstance().getOpenProject().getsensors().contains(arg2))
				FirebaseDB.getInstance().getOpenProject().getsensors().add(arg2);
		}
				);
		cboTime.valueProperty().addListener((arg0,arg1,arg2) ->
			params.put("time", arg2)
		
				);

	}

	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
		if (model.getparams().containsKey(SENSOR)) {
			String sensorName = model.getparams().get(SENSOR).toString();
			cboSensor.setValue(sensorName);
		} 
		if(model.getparams().containsKey("time")) {
			cboTime.setValue((String)model.getparams().get("time"));
		}
	}

}
