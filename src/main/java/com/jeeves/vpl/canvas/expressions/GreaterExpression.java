package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;

import static com.jeeves.vpl.Constants.*;

public class GreaterExpression extends Expression  { // NO_UCD (unused code)
	public static final String NAME = "is this more than that?";
	public static final String DESC = "returns true if the left number is more than the right number";
	@Override
	public void setup() {
		name = NAME;
		description = DESC;
		this.varType = VAR_BOOLEAN;
		operand.setText("is more than");
		receivers.add(new ExpressionReceiver(VAR_NUMERIC));
		receivers.add(new ExpressionReceiver(VAR_NUMERIC));

	}
}