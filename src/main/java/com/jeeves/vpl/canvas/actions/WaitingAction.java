package com.jeeves.vpl.canvas.actions;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import com.jeeves.vpl.TextUtils;
import com.jeeves.vpl.firebase.FirebaseAction;

import static com.jeeves.vpl.Constants.*;

/**
 * Action to remain in the execution of a trigger temporarily
 * 
 * @author Daniel
 *
 */
public class WaitingAction extends Action { // NO_UCD (unused code)
	public static final String DESC = "Add a delay between actions happening";
	public static final String NAME = "Wait";
	@FXML
	private ComboBox<String> cboWaitGranularity;
	@FXML
	private HBox hbox;
	@FXML
	private TextField txtWaitTime;
	ChangeListener<String> selectionListener;

	public WaitingAction() {
		this(new FirebaseAction());
	}

	public WaitingAction(FirebaseAction data) {
		super(data);
		cboWaitGranularity.getItems().addAll("seconds", "minutes", "hours");
	}

	@Override
	public void addListeners() {
		super.addListeners();
		ChangeListener<String> textChanged = new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {

				txtWaitTime.setPrefWidth(
						TextUtils.computeTextWidth(txtWaitTime.getFont(), txtWaitTime.getText(), 0.0D) + 10);
				autosize();
				params.put("time", txtWaitTime.getText());
			}

		};
		txtWaitTime.addEventFilter(KeyEvent.KEY_TYPED, numberHandler);

		txtWaitTime.textProperty().addListener(textChanged);
		selectionListener = new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (arg2 != null) // aaaaaaaargh

					params.put("granularity", arg2);
			}

		};
		cboWaitGranularity.valueProperty().addListener(selectionListener);
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		description = DESC;
		txtWaitTime.setPrefWidth(20);
		txtWaitTime.setMinHeight(20);
		txtWaitTime.setPrefHeight(20);
		txtWaitTime.getStyleClass().add("textfield");
	}

	@Override
	public String getViewPath() {
		return String.format("/actionWaiting.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { cboWaitGranularity, txtWaitTime };
	}

	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();
		if (params.isEmpty())
			return;
		styleTextCombo(cboWaitGranularity);

		if (params.containsKey("granularity"))
			cboWaitGranularity.setValue(params.get("granularity").toString());
		if (params.containsKey("time"))
			txtWaitTime.setText(params.get("time").toString());

	}

}
