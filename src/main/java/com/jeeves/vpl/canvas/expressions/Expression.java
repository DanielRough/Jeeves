package com.jeeves.vpl.canvas.expressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
/**
 * A class which represents an Expression. This has a type such as Boolean or Numeric, and takes two or more
 * variables and returns an end result
 * @author Daniel
 *
 */

public abstract class Expression extends ViewElement<FirebaseExpression> implements Typed{
	
	protected HBox box;
	protected Map<String,Object> params = new HashMap<String,Object>();

	private Label lbracket; //= new Label("(");
	private Label rbracket; //= new Label(")");
	protected Label operand; //= new Label(); // Label with the operation symbol
	protected ArrayList<ExpressionReceiver> receivers; //= new ArrayList<ExpressionReceiver>();
	protected String varType;// = VAR_BOOLEAN;
	
	public String getVarType(){
		return varType;
	}
	public Node[] getWidgets(){
		Node[] nodes = new Node[receivers.size()];
		for(int i = 0; i < receivers.size(); i++)
			nodes[i] = receivers.get(i);
		return nodes;
	}


	public Expression(){
		super(FirebaseExpression.class);
//		for (ExpressionReceiver receiver : receivers) {
//			receiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
//			ViewElement expr = receiver.getChildElements().get(0);
//			System.out.println("my new width is " + box.getWidth());
//			System.out.println("my new height is " + box.getHeight());
//			autosize();
//		});
//		};
	//	setup();
	}
	public static Expression create(FirebaseExpression exprmodel){
		String classname = exprmodel.gettype();
		try {
			return (Expression)Class.forName(classname).getConstructor(FirebaseExpression.class).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Expression(FirebaseExpression data){
		super(data,FirebaseExpression.class);

		//DJR do we even NEED listeners here? When we save, can just add the receiver's stuff to it
//		
//		for (ExpressionReceiver receiver : receivers) {
//			receiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
//			ViewElement expr = receiver.getChildElements().get(0);
//			
//			autosize();System.out.println("my new width is " + getWidth());
//			System.out.println("my new height is " + getHeight());
//		});
//		};
//			receiver.text.textProperty().addListener(new ChangeListener<String>(){
//
//				@Override
//				public void changed(ObservableValue<? extends String> arg0,
//						String arg1, String arg2) {
//						addTextToReceiver(arg2,receivers.indexOf(receiver));
//				}
//				
//			});
//		}

	}
//
//	
//	private void addTextToReceiver(String text, int index){
//		FirebaseExpression expr= new FirebaseExpression();
//		expr.setIsValue(true);
//		expr.setValue(text);
//		expr.setIndex(index);
//		if(model.getvariables().size() > index)
//			model.getvariables().set(index, expr);
//		else
//			model.getvariables().add(expr);
//	}
//	/**
//	 * Adds an element to one of the receivers of this expression, determined by the index
//	 * @param childModel The element to add
//	 * @param index The index of the receiver to add it to
//	 */
//	private void addExprToReceiver(ViewElement childModel, int index) {
//		((FirebaseExpression)childModel.getModel()).setIndex(index); //I don't much like this line
//		if(model.getvariables().size() > index)
//			model.getvariables().set(index, (FirebaseExpression)childModel.getModel());
//		else
//			model.getvariables().add((FirebaseExpression)childModel.getModel());
//
//	}
//	
//	private void removeExprFromReceiver(int index){
//		for(FirebaseExpression vars : model.getvariables()){
//			if(vars.getindex() == index){
//				model.getvariables().remove(vars);
//				break;
//			}
//		}
//	}

	public void fxmlInit(){
		this.type = ElementType.EXPRESSION;
		receivers = new ArrayList<ExpressionReceiver>();
		operand = new Label();
	//	if(!(this instanceof UserVariable)){
			box = new HBox();
			box.setSpacing(2);
			box.setAlignment(Pos.CENTER);
			getChildren().add(box);
	//		}
		updatePane();
	}
	@Override
	public ViewElement<FirebaseExpression> getInstance() {
		return this;
	}
	
//	public void addListeners(){
//		super.addListeners();	
//	}
	public FirebaseExpression getModel(){
		return model;
	}
	public void setData(FirebaseExpression model){
		super.setData(model);

		List<FirebaseExpression> variables = model.getvariables();
		if(variables == null)return;
		for (FirebaseExpression var : variables) {
			int index = (int)(var.getindex());
				if(index > 1)continue; //Temporary fix hopefully
				if(var.getisValue() == false){
					receivers.get(index).addChild(Expression.create(var), 0, 0); //I cannot foresee this working tbh
				}
//				else{
//					receivers.get(index).setText(var.getvalue()); //And we set this text here
//				}
		}

	}


	public abstract void setup();
	public void updatePane(){
		setup();
		lbracket = new Label("(");
		rbracket = new Label(")");
	//	operand = new Label(); 
		box.getChildren().removeAll(box.getChildren());
		if(receivers.size()==2)
			box.getChildren().addAll(lbracket,receivers.get(0), operand,receivers.get(1),rbracket);
		else if(receivers.size()==1)
			box.getChildren().addAll(lbracket,receivers.get(0),operand,rbracket);
		box.getStyleClass().add(this.varType);
			box.setPadding(new Insets(0,4, 0,4));
	}

}
