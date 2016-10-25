package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

public class GreaterExpression extends Expression  { // NO_UCD (unused code)
	ExpressionReceiver var1;
	ExpressionReceiver var2;
	
	public GreaterExpression() {
		this(new FirebaseExpression());
	}
	public GreaterExpression(FirebaseExpression data) {
		super(data);
		name.setValue("IS GREATER THAN");
		description = "Returns true if two expressions are equal, false otherwise";
		addListeners();
	}
	@Override
	public void setup() {
		this.varType = Expression.VAR_BOOLEAN;
		operand.setText(">");
		var1 = new ExpressionReceiver(Expression.VAR_NUMERIC);
		var2 = new ExpressionReceiver(Expression.VAR_NUMERIC);
		receivers.add(var1);
		receivers.add(var2);

	}
}