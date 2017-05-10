package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.DATETIME;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
public class QuestionDateTime extends QuestionView{

	@FXML private HBox hboxOptions;
	@FXML private CheckBox chkDate;
	@FXML private CheckBox chkTime;
	@FXML private VBox vboxDateOpts;
	@FXML private RadioButton rdioAny;
	@FXML private RadioButton rdioPast;
	@FXML private RadioButton rdioFuture;
	private ToggleGroup tgroup;
	private Map<String,Object> options;

	public String getLabel(){
		return "Select a Date/Time";
	}
	public String getImagePath(){
		return "/img/icons/imgdate.png";
	}

	public void loadOptions(){
		 tgroup = new ToggleGroup();

		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/OptionsDateTime.fxml"));
		 try {
			 optionsPane = (Pane) surveyLoader.load();
			 addEventHandlers();
		 }
		 catch(IOException e){
			 System.out.println("Hello?");
		 }
		 rdioAny.setToggleGroup(tgroup);
		 rdioAny.setUserData(0);
		 rdioFuture.setToggleGroup(tgroup);
		 rdioFuture.setUserData(1);
		 rdioPast.setToggleGroup(tgroup);
		 rdioPast.setUserData(2);
		 options = new HashMap<String,Object>();
	}
	@Override
	public void showEditOpts(Map<String,Object> opts) {
		if(opts.containsKey("useDate"))
			chkDate.setSelected((boolean)opts.get("useDate"));
		if(opts.containsKey("useTime"))
			chkDate.setSelected((boolean)opts.get("useTime"));
		if(opts.containsKey("dateConstraint"))
			switch((int)opts.get("dateConstraint")){
			case 0:
				tgroup.selectToggle(rdioAny);break;
			case 1:
				tgroup.selectToggle(rdioFuture);break;
			case 2:
				tgroup.selectToggle(rdioPast);break;
			}
	}

	@Override
	public void addEventHandlers() {
		chkDate.selectedProperty().addListener((x,y,z)->{
			if(chkDate.isSelected())
				vboxDateOpts.setVisible(true);
			else
				vboxDateOpts.setVisible(false);
			options.put("useDate", chkDate.isSelected());
			model.setOptions(options);
		});
		chkTime.selectedProperty().addListener((x,y,z)->{
			options.put("useTime", chkTime.isSelected());
			model.setOptions(options);
		});
		tgroup.selectedToggleProperty().addListener((x,y,z)->{
			options.put("dateConstraint", tgroup.getSelectedToggle().getUserData());
			model.setOptions(options);
		});
		
	}

	@Override
	public int getQuestionType() {
		// TODO Auto-generated method stub
		return DATETIME;
	}


}
