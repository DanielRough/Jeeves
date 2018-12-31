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
import javafx.scene.control.Label;

public class DateExpression extends Expression { // NO_UCD (unused code)
	private static final String DATEEARLY = "dateBoundEarly";
	private static final String DATELATE = "dateBoundLate";
	private DateReceiver exprDateFrom;
	private DateReceiver exprDateTo;

	public DateExpression(String name) {
		this(new FirebaseExpression(name));
	}

	public DateExpression(FirebaseExpression data) {
		super(data);
		addListeners();

	}
	@Override
	public void setParentPane(DragPane parent) {
		super.setParentPane(parent);
			if (exprDateFrom.getChildExpression() != null)
				exprDateFrom.getChildExpression().setParentPane(parent);
			if (exprDateTo.getChildExpression() != null)
				exprDateTo.getChildExpression().setParentPane(parent);
	}
	@Override
	public void addListeners() {
		super.addListeners();

		exprDateFrom.getChildElements().addListener((ListChangeListener<ViewElement>) listener -> 
			params.put(DATEEARLY,exprDateFrom.getChildModel())
		);
		exprDateFrom.getTextField().textProperty().addListener(listen ->
			params.put(DATEEARLY, exprDateFrom.getText())
		);
		exprDateTo.getChildElements().addListener((ListChangeListener<ViewElement>) listener ->
			params.put(DATELATE,exprDateTo.getChildModel())
		);
		exprDateTo.getTextField().textProperty().addListener(listen -> 
			params.put(DATELATE, exprDateTo.getText())
		);
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		
		exprDateFrom = new DateReceiver(VAR_DATE);
		exprDateTo = new DateReceiver(VAR_DATE);


		box.getChildren().clear();
		box.getChildren().addAll(exprDateFrom, new Label("to"), exprDateTo);
		box.setSpacing(10);
		box.setPadding(new Insets(0, 14, 0, 14));
	}

	private void addVarListener(String date,DateReceiver receiver) {
		@SuppressWarnings("unchecked")
		String name = ((Map<String,Object>)params.get(date)).get("name").toString();
		Constants.getOpenProject().registerVarListener(listener->{
			listener.next();
			if(listener.wasAdded()){
				for(FirebaseVariable var : listener.getAddedSubList()){
					if(var.getname().equals(name)){
						receiver.addChild(UserVariable.create(var), 0,0);
						setParentPane(parentPane);
					}
				}
			}
		});
	}
	@Override
	public void setData(FirebaseExpression model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();
		if (params.containsKey(DATEEARLY)){
			if (params.get(DATEEARLY) instanceof String) {
				exprDateFrom.setText(params.get(DATEEARLY).toString()); //For plain dates, no variables
			}//Otherwise at some point we dragged a date variable into here
			else{ 
				addVarListener(DATEEARLY,exprDateFrom);
			}
		} 
		if (params.containsKey(DATELATE)){
			if (params.get(DATELATE) instanceof String) {
				exprDateTo.setText(params.get(DATELATE).toString()); //For plain dates, no variables
			}//Otherwise at some point we dragged a date variable into here
			else{ 
				addVarListener(DATELATE,exprDateTo);
			}
		} 
	}


	@Override
	public void setup() {
		operand.setText("is");

	}

}
