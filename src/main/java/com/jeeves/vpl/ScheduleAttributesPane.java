package com.jeeves.vpl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ScheduleAttributesPane extends Pane {
	final Logger logger = LoggerFactory.getLogger(RandomNumberPane.class);
	private AttributesPane paneAttributes;
	@FXML private TextField txtStartDate;
	@FXML private TextField txtEndDate;
	@FXML private TextField txtWakeTime;
	@FXML private TextField txtSleepTime;
	@FXML private Button btnAdd;

	private Stage stage;
	public ScheduleAttributesPane(Stage stage, AttributesPane paneAttributes) {
		this.stage = stage;
		this.paneAttributes = paneAttributes;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/ScheduleAttributes.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = fxmlLoader.load();
			getChildren().add(root);
			this.stage = stage;
		} catch (Exception e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
	}
	@FXML
	public void add(Event e) {
		if(txtStartDate.getText().isEmpty() || txtEndDate.getText().isEmpty() || txtWakeTime.getText().isEmpty() || txtSleepTime.getText().isEmpty())
			Constants.makeInfoAlert("Error", "You have empty fields","Please make sure you enter values for all fields");
		else{
			String[] attrNames = {txtStartDate.getText(),txtEndDate.getText(),txtWakeTime.getText(),txtSleepTime.getText()};
			for(FirebaseVariable v : Constants.getOpenProject().getvariables()) {
				for(String name : attrNames) {
					if(name.equals(v.getname())) {
						Constants.makeInfoAlert("Duplicate", "Duplicate Attribute Name", "Attribute with name "  + name + " already exists");
						return;
					}
				}
			}
			Map<String,Object> scheduleAttrs = new HashMap<>();
			FirebaseVariable startDateVar = new FirebaseVariable();
			startDateVar.setname(txtStartDate.getText());
			startDateVar.setVartype(Constants.VAR_DATE);
			startDateVar.setisCustom(true);
			startDateVar.settimeCreated(System.currentTimeMillis());
			scheduleAttrs.put("startdate",txtStartDate.getText());
			FirebaseDB.getInstance().getOpenProject().getvariables().add(startDateVar);
			
			FirebaseVariable endDateVar = new FirebaseVariable();
			endDateVar.setname(txtEndDate.getText());
			endDateVar.setVartype(Constants.VAR_DATE);
			endDateVar.setisCustom(true);
			endDateVar.settimeCreated(System.currentTimeMillis());
			scheduleAttrs.put("enddate",txtEndDate.getText());
			FirebaseDB.getInstance().getOpenProject().getvariables().add(endDateVar);
			
			FirebaseVariable wakeTimeVar = new FirebaseVariable();
			wakeTimeVar.setname(txtWakeTime.getText());
			wakeTimeVar.setVartype(Constants.VAR_CLOCK);
			wakeTimeVar.setisCustom(true);
			wakeTimeVar.settimeCreated(System.currentTimeMillis());
			scheduleAttrs.put("waketime",txtWakeTime.getText());
			FirebaseDB.getInstance().getOpenProject().getvariables().add(wakeTimeVar);
			
			FirebaseVariable sleepTimeVar = new FirebaseVariable();
			sleepTimeVar.setname(txtSleepTime.getText());
			sleepTimeVar.setVartype(Constants.VAR_CLOCK);
			sleepTimeVar.setisCustom(true);
			sleepTimeVar.settimeCreated(System.currentTimeMillis());
			scheduleAttrs.put("sleeptime",txtSleepTime.getText());
			FirebaseDB.getInstance().getOpenProject().getvariables().add(sleepTimeVar);	
			
			paneAttributes.loadVariables();
			Constants.getOpenProject().sethasSchedule(true);
			Constants.getOpenProject().setscheduleAttrs(scheduleAttrs);
			stage.close();
		}
	}	
	@FXML
	public void close(Event e){
		stage.close();
	}
}
