package com.jeeves.vpl.survey.questions;

import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;

import javafx.scene.layout.Pane;
import static com.jeeves.vpl.survey.QuestionEditor.*;

public class QuestionText extends QuestionView{
//
//	public QuestionView clone(){
//		return new QuestionText(super.getModel());
//	}
	public String getImagePath(){
		return "/img/icons/imgfreetext.png";
	}
	public void loadOptions(){
		optionsPane = new Pane();

	}
//	public QuestionText(FirebaseQuestion question) {
//		super(question);
//		setImage("/img/icons/imgfreetext.png");
//	//	setQuestionText("Open Text");
//		//this.description = "User answers with text";
//	}
	public String getLabel(){
		return "Enter free text into a text box";
	}
	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub
		
	}
//
//	@Override
//	public void showCheckQOpts() {
//		txtAnswer.setVisible(true);
//		txtAnswer.clear();
//
//	}
//
//	@Override
//	public void handleCheckQ(String scon) {
//		if(!scon.isEmpty())
//		txtAnswer.setText(scon);
//		else
//			txtAnswer.clear();
//
//
//	}

	@Override
	public void showEditOpts(Map<String,Object> opts) {
		// TODO Auto-generated method stub
		
	}

	
}
