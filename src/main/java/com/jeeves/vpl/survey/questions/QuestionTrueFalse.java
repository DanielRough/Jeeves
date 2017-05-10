package com.jeeves.vpl.survey.questions;

import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;

import javafx.scene.layout.Pane;
import static com.jeeves.vpl.Constants.*;

public class QuestionTrueFalse extends QuestionView{
	
//	public QuestionView clone(){
//		return new QuestionTrueFalse(super.getModel());
//	}
//	
	public void loadOptions(){

	}
//	public QuestionTrueFalse(FirebaseQuestion model) {
//		super(model);
//		setImage("/img/icons/imgbool.png");
//	//	setQuestionText("True/False");
////		this.description = "User chooses yes or no";
//	}

	public String getLabel(){
		return "Choose true or false";
	}
	public String getImagePath(){
		return "/img/icons/imgbool.png";
	}

	public void populateQ(FirebaseQuestion entry) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void showCheckQOpts() {
//		rdioTrue.setVisible(true);
//		rdioFalse.setVisible(true);
//		rdioFalse.setSelected(false);
//		rdioTrue.setSelected(false);
//
//	}
//	@Override
//	public void handleCheckQ(String scon) {
//
//		if(!scon.isEmpty()){
//		if(Boolean.parseBoolean(scon))
//			rdioTrue.setSelected(true);
//		else
//			rdioFalse.setSelected(true);		
//		}
//		else{
//			rdioFalse.setSelected(false);
//			rdioTrue.setSelected(false);
//		}
//		}

	@Override
	public void showEditOpts(Map<String,Object> opts) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getQuestionType() {
		return BOOLEAN;
	}

}
