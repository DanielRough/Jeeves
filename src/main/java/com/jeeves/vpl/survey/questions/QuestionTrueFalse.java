package com.jeeves.vpl.survey.questions;

import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;

import static com.jeeves.vpl.Constants.*;

public class QuestionTrueFalse extends QuestionView {

	public QuestionTrueFalse(String label) throws Exception  {
		this(new FirebaseQuestion(label));
	}

	public QuestionTrueFalse(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public void addEventHandlers() {
	}

	@Override
	public String getImagePath() {
		return "/img/icons/imgbool.png";
	}



	@Override
	public String getQuestionType() {
		return BOOLEAN;
	}

	@Override
	public void loadOptions() {

	}
	public void populateQ(FirebaseQuestion entry) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		// TODO Auto-generated method stub

	}

}
