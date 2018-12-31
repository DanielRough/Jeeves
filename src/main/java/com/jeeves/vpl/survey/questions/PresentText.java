package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.TEXTPRESENT;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.cloud.storage.Storage;
import com.jeeves.vpl.Constants;
import com.jeeves.vpl.firebase.FirebaseQuestion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;

public class PresentText extends QuestionView{
	@FXML
	private TextArea txtPresent;
	public PresentText(String label) {
		this(new FirebaseQuestion(label));
	}


	public PresentText(FirebaseQuestion data) {
		super(data);
	}
	static Storage storage;
	@Override
	public void addEventHandlers() {
		txtPresent.textProperty().addListener(change -> {
			Map<String, Object> textOpts = new HashMap<>();
			textOpts.put("text", txtPresent.getText());
			model.getparams().put("options",textOpts);
		});
	}

	@Override
	public String getImagePath() {
		return "/img/icons/textpresent.png";

	}

	@Override
	public String getQuestionType() {
		return TEXTPRESENT;

	}

	@Override
	public void loadOptions() {
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/OptionsTextPresent.fxml"));
		try {
			optionsPane = surveyLoader.load();
			addEventHandlers();
		} catch (IOException e) {
			System.exit(1);
		}		
	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		if(opts != null) {
			String text = (String)opts.get("text");
			txtPresent.setText(text);
		}
	}


	@Override
	public String getAnswerType() {
		return Constants.VAR_NONE;
	}


}

