package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.VAR_BOOLEAN;
import static com.jeeves.vpl.Constants.VAR_NUMERIC;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

public class EqualsExpression extends Expression { // NO_UCD (unused code)

	public EqualsExpression(String name) {
		this(new FirebaseExpression(name));
	}

	public EqualsExpression(FirebaseExpression data) {
		super(data);
	}

	@Override
	public void setup() {
		operand.setText("is equal to");
		receivers.add(new ExpressionReceiver(VAR_NUMERIC));
		receivers.add(new ExpressionReceiver(VAR_NUMERIC));

	}
}
