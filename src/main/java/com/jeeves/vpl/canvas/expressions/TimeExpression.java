package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.VAR_CLOCK;

import java.util.Map;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.TimeReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;

public class TimeExpression extends Expression { // NO_UCD (unused code)
	private static final String TIMEEARLY = "timeBoundEarly";
	private static final String TIMELATE = "timeBoundLate";
	private TimeReceiver exprDateFrom;
	private TimeReceiver exprDateTo;

	public TimeExpression(String name) {
		this(new FirebaseExpression(name));
	}

	public TimeExpression(FirebaseExpression data) {
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
			params.put(TIMEEARLY,exprDateFrom.getChildModel())
		);
		exprDateFrom.getTextField().textProperty().addListener(listen -> 
			params.put(TIMEEARLY, exprDateFrom.getText())
		);
		exprDateTo.getChildElements().addListener((ListChangeListener<ViewElement>) listener -> 
			params.put(TIMELATE,exprDateTo.getChildModel())
		);
		exprDateTo.getTextField().textProperty().addListener(listen -> 
			params.put(TIMELATE, exprDateTo.getText())
		);

	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		
		exprDateFrom = new TimeReceiver(VAR_CLOCK);
		exprDateTo = new TimeReceiver(VAR_CLOCK);


		box.getChildren().clear();
		box.getChildren().addAll(exprDateFrom, new Label("to"), exprDateTo);
		box.setSpacing(10);
		box.setPadding(new Insets(0, 14, 0, 14));
	}

	private void addVarListener(String date,TimeReceiver receiver) {
		@SuppressWarnings("unchecked")
		String name = ((Map<String,Object>)params.get(date)).get("name").toString();
		//Wee snippet of code that we use elsewhere, I haven't got time to fuck about
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
		if (params.containsKey(TIMEEARLY)){
			if (params.get(TIMEEARLY) instanceof String) {
				exprDateFrom.setText(params.get(TIMEEARLY).toString()); //For plain dates, no variables
			}//Otherwise at some point we dragged a date variable into here
			else{ 
				addVarListener(TIMEEARLY,exprDateFrom);
			}

		} 
		if (params.containsKey(TIMELATE)){
			if (params.get(TIMELATE) instanceof String) {
				exprDateTo.setText(params.get("dateTo").toString()); //For plain dates, no variables
			}//Otherwise at some point we dragged a date variable into here
			else{ 
				addVarListener(TIMELATE,exprDateTo);

			}
		} 
	}


	@Override
	public void setup() {
		operand.setText("is");

	}

}
