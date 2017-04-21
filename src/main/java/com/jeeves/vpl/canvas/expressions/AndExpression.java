
package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.VAR_BOOLEAN;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;

public class AndExpression extends Expression { // NO_UCD (unused code)
	public static final String NAME = "are this AND that true?";
	public static final String DESC = "evaluates to true if both expressions/attributes are true";
	@Override
	public void setup() {
		name = NAME;
		description = DESC;
		this.varType = VAR_BOOLEAN;
		operand.setText("and");
		receivers.add(new ExpressionReceiver(VAR_BOOLEAN));
		receivers.add(new ExpressionReceiver(VAR_BOOLEAN));
	}

}
