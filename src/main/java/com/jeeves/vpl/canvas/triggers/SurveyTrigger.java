package com.jeeves.vpl.canvas.triggers;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.firebase.FirebaseSurvey;
import com.jeeves.vpl.firebase.FirebaseTrigger;

/**
 * This class represents a clock trigger that can be dragged around on the
 * canvas and have its fields edited
 *
 * @author Daniel
 */
public class SurveyTrigger extends Trigger { // NO_UCD (use default)
	private static final String RESULT = "result";
	private static final String SURVEY = "selectedSurvey";
	private static final String MISSED = "missed";
	@FXML
	private ComboBox<String> cboCompMissed;
	@FXML
	private ComboBox<String> cboSurvey;

	public SurveyTrigger(String name) {
		this(new FirebaseTrigger(name));
	}

	public SurveyTrigger(FirebaseTrigger data) {
		super(data);
		}

	@Override
	public void addListeners() {
		super.addListeners();
		ChangeListener<String> listener;

		ObservableList<FirebaseSurvey> surveys = Constants.getOpenProject().getObservableSurveys();
		cboSurvey.getItems().clear();
		surveys.forEach(survey ->{
			cboSurvey.getItems().add(survey.gettitle());
		addTitleListener(survey);
		});
		
		Constants.getOpenProject().registerSurveyListener(
				(ListChangeListener.Change<? extends FirebaseSurvey> change)-> {
				String value = cboSurvey.getValue();
				cboSurvey.getItems().clear();
				surveys.forEach(survey -> {
					cboSurvey.getItems().add(survey.gettitle());
					if (survey.gettitle().equals(value))
						cboSurvey.setValue(value);
					addTitleListener(survey);
				});

		});

		listener = (arg0,arg1,arg2)-> {
				if (arg2 != null) // aaaaaaaargh
					params.put(SURVEY, cboSurvey.getValue());

		};
		cboSurvey.valueProperty().addListener(listener);
		cboCompMissed.valueProperty().addListener((o,v0,v1)->
				params.put(RESULT, cboCompMissed.getValue())
		);
	}

	public void addTitleListener(FirebaseSurvey survey) {
		survey.getTitleProperty().addListener((arg0,arg1,arg2)->{
			int index = cboSurvey.getSelectionModel().getSelectedIndex();
			cboSurvey.getItems().clear();
			Constants.getOpenProject().getObservableSurveys().forEach(survey2 -> 
			cboSurvey.getItems().add(survey2.gettitle())
			);
			if (index >= 0)
				cboSurvey.getSelectionModel().clearAndSelect(index);

	});
	}
	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		if (params.containsKey(SURVEY) && params.get(SURVEY) != null) { 
			cboSurvey.setValue(params.get(SURVEY).toString());
		}
		cboCompMissed.getItems().clear();
		cboCompMissed.getItems().addAll("completed", MISSED);
		String completed = params.containsKey(RESULT) ? params.get(RESULT).toString() : MISSED;

		cboCompMissed.setValue(completed);

	}
}