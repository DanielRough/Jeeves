package com.jeeves.vpl.canvas.actions;

import static com.jeeves.vpl.Constants.VAR_ANY;
import static com.jeeves.vpl.Constants.VAR_LOCATION;
import static com.jeeves.vpl.Constants.VAR_WIFI;
import static com.jeeves.vpl.Constants.VAR_BLUETOOTH;
import static com.jeeves.vpl.Constants.VAR_NONE;

import java.util.Map;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.canvas.receivers.VariableReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseExpression;

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

public class AskForDataAction extends Action{
	public final String DESC = "Ask for semantic data, such as a participant's work WiFi, home location, or partner's Bluetooth";
	public final String NAME = "Ask For Data";
	private ExpressionReceiver variablereceiver;
	private UserVariable variable;

	@FXML
	private HBox hbox;
	@FXML
	private TextField txtPrompt;

	public AskForDataAction() {
		this(new FirebaseAction());
	}

	public AskForDataAction(FirebaseAction data) {
		super(data);
	}
	protected void updateReceivers() {
		if (!variablereceiver.getChildElements().isEmpty()) {
			ViewElement child = variablereceiver.getChildElements().get(0);
			variable = ((UserVariable) child);
			vars.clear();
			vars.add(0, variable.getModel());
			if (variable.getVarType().equals(VAR_LOCATION)) {
				txtPrompt.setPromptText("Please select the GPS location of your " + variable.getName());
			}
			else if(variable.getVarType().equals(VAR_WIFI)){
				txtPrompt.setPromptText("Please select your " + variable.getName() + "'s WiFi network");
			}
			else if(variable.getVarType().equals(VAR_BLUETOOTH)){
				txtPrompt.setPromptText("Please select your " + variable.getName() + "'s Bluetooth device");
			}
		}
		else{
			txtPrompt.setText("Please select your...");
			variable = (null);
			hbox.getChildren().remove(variablereceiver);
			variablereceiver = new VariableReceiver(VAR_ANY);
			hbox.getChildren().add(1, variablereceiver);
			vars.clear();
			addListeners();
		}
	}
	@Override
	public void addListeners() {
		super.addListeners();
		variablereceiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
			updateReceivers();

		});
		txtPrompt.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {

//				smsText.setPrefWidth(Math.max(smsText.getWidth(), txtPrompt.getWidth()));
//				smsText.setPrefColumnCount(arg2.length() + 1);
				params.put("msgtext", txtPrompt.getText());// }
			//	txtPrompt.setText(smsText.getText());
			}
		});
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		description = DESC;
		variablereceiver = new VariableReceiver(VAR_ANY);
		hbox.getChildren().add(1, variablereceiver);
	}

	@Override
	public String getViewPath() {
		return String.format("/actionAskForData.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { txtPrompt };
	}

	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
		
		if (model.getvars().isEmpty())
			return;
		FirebaseExpression variable = (FirebaseExpression) model.getvars().get(0);// (FirebaseExpression)params.get("variable");
		variablereceiver.addChild(UserVariable.create(variable), 0, 0); 
		variablereceiver.setReceiveType(variable.getvartype());
		Map<String, Object> params = model.getparams();
		if (!params.containsKey("msgtext"))
			return;
		String prompttext = params.get("msgtext").toString();
		txtPrompt.setText(prompttext);

	}

}
