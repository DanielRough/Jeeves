package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.stage.Popup;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

public class SensorExpression extends Expression  { // NO_UCD (unused code)
	public static final String NAME = "Does this sensor return that result?";
	public static final String DESC = "Returns true if the specified sensor returns a particular result";
		private ComboBox<String> cboSensor;
		private ComboBox<String> cboClassifications;
		private String sensorname;
		private String returnstatus;
		private ExpressionReceiver locReceiver;
		Popup pop = new Popup();
		public boolean manualChange = false;
		protected String result = "";

		public Node[] getWidgets(){
			return new Node[]{cboSensor,cboClassifications};
		}
public SensorExpression() {
	this(new FirebaseExpression());
}
		public void setReturnStatus(String returnstatus){
			this.returnstatus = returnstatus;
		}
		public void setSensorName(String sensorname){
			this.sensorname = sensorname;
		}
		public String getSensorName(){
			return sensorname;
		}
		public String getReturnStatus(){
			return returnstatus;
		}

		public SensorExpression(FirebaseExpression data) {
			super(data);
			for(Sensor s : sensors){
				cboSensor.getItems().add(s.getname());
			}
			cboSensor.valueProperty().addListener(new ChangeListener<String>(){
				@Override
				public void changed(ObservableValue<? extends String> arg0,
						String arg1, String arg2) {
					for(Sensor s : sensors){
						if(s.getname().equals(arg2))
							setSelectedSensor(s);
						model.getparams().put("selectedSensor", arg2);
					}				
				}	
			});

			cboClassifications.valueProperty().addListener(
					(ChangeListener<String>) (arg0, arg1, arg2) -> model.getparams().put("result", arg2));
			addListeners();

		}
		public void setData(FirebaseExpression model){
			super.setData(model);
			updatePane();
			 params = model.getparams();
			if(params.containsKey("selectedSensor")){
			String sensorName = params.get("selectedSensor").toString();
			for(Sensor s : sensors){
				if(s.getname().equals(sensorName))
					setSelectedSensor(s);
			}
			}
			else
				return;
			if(params.containsKey("result")){
			String result = params.get("result").toString();
			setResult(result);
			}
		}
		protected void setResult(String result) {
			if(result != null && !result.equals("")){
				this.result = result;
				cboClassifications.setValue(result);
			}
		}
		@Override
		public void setup() {
			name = NAME;
			description = DESC;
			this.varType = VAR_BOOLEAN;
			operand.setText("returns");
			box.getStyleClass().add(this.varType);

		}
		
		public void updateReturnVals(){
			if(!cboSensor.getValue().equals("Location"))return;
			cboClassifications.getItems().clear();
			if(!params.get("returns").equals(""))
				cboClassifications.setValue(params.get("returns").toString());

		}

		protected void setSelectedSensor(Sensor sensor) {
			
				String[]classifications = (sensor.getvalues());
				cboSensor.setValue(sensor.getname());
				cboClassifications.getItems().clear();
				cboClassifications.getItems().addAll(classifications);

				if(model.getparams().get("result") != null)
					cboClassifications.setValue(model.getparams().get("result").toString());
				else if(classifications.length > 0)
					cboClassifications.setValue((String)classifications[0]);
				if(sensor.getname().equals("Location")){ //a merciless hack that I'll eventually fix
					locReceiver = new ExpressionReceiver(VAR_LOCATION);
					box.getChildren().remove(cboClassifications);
					box.getChildren().add(locReceiver);
				}
				else{
					box.getChildren().remove(locReceiver);
					box.getChildren().remove(cboClassifications);
					box.getChildren().add(cboClassifications);
				}
		}
		@Override
		public void updatePane() {
			cboSensor = new ComboBox<String>();
			cboClassifications = new ComboBox<String>();
			styleTextCombo(cboSensor);
			styleTextCombo(cboClassifications);
			setup();
			box.getChildren().clear();
			box.getChildren().addAll(cboSensor,operand,cboClassifications);
			box.setPadding(new Insets(0,4, 0,4));

		}

	}
