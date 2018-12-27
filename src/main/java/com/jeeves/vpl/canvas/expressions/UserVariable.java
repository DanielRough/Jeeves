package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.firebase.FirebaseExpression;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UserVariable extends Expression {
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
	protected String varType;

	protected String varValue; // NO_UCD (use private)

	public UserVariable() throws Exception {
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

	public String getVarType() {
		return varType;
	}

	@Override
	public void setData(FirebaseExpression model) {
		super.setData(model);
		name = (model.getname());
		varType = model.getvartype();
		varValue = model.getvalue();
		getStyleClass().add(varType);
		label.setText(model.getname());
		ImageView testImg = new ImageView();
		testImg.setFitHeight(15);
		testImg.setFitWidth(15);
		testImg.setY(2);
		getChildren().add(testImg);
		
		if(model.getisValue() == false){
		getChildren().add(label);
		label.setAlignment(Pos.CENTER);
		label.autosize();
		}
	}

	@Override
	public void setup() {
	}
}
