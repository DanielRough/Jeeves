package com.jeeves.vpl.canvas.expressions;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseVariable;

public class UserVariable extends Expression {
	private Label label;
//	private Pane layoutPane; //= new StackPane();

	protected String varValue; // NO_UCD (use private)
	private boolean isCustom = true;

	public Node[] getWidgets(){
		return new Node[]{};
	}

	

	public UserVariable(FirebaseExpression data) {
		super(data);
		//getChildren().add(layoutPane);
		//layoutPane.getStyleClass().add(varType);
		getStyleClass().add(varType);
		label.setFont(Font.font("Calibri",FontWeight.BOLD,12));
	}

public UserVariable() {
	this(new FirebaseVariable());
}
	@Override
	public ViewElement getInstance() {
		return this;
	}
	
	public Label getLabel(){
		return label;
	}

	public void setCustom(boolean custom){
		this.isCustom = custom;
	}
	public boolean getCustom(){
		return isCustom;
	}

	public void setData(FirebaseExpression model){
		
		super.setData(model);
		name.setValue(model.getname());
		varType = model.getvartype();
		varValue = model.getvalue();
		isCustom = (model).getisCustom();
		getStyleClass().add(varType);
		label.setText(name.get());
		getChildren().add(label);
		label.setFont(Font.font("Calibri",FontWeight.BOLD,14));
		label.getStyleClass().clear();
		if(varType.equals(Expression.VAR_CLOCK))
			label.setTextFill(Color.WHITE);
		else
			label.setTextFill(Color.PURPLE);		
		label.setTextAlignment(TextAlignment.CENTER);
		label.setAlignment(Pos.CENTER);
		label.setPadding(new Insets(0, 15, 0, 15));
		label.autosize();		
		

	}

	public void fxmlInit(){
		label = new Label();
		setPrefHeight(20);

	}
	public void setVarType(String type){
		this.varType = type;
	}

	public void setValue(String variable){
		this.varValue = variable;
	}

	@Override
	public void setup() {
	
		
	}

}
