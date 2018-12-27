package com.jeeves.vpl.survey.questions;

import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;
import static com.jeeves.vpl.Constants.*;

public class QuestionNumber extends QuestionView {

	public QuestionNumber(String label)  throws Exception {
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

	}

	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadOptions() {
		// TODO Auto-generated method stub
		
	}

}
