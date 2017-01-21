package com.jeeves.vpl.canvas.actions;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import com.jeeves.vpl.MainController;
import com.jeeves.vpl.firebase.FirebaseAction;

public class SendTextAction extends Action { // NO_UCD (unused code)
	private String messagetext;
	@FXML
	private TextField txtMessage;
	@FXML
	private ComboBox<String> cboRecipient;
	@FXML public HBox hboxSMS;
	private TextArea smsText;
	
	public SendTextAction() {
		this(new FirebaseAction());
	}
	public SendTextAction(FirebaseAction data) {
		super(data);
		this.name.setValue("SEND TEXT ACTION");
		this.description = "Sends an SMS with the given text to the specified recipient";

		addListeners();
	}
	public Node[] getWidgets() {
		return new Node[] { txtMessage, cboRecipient };
	}

	public void fxmlInit(){
		super.fxmlInit();
		smsText = new TextArea();
		cboRecipient.getItems().clear();
		cboRecipient.getItems().addAll("Last sender", "Emergency contact",
				"Researcher");
	}
	public void setData(FirebaseAction model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();

		if(params.containsKey("msgtext")){
		messagetext = params.get("msgtext").toString();
		txtMessage.setText(messagetext);
		smsText.setText(messagetext);
		}
		if(params.containsKey("recipient"))
		setRecipient(params.get("recipient").toString());

	}

	@Override
	public String getViewPath() {
		return String.format("/actionSendText.fxml", this.getClass()
				.getSimpleName());
	}

	public void setRecipient(String rec) {
		cboRecipient.getItems().clear();
		cboRecipient.getItems().addAll("Last sender", "Emergency contact","User",
				"Researcher");
		cboRecipient.setValue(rec);
	}

	@Override
	protected void addListeners() {
		super.addListeners();
		smsText.setWrapText(true);
		txtMessage.setOnMouseClicked(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) {
				smsText.setVisible(true);
				MainController.currentGUI.getMainPane().getChildren().add(smsText);
				smsText.toFront();
				Bounds txtMsgBounds = txtMessage.localToScene(txtMessage.getBoundsInLocal());
				smsText.setLayoutX(txtMsgBounds.getMinX());
				smsText.setLayoutY(txtMsgBounds.getMinY());
				smsText.requestFocus(); 
				smsText.setPrefWidth(Math.max(txtMessage.getWidth(),smsText.getWidth()));
				
			}
			
		});
		smsText.focusedProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				if(arg2.equals(false)){
					smsText.setVisible(false);
					MainController.currentGUI.getMainPane().getChildren().remove(smsText);
				}
				}
			
		});
		smsText.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
			//	smsText.setPrefWidth(smsText.getWidth(),txtMessage.getWidth());
				smsText.setPrefColumnCount(arg2.length()+1);
				params.put("msgtext", smsText.getText());// }
				txtMessage.setText(smsText.getText());
			}
		});
		cboRecipient.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				params.put("recipient", arg2);
			}
		});
	}

}