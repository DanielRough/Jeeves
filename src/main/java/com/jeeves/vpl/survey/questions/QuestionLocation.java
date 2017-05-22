package com.jeeves.vpl.survey.questions;

import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;

import static com.jeeves.vpl.Constants.*;

public class QuestionLocation extends QuestionView {
	public QuestionLocation() {
		super();
	}

	//
	// public QuestionLocation(FirebaseQuestion model) {
	// super(model);
	// setImage("/img/icons/imggeo.png");
	// // setQuestionText("Location");
	// //this.description = "User selects a location using a Google map";
	// }
	public QuestionLocation(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getImagePath() {
		return "/img/icons/imggeo.png";
	}

	@Override
	public String getLabel() {
		return "Choose a location on a map";
	}

	//
	// @Override
	// public void handleCheckQ(String scon) {
	// // TODO Auto-generated method stub
	//
	// }
	@Override
	public int getQuestionType() {
		return GEO;
	}

	@Override
	public void loadOptions() {

	}

	// public QuestionView clone(){
	// return new QuestionLocation(super.getModel());
	// }
	// @Override
	// public void showCheckQOpts() {
	// // TODO Auto-generated method stub
	//
	// }
	@Override
	public void showEditOpts(Map<String, Object> opts) {
		// TODO Auto-generated method stub

	}

}
