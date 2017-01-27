package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

public class LessExpression extends Expression  { // NO_UCD (unused code)
	ExpressionReceiver var1;
	ExpressionReceiver var2;

public LessExpression() {
	this(new FirebaseExpression());
}
	public LessExpression(FirebaseExpression data) {
		super(data);
		name.setValue("Is Less Than");
		description = "Returns true if two expressions are equal, false otherwise";
		addListeners();

	}
	@Override
	public void setup() {
		this.varType = Expression.VAR_BOOLEAN;
		operand.setText("<");
		var1 = new ExpressionReceiver(Expression.VAR_NUMERIC);
		var2 = new ExpressionReceiver(Expression.VAR_NUMERIC);
		receivers.add(var1);
		receivers.add(var2);

	}
}