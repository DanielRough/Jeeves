package com.jeeves.vpl.survey.questions;

import java.util.Map;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.firebase.FirebaseQuestion;

import static com.jeeves.vpl.Constants.*;

public class QuestionText extends QuestionView {
	public QuestionText(String label) {
		this(new FirebaseQuestion(label));
	}

	public QuestionText(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public void addEventHandlers() {
		//No handlers
	}

	@Override
	public String getImagePath() {
		return "/img/icons/imgfreetext.png";
	}

	@Override
	public String getQuestionType() {
		return OPEN_ENDED;
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
		return Constants.VAR_NONE;
	}
}
