package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.*;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.stage.Popup;

import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseVariable;

public class SensorExpression extends Expression { // NO_UCD (unused code)
	public static final String DESC = "Returns true if the specified sensor returns a particular result";
	public static final String NAME = "Sensor Result";
	public boolean manualChange = false;
	private ComboBox<String> cboClassifications;
	private ComboBox<String> cboSensor;
	private ExpressionReceiver locReceiver;
	private String returnstatus;
	private String sensorname;
	protected String result = "";
	private Sensor selectedSensor;
	Popup pop = new Popup();

	public SensorExpression() {
		this(new FirebaseExpression());
	}
	@Override
	public void setParentPane(ParentPane parent) {
		super.setParentPane(parent);
			if (locReceiver.getChildExpression() != null)
				locReceiver.getChildExpression().setParentPane(parent);
	}
	public SensorExpression(FirebaseExpression data) {
		super(data);

		for (Sensor s : sensors) {
			if(s.isPull()) //Only want to add sensors that we can pull from!
			cboSensor.getItems().add(s.getname());
		}
		cboSensor.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				for (Sensor s : sensors) {
					if (s.getname().equals(arg2))
						setSelectedSensor(s);
				//	model.getparams().put("selectedSensor", arg2);
					params.put("selectedSensor", arg2);
				}
			}
		});
		locReceiver.getChildElements().addListener(
				(ListChangeListener<ViewElement>) listener -> {listener.next(); if(listener.wasRemoved())return; 
				//model.getparams().put("result", locReceiver.getChildModel().getname());
				params.put("result", locReceiver.getChildModel().getname());

				});// timeReceiverFrom.getChildElements().get(0).getModel())));		
		
		cboClassifications.valueProperty()
				.addListener((ChangeListener<String>) (arg0, arg1, arg2) -> 
		//		model.getparams().put("result", arg2));
				params.put("result", arg2));

				addListeners();

	}

	public String getReturnStatus() {
		return returnstatus;
	}

	public String getSensorName() {
		return sensorname;
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { cboSensor, cboClassifications };
	}

	@Override
	public void setData(FirebaseExpression model) {
		super.setData(model);
		updatePane();
		//params = model.getparams();
		if (model.getparams().containsKey("selectedSensor")) {
			String sensorName = model.getparams().get("selectedSensor").toString();
			for (Sensor s : sensors) {
				if (s.getname().equals(sensorName))
					setSelectedSensor(s);
			}
		} else
			return;
		if (model.getparams().containsKey("result")) {
			String result = model.getparams().get("result").toString();
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
		name = NAME;
		description = DESC;
		this.varType = VAR_BOOLEAN;
		operand.setText("returns");
		box.getStyleClass().add(this.varType);

	}

	@Override
	public void updatePane() {
		cboSensor = new ComboBox<String>();
		cboClassifications = new ComboBox<String>();
		locReceiver = new ExpressionReceiver(VAR_LOCATION);

		styleTextCombo(cboSensor);
		styleTextCombo(cboClassifications);
		setup();
		box.getChildren().clear();
		box.getChildren().addAll(cboSensor, operand, cboClassifications);
		box.setPadding(new Insets(0, 4, 0, 4));

	}

	public void updateReturnVals() {
		if (!cboSensor.getValue().equals("Location"))
			return;
		cboClassifications.getItems().clear();
		if (!params.get("returns").equals(""))
			cboClassifications.setValue(params.get("returns").toString());

	}

	protected void setResult(String result) {
		if (result != null && !result.equals("")) {
//			if(selectedSensor == locSensor){
//				gui.registerVarListener(listener->{
//					listener.next();
//					if(listener.wasAdded()){
//						List<FirebaseVariable> list = (List<FirebaseVariable>) listener.getAddedSubList();
//						if(list.get(0).getname().equals(result))
//							locReceiver.addChild(UserVariable.create(list.get(0)),0,0);
//					}
//				});
//				
//			}
			this.result = result;
			cboClassifications.setValue(result);
		}
	}

	protected void setSelectedSensor(Sensor sensor) {
		this.selectedSensor = sensor;
		String[] classifications = (sensor.getvalues());
		cboSensor.setValue(sensor.getname());
		cboClassifications.getItems().clear();
		cboClassifications.getItems().addAll(classifications);

		if (model.getparams().get("result") != null)
			cboClassifications.setValue(model.getparams().get("result").toString());
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
