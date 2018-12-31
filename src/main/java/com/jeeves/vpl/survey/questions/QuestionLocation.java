package com.jeeves.vpl.survey.questions;

import java.util.Map;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.firebase.FirebaseQuestion;

import static com.jeeves.vpl.Constants.*;

public class QuestionLocation extends QuestionView {
	public QuestionLocation(String label) {
		this(new FirebaseQuestion(label));
	}


	public QuestionLocation(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public void addEventHandlers() {
		//No handlers
	}

	@Override
	public String getImagePath() {
		return "/img/icons/imggeo.png";
	}


	@Override
	public String getQuestionType() {
		return GEO;
	}

	@Override
	public void loadOptions() {
		//No options
	}
	@Override
	public void showEditOpts(Map<String, Object> opts) {
		//No options
	}
	@Override
	public String getAnswerType() {
		return Constants.VAR_LOCATION;
	}
}
