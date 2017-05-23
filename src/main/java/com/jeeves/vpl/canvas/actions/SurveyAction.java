package com.jeeves.vpl.canvas.actions;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseSurvey;

public class SurveyAction extends Action { // NO_UCD (unused code)
	public static final String DESC = "Notify patient that a survey is to be completed";
	public static final String NAME = "Send a survey";
	@FXML
	private ComboBox<String> cboSurveyName;
	ChangeListener<String> selectionListener;

	public SurveyAction() {
		this(new FirebaseAction());
	}

	public SurveyAction(FirebaseAction data) {
		super(data);
	}

	@Override
	public void addListeners() {
		super.addListeners();
		ObservableList<FirebaseSurvey> surveys = gui.getSurveys();

		cboSurveyName.getItems().clear();
		surveys.forEach(survey -> {
			cboSurveyName.getItems().add(survey.gettitle());
		});
		gui.registerSurveyListener(new ListChangeListener<FirebaseSurvey>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FirebaseSurvey> c) {
				ObservableList<FirebaseSurvey> surveys = gui.getSurveys();
				String value = cboSurveyName.getValue();
				cboSurveyName.getItems().clear();
				surveys.forEach(survey -> {
					cboSurveyName.getItems().add(survey.gettitle());
					if (survey.gettitle().equals(value))
						cboSurveyName.setValue(value);
					survey.title.addListener(new ChangeListener<String>() {

						@Override
						public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
							int index = cboSurveyName.getSelectionModel().getSelectedIndex();
							cboSurveyName.getItems().clear();
							surveys.forEach(survey2 -> {
								cboSurveyName.getItems().add(survey2.gettitle());
							});

							if (index >= 0)
								cboSurveyName.setValue(surveys.get(index).getname());

						}

					});
				});
			}

		});

		selectionListener = new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (arg2 != null) // aaaaaaaargh
					params.put("survey", arg2);
			}

		};
		cboSurveyName.valueProperty().addListener(selectionListener);
		cboSurveyName.getSelectionModel().selectFirst();
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		description = DESC;
	}

	@Override
	public String getViewPath() {
		return String.format("/ActionSendSurvey.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { cboSurveyName };
	}

	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();
		if (params.containsKey("survey")) {
			cboSurveyName.setValue(params.get("survey").toString());
		}
	}

}