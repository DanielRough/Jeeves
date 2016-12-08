package com.jeeves.vpl.canvas.actions;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import com.jeeves.vpl.MainController;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseSurvey;

public class SurveyAction extends Action { // NO_UCD (unused code)
	@FXML
	private ComboBox<String> cboSurveyName;
	ChangeListener<String> selectionListener;
	
	public SurveyAction() {
		this(new FirebaseAction());
	}
	public SurveyAction(FirebaseAction data) {
		super(data);
		name.setValue("SURVEY ACTION");
		description = "Send a notification to complete a survey";
		addListeners();
	}
	public Node[] getWidgets() {
		return new Node[] { cboSurveyName };
	}

	public void setData(FirebaseAction model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();
		ObservableList<FirebaseSurvey> surveys = MainController.currentGUI.currentsurveys;

		surveys.addListener(new ListChangeListener<FirebaseSurvey>() {

			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends FirebaseSurvey> c) {
				changeSurveys();

			}

		});
		if(params.containsKey("survey")){
			cboSurveyName.setValue(params.get("survey").toString());

		}
	}

	@Override
	public String getViewPath() {
		return String.format("/actionSendSurvey.fxml", this.getClass()
				.getSimpleName());
	}

	public void changeSurveys(){
		ObservableList<FirebaseSurvey> surveys = MainController.currentGUI.currentsurveys;
		String value = cboSurveyName.getValue();
		cboSurveyName.getItems().clear();
		surveys.forEach(survey->{cboSurveyName.getItems().add(survey.getname());
		if(survey.getname().equals(value))cboSurveyName.setValue(value);
		survey.name.addListener(new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				int index = cboSurveyName.getSelectionModel().getSelectedIndex();
				cboSurveyName.getItems().clear();
				surveys.forEach(survey2->{cboSurveyName.getItems().add(survey2.getname());});
				
				if(index >=0)
					cboSurveyName.setValue(surveys.get(index).getname());
				
			}
			
		});});
	}
	@Override
	protected void addListeners() {
		super.addListeners();
		ObservableList<FirebaseSurvey> surveys = MainController.currentGUI.currentsurveys;
		cboSurveyName.getItems().clear();
		surveys.forEach(survey -> {
			cboSurveyName.getItems().add(survey.getname());
			cboSurveyName.getSelectionModel().selectFirst();
		});
		surveys.addListener(new ListChangeListener<FirebaseSurvey>() {

			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends FirebaseSurvey> c) {
				changeSurveys();

			}

		});
	 selectionListener = new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				if(arg2 != null) //aaaaaaaargh

				getModel().getparams().put("survey", arg2);
			}
			
		};
		cboSurveyName.valueProperty().addListener(selectionListener);
		cboSurveyName.getSelectionModel().selectFirst();
	}

}