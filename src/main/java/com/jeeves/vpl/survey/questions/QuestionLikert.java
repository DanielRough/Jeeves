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

	@FXML
	private CheckBox chkAssignScore;

	private TextField[] fields;

	// public QuestionLikert(FirebaseQuestion model) {
	// super(model);
	// setImage("/img/icons/imgscale.png");
	//// setQuestionText("Likert Scale");
	// // this.description = "User answers by selecting from a scale";
	// }
	@FXML
	private RadioButton rdioButton5;
	@FXML
	private RadioButton rdioButton7;
	@FXML
	private TextField txtLikert1;
	@FXML
	private TextField txtLikert2;
	@FXML
	private TextField txtLikert3;
	@FXML
	private TextField txtLikert4;
	@FXML
	private TextField txtLikert5;
	@FXML
	private TextField txtLikert6;
	@FXML
	private TextField txtLikert7;
	public QuestionLikert() {
		super();
	}
	// public QuestionView clone(){
	// return new QuestionLikert(super.getModel());
	// }
	//
	public QuestionLikert(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub
		final ToggleGroup group = new ToggleGroup();
		rdioButton5.setToggleGroup(group);
		rdioButton7.setToggleGroup(group);
		rdioButton7.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					txtLikert6.setVisible(true);
					txtLikert7.setVisible(true);
					handleUpdateScale();
				}
			}

		});
		rdioButton5.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					txtLikert6.setVisible(false);
					txtLikert7.setVisible(false);
					handleUpdateScale();
				}
			}

		});

		fields = new TextField[] { txtLikert1, txtLikert2, txtLikert3, txtLikert4, txtLikert5, txtLikert6, txtLikert7 };


		for (TextField field : fields) {
			field.textProperty().addListener(change -> handleUpdateScale());
		}

	}

	@Override
	public String getImagePath() {
		return "/img/icons/imgscale.png";
	}

	@Override
	public String getLabel() {
		return "Select from a Likert Scale";
	}

	@Override
	public int getQuestionType() {
		return SCALE;
	}

	@FXML
	public void handleAssignScore(Event e) {
		getModel().setAssign(chkAssignScore.isSelected()); // This is
		// getting
		// silly
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
		int count = 1;
		for (TextField field : fields) {
			field.setText(""+count++); //add default values in so that we have at least SOME sort of parameters
		}
	}
	@Override
	public void showEditOpts(Map<String, Object> opts) {
		if (opts == null) {
			rdioButton5.setSelected(true);
			for (TextField field : fields) {
				field.setText("");
			}
		} else {
			if (opts.containsKey("number")) {
				String number = opts.get("number").toString();
				if (number.equals("5"))
					rdioButton5.setSelected(true);
				else if (number.equals("7"))
					rdioButton7.setSelected(true);
			}
			if (opts.containsKey("labels")) {
				@SuppressWarnings("unchecked")
				ArrayList<String> labels = (ArrayList<String>) opts.get("labels");
				int count = 0;
				for (String label : labels) {
					fields[count].setText(label);
					count++;
				}
			}
		}
		boolean assigntoscore = getModel().getparams().get("assignToScore") == null ? false
				: Boolean.parseBoolean(getModel().getparams().get("assignToScore").toString());
		if (assigntoscore == true) {
			chkAssignScore.setSelected(true);
		} else
			chkAssignScore.setSelected(false);
	}

	private void handleUpdateScale() {
		String number = rdioButton5.isSelected() ? "5" : "7";
		Map<String, Object> qScaleVals = new HashMap<String, Object>();
		qScaleVals.put("number", number);
		ArrayList<String> labels = new ArrayList<String>();
		for (TextField field : fields) {
			labels.add(field.getText());

		}
		qScaleVals.put("labels", labels);
		Map<String,Object> params = model.getparams();
		params.put("options",qScaleVals);

	}

}
