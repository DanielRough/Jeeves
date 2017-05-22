package com.jeeves.vpl.survey.questions;

import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;

import static com.jeeves.vpl.Constants.*;

public class QuestionText extends QuestionView {
	public QuestionText() {
		super();
	}

	public QuestionText(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub

	}
	//
	// @Override
	// public void showCheckQOpts() {
	// txtAnswer.setVisible(true);
	// txtAnswer.clear();
	//
	// }
	//
	// @Override
	// public void handleCheckQ(String scon) {
	// if(!scon.isEmpty())
	// txtAnswer.setText(scon);
	// else
	// txtAnswer.clear();
	//
	//
	// }

	//
	// public QuestionView clone(){
	// return new QuestionText(super.getModel());
	// }
	@Override
	public String getImagePath() {
		return "/img/icons/imgfreetext.png";
	}

	// public QuestionText(FirebaseQuestion question) {
	// super(question);
	// setImage("/img/icons/imgfreetext.png");
	// // setQuestionText("Open Text");
	// //this.description = "User answers with text";
	// }
	@Override
	public String getLabel() {
		return "Enter free text into a text box";
	}

	@Override
	public int getQuestionType() {
		return OPEN_ENDED;
	}

	@Override
	public void loadOptions() {

	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		// TODO Auto-generated method stub

	}

}
