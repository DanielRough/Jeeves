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
import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.firebase.FirebaseVariable;

import static com.jeeves.vpl.Constants.*;

public class UserVariable extends ViewElement<FirebaseVariable> implements Typed{
	private Label label;
	protected String name;
	protected String varType;// = VAR_BOOLEAN;
	protected String varValue; // NO_UCD (use private)
//	private boolean isCustom = true;

	public Node[] getWidgets(){
		return new Node[]{};
	}

//
//
	public String getVarType(){
		return varType;
	}
	public UserVariable(FirebaseVariable data) {
		super(data,FirebaseVariable.class);
		this.model = data;
		//getChildren().add(layoutPane);
		//layoutPane.getStyleClass().add(varType);
	//	getStyleClass().add(varType);
	//	System.out.println("var type her e is " + varType);
	//	label.setFont(Font.font("Calibri",FontWeight.BOLD,12));
	}

	public UserVariable() {
		super(FirebaseVariable.class);
		//	this(new FirebaseVariable());
	}
	@Override
	public ViewElement<FirebaseVariable> getInstance() {
		return this;
	}

//	public Label getLabel(){
//		return label;
//	}

//	public void setCustom(boolean custom){
//		this.isCustom = custom;
//	}
//	public boolean getCustom(){
//		return isCustom;
//	}

	public void setData(FirebaseVariable model){
		super.setData(model);
		name= (model.getname());
		varType = model.getvartype();
		varValue = model.getvalue();
	//	isCustom = (model).getisCustom();
		getStyleClass().add(varType);
		label.setText(model.getname());
		getChildren().add(label);
	//	label.setFont(Font.font("Calibri",FontWeight.BOLD,14));
	//	label.getStyleClass().clear();
//		if(varType.equals(VAR_CLOCK))
//			label.setTextFill(Color.WHITE);
//		else
//			label.setTextFill(Color.PURPLE);		
	//	label.setTextAlignment(TextAlignment.CENTER);
		label.setAlignment(Pos.CENTER);
	//	label.setPadding(new Insets(0, 15, 0, 15));
		label.autosize();		


	}

	public void fxmlInit(){
		this.type = ElementType.VARIABLE;
		label = new Label();
		setPrefHeight(20);
		label.setPadding(new Insets(0,15,0,15));

	}
//	public void setVarType(String type){
//		this.varType = type;
//	}
//
//	public void setValue(String variable){
//		this.varValue = variable;
//	}
//
//	public void setName(String name){
//		this.name = name;
//	}
	public String getName(){
		return name;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}
