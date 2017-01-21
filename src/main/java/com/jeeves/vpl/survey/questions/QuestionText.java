package com.jeeves.vpl.survey.questions;

import java.io.IOException;
import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.survey.QuestionView;
import com.jeeves.vpl.survey.Survey;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class QuestionText extends QuestionView{

	public QuestionView clone(){
		return new QuestionText(super.getModel(),mySurvey);
	}
	
	public void loadOptions(){
		optionsPane = new Pane();

	}
	public QuestionText(FirebaseQuestion question, Survey survey) {
		super(question, survey);
		setImage("/img/icons/imgfreetext.png");
		setQuestionText("Open Text");
	}

	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showCheckQOpts() {
		mySurvey.txtAnswer.setVisible(true);
		mySurvey.txtAnswer.clear();

	}

	@Override
	public void handleCheckQ(String scon) {
		if(!scon.isEmpty())
		mySurvey.txtAnswer.setText(scon);
		else
			mySurvey.txtAnswer.clear();


	}

	@Override
	public void showEditOpts(Map<String,Object> opts) {
		// TODO Auto-generated method stub
		
	}

	
}
