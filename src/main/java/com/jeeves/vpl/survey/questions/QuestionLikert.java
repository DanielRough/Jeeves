package com.jeeves.vpl.survey.questions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.survey.QuestionView;
import com.jeeves.vpl.survey.Survey;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;

public class QuestionLikert extends QuestionView {
	
	public QuestionView clone(){
		return new QuestionLikert(super.getModel(),mySurvey);
	}
	public QuestionLikert(FirebaseQuestion model,Survey survey) {
		super(model,survey);
		setImage("/img/icons/imgscale.png");
		setQuestionText("Likert Scale");
		this.description = "User answers by selecting from a scale";
	}
	@FXML private RadioButton rdioButton5;
	@FXML private RadioButton rdioButton7;
	@FXML private TextField txtLikert1;
	@FXML private TextField txtLikert2;
	@FXML private TextField txtLikert3;
	@FXML private TextField txtLikert4;
	@FXML private TextField txtLikert5;
	@FXML private TextField txtLikert6;
	@FXML private TextField txtLikert7;
	@FXML
	private CheckBox chkAssignScore;
	private TextField[] fields;

	public String getImagePath(){
		return "/img/icons/imgscale.png";
	}
	public void loadOptions(){
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/scaleopts.fxml"));
		 try {
			 optionsPane = (Pane) surveyLoader.load();
			 addEventHandlers();
		 }
		 catch(IOException e){
			 
		 }
	}
	public void addEventHandlers() {
		// TODO Auto-generated method stub
		final ToggleGroup group = new ToggleGroup();
		rdioButton5.setToggleGroup(group);
		rdioButton7.setToggleGroup(group);
		rdioButton7.selectedProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
					Boolean newValue) {
				if(newValue){
					txtLikert6.setVisible(true);
					txtLikert7.setVisible(true);
					handleUpdateScale();
				}						
			}
			
		});
		rdioButton5.selectedProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
					Boolean newValue) {
				if(newValue){
					txtLikert6.setVisible(false);
					txtLikert7.setVisible(false);
					handleUpdateScale();
				}
			}
			
		});
		
	    fields = new TextField[]{txtLikert1,txtLikert2,txtLikert3,txtLikert4,txtLikert5,txtLikert6,txtLikert7};
	    for(TextField field : fields){
	    	field.setText("");
	    }

		for(TextField field : fields){
			field.textProperty().addListener(change->handleUpdateScale());
		}
	}

	@Override
	public void showCheckQOpts() {
		mySurvey.cboLessMore.setVisible(true);
		mySurvey.txtNumAnswer.setVisible(true);
		mySurvey.cboLessMore.getSelectionModel().clearSelection();
		mySurvey.txtNumAnswer.clear();
	}
	@Override
	public void handleCheckQ(String scon) {

		if(!scon.isEmpty()){
		String[] components = scon.split(";");
		mySurvey.cboLessMore.setValue(components[0]);
		mySurvey.txtNumAnswer.setText(components[1]);			
		}
		else{
			mySurvey.cboLessMore.getSelectionModel().clearSelection();
			mySurvey.txtNumAnswer.clear();
		}
		}
	private void handleUpdateScale(){
		String number = rdioButton5.isSelected() ? "5" : "7";
		Map<String,Object> qScaleVals = new HashMap<String,Object>();
		qScaleVals.put("number", number);
		ArrayList<String> labels = new ArrayList<String>();
		for(TextField field : fields){
			labels.add(field.getText());
			
		}
		qScaleVals.put("labels",labels);

		model.setOptions(qScaleVals);

	}

	@Override
	public void showEditOpts(Map<String,Object> opts) {
		if(opts == null){
			rdioButton5.setSelected(true);
			for(TextField field : fields){
				field.setText("");
			}
		}
		else{
			if(opts.containsKey("number")){
				String number = opts.get("number").toString();
				if(number.equals("5"))rdioButton5.setSelected(true);
				else if(number.equals("7"))rdioButton7.setSelected(true);
			}
			if(opts.containsKey("labels")){
				ArrayList<String> labels = (ArrayList<String>)opts.get("labels");
				int count = 0;
				for(String label : labels){
					fields[count].setText(label);
					count++;
				}
			}
		}	
		boolean assigntoscore = getModel().getparams().get("assignToScore") == null ? false
				: Boolean.parseBoolean(getModel().getparams().get("assignToScore").toString());
		if (assigntoscore == true) {
			chkAssignScore.setSelected(true);
		} else
			chkAssignScore.setSelected(false);
	}
	@FXML
	public void handleAssignScore(Event e) {
		getModel().setAssign(chkAssignScore.isSelected()); // This is
		// getting
		// silly
	}
}
