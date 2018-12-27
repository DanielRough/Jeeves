package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.TEXTPRESENT;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.ChangeListener;

import com.google.cloud.storage.Storage;
import com.jeeves.vpl.firebase.FirebaseQuestion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

public class PresentText extends QuestionView{
	@FXML
	private TextArea txtPresent;
	public PresentText(String label)  throws Exception {
		this(new FirebaseQuestion(label));
	}


	public PresentText(FirebaseQuestion data) {
		super(data);
	}
	static Storage storage;
	@Override
	public void addEventHandlers() {
		txtPresent.textProperty().addListener(change -> {
			Map<String, Object> textOpts = new HashMap<String, Object>();
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
			optionsPane = (Pane) surveyLoader.load();
			addEventHandlers();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		if(opts != null) {
			String text = (String)opts.get("text");
			txtPresent.setText(text);
		}
	}


}

