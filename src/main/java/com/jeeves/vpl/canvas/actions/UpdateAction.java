package com.jeeves.vpl.canvas.actions;

import static com.jeeves.vpl.Constants.VAR_ANY;
import static com.jeeves.vpl.Constants.VAR_BOOLEAN;
import static com.jeeves.vpl.Constants.VAR_CLOCK;
import static com.jeeves.vpl.Constants.VAR_DATE;
import static com.jeeves.vpl.Constants.VAR_NONE;
import static com.jeeves.vpl.Constants.VAR_NUMERIC;

import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.DateReceiver;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.canvas.receivers.TimeReceiver;
import com.jeeves.vpl.canvas.receivers.VariableReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseExpression;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

@SuppressWarnings("rawtypes")
public class UpdateAction extends Action { // NO_UCD (unused code)
	@FXML
	private ComboBox<String> cboChoice;
	@FXML
	private HBox hbox;
	private ViewElement value; 
	private ExpressionReceiver variablereceiver;
	ListChangeListener<ViewElement> mylistener;
	private TimeReceiver timereceiver;
	private DateReceiver datereceiver;
	private ExpressionReceiver numericreceiver;

	
	public UpdateAction(String name) {
		this(new FirebaseAction(name));
	}

	public UpdateAction(FirebaseAction data) {
		super(data);
	}

	@Override
	public void addListeners() {
		super.addListeners();
		cboChoice = new ComboBox<>();
		variablereceiver = new VariableReceiver(VAR_ANY);
		hbox.getChildren().add(1, variablereceiver);
		numericreceiver = new ExpressionReceiver(VAR_NONE);
		timereceiver = new TimeReceiver(VAR_CLOCK);
		datereceiver = new DateReceiver(VAR_DATE);
		hbox.setSpacing(5);
		hbox.getStyleClass().remove("action");
		cboChoice.getItems().add("true");
		cboChoice.getItems().add("false");
		variablereceiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> 
			updateReceivers()
		);

		cboChoice.getSelectionModel().selectedItemProperty().addListener(listener->{
			FirebaseExpression expr = new FirebaseExpression();
			expr.setIsValue(true);
			expr.setIndex(1);
			expr.setVartype(VAR_BOOLEAN);
			expr.setvalue(cboChoice.getSelectionModel().getSelectedItem());
			if(vars.size()<2)
				vars.add(null);
			vars.set(1, expr);			
		});


		//For numeric/text values
		numericreceiver.getTextField().textProperty().addListener(listener->{
			FirebaseExpression expr = new FirebaseExpression();
			expr.setIsValue(true);
			expr.setIndex(1);
			expr.setVartype(VAR_NUMERIC);
			expr.setvalue(numericreceiver.getTextField().getText());
			if(vars.size()<2)
				vars.add(null);
			vars.set(1, expr);
		});
		//For clock values
		timereceiver.getTextField().textProperty().addListener(listener->{
			FirebaseExpression expr = new FirebaseExpression();
			expr.setIsValue(true);
			expr.setIndex(1);
			expr.setVartype(VAR_CLOCK);
			expr.setvalue(timereceiver.getText());
			if(vars.size()<2)
				vars.add(null);
			vars.set(1, expr);
		});
//		timereceiver.
		//For date values
		datereceiver.getTextField().textProperty().addListener(listener->{
			FirebaseExpression expr = new FirebaseExpression();
			expr.setIsValue(true);
			expr.setIndex(1);
			expr.setVartype(VAR_DATE);
			expr.setvalue(datereceiver.getText());
			if(vars.size()<2)
				vars.add(null);
			vars.set(1, expr);
		});
		if (mylistener != null){
			numericreceiver.getChildElements().removeListener(mylistener);
			timereceiver.getChildElements().removeListener(mylistener);
			datereceiver.getChildElements().removeListener(mylistener);

		}
		mylistener = (javafx.collections.ListChangeListener.Change<? extends ViewElement> arg0)-> {
				arg0.next();
				if (!arg0.getAddedSubList().isEmpty()) {
					ViewElement child = getActiveReceiver().getChildElements().get(0);
						value = (child);
						vars.add(1, ((Expression)value).getModel());
				} else {
					value = (null);
					vars.remove(1);
				}

		};
		numericreceiver.getChildElements().addListener(mylistener);
		timereceiver.getChildElements().addListener(mylistener);
		datereceiver.getChildElements().addListener(mylistener);
	}

	public ExpressionReceiver getActiveReceiver(){
		return activeReceiver;
	}
	private ExpressionReceiver activeReceiver;


	
	@Override
	public void setParentPane(DragPane parent) {
		super.setParentPane(parent);
		if(variablereceiver.getChildExpression() != null) {
			variablereceiver.getChildExpression().setParentPane(parent);
		}
		if(numericreceiver.getChildExpression() != null) {
			numericreceiver.getChildExpression().setParentPane(parent);
		}
		if(timereceiver.getChildExpression() != null) {
			timereceiver.getChildExpression().setParentPane(parent);
		}
		if(datereceiver.getChildExpression() != null) {
			datereceiver.getChildExpression().setParentPane(parent);
		}
	}
	
	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);

		if (model.getvars().isEmpty())
			return;
		FirebaseExpression newVariable = model.getvars().get(0);
		variablereceiver.addChild(UserVariable.create(newVariable), 0, 0); 
		variablereceiver.setReceiveType(newVariable.getvartype());

		if(model.getvars().size()==1)
			return;
		FirebaseExpression newValue =  model.getvars().get(1);
		if(newValue.getisValue()) {
			if(newVariable.getvartype().equals(VAR_NUMERIC)) {
				numericreceiver.getTextField().setText(newValue.getvalue());
			}
			else if(newVariable.getvartype().equals(VAR_BOOLEAN)) {
				cboChoice.setValue(newValue.getvalue());
			}
			else if(newVariable.getvartype().equals(VAR_DATE)) {
				datereceiver.setText(newValue.getvalue());
			}
			else if(newVariable.getvartype().equals(VAR_CLOCK)) {
				timereceiver.setText(newValue.getvalue());
			}
		}
		else {
			activeReceiver.addChild(UserVariable.create(newValue), 0, 0);
		}


	}

	protected void updateReceivers() {
		UserVariable variable;
		Node[] potentialWidgets = { numericreceiver, timereceiver, datereceiver,cboChoice};
		hbox.getChildren().removeAll(potentialWidgets);
		if (!variablereceiver.getChildElements().isEmpty()) {
			ViewElement child = variablereceiver.getChildElements().get(0);
			variable = ((UserVariable) child);

			if(vars.isEmpty()) //We have added this manually
				vars.add(0, variable.getModel());

			if (variable.getVarType().equals(VAR_BOOLEAN)) {
				hbox.getChildren().add(cboChoice);
			} else if(variable.getVarType().equals(VAR_CLOCK)){
				hbox.getChildren().add(timereceiver);
				activeReceiver = timereceiver;
			}
			else if(variable.getVarType().equals(VAR_DATE)){
				hbox.getChildren().add(datereceiver);
				activeReceiver = datereceiver;
			}
			else{
				hbox.getChildren().add(numericreceiver);
				activeReceiver = numericreceiver;
			}
			variablereceiver.setReceiveType(variable.getVarType());
			numericreceiver.setReceiveType(variable.getVarType());

		} else {
			numericreceiver.setReceiveType(VAR_NONE);
			hbox.getChildren().remove(variablereceiver);
			vars.clear();
			addListeners();
		}
	}

}
