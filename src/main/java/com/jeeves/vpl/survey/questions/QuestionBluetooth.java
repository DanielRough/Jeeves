package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.BLUETOOTH;

import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;

public class QuestionBluetooth extends QuestionView{

	public QuestionBluetooth() {
		super();
	}


	public QuestionBluetooth(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getImagePath() {
		return "/img/icons/bluetooth.png";

	}

	@Override
	public String getLabel() {
		return "Select a Bluetooth device";

	}

	@Override
	public int getQuestionType() {
		return BLUETOOTH;

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
