package com.jeeves.vpl.canvas.receivers;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ViewElement;
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

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isValidElement(ViewElement dragged) {
		if(dragged.getType() == ElementType.VARIABLE){
			if(((UserVariable)dragged).getVarType() == getReceiveType())
					return true;
		}
		return false;
	}
}
