package com.jeeves.vpl.survey.questions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.firebase.FirebaseQuestion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import static com.jeeves.vpl.Constants.*;

public class QuestionLikert extends QuestionView {
	private static final String LABELS = "labels";
	private static final String NUMBER = "number";
	private TextField[] fields;

	@FXML
	private TextField txtNumOptions;
	@FXML
	private TextField txtBegin;
	@FXML
	private TextField txtMiddle;
	@FXML
	private TextField txtEnd;
	public QuestionLikert(String label) {
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
			optionsPane = surveyLoader.load();
			addEventHandlers();
		} catch (IOException e) {
			System.exit(1);
		}
	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		if (opts == null) {
			for (TextField field : fields) {
				field.setText("");
			}
		} else {
			if (opts.containsKey(NUMBER)) {
				String number = opts.get(NUMBER).toString();
				txtNumOptions.setText(number);
			}
			if (opts.containsKey(LABELS)) {
				@SuppressWarnings("unchecked")
				ArrayList<String> labels = (ArrayList<String>) opts.get(LABELS);
				for (int count = 0; count < fields.length; count++) {
					fields[count].setText(labels.get(count));
				}
			}
		}
	}

	private void handleUpdateScale() {
		String number = txtNumOptions.getText();
		Map<String, Object> qScaleVals = new HashMap<>();
		qScaleVals.put(NUMBER, number);
		if(number.isEmpty())
			qScaleVals.put(NUMBER, "7");
		ArrayList<String> labels = new ArrayList<>();
		for (TextField field : fields) {
			labels.add(field.getText());

		}
		qScaleVals.put(LABELS, labels);
		Map<String,Object> params = model.getparams();
		params.put("options",qScaleVals);

	}
	@Override
	public String getAnswerType() {
		return Constants.VAR_NUMERIC;
	}
}
