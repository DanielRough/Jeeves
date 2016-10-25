package com.jeeves.vpl.canvas.receivers;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.expressions.UserVariable;

/**
 * This is a simple extension of the ExpressionReceiver class, that only allows Variables to be dragged into it,
 * and not complex expressions
 *
 * @author Daniel
 */
public class VariableReceiver extends ExpressionReceiver{

	public VariableReceiver(String receiveType) {
		super(receiveType);
	}
	@Override
	public boolean isValidElement(ViewElement dragged) {
		if (childList.get(0) != null && !(childList.get(0) instanceof UserVariable)) {
			return false;
		}
		if (!(dragged instanceof UserVariable))
			return false;
		if (dragged instanceof UserVariable && !((UserVariable) dragged).varType.equals(getReceiveType())
				&& !(getReceiveType().equals(Expression.VAR_ANY)))
			return false;
		return true;
	}
}
