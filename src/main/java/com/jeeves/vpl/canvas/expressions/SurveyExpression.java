package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseSurvey;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.stage.Popup;

public class SurveyExpression extends Expression { // NO_UCD (unused code)
	private static final String RESULT = "result";
	private static final String SURVEY = "survey";
	private ComboBox<String> cboSurveys;
	private ComboBox<String> cboDoneOrNot;
	private String surveyname;
	protected String doneOrNot = "";
	Popup pop = new Popup();
	ChangeListener<String> selectionListener;

	public SurveyExpression(String name) {
		this(new FirebaseExpression(name));
	}
	public void addTitleListener(FirebaseSurvey survey) {
		survey.getTitleProperty().addListener((arg0,arg1,arg2)->{
			int index = cboSurveys.getSelectionModel().getSelectedIndex();
			cboSurveys.getItems().clear();
			Constants.getOpenProject().getObservableSurveys().forEach(survey2 -> 
			cboSurveys.getItems().add(survey2.gettitle())
			);
			if (index >= 0)
				cboSurveys.getSelectionModel().clearAndSelect(index);

	});
	}
	@Override
	public void addListeners() {
		super.addListeners();
		ObservableList<FirebaseSurvey> surveys = Constants.getOpenProject().getObservableSurveys();

		cboSurveys.getItems().clear();
		surveys.forEach(survey -> {
			cboSurveys.getItems().add(survey.gettitle());
			addTitleListener(survey);
	}
		);
		Constants.getOpenProject().registerSurveyListener(
				(javafx.collections.ListChangeListener.Change<? extends FirebaseSurvey> c) ->{
				ObservableList<FirebaseSurvey> newSurveys = Constants.getOpenProject().getObservableSurveys();
				String value = cboSurveys.getValue();
				cboSurveys.getItems().clear();
				newSurveys.forEach(survey -> {
					cboSurveys.getItems().add(survey.gettitle());
					if (survey.gettitle().equals(value))
						cboSurveys.setValue(value);
					addTitleListener(survey);
				});

		});
		selectionListener = (arg0, arg1, arg2) ->{
				if (arg2 != null) // aaaaaaaargh
					params.put(SURVEY, arg2);

		};
		cboSurveys.valueProperty().addListener(selectionListener);
		cboSurveys.getSelectionModel().selectFirst();
	}
	public SurveyExpression(FirebaseExpression data) {
		super(data);
		
		cboDoneOrNot.getItems().addAll("completed","missed");
		params.put(RESULT, "missed");
		cboDoneOrNot.valueProperty()
				.addListener((ChangeListener<String>) (arg0, arg1, arg2) -> {
				params.put(RESULT, arg2);
				doneOrNot = arg2;});
				
				addListeners();

	}

	public String getDoneOrNot() {
		return doneOrNot;
	}

	public String getSurveyName() {
		return surveyname;
	}

	@Override
	public void setData(FirebaseExpression model) {
		super.setData(model);
		updatePane();
		if (model.getparams().containsKey(SURVEY)) {
			String surveyName = model.getparams().get(SURVEY).toString();
			cboSurveys.getSelectionModel().select(surveyName);
		} else {
			return;
		}
		if (model.getparams().containsKey(RESULT)) {
			String result = model.getparams().get(RESULT).toString();
			cboDoneOrNot.getSelectionModel().select(result);
		}
	}

	public void setDoneOrNot(String doneOrNot) {
		this.doneOrNot = doneOrNot;
	}

	public void setSurveyName(String surveyname) {
		this.surveyname = surveyname;
	}

	@Override
	public void setup() {
		operand.setText("was");
	}

	@Override
	public void updatePane() {
		super.updatePane();
		cboSurveys = new ComboBox<>();
		cboDoneOrNot = new ComboBox<>();
		box.getChildren().clear();
		box.getChildren().addAll(cboSurveys, operand, cboDoneOrNot);
		box.setPadding(new Insets(0, 4, 0, 4));

	}






}
