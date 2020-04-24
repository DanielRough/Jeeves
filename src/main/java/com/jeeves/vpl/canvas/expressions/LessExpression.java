package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

import static com.jeeves.vpl.Constants.*;

public class LessExpression extends Expression { // NO_UCD (unused code)

	public LessExpression(String name) {
		this(new FirebaseExpression(name));
	}

	public LessExpression(FirebaseExpression data) {
		super(data);
	}

	@Override
	public void setup() {
		operand.setText("is less than");
		receivers.add(new ExpressionReceiver(VAR_NUMERIC));
		receivers.add(new ExpressionReceiver(VAR_NUMERIC));

	}
}