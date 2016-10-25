
package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

public class AndExpression extends Expression { // NO_UCD (unused code)

	public AndExpression(){
		this(new FirebaseExpression());
	}
	public AndExpression(FirebaseExpression data) {
		super(data);
		this.name.setValue("AND");
		this.description = "Returns true if two expressions are both true, false otherwise";
		addListeners();

	}
	@Override
	public void setup() {
		this.varType = Expression.VAR_BOOLEAN;
		operand.setText("and");
		receivers.add(new ExpressionReceiver(Expression.VAR_BOOLEAN));
		receivers.add(new ExpressionReceiver(Expression.VAR_BOOLEAN));
	}
	@Override
	public void addListeners() {
		super.addListeners();
	}
}
