package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.TIME;

import java.io.IOException;
import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


public class QuestionTime extends QuestionView {

	@FXML
	private RadioButton rdioDate;
	@FXML
	private RadioButton rdioTime;

	private ToggleGroup askForGroup;
	@FXML
	private HBox hboxOptions;
	@FXML
	private RadioButton rdioAny;
	@FXML
	private RadioButton rdioFuture;
	@FXML
	private RadioButton rdioPast;
	private ToggleGroup tgroup;
	@FXML
	private VBox vboxDateOpts;

	public QuestionTime() {
		super();
	}

	public QuestionTime(FirebaseQuestion data) {
		super(data);
	}


	@Override
	public String getImagePath() {
		return "/img/icons/imgtime.png";
	}

	@Override
	public String getLabel() {
		return "Select a Time";
	}

	@Override
	public int getQuestionType() {
		// TODO Auto-generated method stub
		return TIME;
	}

	@Override
	public void loadOptions() {
//		tgroup = new ToggleGroup();
//		askForGroup = new ToggleGroup();
//		FXMLLoader surveyLoader = new FXMLLoader();
//		surveyLoader.setController(this);
//		surveyLoader.setLocation(getClass().getResource("/OptionsDateTime.fxml"));
//		try {
//			optionsPane = (Pane) surveyLoader.load();
//
//			addEventHandlers();
//		} catch (IOException e) {
//		}
//		rdioDate.setToggleGroup(askForGroup);
//		rdioDate.setUserData("date");
//		rdioTime.setToggleGroup(askForGroup);
//		rdioTime.setUserData("time");
//
//		
//		rdioAny.setToggleGroup(tgroup);
//		rdioAny.setUserData(new Long(0));
//		rdioFuture.setToggleGroup(tgroup);
//		rdioFuture.setUserData(new Long(1));
//		rdioPast.setToggleGroup(tgroup);
//		rdioPast.setUserData(new Long(2));
	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		
	}

	@Override
	public void addEventHandlers() {
	}

}
