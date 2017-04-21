package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.VAR_BOOLEAN;
import static com.jeeves.vpl.Constants.VAR_NUMERIC;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;

public class EqualsExpression extends Expression  { // NO_UCD (unused code)
	public static final String NAME = "are this and that equal?";
	public static final String DESC = "evaluates to true if the two contained expressions/attributes are equal";
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
