package com.jeeves.vpl.canvas.actions;

import static com.jeeves.vpl.Constants.*;
import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.DateReceiver;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.canvas.receivers.TimeReceiver;
import com.jeeves.vpl.canvas.receivers.VariableReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseExpression;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

@SuppressWarnings("rawtypes")
public class UpdateAction extends Action { // NO_UCD (unused code)
	public static final String DESC = "Set one of the patient's attributes to a new value";
	public static final String NAME = "Update patient attribute";
	@FXML
	private RadioButton falseButton;
	private ToggleGroup group; // for when we're updating a
								@FXML
	private HBox hbox;
	@FXML
	private RadioButton trueButton;
	private ViewElement value; 
	private UserVariable variable;
	private ExpressionReceiver variablereceiver;
	ListChangeListener<ViewElement> mylistener;
	private TimeReceiver timereceiver;
	private DateReceiver datereceiver;
	private ExpressionReceiver numericreceiver;

	//
	public UpdateAction() {
		this(new FirebaseAction());
	}

	public UpdateAction(FirebaseAction data) {
		super(data);
	}

	@Override
	public void addListeners() {
		super.addListeners();
		variablereceiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
			updateReceivers();

		});

		//For boolean values
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> arg0, Toggle arg1, Toggle arg2) {
				FirebaseExpression expr = new FirebaseExpression();
				expr.setIsValue(true);
				;
				expr.setIndex(1);
				expr.setVartype(VAR_BOOLEAN);
				
				if (arg2 != null && arg2.equals(trueButton)) {
					expr.setvalue("true");
				} else{
					expr.setvalue("false");
				}
					if(vars.size()<2)
					vars.add(null);
				vars.set(1, expr);
			}

		});
		//For numeric/text values
		numericreceiver.getTextField().textProperty().addListener(listener->{
			FirebaseExpression expr = new FirebaseExpression();
			expr.setIsValue(true);
			;
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
			;
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
			;
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
		mylistener = new ListChangeListener<ViewElement>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ViewElement> arg0) {
				arg0.next();
				if (!arg0.getAddedSubList().isEmpty()) {
					ViewElement child = getActiveReceiver().getChildElements().get(0);
						value = (child);
					vars.add(1, ((Expression)value).getModel());
				} else {
					value = (null);
					vars.remove(1);
				}
			}

		};
		numericreceiver.getChildElements().addListener(mylistener);
		timereceiver.getChildElements().addListener(mylistener);
		datereceiver.getChildElements().addListener(mylistener);
	}

	public ExpressionReceiver getActiveReceiver(){
		return activeReceiver;
	}
	public ExpressionReceiver activeReceiver;
	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		description = DESC;
		variablereceiver = new VariableReceiver(VAR_ANY);
		hbox.getChildren().add(1, variablereceiver);
		numericreceiver = new ExpressionReceiver(VAR_NONE);
		timereceiver = new TimeReceiver(VAR_CLOCK);
		datereceiver = new DateReceiver(VAR_DATE);
		hbox.getChildren().add(3, numericreceiver);

		hbox.setSpacing(5);
		// hbox.setPadding(new Insets(5, 5, 5, 5));
		hbox.getStyleClass().remove("action");
		group = new ToggleGroup();

		trueButton.setToggleGroup(group);
		falseButton.setToggleGroup(group);
		hbox.getChildren().removeAll(trueButton, falseButton);

	}

	@Override
	public String getViewPath() {
		return String.format("/actionUpdateModel.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { numericreceiver, variablereceiver };
	}

	
	@Override
	public void setParentPane(ParentPane parent) {
		super.setParentPane(parent);
		System.out.println("second you should do me");
		if(variablereceiver.getChildExpression() != null)
		variablereceiver.getChildExpression().setParentPane(parent);
		if(numericreceiver.getChildExpression() != null)
			numericreceiver.getChildExpression().setParentPane(parent);
		if(timereceiver.getChildExpression() != null)
			timereceiver.getChildExpression().setParentPane(parent);
		if(datereceiver.getChildExpression() != null)
			datereceiver.getChildExpression().setParentPane(parent);

	}
	
	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
		System.out.println("first you should do me");
		Node[] potentialWidgets = {trueButton, falseButton, numericreceiver, timereceiver, datereceiver};
		
		if (model.getvars().isEmpty())
			return;
		FirebaseExpression variable = (FirebaseExpression) model.getvars().get(0);// (FirebaseExpression)params.get("variable");
		FirebaseExpression value;// = (FirebaseExpression) vars.get(1);// (FirebaseExpression)params.get("value");
	//	String textvalue;
		variablereceiver.addChild(UserVariable.create(variable), 0, 0); 
		variablereceiver.setReceiveType(variable.getvartype());
		hbox.getChildren().removeAll(potentialWidgets);
		
		
		if (variable.getvartype().equals(VAR_BOOLEAN)) {
			hbox.getChildren().addAll(trueButton, falseButton);
			trueButton.setToggleGroup(group);
			falseButton.setToggleGroup(group);
			if(model.getvars().size() == 1)return;
			value = (FirebaseExpression) model.getvars().get(1);
			//If it's boolean, it's gonna be a text value
			boolean result = Boolean.parseBoolean(value.getvalue());
			if (result)
				trueButton.setSelected(true);
			else
				falseButton.setSelected(true);
		} 
		else if(variable.getvartype().equals(VAR_CLOCK)){
			hbox.getChildren().add(timereceiver);
			activeReceiver = timereceiver;
			if(model.getvars().size() == 1)return;
			value = (FirebaseExpression) model.getvars().get(1);
			if (value.getisValue() == false){
				timereceiver.addChild(Expression.create(value), 0, 0);
			}
			else{
				String textvalue = value.getvalue();
				timereceiver.setText(textvalue);
			}

		}
		else if(variable.getvartype().equals(VAR_DATE)){
			hbox.getChildren().add(datereceiver);
			activeReceiver = datereceiver;

			if(model.getvars().size() == 1)return;
			value = (FirebaseExpression) model.getvars().get(1);
			if (value.getisValue() == false){
				datereceiver.addChild(Expression.create(value), 0, 0);
			}
			else{
				String textvalue = value.getvalue();
				datereceiver.setText(textvalue);
			}

		}
		else{ //This applies for numeric and location variables, which react in the same uncomplicated way
			hbox.getChildren().add(numericreceiver);
			activeReceiver = numericreceiver;

			numericreceiver.setReceiveType(variable.getvartype());
			if(model.getvars().size() == 1)return;
			value = (FirebaseExpression) model.getvars().get(1);
			if (value.getisValue() == false){
				numericreceiver.addChild(Expression.create(value), 0, 0); 
			}
			else{
				String textvalue = value.getvalue();
				numericreceiver.getTextField().setText(textvalue);
			}
		}

	}

	protected void updateReceivers() {
		Node[] potentialWidgets = {trueButton, falseButton, numericreceiver, timereceiver, datereceiver};
		hbox.getChildren().removeAll(potentialWidgets);
		if (!variablereceiver.getChildElements().isEmpty()) {
			ViewElement child = variablereceiver.getChildElements().get(0);
			variable = ((UserVariable) child);
			vars.clear();
			vars.add(0, variable.getModel());
			if (variable.getVarType().equals(VAR_BOOLEAN)) {
//				hbox.getChildren().removeAll(potentialWidgets);
				group.getToggles().clear();
				hbox.getChildren().addAll(trueButton, falseButton);
				trueButton.setToggleGroup(group);
				falseButton.setToggleGroup(group);
				group.selectToggle(trueButton);
			} else if(variable.getVarType().equals(VAR_CLOCK)){
				hbox.getChildren().add(timereceiver);
				activeReceiver = timereceiver;

			}
			else if(variable.getVarType().equals(VAR_DATE)){
				hbox.getChildren().add(datereceiver);
				activeReceiver = datereceiver;

			}
			else{
//				hbox.getChildren().removeAll(valuereceiver, trueButton, falseButton);
				hbox.getChildren().add(numericreceiver);
				activeReceiver = numericreceiver;

			}
			variablereceiver.setReceiveType(variable.getVarType());
			numericreceiver.setReceiveType(variable.getVarType());

		} else {
			variable = (null);
			numericreceiver.setReceiveType(VAR_NONE);
			hbox.getChildren().remove(variablereceiver);
			variablereceiver = new VariableReceiver(VAR_ANY);
			hbox.getChildren().add(1, variablereceiver);
			hbox.getChildren().removeAll(numericreceiver, trueButton, falseButton);
			numericreceiver = new ExpressionReceiver(VAR_NONE);
			vars.clear();
			hbox.getChildren().add(numericreceiver);
			addListeners();
		}
	}

}
