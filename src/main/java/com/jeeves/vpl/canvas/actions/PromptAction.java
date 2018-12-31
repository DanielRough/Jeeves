package com.jeeves.vpl.canvas.actions;

import java.util.Map;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import com.jeeves.vpl.firebase.FirebaseAction;

public class PromptAction extends Action { // NO_UCD (unused code)
	private TextArea smsText;
	private static final String MSG_TEXT = "msgtext";
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

		txtPrompt.setOnMouseClicked(arg0->{
				smsText.setVisible(true);
				gui.getMainPane().getChildren().add(smsText);
				smsText.toFront();
				Bounds txtMsgBounds = localToScene(txtPrompt.getBoundsInParent());
				smsText.setLayoutX(txtMsgBounds.getMinX());
				smsText.setLayoutY(txtMsgBounds.getMinY());
				smsText.requestFocus();
				smsText.setPrefWidth(Math.max(txtPrompt.getWidth(), smsText.getWidth()));

		});
		smsText.focusedProperty().addListener((arg0,arg1,arg2)-> {

				if (arg2.equals(false)) { 
					gui.getMainPane().getChildren().remove(smsText);
					smsText.setVisible(false);

				}

		});
		smsText.textProperty().addListener((arg0,arg1,arg2)-> {

				smsText.setPrefWidth(Math.max(smsText.getWidth(), txtPrompt.getWidth()));
				smsText.setPrefColumnCount(arg2.length() + 1);
				params.put(MSG_TEXT, smsText.getText());
				txtPrompt.setText(smsText.getText());
			
		});
	}



	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
		String prompttext;
		Map<String, Object> params = model.getparams();
		if (!params.containsKey(MSG_TEXT))
			return;

		prompttext = params.get(MSG_TEXT).toString();
		txtPrompt.setText(prompttext);
		smsText.setText(prompttext);

	}

}