package com.jeeves.vpl.canvas.receivers;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.canvas.actions.Control;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.survey.questions.AnswerControl;

import javafx.application.Platform;

public class QuestionReceiver extends ActionReceiver{

	@Override
	public void addChildAtIndex(ViewElement<?> child, int index){
		super.addChildAtIndex(child, index);
		addChildHandlers(child);
		
		if (child.getType() == ElementType.CTRL_QUESTION) {
			((AnswerControl) child).getMyReceiver().setParentReceiver(this);
		}
		Platform.runLater(()->{
				heightChanged(child.getHeight());
				
		}
		);
	}
	/**
	 * Convenience method to check whether what we've dragged over the receiver
	 * is a valid action
	 */
	@Override
	public boolean isValidElement(ViewElement<?> dragged) {
		return (dragged.getType() == ElementType.QUESTION || dragged.getType() == ElementType.CTRL_QUESTION);
	}
	
	@Override
	public void removeChild(ViewElement<?> child) {
		child.setOnMouseDragExited(null);
		child.setOnMouseDragReleased(null);
		child.setOnMouseDragEntered(null);
		child.setManaged(false);
		if (child.getType() == ElementType.CTRL_QUESTION) {
			((AnswerControl) child).getMyReceiver().setParentReceiver(null);
		}
		elements.getChildren().remove(child);
		childList.remove(child);
		Platform.runLater(() ->
				heightChanged(-child.getHeight())
		);
	}
}
