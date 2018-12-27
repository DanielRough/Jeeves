package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.VAR_DATE;

import java.util.Map;

import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.DateReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Label;

public class DateExpression extends Expression { // NO_UCD (unused code)
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

		exprDateFrom.getChildElements().addListener((ListChangeListener<ViewElement>) listener -> {
			params.put("dateBoundEarly",exprDateFrom.getChildModel());
		});
		exprDateFrom.getTextField().textProperty().addListener(listen -> {
			params.put("dateBoundEarly", exprDateFrom.getText());
		});
		exprDateTo.getChildElements().addListener((ListChangeListener<ViewElement>) listener -> {
			params.put("dateBoundLate",exprDateTo.getChildModel());
		});
		exprDateTo.getTextField().textProperty().addListener(listen -> {
			params.put("dateBoundLate", exprDateTo.getText());
		});

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

	@Override
	public void setData(FirebaseExpression model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();
		if (params.containsKey("dateBoundEarly")){
			if (params.get("dateBoundEarly") instanceof String) {
				exprDateFrom.setText(params.get("dateBoundEarly").toString()); //For plain dates, no variables
			}//Otherwise at some point we dragged a date variable into here
			else{ 
				String name = ((Map<String,Object>)params.get("dateBoundEarly")).get("name").toString();
				//Wee snippet of code that we use elsewhere, I haven't got time to fuck about
				gui.registerVarListener(listener->{
					listener.next();
					if(listener.wasAdded()){
						for(FirebaseVariable var : listener.getAddedSubList()){
							if(var.getname().equals(name)){
								exprDateFrom.addChild(UserVariable.create(var), 0,0);
								setParentPane(parentPane);
							}
						}
					}
				});
			}
			//	exprTimeVar.addChild(UserVariable.create(model.getdateFrom()), 0, 0);
		} 
		if (params.containsKey("dateBoundLate")){
			if (params.get("dateBoundLate") instanceof String) {
				exprDateTo.setText(params.get("dateBoundLate").toString()); //For plain dates, no variables
			}//Otherwise at some point we dragged a date variable into here
			else{ 
				String name = ((Map<String,Object>)params.get("dateBoundLate")).get("name").toString();
				//Wee snippet of code that we use elsewhere, I haven't got time to fuck about
				gui.registerVarListener(listener->{
					listener.next();
					if(listener.wasAdded()){
						for(FirebaseVariable var : listener.getAddedSubList()){
							if(var.getname().equals(name)){
								exprDateTo.addChild(UserVariable.create(var), 0,0);
								setParentPane(parentPane);
							}
						}
					}
				});
			}
			//	exprTimeVar.addChild(UserVariable.create(model.getdateFrom()), 0, 0);
		} 
	}


	@Override
	public void setup() {
		operand.setText("is");

	}

}
