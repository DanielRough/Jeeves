package com.jeeves.vpl.survey.questions;

import java.io.IOException;
import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.survey.QuestionView;
import com.jeeves.vpl.survey.Survey;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class QuestionLocation extends QuestionView{

	public QuestionLocation(FirebaseQuestion model, Survey survey) {
		super(model, survey);
		setImage("/img/icons/imggeo.png");
		setQuestionText("Location");
		}

	public void loadOptions(){
		optionsPane = new Pane();

	}
	public QuestionView clone(){
		return new QuestionLocation(super.getModel(),mySurvey);
	}
	@Override
	public void showCheckQOpts() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void showEditOpts(Map<String,Object> opts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleCheckQ(String scon) {
		// TODO Auto-generated method stub
		
	}

}
