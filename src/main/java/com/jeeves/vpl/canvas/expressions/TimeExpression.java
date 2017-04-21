package com.jeeves.vpl.canvas.expressions;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import com.jeeves.vpl.canvas.receivers.DateReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

import static com.jeeves.vpl.Constants.*;

public class TimeExpression extends Expression { // NO_UCD (unused code)
	public static final String NAME = "Are we at this time right now?";
	public static final String DESC = "Returns true if the current time is within the specified bounds of the expression time";
	private ComboBox<String> cboTimeDiff;
	private ComboBox<String> cboBeforeAfter;
	private DateReceiver exprTimeVar;
public TimeExpression() {
	this(new FirebaseExpression());
}
	public Node[] getWidgets(){
		return new Node[]{cboTimeDiff,cboBeforeAfter,exprTimeVar};
	}
	
public void setData(FirebaseExpression model){
	super.setData(model);
	Map<String,Object> params = model.getparams();
	if(params.containsKey("timeDiff"))
		cboTimeDiff.setValue(params.get("timeDiff").toString());
	if(params.containsKey("beforeAfter"))
		cboBeforeAfter.setValue(params.get("beforeAfter").toString());
	if(params.containsKey("timevar") == false)return;
		exprTimeVar.setText(params.get("timevar").toString());
}

public void addListeners(){
	super.addListeners();
	model.getparams().put("timeDiff", cboTimeDiff.getValue());
	model.getparams().put("beforeAfter", cboBeforeAfter.getValue());
	model.getparams().put("timeVar", exprTimeVar.getText());
	cboTimeDiff.valueProperty().addListener(new ChangeListener<String>(){

		@Override
		public void changed(ObservableValue<? extends String> arg0,
				String arg1, String arg2) {
			model.getparams().put("timeDiff", arg2);			
		}
	});
//	(value->model.getparams().put("timeDiff", value));
	cboBeforeAfter.valueProperty().addListener(new ChangeListener<String>(){

		@Override
		public void changed(ObservableValue<? extends String> arg0,
				String arg1, String arg2) {
			model.getparams().put("beforeAfter", arg2);			
		}
	});
	
	//DJR again, don't need this listener here, the expression knows its own time and that should be enough
//	exprTimeVar.text.textProperty().addListener(new ChangeListener<String>(){
//
//		@Override
//		public void changed(ObservableValue<? extends String> arg0,
//				String arg1, String arg2) {
//			model.getparams().put("timeVar", arg2);
//		}
//		
//	});
}

public void fxmlInit(){
	super.fxmlInit();
	cboTimeDiff = new ComboBox<String>();
	cboBeforeAfter = new ComboBox<String>();
	cboTimeDiff.getStyleClass().addAll("shadowy","styled-select");
	exprTimeVar = new DateReceiver(VAR_DATE);
	cboTimeDiff.getItems().addAll("1 month","1 week","1 day");
	cboBeforeAfter.getItems().addAll("before","after");
	cboTimeDiff.getSelectionModel().clearAndSelect(0);
	cboBeforeAfter.getSelectionModel().clearAndSelect(0);
	styleTextCombo(cboBeforeAfter);

//	setup();
	box.getChildren().clear();
	box.getChildren().addAll(cboTimeDiff,cboBeforeAfter,exprTimeVar);
	box.setSpacing(20);
	box.setPadding(new Insets(0,14,0,14));
}
public TimeExpression(FirebaseExpression data) {
	super(data);
	addListeners();

}
	@Override
	public void setup() {
		name = NAME;
		description = DESC;
		this.varType = VAR_BOOLEAN;
		operand.setText("is");
		box.getStyleClass().add(this.varType);

	}

}
