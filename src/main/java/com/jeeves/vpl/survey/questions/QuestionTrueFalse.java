package com.jeeves.vpl.survey.questions;

import java.io.IOException;
import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.survey.QuestionView;
import com.jeeves.vpl.survey.Survey;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class QuestionTrueFalse extends QuestionView{
	
	public QuestionView clone(){
		return new QuestionTrueFalse(super.getModel(),mySurvey);
	}
	
	public void loadOptions(){
		optionsPane = new Pane();

	}
	public QuestionTrueFalse(FirebaseQuestion model, Survey survey) {
		super(model,survey);
		setImage("/img/icons/imgbool.png");
		setQuestionText("True/False");
	}



	public void populateQ(FirebaseQuestion entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showCheckQOpts() {
		mySurvey.rdioTrue.setVisible(true);
		mySurvey.rdioFalse.setVisible(true);
		mySurvey.rdioFalse.setSelected(false);
		mySurvey.rdioTrue.setSelected(false);

	}
	@Override
	public void handleCheckQ(String scon) {

		if(!scon.isEmpty()){
		if(Boolean.parseBoolean(scon))
			mySurvey.rdioTrue.setSelected(true);
		else
			mySurvey.rdioFalse.setSelected(true);		
		}
		else{
			mySurvey.rdioFalse.setSelected(false);
			mySurvey.rdioTrue.setSelected(false);
		}
		}

	@Override
	public void showEditOpts(Map<String,Object> opts) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub
		
	}

}
