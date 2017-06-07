package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.VAR_BOOLEAN;
import static com.jeeves.vpl.Constants.VAR_NUMERIC;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

public class EqualsExpression extends Expression { // NO_UCD (unused code)
	public static final String DESC = "evaluates to true if the two contained expressions/attributes are equal";
	public static final String NAME = "Equality";

	public EqualsExpression() {
		this(new FirebaseExpression());
	}

	public EqualsExpression(FirebaseExpression data) {
		super(data);
	}

	@Override
	public void setup() {
		name = NAME;
		description = DESC;
		this.varType = VAR_BOOLEAN;
		operand.setText("is equal to");
		receivers.add(new ExpressionReceiver(VAR_NUMERIC));
		receivers.add(new ExpressionReceiver(VAR_NUMERIC));

	}
}
