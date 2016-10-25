package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

public class EqualsExpression extends Expression  { // NO_UCD (unused code)
	ExpressionReceiver var1;
	ExpressionReceiver var2;

	public EqualsExpression() {
		this(new FirebaseExpression());
	}
	public EqualsExpression(FirebaseExpression data) {
		super(data);
		name.setValue("IS EQUAL");
		description = "Returns true if two expressions are equal, false otherwise";
		addListeners();
		
	}
	@Override
	public void setup() {
		this.varType = Expression.VAR_BOOLEAN;
		operand.setText("==");
		var1 = new ExpressionReceiver(Expression.VAR_NUMERIC);
		var2 = new ExpressionReceiver(Expression.VAR_NUMERIC);
		receivers.add(var1);
		receivers.add(var2);

	}
	@Override
	public void addListeners() {
		super.addListeners();
	}
}
