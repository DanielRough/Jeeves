package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.VAR_BOOLEAN;
import static com.jeeves.vpl.Constants.VAR_CLOCK;

import java.util.Map;

import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.TimeReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class TimeExpression extends Expression { // NO_UCD (unused code)
	public static final String DESC = "Returns true if the current date is within the specified bounds";
	public static final String NAME = "Time Bounds";
	private TimeReceiver exprDateFrom;
	private TimeReceiver exprDateTo;

	public TimeExpression() {
		this(new FirebaseExpression());
	}

	public TimeExpression(FirebaseExpression data) {
		super(data);
		addListeners();

	}
	@Override
	public void setParentPane(ParentPane parent) {
		super.setParentPane(parent);
			if (exprDateFrom.getChildExpression() != null)
				exprDateFrom.getChildExpression().setParentPane(parent);
			if (exprDateTo.getChildExpression() != null)
				exprDateTo.getChildExpression().setParentPane(parent);
	}
	@Override
	public void addListeners() {
		super.addListeners();

		model.getparams().put("timeBoundEarly", exprDateFrom.getText());
		model.getparams().put("timeBoundLate", exprDateTo.getText());

		exprDateFrom.getChildElements().addListener((ListChangeListener<ViewElement>) listener -> {
			params.put("timeBoundEarly",exprDateFrom.getChildModel());
		});
		exprDateFrom.getTextField().textProperty().addListener(listen -> {
			params.put("timeBoundEarly", exprDateFrom.getText());
		});
		exprDateTo.getChildElements().addListener((ListChangeListener<ViewElement>) listener -> {
			params.put("timeBoundLate",exprDateTo.getChildModel());
		});
		exprDateTo.getTextField().textProperty().addListener(listen -> {
			params.put("timeBoundLate", exprDateTo.getText());
		});

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

	@Override
	public Node[] getWidgets() {
		return new Node[] { exprDateFrom, exprDateTo };
	}

	@Override
	public void setData(FirebaseExpression model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();
		if (params.containsKey("timeBoundEarly")){
			if (params.get("timeBoundEarly") instanceof String) {
				exprDateFrom.setText(params.get("timeBoundEarly").toString()); //For plain dates, no variables
			}//Otherwise at some point we dragged a date variable into here
			else{ 
				String name = ((Map<String,Object>)params.get("timeBoundEarly")).get("name").toString();
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
		if (params.containsKey("timeBoundLate")){
			if (params.get("timeBoundLate") instanceof String) {
				exprDateTo.setText(params.get("dateTo").toString()); //For plain dates, no variables
			}//Otherwise at some point we dragged a date variable into here
			else{ 
				String name = ((Map<String,Object>)params.get("timeBoundLate")).get("name").toString();
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
		name = NAME;
		description = DESC;
		this.varType = VAR_BOOLEAN;
		operand.setText("is");
		box.getStyleClass().add(this.varType);

	}

}
