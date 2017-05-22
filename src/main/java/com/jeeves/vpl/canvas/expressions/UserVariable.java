package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.firebase.FirebaseExpression;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class UserVariable extends Expression implements Typed {
	public static UserVariable create(FirebaseExpression exprmodel) {
		try {
			return new UserVariable(exprmodel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private Label label;
	protected String name;
	protected String varType;// = VAR_BOOLEAN;

	protected String varValue; // NO_UCD (use private)

	public UserVariable() {
		super();
	}

	public UserVariable(FirebaseExpression data) {
		super(data);
		this.model = data;
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		this.type = ElementType.VARIABLE;
		label = new Label();
		setPrefHeight(20);
		label.setPadding(new Insets(0, 15, 0, 15));

	}

	@Override
	public ViewElement<FirebaseExpression> getInstance() {
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getVarType() {
		return varType;
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] {};
	}

	@Override
	public void setData(FirebaseExpression model) {
		super.setData(model);
		name = (model.getname());
		varType = model.getvartype();
		varValue = model.getvalue();
		getStyleClass().add(varType);
		label.setText(model.getname());
		getChildren().add(label);
		label.setAlignment(Pos.CENTER);
		label.autosize();
	}

	@Override
	public void setup() {
		// TODO Auto-generated method stub

	}
}
