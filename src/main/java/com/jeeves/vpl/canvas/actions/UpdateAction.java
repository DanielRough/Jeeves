package com.jeeves.vpl.canvas.actions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.canvas.receivers.VariableReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseExpression;

import static com.jeeves.vpl.Constants.*;

@SuppressWarnings("rawtypes")
public class UpdateAction extends Action { // NO_UCD (unused code)
	public static final String NAME = "Update patient attribute";
	public static final String DESC = "Set one of the patient's attributes to a new value";
	@FXML
	private HBox hbox;
	private Expression value;
	private ExpressionReceiver valuereceiver;
	private Expression variable;
	private ExpressionReceiver variablereceiver;
	ListChangeListener<ViewElement> mylistener;
	private ToggleGroup group = new ToggleGroup(); // for when we're updating a
	// Boolean variable
	@FXML
	private RadioButton trueButton;
	@FXML
	private RadioButton falseButton;
//
//	public UpdateAction() {
//		this(new FirebaseAction());
//	}
//	public UpdateAction(FirebaseAction data) {
//		super(data);
//		
//		addListeners();
//	}
	public Node[] getWidgets() {
		return new Node[] { valuereceiver, variablereceiver };
	}

	public void fxmlInit(){
		super.fxmlInit();
		name = NAME;
		description = DESC;
		variablereceiver = new VariableReceiver(VAR_ANY);
		hbox.getChildren().add(1, variablereceiver);
		valuereceiver = new ExpressionReceiver(VAR_NONE);
		hbox.getChildren().add(3, valuereceiver);

		hbox.setSpacing(5);
		//hbox.setPadding(new Insets(5, 5, 5, 5));
		hbox.getStyleClass().remove("action");

		trueButton.setToggleGroup(group);
		falseButton.setToggleGroup(group);
		hbox.getChildren().removeAll(trueButton, falseButton);
		group = new ToggleGroup();

	}
	
	protected void updateReceivers(){
		System.out.println("DAFUK DOES THIS HAPPEN FOR");

		if (variablereceiver.getChildElements().get(0) != null) {
			ViewElement child = variablereceiver.getChildElements().get(0);
			variable = ((Expression) child);
			model.getvars().clear();
			model.getvars().add(0,variable.getModel());
			if (variable.getVarType().equals(VAR_BOOLEAN)) {
				hbox.getChildren().removeAll(trueButton, falseButton, valuereceiver);
				hbox.getChildren().addAll(trueButton, falseButton);
				trueButton.setToggleGroup(group);
				falseButton.setToggleGroup(group);
			} else {
				hbox.getChildren().removeAll(valuereceiver, trueButton, falseButton);
				hbox.getChildren().add(valuereceiver);
			}
			variablereceiver.setReceiveType(variable.getVarType());
			valuereceiver.setReceiveType(variable.getVarType());

		} else {
			variable = (null);
			valuereceiver.setReceiveType(VAR_NONE);
			hbox.getChildren().remove(variablereceiver);
			variablereceiver = new VariableReceiver(VAR_ANY);
			hbox.getChildren().add(1, variablereceiver);
			hbox.getChildren().removeAll(valuereceiver, trueButton, falseButton);
			valuereceiver = new ExpressionReceiver(VAR_NONE);
			model.getvars().clear();
			hbox.getChildren().add(valuereceiver);
			addListeners();
		}
	}
	
	protected void addListeners() {
super.addListeners();
		variablereceiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
			updateReceivers();

		});
		
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){

			@Override
			public void changed(ObservableValue<? extends Toggle> arg0,
					Toggle arg1, Toggle arg2) {
				FirebaseExpression expr= new FirebaseExpression();
				expr.setIsValue(true);;
				expr.setIndex(1);
				expr.setVartype(VAR_BOOLEAN);
				if(arg2.equals(trueButton)){
					expr.setValue("true");
				}
				else
					expr.setValue("false");
				model.getvars().add(1, expr);
			}
			
		});
		if(mylistener!=null) 
		valuereceiver.getChildElements().removeListener(mylistener);

		mylistener = new ListChangeListener<ViewElement>(){

			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends ViewElement> arg0) {
				arg0.next();
				if(arg0.getAddedSubList().get(0) != null){
					ViewElement child = valuereceiver.getChildElements().get(0);
					value = ((Expression) child);
					model.getvars().add(1,value.getModel());
					}
				else{
//					System.out.println("added size is " + arg0.getAddedSize());
//					System.out.println("removed size is " + arg0.getRemovedSize());
					System.out.println("added was " + arg0.getAddedSubList().get(0));
					System.out.println("removed was " + arg0.getRemoved().get(0));
					value = (null);
					System.out.println("vars size is " + model.getvars().size());
					model.getvars().remove(1);
				}				
			}
			
		};
		//System.out.println("How often is this added?");
		valuereceiver.getChildElements().addListener(mylistener);
//DJR again another case of a listener I likely don't need
		//		valuereceiver.text.textProperty().addListener(new ChangeListener<String>(){
//
//			@Override
//			public void changed(ObservableValue<? extends String> arg0,
//					String arg1, String arg2) {
//				FirebaseExpression expr= new FirebaseExpression();
//				expr.setIsValue(true);
//				expr.setValue(arg2);
//				expr.setIndex(1);
//				expr.setVartype(Expression.VAR_NUMERIC);
//				model.getvars().add(1,expr);
//			}
//			
//		});
	}

	public void setData(FirebaseAction model) {
		super.setData(model);

		if(model.getvars().isEmpty())return;
		FirebaseExpression variable = model.getvars().get(0);//(FirebaseExpression)params.get("variable");
		variablereceiver.addChild(Expression.create(variable), 0, 0); //I cannot foresee this working tbh
		variablereceiver.setReceiveType(variable.getvartype());

		if(variable.getvartype().equals(VAR_BOOLEAN)){
			hbox.getChildren().removeAll(trueButton, falseButton, valuereceiver);
			hbox.getChildren().addAll(trueButton, falseButton);
			trueButton.setToggleGroup(group);
			falseButton.setToggleGroup(group);
		}
		else
			valuereceiver.setReceiveType(variable.getvartype());
		if(model.getvars().size() ==1)return;
		FirebaseExpression value = model.getvars().get(1);//(FirebaseExpression)params.get("value");
		if(value.getisValue() == false)
			valuereceiver.addChild(Expression.create(value), 0, 0); //I cannot foresee this working tbh
		else if(value.getvartype().equals(VAR_BOOLEAN)){
			boolean result = Boolean.parseBoolean(value.getvalue());
			if(result)
				trueButton.setSelected(true);
			else
				falseButton.setSelected(true);
		}
//		else
//			valuereceiver.setText(value.getvalue()); //And we set this text here
	//	updateReceivers();

	}

	@Override
	public String getViewPath() {
		return String.format("/ActionUpdateModel.fxml", this.getClass().getSimpleName());
	}



}
