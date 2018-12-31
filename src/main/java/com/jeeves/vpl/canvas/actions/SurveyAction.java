package com.jeeves.vpl.canvas.actions;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseSurvey;

public class SurveyAction extends Action { // NO_UCD (unused code)
	private static final String SURVEY = "survey";
	@FXML
	private ComboBox<String> cboSurveyName;
	ChangeListener<String> selectionListener;

	public SurveyAction(String name) {
		this(new FirebaseAction(name));
	}

	public SurveyAction(FirebaseAction data) {
		super(data);
	}

	@Override
	public void addListeners() {
		super.addListeners();
		ObservableList<FirebaseSurvey> surveys = Constants.getOpenProject().getObservableSurveys();

		cboSurveyName.getItems().clear();
		surveys.forEach(survey -> {
			cboSurveyName.getItems().add(survey.gettitle());
			addTitleListener(survey);
		}
		);

		Constants.getOpenProject().registerSurveyListener(
				(javafx.collections.ListChangeListener.Change<? extends FirebaseSurvey> c)->{
				ObservableList<FirebaseSurvey> newsurveys = Constants.getOpenProject().getObservableSurveys();
				String value = cboSurveyName.getValue();
				cboSurveyName.getItems().clear();
				newsurveys.forEach(survey -> {
					addTitleListener(survey);
					cboSurveyName.getItems().add(survey.gettitle());
					if (survey.gettitle().equals(value))
						cboSurveyName.setValue(value);
					
				});

		});

		selectionListener = (arg0,arg1,arg2)->{
				if (arg2 != null) 
					params.put(SURVEY, arg2);
			};
		cboSurveyName.valueProperty().addListener(selectionListener);
		cboSurveyName.getSelectionModel().selectFirst();
	}

	public void addTitleListener(FirebaseSurvey survey) {
		survey.getTitleProperty().addListener((arg0,arg1,arg2)->{
			int index = cboSurveyName.getSelectionModel().getSelectedIndex();
			cboSurveyName.getItems().clear();
			Constants.getOpenProject().getObservableSurveys().forEach(survey2 -> 
				cboSurveyName.getItems().add(survey2.gettitle())
			);
			if (index >= 0)
				cboSurveyName.getSelectionModel().clearAndSelect(index);

	});
	}

	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();
		if (params.containsKey(SURVEY)) {
			cboSurveyName.setValue(params.get(SURVEY).toString());
		}
	}

}