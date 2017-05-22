package com.jeeves.vpl.canvas.uielements;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.StackPane;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.firebase.FirebaseUI;

public class UIButton extends UIElement { // NO_UCD (unused code)
	public static final String DESC = "A button that the patient can press";
	public static final String NAME = "button";
	@FXML
	private Button btnButton;
	@FXML
	private StackPane panePane;

	public UIButton() {
		this(new FirebaseUI());
	}

	public UIButton(FirebaseUI data) {
		super(data);
		btnButton.setMaxWidth(215);
		addListeners();
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		description = DESC;
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
	public String getViewPath() {
		return String.format("/UIButton.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { btnButton };
	}

	@Override
	public void setData(FirebaseUI data) {
		super.setData(data);
	}

	@Override
	public void setText(String text) {
		btnButton.setText(text);
		model.settext(text);
	}

	@Override
	protected void addListeners() {
		super.addListeners();
		btnButton.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				model.settext(arg2);
			}

		});
	}
}
