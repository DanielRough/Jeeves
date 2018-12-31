package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.HEART;

import java.util.Map;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.firebase.FirebaseQuestion;

public class QuestionHeart extends QuestionView{

	public QuestionHeart(String label) {
		this(new FirebaseQuestion(label));
	}


	public QuestionHeart(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public String getImagePath() {
		return "/img/icons/heart.png";

	}


	@Override
	public String getQuestionType() {
		return HEART;

	}


	@Override
	public void addEventHandlers() {
		//Does not need any
	}


	@Override
	public void loadOptions() {
		//Has no options
		
	}


	@Override
	public void showEditOpts(Map<String, Object> opts) {
		//Has no options
	}

	@Override
	public String getAnswerType() {
		return Constants.VAR_NONE;
	}


}
