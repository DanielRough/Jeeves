package com.jeeves.vpl.canvas.uielements;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.firebase.FirebaseUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class UILabel extends UIElement { // NO_UCD (unused code)
	public static final String NAME = "label";
	@FXML
	private Label lblLabel;
	@FXML
	public StackPane panePane;

	public UILabel() {
		this(new FirebaseUI());
	}

	public UILabel(FirebaseUI data) {
		super(data);
		lblLabel.setMaxWidth(215);
		addListeners();
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
	}

	@Override
	public Control getChild() {
		return lblLabel;
	}

	@Override
	public ViewElement<FirebaseUI> getInstance() {
		return this;
	}

	@Override
	public String getText() {
		return lblLabel.getText();
	}

	@Override
	public String getViewPath() {
		return String.format("/uiLabel.fxml", this.getClass().getSimpleName());
	}

	@Override
	public void setData(FirebaseUI data) {
		super.setData(data);
		// setText(model.gettext());
	}

	@Override
	public void setText(String text) {
		model.settext(text);
		lblLabel.setText(text);
	}

	// public StringProperty getTextProperty(){
	// return lblLabel.textProperty();
	// }
	@Override
	protected void addListeners() {
		super.addListeners();

		lblLabel.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				model.settext(arg2);
			}

		});
	}
}
