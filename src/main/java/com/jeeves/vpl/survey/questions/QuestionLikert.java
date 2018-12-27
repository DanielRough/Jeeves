package com.jeeves.vpl.survey.questions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import static com.jeeves.vpl.Constants.*;

public class QuestionLikert extends QuestionView {

	private TextField[] fields;

	@FXML
	private TextField txtNumOptions;
	@FXML
	private TextField txtBegin;
	@FXML
	private TextField txtMiddle;
	@FXML
	private TextField txtEnd;
	public QuestionLikert(String label)  throws Exception {
		this(new FirebaseQuestion(label));
	}
	public QuestionLikert(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public void addEventHandlers() {

		fields = new TextField[] {txtBegin, txtMiddle, txtEnd };

		for (TextField field : fields) {
			field.textProperty().addListener(change -> handleUpdateScale());
		}
		txtNumOptions.textProperty().addListener(change -> handleUpdateScale());

	}

	@Override
	public String getImagePath() {
		return "/img/icons/imgscale.png";
	}


	@Override
	public String getQuestionType() {
		return SCALE;
	}


	@Override
	public void loadOptions() {
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/OptionsLikert.fxml"));
		try {
			optionsPane = (Pane) surveyLoader.load();
			addEventHandlers();
		} catch (IOException e) {

		}
	}

	@Override
	public void fxmlInit(){
		super.fxmlInit();
	}
	@Override
	public void showEditOpts(Map<String, Object> opts) {
		if (opts == null) {
			for (TextField field : fields) {
				field.setText("");
			}
		} else {
			if (opts.containsKey("number")) {
				String number = opts.get("number").toString();
				txtNumOptions.setText(number);
			}
			if (opts.containsKey("labels")) {
				@SuppressWarnings("unchecked")
				ArrayList<String> labels = (ArrayList<String>) opts.get("labels");
				for (int count = 0; count < fields.length; count++) {
					fields[count].setText(labels.get(count));
				}
			}
		}
	}

	private void handleUpdateScale() {
		String number = txtNumOptions.getText();
		Map<String, Object> qScaleVals = new HashMap<String, Object>();
		qScaleVals.put("number", number);
		if(number.isEmpty())
			qScaleVals.put("number", "7");
		ArrayList<String> labels = new ArrayList<String>();
		for (TextField field : fields) {
			labels.add(field.getText());

		}
		qScaleVals.put("labels", labels);
		Map<String,Object> params = model.getparams();
		params.put("options",qScaleVals);

	}

}
