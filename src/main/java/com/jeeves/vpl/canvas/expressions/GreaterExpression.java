package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

import static com.jeeves.vpl.Constants.*;

public class GreaterExpression extends Expression { // NO_UCD (unused code)

	public GreaterExpression(String name) {
		this(new FirebaseExpression(name));
	}

	public GreaterExpression(FirebaseExpression data) {
		super(data);
	}

	@Override
	public void setup() {
		operand.setText("is more than");
		receivers.add(new ExpressionReceiver(VAR_NUMERIC));
		receivers.add(new ExpressionReceiver(VAR_NUMERIC));

	}
}