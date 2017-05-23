package com.jeeves.vpl.canvas.actions;

import static com.jeeves.vpl.Constants.VAR_ANY;
import static com.jeeves.vpl.Constants.VAR_BOOLEAN;
import static com.jeeves.vpl.Constants.VAR_NONE;

import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.canvas.receivers.VariableReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseVariable;

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
	private ToggleGroup group = new ToggleGroup(); // for when we're updating a
								@FXML
	private HBox hbox;
	@FXML
	private RadioButton trueButton;
	private ViewElement value; 
	private ExpressionReceiver valuereceiver;
	private UserVariable variable;
	private ExpressionReceiver variablereceiver;
	ListChangeListener<ViewElement> mylistener;

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
				} else
					expr.setvalue("false");
				model.getvars().add(1, expr);
			}

		});
		if (mylistener != null)
			valuereceiver.getChildElements().removeListener(mylistener);

		mylistener = new ListChangeListener<ViewElement>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ViewElement> arg0) {
				arg0.next();
				if (!arg0.getAddedSubList().isEmpty()) {
					ViewElement child = valuereceiver.getChildElements().get(0);
						value = (child);
					model.getvars().add(1, ((Expression)value).getModel());
				} else {
					value = (null);
					model.getvars().remove(1);
				}
			}

		};
		valuereceiver.getChildElements().addListener(mylistener);
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		description = DESC;
		variablereceiver = new VariableReceiver(VAR_ANY);
		hbox.getChildren().add(1, variablereceiver);
		valuereceiver = new ExpressionReceiver(VAR_NONE);
		hbox.getChildren().add(3, valuereceiver);

		hbox.setSpacing(5);
		// hbox.setPadding(new Insets(5, 5, 5, 5));
		hbox.getStyleClass().remove("action");

		trueButton.setToggleGroup(group);
		falseButton.setToggleGroup(group);
		hbox.getChildren().removeAll(trueButton, falseButton);
		group = new ToggleGroup();

	}

	@Override
	public String getViewPath() {
		return String.format("/ActionUpdateModel.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { valuereceiver, variablereceiver };
	}

	
	@Override
	public void setParentPane(ParentPane parent) {
		super.setParentPane(parent);
		if(variablereceiver.getChildExpression() != null)
		variablereceiver.getChildExpression().setParentPane(parent);
		if(valuereceiver.getChildExpression() != null)
		valuereceiver.getChildExpression().setParentPane(parent);

	}
	
	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);

		if (model.getvars().isEmpty())
			return;
		FirebaseExpression variable = (FirebaseExpression) model.getvars().get(0);// (FirebaseExpression)params.get("variable");
		variablereceiver.addChild(UserVariable.create(variable), 0, 0); 
		variablereceiver.setReceiveType(variable.getvartype());

		if (variable.getvartype().equals(VAR_BOOLEAN)) {
			hbox.getChildren().removeAll(trueButton, falseButton, valuereceiver);
			hbox.getChildren().addAll(trueButton, falseButton);
			trueButton.setToggleGroup(group);
			falseButton.setToggleGroup(group);
		} else
			valuereceiver.setReceiveType(variable.getvartype());
		if (model.getvars().size() == 1)
			return;
		FirebaseExpression value = (FirebaseExpression) model.getvars().get(1);// (FirebaseExpression)params.get("value");
		if (value.getisValue() == false)
			valuereceiver.addChild(Expression.create(value), 0, 0); 
		else if (value.getvartype().equals(VAR_BOOLEAN)) {
			boolean result = Boolean.parseBoolean(value.getvalue());
			if (result)
				trueButton.setSelected(true);
			else
				falseButton.setSelected(true);
		}
	}

	protected void updateReceivers() {

		if (!variablereceiver.getChildElements().isEmpty()) {
			ViewElement child = variablereceiver.getChildElements().get(0);
			variable = ((UserVariable) child);
			model.getvars().clear();
			model.getvars().add(0, variable.getModel());
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

}
