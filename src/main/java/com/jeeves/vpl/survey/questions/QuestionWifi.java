package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.WIFI;

import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;

public class QuestionWifi extends QuestionView{

	public QuestionWifi() {
		super();
	}


	public QuestionWifi(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getImagePath() {
		return "/img/icons/wifi.png";

	}

	@Override
	public String getLabel() {
		return "Select a WiFi network";

	}

	@Override
	public int getQuestionType() {
		return WIFI;

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