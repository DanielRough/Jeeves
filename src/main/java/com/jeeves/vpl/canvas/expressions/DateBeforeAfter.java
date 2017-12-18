package com.jeeves.vpl.canvas.expressions;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.DateReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseVariable;

import static com.jeeves.vpl.Constants.*;

public class DateBeforeAfter extends Expression { // NO_UCD (unused code)
	public static final String DESC = "Returns true if the current time is within the specified bounds of the expression time";
	public static final String NAME = "Date Before/After";
	private ComboBox<String> cboBeforeAfter;
	private ComboBox<String> cboTimeDiff;
	private DateReceiver exprTimeVar;

	public DateBeforeAfter() {
		this(new FirebaseExpression());
	}

	public DateBeforeAfter(FirebaseExpression data) {
		super(data);
		addListeners();

	}
	@Override
	public void setParentPane(ParentPane parent) {
		super.setParentPane(parent);
			if (exprTimeVar.getChildExpression() != null)
				exprTimeVar.getChildExpression().setParentPane(parent);
	}
	@Override
	public void addListeners() {
		super.addListeners();
		model.getparams().put("timeDiff", cboTimeDiff.getValue());
		model.getparams().put("beforeAfter", cboBeforeAfter.getValue());
		model.getparams().put("timeVar", exprTimeVar.getText());
		cboTimeDiff.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				params.put("timeDiff", arg2);
			}
		});
		// (value->model.getparams().put("timeDiff", value));
		cboBeforeAfter.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				params.put("beforeAfter", arg2);
			}
		});
		exprTimeVar.getChildElements().addListener((ListChangeListener<ViewElement>) listener -> {
			params.put("timeVar",exprTimeVar.getChildModel());
		});
		exprTimeVar.getTextField().textProperty().addListener(listen -> {
			params.put("timeVar", exprTimeVar.getText());
		});

	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		cboTimeDiff = new ComboBox<String>();
		cboBeforeAfter = new ComboBox<String>();
		cboTimeDiff.getStyleClass().addAll("shadowy", "styled-select");
		exprTimeVar = new DateReceiver(VAR_DATE);
		cboTimeDiff.getItems().addAll("1 month", "1 week", "1 day");
		cboBeforeAfter.getItems().addAll("before", "after");
		cboTimeDiff.getSelectionModel().clearAndSelect(0);
		cboBeforeAfter.getSelectionModel().clearAndSelect(0);
		styleTextCombo(cboBeforeAfter);

		box.getChildren().clear();
		box.getChildren().addAll(cboTimeDiff, cboBeforeAfter, exprTimeVar);
		box.setSpacing(10);
		box.setPadding(new Insets(0, 14, 0, 14));
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { cboTimeDiff, cboBeforeAfter, exprTimeVar };
	}

	@Override
	public void setData(FirebaseExpression model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();
		if (params.containsKey("timeDiff"))
			cboTimeDiff.setValue(params.get("timeDiff").toString());
		if (params.containsKey("beforeAfter"))
			cboBeforeAfter.setValue(params.get("beforeAfter").toString());
		if (params.containsKey("timeVar")){
			if (params.get("timeVar") instanceof String) {
				exprTimeVar.setText(params.get("timeVar").toString()); //For plain dates, no variables
			}//Otherwise at some point we dragged a date variable into here
			else{ 
				String name = ((Map<String,Object>)params.get("timeVar")).get("name").toString();
				//Wee snippet of code that we use elsewhere, I haven't got time to fuck about
				gui.registerVarListener(listener->{
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