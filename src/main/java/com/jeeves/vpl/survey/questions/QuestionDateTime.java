package com.jeeves.vpl.survey.questions;

import java.io.IOException;
import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.survey.QuestionView;
import com.jeeves.vpl.survey.Survey;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class QuestionDateTime extends QuestionView{

	
	public QuestionView clone(){
		return new QuestionDateTime(super.getModel(),mySurvey);
	}
	public QuestionDateTime(FirebaseQuestion model, Survey survey) {
		super(model,survey);
		setImage("/img/icons/imgdate.png");
		setQuestionText("Date/Time");
		this.description = "User selects a date and/or time";
	}

	public void handleCheckQ(){
		
	}

	public String getImagePath(){
		return "/img/icons/imgdate.png";
	}
	
	public void loadOptions(){
		optionsPane = new Pane();
	}
	@Override
	public void showEditOpts(Map<String,Object> opts) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void handleCheckQ(String scon) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void showCheckQOpts() {
		// TODO Auto-generated method stub
		
	}


}
