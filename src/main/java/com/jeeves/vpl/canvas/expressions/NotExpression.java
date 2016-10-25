package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

public class NotExpression extends Expression { // NO_UCD (unused code)

public NotExpression() {
	this(new FirebaseExpression());
}
	public NotExpression(FirebaseExpression data) {
		super(data);
		name.setValue("NOT");
		description = "Returns true if either of two expressions are true, false otherwise";
		addListeners();

	}
	@Override
	public void setup() {
		this.varType = Expression.VAR_BOOLEAN;
		operand.setText("is false");
		receivers.add(new ExpressionReceiver(Expression.VAR_BOOLEAN));
	}
}
