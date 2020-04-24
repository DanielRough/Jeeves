package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.TIME;

import java.util.Map;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.firebase.FirebaseQuestion;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class QuestionTime extends QuestionView {

	@FXML
	private RadioButton rdioDate;
	@FXML
	private RadioButton rdioTime;

	@FXML
	private HBox hboxOptions;
	@FXML
	private RadioButton rdioAny;
	@FXML
	private RadioButton rdioFuture;
	@FXML
	private RadioButton rdioPast;
	@FXML
	private VBox vboxDateOpts;

	public QuestionTime(String label) {
		this(new FirebaseQuestion(label));
	}

	public QuestionTime(FirebaseQuestion data) {
		super(data);
	}


	@Override
	public String getImagePath() {
		return "/img/icons/imgtime.png";
	}


	@Override
	public String getQuestionType() {
		return TIME;
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
	public void addEventHandlers() {
		//No handlers
	}
	@Override
	public String getAnswerType() {
		return Constants.VAR_CLOCK;
	}
}
