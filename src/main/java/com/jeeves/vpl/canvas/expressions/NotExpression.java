package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;

import static com.jeeves.vpl.Constants.*;

public class NotExpression extends Expression { // NO_UCD (unused code)
	public static final String NAME = "is this false?";
	public static final String DESC = "returns true if the contained expression/attribute is false";
	@Override
	public void setup() {
		name = NAME;
		description = DESC;
		this.varType = VAR_BOOLEAN;
		operand.setText("is false");
		receivers.add(new ExpressionReceiver(VAR_BOOLEAN));
	}
}
