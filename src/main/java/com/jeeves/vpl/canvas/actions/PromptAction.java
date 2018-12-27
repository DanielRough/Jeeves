package com.jeeves.vpl.canvas.actions;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import com.jeeves.vpl.firebase.FirebaseAction;

public class PromptAction extends Action { // NO_UCD (unused code)
	private String prompttext;
	private TextArea smsText;
	@FXML
	private TextField txtPrompt;

	public PromptAction(String name) {
		this(new FirebaseAction(name));
	}

	public PromptAction(FirebaseAction data) {
		super(data);
	}

	@Override
	public void addListeners() {
		super.addListeners();
		smsText = new TextArea();

		txtPrompt.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				smsText.setVisible(true);
				gui.getMainPane().getChildren().add(smsText);
				smsText.toFront();
				Bounds txtMsgBounds = localToScene(txtPrompt.getBoundsInParent());
				smsText.setLayoutX(txtMsgBounds.getMinX());
				smsText.setLayoutY(txtMsgBounds.getMinY());
				smsText.requestFocus();
				smsText.setPrefWidth(Math.max(txtPrompt.getWidth(), smsText.getWidth()));

			}

		});
		smsText.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {

				if (arg2.equals(false)) { 
					gui.getMainPane().getChildren().remove(smsText);
					smsText.setVisible(false);

				}
			}

		});
		smsText.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {

				smsText.setPrefWidth(Math.max(smsText.getWidth(), txtPrompt.getWidth()));
				smsText.setPrefColumnCount(arg2.length() + 1);
				params.put("msgtext", smsText.getText());// }
				txtPrompt.setText(smsText.getText());
			}
		});
	}



	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
		
		Map<String, Object> params = model.getparams();
		if (!params.containsKey("msgtext"))
			return;

		prompttext = params.get("msgtext").toString();
		txtPrompt.setText(prompttext);
		smsText.setText(prompttext);

	}

}