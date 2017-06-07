package com.jeeves.vpl.canvas.expressions;

import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;

import static com.jeeves.vpl.Constants.*;

public class NotExpression extends Expression { // NO_UCD (unused code)
	public static final String DESC = "returns true if the contained expression/attribute is false";
	public static final String NAME = "Not True";

	public NotExpression() {
		this(new FirebaseExpression());
	}

	public NotExpression(FirebaseExpression data) {
		super(data);
	}

	@Override
	public void setup() {
		name = NAME;
		description = DESC;
		this.varType = VAR_BOOLEAN;
		operand.setText("is false");
		receivers.add(new ExpressionReceiver(VAR_BOOLEAN));
	}
}
