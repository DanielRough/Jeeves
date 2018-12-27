package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

import static com.jeeves.vpl.Constants.*;

public class NotExpression extends Expression { // NO_UCD (unused code)

	public NotExpression(String name) {
		this(new FirebaseExpression(name));
	}

	public NotExpression(FirebaseExpression data) {
		super(data);
	}

	@Override
	public void setup() {
		operand.setText("is false");
		receivers.add(new ExpressionReceiver(VAR_BOOLEAN));
	}
}
