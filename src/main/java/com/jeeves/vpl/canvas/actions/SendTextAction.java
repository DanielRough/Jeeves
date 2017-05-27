package com.jeeves.vpl.canvas.actions;

import static com.jeeves.vpl.Constants.VAR_NUMERIC;

import java.util.Map;

import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class SendTextAction extends Action { // NO_UCD (unused code)
	public static final String DESC = "Send a text message to a specified recipient";
	public static final String NAME = "Send SMS";
	@FXML
	public HBox hboxSMS;
	@FXML
	//private ComboBox<String> cboRecipient;
	private String messagetext;
	private TextArea smsText;
	@FXML
	private TextField txtMessage;
	private ExpressionReceiver numberReceiver;
	public SendTextAction() {
		this(new FirebaseAction());
	}

	public SendTextAction(FirebaseAction data) {
		super(data);
	}

	@Override
	public void addListeners() {
		super.addListeners();
		smsText.setWrapText(true);
		txtMessage.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				smsText.setVisible(true);
				gui.getMainPane().getChildren().add(smsText);
				smsText.toFront();
				Bounds txtMsgBounds = txtMessage.localToScene(txtMessage.getBoundsInLocal());
				smsText.setLayoutX(txtMsgBounds.getMinX());
				smsText.setLayoutY(txtMsgBounds.getMinY());
				smsText.requestFocus();
				smsText.setPrefWidth(200);

			}

		});
		smsText.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (arg2.equals(false)) {
					smsText.setVisible(false);
					gui.getMainPane().getChildren().remove(smsText);
				}
			}

		});
		smsText.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				smsText.setPrefColumnCount(arg2.length() + 1);
				params.put("msgtext", smsText.getText());// }
				txtMessage.setText(smsText.getText());
			}
		});
    numberReceiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
		if (!numberReceiver.getChildElements().isEmpty()) {
			ViewElement child = numberReceiver.getChildExpression();
			params.put("recipient", child.getModel());
		} else {
			params.put("recipient", "");
		}
	});
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		description = DESC;
		smsText = new TextArea();
		numberReceiver = new ExpressionReceiver(VAR_NUMERIC);
		hboxSMS.getChildren().add(numberReceiver);
//		cboRecipient.getItems().clear();
//		cboRecipient.getItems().addAll("Last sender", "Emergency contact", "Researcher");
	}

	@Override
	public String getViewPath() {
		return String.format("/ActionSendText.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { txtMessage, numberReceiver};
	}
	@Override
	public void setParentPane(ParentPane parent) {
		super.setParentPane(parent);
		if(numberReceiver.getChildExpression() != null)
			numberReceiver.getChildExpression().setParentPane(parent);

	}
	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();

		if (params.containsKey("msgtext")) {
			messagetext = params.get("msgtext").toString();
			txtMessage.setText(messagetext);
			smsText.setText(messagetext);
		}
		if (params.containsKey("recipient"))
			setRecipient((Map<String,Object>)params.get("recipient"));

	}

	public void setRecipient(Map<String,Object> rec) {
		if(rec.isEmpty())
			return;
		String name = rec.get("name").toString();
		System.out.println("HERE WE ARE VARS LENGTH IS  " + gui.getVariables().size());
		gui.registerVarListener(listener->{
			listener.next();
			if(listener.wasAdded()){
				for(FirebaseVariable var : listener.getAddedSubList()){
					if(var.getname().equals(name)){
						numberReceiver.addChild(UserVariable.create(var), 0,0);
						setParentPane(getInstance().parentPane);
					}
					}
			}
		});
	}

}