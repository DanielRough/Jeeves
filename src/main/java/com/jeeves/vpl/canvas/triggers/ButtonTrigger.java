package com.jeeves.vpl.canvas.triggers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleGroup;

import com.jeeves.vpl.firebase.FirebaseTrigger;
import com.jeeves.vpl.firebase.FirebaseUI;

/**
 * This class represents a clock trigger that can be dragged around on the
 * canvas and have its fields edited
 *
 * @author Daniel
 */
public class ButtonTrigger extends Trigger { // NO_UCD (unused code)
	public static final String DESC = "Schedule actions to take place when patient presses an app button";
	public static final String NAME = "On app button pressed";
	@FXML
	private ComboBox<String> cboButton;
	final ToggleGroup group = new ToggleGroup();
	String value;

	public ButtonTrigger() {
		this(new FirebaseTrigger());
	}

	public ButtonTrigger(FirebaseTrigger data) {
		super(data);

	}

	@Override
	public void addListeners() {
		super.addListeners();
		cboButton.getItems().clear();

		ObservableList<FirebaseUI> uielements = gui.getUIElements(); // UGH THIS
																		// IS
																		// HORRIBLE
																		// PLEASE
																		// FIX
																		// //myCanvas.getProject().getUIElements();
		uielements.forEach(survey -> {
			cboButton.getItems().add(survey.gettext());
			cboButton.getSelectionModel().selectFirst();
		});
		gui.registerElementListener(new ListChangeListener<FirebaseUI>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FirebaseUI> c) {
				if (cboButton.getValue() != null)
					value = cboButton.getValue();
				cboButton.getItems().clear();
				for (FirebaseUI button : uielements) {
					if (button.gettext() == null)
						continue;
					cboButton.getItems().add(button.gettext());
					if (button.gettext().equals(value)) {
						cboButton.setValue(value); // reset it if the original
													// survey we had selected
													// didn't change
						params.put("selectedButton", value);
					}
				}

			}

		});

		cboButton.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				params.put("selectedButton", arg2);
			}
		});
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		description = DESC;
	}

	@Override
	public String getViewPath() {
		return String.format("/TriggerButton.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { cboButton };
	}

	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		if (cboButton.getItems() != null && cboButton.getItems().size() > 0)
			cboButton.getSelectionModel().clearAndSelect(0);
		if (params.get("selectedButton") == null)
			return;
		cboButton.setValue(params.get("selectedButton").toString());

	}
}
