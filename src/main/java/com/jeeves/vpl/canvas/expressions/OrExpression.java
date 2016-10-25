package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

public class OrExpression extends Expression  { // NO_UCD (unused code)

public OrExpression() {
	this(new FirebaseExpression());
}
	public OrExpression(FirebaseExpression data) {
		super(data);
		name.setValue("OR");
		description = "Returns true if either of two expressions are true, false otherwise";
		addListeners();

	}
	@Override
	public void setup() {
		this.varType = Expression.VAR_BOOLEAN;
		operand.setText("or");
		receivers.add(new ExpressionReceiver(Expression.VAR_BOOLEAN));
		receivers.add(new ExpressionReceiver(Expression.VAR_BOOLEAN));
	}
}
