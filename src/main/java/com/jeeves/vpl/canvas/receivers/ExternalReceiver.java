package com.jeeves.vpl.canvas.receivers;

import javafx.scene.layout.VBox;
import com.jeeves.vpl.ViewElement;

public abstract class ExternalReceiver extends Receiver{

	
	protected VBox container;

	public ExternalReceiver() {
		elements = new VBox();
		container = new VBox();
	}
	

	public abstract void addChildListeners();

	@Override
	public abstract boolean isValidElement(ViewElement<?> element);

	@Override
	public void removeChild(ViewElement<?> child) {
		child.setOnMouseDragExited(null);
		child.setOnMouseDragReleased(null);
		child.setOnMouseDragEntered(null);
		child.setManaged(false);
		elements.getChildren().remove(child);
		childList.remove(child);
	}

}
