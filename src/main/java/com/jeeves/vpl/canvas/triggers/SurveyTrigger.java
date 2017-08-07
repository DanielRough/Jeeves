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
	public static final String DESC = "Schedule actions to take place when a user has completed or ignored a survey a certain number of times";
	public static final String NAME = "Survey Trigger";
	@FXML
	private ComboBox<String> cboCompMissed;
	@FXML
	private ComboBox<String> cboSurvey;
	private ChangeListener<String> listener;
	@FXML
	private TextField txtNumberOfTimes;

	public SurveyTrigger() {
		this(new FirebaseTrigger());
	}

	public SurveyTrigger(FirebaseTrigger data) {
		super(data);
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
					if (isReadOnly)
						return;
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
								//	cboSurvey.setValue(surveys.get(index).gettitle());

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

	//	cboSurvey.getSelectionModel().clearAndSelect(0); //default
		cboCompMissed.valueProperty().addListener(new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				params.put("result", cboCompMissed.getValue());
				System.out.println("Result is " + cboCompMissed.getValue());
			}
			
		});
		//if we don't already have a result, give it a default
		if(!model.getparams().containsKey("result")){
			cboCompMissed.setValue("completed");
			System.out.println("THIS OUGHTA BE HAPPENIN");
		}
		else
			System.out.println("RESULT IS " + model.getparams().get("result"));
		txtNumberOfTimes.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent arg0) {
				try {
					Long.parseLong(arg0.getCharacter());
				} catch (NumberFormatException e) {
					arg0.consume();
					return;
				}
			}
		});
		//txtNumberOfTimes.setText("1"); //default

		txtNumberOfTimes.textProperty().addListener(new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				params.put("numTimes", txtNumberOfTimes.getText());
				System.out.println("Result is " + txtNumberOfTimes.getText());

			}
			
		});
		//Again, if we don't already have a number of times, give it a default
		if(!model.getparams().containsKey("numTimes"))
			txtNumberOfTimes.setText("1");
//		txtNumberOfTimes.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
//
//			@Override
//			public void handle(KeyEvent arg0) {
//				params.put("numTimes", txtNumberOfTimes.getText());
//			}
//		});
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		description = DESC;
		cboCompMissed.getItems().clear();
		cboCompMissed.getItems().addAll("completed", "missed");
		cboCompMissed.setValue("missed");
	}

	@Override
	public String getViewPath() {
		return String.format("/TriggerSurvey.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { cboSurvey, cboCompMissed, txtNumberOfTimes };
	}

	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		// Map<String,Object> params = model.getparams();
		if (params.containsKey("selectedSurvey") && params.get("selectedSurvey") != null) { // Bah
																							// this
																							// is
																							// dodgy
			cboSurvey.setValue(params.get("selectedSurvey").toString());
		}

		String completed = params.containsKey("result") ? params.get("result").toString() : "missed";

		cboCompMissed.setValue(completed);
		txtNumberOfTimes.setText(params.containsKey("numTimes") ? params.get("numTimes").toString() : "");

	}
}