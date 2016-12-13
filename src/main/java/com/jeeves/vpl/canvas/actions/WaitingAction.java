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

import com.jeeves.vpl.canvas.receivers.TextUtils;
import com.jeeves.vpl.firebase.FirebaseAction;

/**
 * Action to remain in the execution of a trigger temporarily
 * 
 * @author Daniel
 *
 */
public class WaitingAction extends Action { // NO_UCD (unused code)
	@FXML
	private ComboBox<String> cboWaitGranularity;
	@FXML
	private HBox hbox;
	@FXML
	private TextField txtWaitTime;
//	private Expression value;
	//private ExpressionReceiver valuereceiver;//= new ExpressionReceiver(Expression.VAR_NUMERIC);
	ChangeListener<String> selectionListener;

	public Node[] getWidgets() {
		return new Node[] { cboWaitGranularity, txtWaitTime };
	}

	public WaitingAction(FirebaseAction data) {
		super(data);
		this.name.setValue("WAIT ACTON");
		this.description = "Wait for a set amount of time before executing the next action";
		cboWaitGranularity.getItems().addAll("seconds", "minutes", "hours");
	//	cboWaitGranularity.setValue("seconds");
		addListeners();

	}


	public WaitingAction() {
		this(new FirebaseAction());
	}
	public void setData(FirebaseAction model) {
		super.setData(model);
		Map<String,Object> params = model.getparams();
		if(params.isEmpty())return;
		styleTextCombo(cboWaitGranularity);

		if(params.containsKey("granularity"))
		cboWaitGranularity.setValue(params.get("granularity").toString());
		if(params.containsKey("time"))
		txtWaitTime.setText(params.get("time").toString());
//		if(params.containsKey("time"))
		//	FirebaseExpression timeexpr = (FirebaseExpression)params.get("time");
//			if(timeexpr == null)return;
//			if(timeexpr.getisValue() == false){
//				valuereceiver.addChild(Expression.create(timeexpr), 0, 0); //I cannot foresee this working tbh
//			}
//			else{
//				valuereceiver.setText(timeexpr.getvalue()); //And we set this text here
//			}

	}

	public void fxmlInit(){
		super.fxmlInit();
//		valuereceiver= new ExpressionReceiver(Expression.VAR_NUMERIC);
//		hbox.getChildren().add(1, valuereceiver);		

	}
	@Override
	public String getViewPath() {
		return String.format("/actionWaiting.fxml", this.getClass().getSimpleName());
	}

	@Override
	protected void addListeners() {
		super.addListeners();
		ChangeListener<String> textChanged = new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				txtWaitTime.setPrefWidth(TextUtils.computeTextWidth(txtWaitTime.getFont(), txtWaitTime.getText(), 0.0D) + 10);
				autosize();
				params.put("time", txtWaitTime.getText());

			//	txtWaitTime.getText();
			}

		};
		txtWaitTime.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent arg0) {
				try{
					Long isValid = Long.parseLong(arg0.getCharacter());
					
				}
				catch(NumberFormatException e){
					arg0.consume();
					return;
				}	
				
			
			}
		});
		txtWaitTime.textProperty().addListener(textChanged);
		 selectionListener = new ChangeListener<String>(){

				@Override
				public void changed(ObservableValue<? extends String> arg0,
						String arg1, String arg2) {
					if(arg2 != null) //aaaaaaaargh

						params.put("granularity", arg2);
				}
				
			};
			cboWaitGranularity.valueProperty().addListener(selectionListener);
	}
}
