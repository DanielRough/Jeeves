package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.exprNames;

import java.util.ArrayList;
import java.util.List;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * A class which represents an Expression. This has a type such as Boolean or
 * Numeric, and takes two or more variables and returns an end result
 * 
 * @author Daniel
 *
 */

public abstract class Expression extends ViewElement<FirebaseExpression>{
	private static final String UPDATE = "update";
	protected ObservableMap<String, Object> params;

	public static Expression create(FirebaseExpression exprmodel) {
		String trigname = exprmodel.getname();
		String classname = "com.jeeves.vpl.canvas.expressions." + exprNames.get(trigname);		try {
			return (Expression) Class.forName(classname).getConstructor(FirebaseExpression.class)
					.newInstance(exprmodel);
		} catch (Exception e) {
			return new UserVariable(exprmodel);
		}
	}

	protected HBox box;
	protected Label operand; 
														
	protected ArrayList<ExpressionReceiver> receivers; 

	public Expression() throws InstantiationException, IllegalAccessException {
		super(FirebaseExpression.class.newInstance(),FirebaseExpression.class);
	}

	public Expression(FirebaseExpression data) {
		super(data, FirebaseExpression.class);
		this.model = data;

	}

	@Override
	public FirebaseExpression getModel(){
		return model;
	}
	@Override
	public void addListeners() {
		super.addListeners();
		params = FXCollections.observableMap(model.getparams());
		if (params != null) {
			params.addListener((
						javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change) ->{

					if (change.wasAdded()) {
						model.getparams().put(change.getKey(), change.getValueAdded());
					} else {
						model.getparams().remove(change.getKey());
					}

			});
		}
		for (ExpressionReceiver receiver : receivers) {
			receiver.getTextField().textProperty().addListener((o, v0, v1)-> {
					FirebaseVariable var = new FirebaseVariable();
					var.setvalue(v1);
					var.setIsValue(true);
					UserVariable variable = UserVariable.create(var);
					addExprToReceiver(variable,receivers.indexOf(receiver));
					params.put(UPDATE,"updated");
					params.remove(UPDATE);	
				});			
			receiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
				if (!receiver.getChildElements().isEmpty())
					addExprToReceiver(receiver.getChildElements().get(0), receivers.indexOf(receiver));
				else
					removeExprFromReceiver(receivers.indexOf(receiver));
				params.put(UPDATE,"updated");
				params.remove(UPDATE);	
				autosize();
			});
			
		}
	}

	@Override
	public void fxmlInit() {
		this.type = ElementType.EXPRESSION;
		this.model = new FirebaseExpression();
		receivers = new ArrayList<>();
		operand = new Label();
		box = new HBox();
		box.setSpacing(2);
		box.setAlignment(Pos.CENTER);
		getChildren().add(box);
		updatePane();
		setPickOnBounds(true);

	}

	@Override
	public ViewElement<FirebaseExpression> getInstance() {
		return this;
	}


	public ObservableMap<String, Object> getparams() {
		return params;
	}


	@Override
	public void setData(FirebaseExpression model) {
		super.setData(model);

		List<FirebaseExpression> variables = model.getvariables();
		if (variables == null)
			return;
		for (FirebaseExpression var : variables) {
			int index = (int) (var.getindex());
			if (index > 1) {
				continue; // Temporary fix hopefully
			}
				Expression expr = Expression.create(var);
				receivers.get(index).addChild(expr, 0, 0); // I cannot foresee
															// this working tbh
				expr.parentPane = this.parentPane;
			

		}

	}

	@Override
	public void setParentPane(DragPane parent) {
		super.setParentPane(parent);
		receivers.forEach(rec -> {
			if (rec.getChildExpression() != null)
				rec.getChildExpression().setParentPane(parent);
		});
	}

	public abstract void setup();

	public void updatePane() {
		setup();
		Label lbracket;
		Label rbracket;
		lbracket = new Label("(");
		rbracket = new Label(")");
		box.getChildren().removeAll(box.getChildren());
		if (receivers.size() == 2)
			box.getChildren().addAll(lbracket, receivers.get(0), operand, receivers.get(1), rbracket);
		else if (receivers.size() == 1)
			box.getChildren().addAll(lbracket, receivers.get(0), operand, rbracket);
		box.getStyleClass().add(Constants.VAR_BOOLEAN);
		box.setPadding(new Insets(0, 4, 0, 4));
	}

	private void addExprToReceiver(ViewElement<?> childModel, int index) {
		((FirebaseExpression) childModel.getModel()).setIndex(index); 
		if (model.getvariables().size() > index)
			model.getvariables().set(index, (FirebaseExpression) childModel.getModel());
		else
			model.getvariables().add((FirebaseExpression) childModel.getModel());
	}

	private void removeExprFromReceiver(int index) {
		for (FirebaseExpression vars : model.getvariables()) {
			if (vars.getindex() == index) {
				model.getvariables().remove(vars);
				break;
			}
		}
	}

}
