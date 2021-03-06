package com.jeeves.vpl.canvas.receivers;

import com.jeeves.vpl.Constants.ElementType;

import static com.jeeves.vpl.Constants.VAR_ANY;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.UserVariable;

/**
 * This is a simple extension of the ExpressionReceiver class, that only allows
 * Variables to be dragged into it, and not complex expressions
 *
 * @author Daniel
 */
public class VariableReceiver extends ExpressionReceiver {

	public VariableReceiver(String receiveType) {
		super(receiveType);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isValidElement(ViewElement dragged) {
		return (dragged.getType() == ElementType.VARIABLE && 
				(((UserVariable) dragged).getVarType() == getReceiveType() || getReceiveType().equals(VAR_ANY)));
				//&&
				//Can't edit those schedule variables
				//!Constants.getOpenProject().getscheduleAttrs().values().contains(((UserVariable)dragged).getName()));
	}
}
