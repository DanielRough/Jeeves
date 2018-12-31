package com.jeeves.vpl.canvas.triggers;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.firebase.FirebaseTrigger;
import com.jeeves.vpl.firebase.FirebaseUI;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleGroup;

/**
 * This class represents a clock trigger that can be dragged around on the
 * canvas and have its fields edited
 *
 * @author Daniel
 */
public class ButtonTrigger extends Trigger { // NO_UCD (unused code)
	private static final String BUTTON = "selectedButton";
	@FXML
	private ComboBox<String> cboButton;
	final ToggleGroup group = new ToggleGroup();
	String value;

	public ButtonTrigger(String name) {
		this(new FirebaseTrigger(name));
	}

	public ButtonTrigger(FirebaseTrigger data) {
		super(data);

	}

	@Override
	public void addListeners() {
		super.addListeners();
		cboButton.getItems().clear();

		ObservableList<FirebaseUI> uielements = Constants.getOpenProject().getUIElements(); 
		uielements.forEach(survey -> {
			if(survey.getname().equals("button")){
			cboButton.getItems().add(survey.gettext());
			cboButton.getSelectionModel().selectFirst();
			params.put(BUTTON, cboButton.getSelectionModel().getSelectedItem());
			}
		});
		Constants.getOpenProject().registerElementListener(
				(ListChangeListener.Change<? extends FirebaseUI> c) ->{
				if (cboButton.getValue() != null)
					value = cboButton.getValue();
				cboButton.getItems().clear();
				for (FirebaseUI button : uielements) {
					if (button.gettext() == null || !button.getname().equals("button"))
						continue;
					cboButton.getItems().add(button.gettext());
					if (button.gettext().equals(value)) {
						cboButton.setValue(value);
						params.put(BUTTON, value);
					}
				}
		});

		cboButton.valueProperty().addListener((arg0,arg1,arg2)->
				params.put(BUTTON, arg2)
		);
	}

	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		if (cboButton.getItems() != null && !cboButton.getItems().isEmpty())
			cboButton.getSelectionModel().clearAndSelect(0);
		if (params.get(BUTTON) == null)
			return;
		cboButton.setValue(params.get(BUTTON).toString());

	}
}
