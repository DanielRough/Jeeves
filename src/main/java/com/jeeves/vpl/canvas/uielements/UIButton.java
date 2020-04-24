package com.jeeves.vpl.canvas.uielements;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.StackPane;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.firebase.FirebaseUI;

public class UIButton extends UIElement { // NO_UCD (unused code)
	@FXML
	private Button btnButton;
	@FXML
	private StackPane panePane;

	public UIButton(String name) {
		this(new FirebaseUI(name));
	}

	public UIButton(FirebaseUI data) {
		super(data);
		btnButton.setMaxWidth(215);
		addListeners();
	}


	@Override
	public Control getChild() {
		return btnButton;
	}

	@Override
	public ViewElement<FirebaseUI> getInstance() {
		return this;
	}

	@Override
	public String getText() {
		return btnButton.getText();

	}

	@Override
	public void setText(String text) {
		btnButton.setText(text);
		model.settext(text);
	}

	@Override
	protected void addListeners() {
		super.addListeners();
		btnButton.textProperty().addListener((arg0,arg1,arg2) ->
				model.settext(arg2)
		);
	}
}
