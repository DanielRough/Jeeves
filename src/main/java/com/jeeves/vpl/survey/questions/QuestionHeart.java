package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.HEART;

import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;

public class QuestionHeart extends QuestionView{

	public QuestionHeart(String label)  throws Exception {
		this(new FirebaseQuestion(label));
	}


	public QuestionHeart(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub
		
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
	public void loadOptions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		// TODO Auto-generated method stub
		
	}

}
