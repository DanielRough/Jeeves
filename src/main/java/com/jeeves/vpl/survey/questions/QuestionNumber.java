package com.jeeves.vpl.survey.questions;

import java.util.Map;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.firebase.FirebaseQuestion;
import static com.jeeves.vpl.Constants.*;

public class QuestionNumber extends QuestionView {

	public QuestionNumber(String label){
		this(new FirebaseQuestion(label));
	}

	public QuestionNumber(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public String getImagePath() {
		return "/img/icons/imgnumeric.png";
	}


	@Override
	public String getQuestionType() {
		return NUMERIC;
	}


	@Override
	public void showEditOpts(Map<String, Object> opts) {
		//No options
	}

	@Override
	public void addEventHandlers() {
		//No handlers
	}

	@Override
	public void loadOptions() {
		//No options
		
	}
	@Override
	public String getAnswerType() {
		return Constants.VAR_NUMERIC;
	}
}
