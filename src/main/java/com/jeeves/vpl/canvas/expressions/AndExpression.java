
package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.VAR_BOOLEAN;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

public class AndExpression extends Expression { // NO_UCD (unused code)

	public AndExpression(String name) {
		this(new FirebaseExpression(name));
	}

	public AndExpression(FirebaseExpression data) {
		super(data);
	}

	@Override
	public void setup() {
		operand.setText("and");
		receivers.add(new ExpressionReceiver(VAR_BOOLEAN));
		receivers.add(new ExpressionReceiver(VAR_BOOLEAN));
	}
}
