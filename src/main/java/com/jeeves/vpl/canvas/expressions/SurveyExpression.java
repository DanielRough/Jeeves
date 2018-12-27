package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseSurvey;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.stage.Popup;

public class SurveyExpression extends Expression { // NO_UCD (unused code)
	public boolean manualChange = false;
	private ComboBox<String> cboSurveys;
	private ComboBox<String> cboDoneOrNot;
	private String surveyname;
	protected String doneOrNot = "";
	Popup pop = new Popup();
	ChangeListener<String> selectionListener;

	public SurveyExpression(String name) {
		this(new FirebaseExpression(name));
	}
	@Override
	public void setParentPane(DragPane parent) {
		super.setParentPane(parent);

	}
	
	@Override
	public void addListeners() {
		super.addListeners();
		ObservableList<FirebaseSurvey> surveys = gui.getSurveys();

		cboSurveys.getItems().clear();
		surveys.forEach(survey -> {
			cboSurveys.getItems().add(survey.gettitle());
		});
		gui.registerSurveyListener(new ListChangeListener<FirebaseSurvey>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FirebaseSurvey> c) {
				ObservableList<FirebaseSurvey> surveys = gui.getSurveys();
				String value = cboSurveys.getValue();
				cboSurveys.getItems().clear();
				surveys.forEach(survey -> {
					cboSurveys.getItems().add(survey.gettitle());
					if (survey.gettitle().equals(value))
						cboSurveys.setValue(value);
					survey.title.addListener(new ChangeListener<String>() {

						@Override
						public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
							int index = cboSurveys.getSelectionModel().getSelectedIndex();
							cboSurveys.getItems().clear();
							surveys.forEach(survey2 -> {
								cboSurveys.getItems().add(survey2.gettitle());
							});

							if (index >= 0)
								cboSurveys.getSelectionModel().clearAndSelect(index);

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
		cboSurveys.valueProperty().addListener(selectionListener);
		cboSurveys.getSelectionModel().selectFirst();
	}
	public SurveyExpression(FirebaseExpression data) {
		super(data);
		
		cboDoneOrNot.getItems().addAll("completed","missed");
		params.put("result", "missed");
		cboDoneOrNot.valueProperty()
				.addListener((ChangeListener<String>) (arg0, arg1, arg2) -> {
				params.put("result", arg2);
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
		if (model.getparams().containsKey("survey")) {
			String surveyName = model.getparams().get("survey").toString();
			cboSurveys.getSelectionModel().select(surveyName);
		} else
			return;
		if (model.getparams().containsKey("result")) {
			String result = model.getparams().get("result").toString();
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
		cboSurveys = new ComboBox<String>();
		cboDoneOrNot = new ComboBox<String>();
		box.getChildren().clear();
		box.getChildren().addAll(cboSurveys, operand, cboDoneOrNot);
		box.setPadding(new Insets(0, 4, 0, 4));

	}






}
