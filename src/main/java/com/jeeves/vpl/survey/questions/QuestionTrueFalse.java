package com.jeeves.vpl.survey.questions;

import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;

import static com.jeeves.vpl.Constants.*;

public class QuestionTrueFalse extends QuestionView {

	public QuestionTrueFalse() {
		super();
	}
	// @Override
	// public void showCheckQOpts() {
	// rdioTrue.setVisible(true);
	// rdioFalse.setVisible(true);
	// rdioFalse.setSelected(false);
	// rdioTrue.setSelected(false);
	//
	// }
	// @Override
	// public void handleCheckQ(String scon) {
	//
	// if(!scon.isEmpty()){
	// if(Boolean.parseBoolean(scon))
	// rdioTrue.setSelected(true);
	// else
	// rdioFalse.setSelected(true);
	// }
	// else{
	// rdioFalse.setSelected(false);
	// rdioTrue.setSelected(false);
	// }
	// }

	public QuestionTrueFalse(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getImagePath() {
		return "/img/icons/imgbool.png";
	}

	@Override
	public String getLabel() {
		return "Choose true or false";
	}

	@Override
	public int getQuestionType() {
		return BOOLEAN;
	}

	// public QuestionView clone(){
	// return new QuestionTrueFalse(super.getModel());
	// }
	//
	@Override
	public void loadOptions() {

	}
	// public QuestionTrueFalse(FirebaseQuestion model) {
	// super(model);
	// setImage("/img/icons/imgbool.png");
	// // setQuestionText("True/False");
	//// this.description = "User chooses yes or no";
	// }

	public void populateQ(FirebaseQuestion entry) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		// TODO Auto-generated method stub

	}

}
