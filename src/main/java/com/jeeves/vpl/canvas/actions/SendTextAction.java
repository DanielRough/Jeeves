package com.jeeves.vpl.canvas.actions;

import static com.jeeves.vpl.Constants.VAR_NUMERIC;

import java.util.Map;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SendTextAction extends Action { // NO_UCD (unused code)
	private static final String MSG_TEXT = "msgtext";
	private static final String RECIPIENT = "recipient";
	@FXML
	public HBox hboxSMS;
	@FXML
	private String messagetext;
	private TextArea smsText;
	@FXML
	private TextField txtMessage;
	private ExpressionReceiver numberReceiver;
	public SendTextAction(String name) {
		this(new FirebaseAction(name));
	}

	public SendTextAction(FirebaseAction data) {
		super(data);
	}

	@Override
	public void addListeners() {
		super.addListeners();
		smsText = new TextArea();
		numberReceiver = new ExpressionReceiver(VAR_NUMERIC);
		hboxSMS.getChildren().add(numberReceiver);
		smsText.setWrapText(true);
		txtMessage.setOnMouseClicked(arg0->{
				smsText.setVisible(true);
				gui.getMainPane().getChildren().add(smsText);
				smsText.toFront();
				Bounds txtMsgBounds = txtMessage.localToScene(txtMessage.getBoundsInLocal());
				smsText.setLayoutX(txtMsgBounds.getMinX());
				smsText.setLayoutY(txtMsgBounds.getMinY());
				smsText.requestFocus();
				smsText.setPrefWidth(200);

		});
		smsText.focusedProperty().addListener((arg0, arg1,arg2)-> {
				if (arg2.equals(false)) {
					smsText.setVisible(false);
					gui.getMainPane().getChildren().remove(smsText);
				}

		});
		smsText.textProperty().addListener((arg0,arg1,arg2)->{
				smsText.setPrefColumnCount(arg2.length() + 1);
				params.put(MSG_TEXT, smsText.getText());
				txtMessage.setText(smsText.getText());
		});
    numberReceiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
		if (!numberReceiver.getChildElements().isEmpty()) {
			ViewElement<?> child = numberReceiver.getChildExpression();
			params.put(RECIPIENT, child.getModel());
		} else {
			params.put(RECIPIENT, "");
		}
	});
    numberReceiver.getTextField().textProperty().addListener(listener->
    	params.put(RECIPIENT, numberReceiver.getTextField().getText())
    );
	}

	@Override
	public void setParentPane(DragPane parent) {
		super.setParentPane(parent);
		if(numberReceiver.getChildExpression() != null)
			numberReceiver.getChildExpression().setParentPane(parent);

	}
	@SuppressWarnings("unchecked")
	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();

		if (params.containsKey(MSG_TEXT)) {
			messagetext = params.get(MSG_TEXT).toString();
			txtMessage.setText(messagetext);
			smsText.setText(messagetext);
		}
		if (params.containsKey(RECIPIENT))
			if(params.get(RECIPIENT) instanceof Map) {
				setRecipient((Map<String,Object>)params.get(RECIPIENT));
			}
			else {
				numberReceiver.getTextField().setText(params.get(RECIPIENT).toString());
			}
		}

	public void setRecipient(Map<String,Object> rec) {
		if(rec.isEmpty())
			return;
		String name = rec.get("name").toString();
		for(FirebaseVariable var : Constants.getOpenProject().getvariables()) {
			if(var.getname().equals(name)){
				numberReceiver.addChild(UserVariable.create(var), 0,0);
				setParentPane(parentPane);
				return;
			}
		}
		Constants.getOpenProject().registerVarListener(listener->{
			listener.next();
			if(listener.wasAdded()){
				for(FirebaseVariable var : listener.getAddedSubList()){
					if(var.getname().equals(name)){
						numberReceiver.addChild(UserVariable.create(var), 0,0);
						setParentPane(parentPane);
					}
					}
			}
		});
	}

}