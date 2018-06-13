package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

import static com.jeeves.vpl.Constants.*;

public class OrExpression extends Expression { // NO_UCD (unused code)
	public static final String NAME = "Either True";

	public OrExpression() {
		this(new FirebaseExpression());
	}

	public OrExpression(FirebaseExpression data) {
		super(data);
	}

	@Override
	public void setup() {
		name = NAME;
		this.varType = VAR_BOOLEAN;
		operand.setText("or");
		receivers.add(new ExpressionReceiver(VAR_BOOLEAN));
		receivers.add(new ExpressionReceiver(VAR_BOOLEAN));
	}
}
