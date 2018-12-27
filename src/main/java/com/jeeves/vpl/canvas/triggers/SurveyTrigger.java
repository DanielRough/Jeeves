package com.jeeves.vpl.canvas.triggers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import com.jeeves.vpl.firebase.FirebaseSurvey;
import com.jeeves.vpl.firebase.FirebaseTrigger;

/**
 * This class represents a clock trigger that can be dragged around on the
 * canvas and have its fields edited
 *
 * @author Daniel
 */
public class SurveyTrigger extends Trigger { // NO_UCD (use default)
	@FXML
	private ComboBox<String> cboCompMissed;
	@FXML
	private ComboBox<String> cboSurvey;
	private ChangeListener<String> listener;
//	@FXML
//	private TextField txtNumberOfTimes;

	public SurveyTrigger(String name) {
		this(new FirebaseTrigger(name));
	}

	public SurveyTrigger(FirebaseTrigger data) {
		super(data);
	}

	{
		cboCompMissed.getItems().clear();
		cboCompMissed.getItems().addAll("completed", "missed");
		cboCompMissed.setValue("missed");
	}
	@Override
	public void addListeners() {
		super.addListeners();
		ObservableList<FirebaseSurvey> surveys = gui.getSurveys();
		cboSurvey.getItems().clear();
		surveys.forEach(survey -> {
			cboSurvey.getItems().add(survey.gettitle());
		});
		// changeSurveys();
		gui.registerSurveyListener(new ListChangeListener<FirebaseSurvey>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FirebaseSurvey> change) {
				String value = cboSurvey.getValue();
				cboSurvey.getItems().clear();
				surveys.forEach(survey -> {
					cboSurvey.getItems().add(survey.gettitle());
					if (survey.gettitle().equals(value))
						cboSurvey.setValue(value);
					survey.title.addListener(new ChangeListener<String>() {

						@Override
						public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
							int index = cboSurvey.getSelectionModel().getSelectedIndex();
							cboSurvey.getItems().clear();
							surveys.forEach(survey2 -> {
								cboSurvey.getItems().add(survey2.gettitle());
							});

							if (index >= 0)
								cboSurvey.getSelectionModel().clearAndSelect(index);

						}

					});
				});
			}

		});

		listener = new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (arg2 != null) // aaaaaaaargh
					params.put("selectedSurvey", cboSurvey.getValue());

			}

		};
		cboSurvey.valueProperty().addListener(listener);
		cboCompMissed.valueProperty().addListener(new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				params.put("result", cboCompMissed.getValue());
			}
			
		});
	}


	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		if (params.containsKey("selectedSurvey") && params.get("selectedSurvey") != null) { 
			cboSurvey.setValue(params.get("selectedSurvey").toString());
		}

		String completed = params.containsKey("result") ? params.get("result").toString() : "missed";

		cboCompMissed.setValue(completed);

	}
}