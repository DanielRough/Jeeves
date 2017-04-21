package com.jeeves.vpl.canvas.receivers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import com.jeeves.vpl.ViewElement;
@SuppressWarnings("rawtypes")

public abstract class Receiver extends Pane{
	protected EventHandler<MouseDragEvent> mentered;
	protected EventHandler<MouseDragEvent> mexited;
	protected EventHandler<MouseDragEvent> mreleased;

	public Receiver(){
		captureRect = new Rectangle();
		getChildren().add(captureRect);
		captureRect.setOpacity(0.5);
		captureRect.setLayoutY(0);
		captureRect.setLayoutX(0);
		captureRect.toBack();
		captureRect.setFill(Color.DARKCYAN);
		defineHandlers();
		captureRect.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, mentered);
		captureRect.addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, mexited);
		captureRect.addEventFilter(MouseDragEvent.MOUSE_DRAG_RELEASED, mreleased);
	}

	public ObservableList<ViewElement> getChildElements() {
		return childList;
	}
	protected ObservableList<ViewElement> childList = FXCollections.observableArrayList();

	protected Rectangle captureRect; // The zone in which events are registered

	protected boolean handleEntered(MouseDragEvent event){
		setPickOnBounds(true);
		return (isValidElement((ViewElement) event.getGestureSource()));
	}
	protected boolean handleExited(MouseDragEvent event){
		event.consume();
		return (isValidElement((ViewElement) event.getGestureSource()));
	}
	protected boolean handleReleased(MouseDragEvent event){
		if (isValidElement((ViewElement) event.getGestureSource())){
			event.consume();
			ViewElement dragged = (ViewElement) event.getGestureSource();
			addChild(dragged, event.getSceneX(), event.getSceneY());
			dragged.toFront();
			return true;
		}
		return false;
	}
	public void defineHandlers(){
		mentered = event -> {
			handleEntered(event);
		};
		mexited = event -> {
			handleExited(event);
		};
		mreleased = event -> {
			handleReleased(event);
		};
	}
	public abstract void addChild(ViewElement element, double x, double y);
	public abstract void removeChild(ViewElement element);
	public abstract boolean isValidElement(ViewElement element);
}
