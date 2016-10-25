package com.jeeves.vpl.canvas.receivers;

import javafx.collections.ObservableList;

import com.jeeves.vpl.ViewElement;


/**
 * The purpose of having a factory for the Receiver class is that some receivers need to be normal panes (i.e. ActionReceivers) and others
 * need to be subclasses (i.e. ExpressionReceivers, which are StackPanes)
 * @author Daniel
 *
 */
public interface IReceiver { // NO_UCD (use default)

	public void addChild(ViewElement child, double mouseX, double mouseY);

	public ObservableList<ViewElement> getChildElements();

	public boolean isValidElement(ViewElement element);

	public void removeChild(ViewElement child);
}
