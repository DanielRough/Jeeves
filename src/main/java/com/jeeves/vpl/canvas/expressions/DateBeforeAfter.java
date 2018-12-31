package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.VAR_DATE;

import java.util.Map;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.DateReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;

public class DateBeforeAfter extends Expression { // NO_UCD (unused code)
	private static final String BEFOREAFTER = "beforeAfter";
	private static final String TIMEDIFF = "timeDiff";
	private static final String TIMEVAR = "timeVar";
	private ComboBox<String> cboBeforeAfter;
	private ComboBox<String> cboTimeDiff;
	private DateReceiver exprTimeVar;

	public DateBeforeAfter(String name) {
		this(new FirebaseExpression(name));
	}

	public DateBeforeAfter(FirebaseExpression data) {
		super(data);
		addListeners();

	}
	@Override
	public void setParentPane(DragPane parent) {
		super.setParentPane(parent);
			if (exprTimeVar.getChildExpression() != null)
				exprTimeVar.getChildExpression().setParentPane(parent);
	}
	@Override
	public void addListeners() {
		super.addListeners();
		cboTimeDiff.valueProperty().addListener((arg0,arg1,arg2)-> 
				params.put(TIMEDIFF, arg2)
		);
		cboBeforeAfter.valueProperty().addListener((arg0,arg1,arg2)-> 
				params.put(BEFOREAFTER, arg2)
		);
		exprTimeVar.getChildElements().addListener((ListChangeListener<ViewElement>) listener -> 
			params.put(TIMEVAR,exprTimeVar.getChildModel())
		);
		exprTimeVar.getTextField().textProperty().addListener(listen -> 
			params.put(TIMEVAR, exprTimeVar.getText())
		);

	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		cboTimeDiff = new ComboBox<>();
		cboBeforeAfter = new ComboBox<>();
		cboTimeDiff.getStyleClass().addAll("shadowy", "styled-select");
		exprTimeVar = new DateReceiver(VAR_DATE);
		cboTimeDiff.getItems().addAll("1 month", "1 week", "1 day");
		cboBeforeAfter.getItems().addAll("before", "after");
		cboTimeDiff.getSelectionModel().clearAndSelect(0);
		cboBeforeAfter.getSelectionModel().clearAndSelect(0);
		

		box.getChildren().clear();
		box.getChildren().addAll(cboTimeDiff, cboBeforeAfter, exprTimeVar);
		box.setSpacing(10);
		box.setPadding(new Insets(0, 14, 0, 14));
	}


	@Override
	public void setData(FirebaseExpression model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();
		if (params.containsKey(TIMEDIFF))
			cboTimeDiff.setValue(params.get(TIMEDIFF).toString());
		if (params.containsKey(BEFOREAFTER))
			cboBeforeAfter.setValue(params.get(BEFOREAFTER).toString());
		if (params.containsKey(TIMEVAR)){
			if (params.get(TIMEVAR) instanceof String) {
				exprTimeVar.setText(params.get(TIMEVAR).toString()); //For plain dates, no variables
			}//Otherwise at some point we dragged a date variable into here
			else{ 
				addVarListener();
			}
		} 
	}
	public void addVarListener() {
		@SuppressWarnings("unchecked")
		String name = ((Map<String,Object>)params.get(TIMEVAR)).get("name").toString();
		//Wee snippet of code that we use elsewhere, I haven't got time to fuck about
		Constants.getOpenProject().registerVarListener(listener->{
			listener.next();
			if(listener.wasAdded()){
				for(FirebaseVariable var : listener.getAddedSubList()){
					if(var.getname().equals(name)){
						exprTimeVar.addChild(UserVariable.create(var), 0,0);
						setParentPane(parentPane);
					}
				}
			}
		});
	}

	@Override
	public void setup() {
		operand.setText("is");

	}

}