package com.jeeves.vpl.canvas.receivers;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class Receiver extends DragPane {
	protected Rectangle captureRect; // The zone in which events are registered
	@SuppressWarnings("rawtypes")
	protected ObservableList<ViewElement> childList = FXCollections.observableArrayList();
	protected VBox elements;
	protected int hoveredIndex = -1;
	protected EventHandler<MouseDragEvent> mentered;

	protected EventHandler<MouseDragEvent> mexited;

	protected EventHandler<MouseDragEvent> mreleased;

	public Receiver() {
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

	@Override
	public void addChild(ViewElement<?> child, double x, double y) {
		hoveredIndex = 0;
		Point2D point = elements.sceneToLocal(x, y);
		for (int i = 0; i < elements.getChildren().size(); i++) {
			Pane elem = ((Pane) elements.getChildren().get(i));
			double max = (elem.getLayoutY() + elem.getHeight());
			if (point.getY() < max) {
				break;
			}
			hoveredIndex = i + 1;
		}
		addChildAtIndex(child, hoveredIndex);

	}

	// Another method for when we KNOW the index it has to go to
	public void addChildAtIndex(ViewElement<?> child, int index) {
		if (index > -1) {
			elements.getChildren().add(index, child);
			childList.add(index, child);

		} else {
			elements.getChildren().add(child);
			childList.add(child);
		}

		child.setManaged(true); // So it sits in the appropriate place
		child.setMouseTransparent(false);
		EventHandler<MouseEvent> removeHandler = e->{
				e.consume();
				if (e.isSecondaryButtonDown()) {
					return;
				}
				child.setOnMousePressed(null);

				child.setOnMouseReleased(handler -> {
					if ((child.getType() == ElementType.QUESTION || child.getType() == ElementType.UIELEMENT))
						addChildAtIndex(child, child.getOldIndex());
				}); 

				removeChild(child);
			
		};
		child.setOnMousePressed(removeHandler);
		child.setOnMouseDragReleased(event -> {
			event.consume();
			captureRect.fireEvent(event);
		});
	}

	public void defineHandlers() {
		mentered = this::handleEntered;
		mexited = this::handleExited;
		mreleased = this::handleReleased;
	}

	@SuppressWarnings("rawtypes")
	public ObservableList<ViewElement> getChildElements() {
		return childList;
	}

	public abstract boolean isValidElement(ViewElement<?> element);

	@Override
	public abstract void removeChild(ViewElement<?> element);

	protected boolean handleEntered(MouseDragEvent event) {
		setPickOnBounds(true);
		return (isValidElement((ViewElement<?>) event.getGestureSource()));
	}

	protected boolean handleExited(MouseDragEvent event) {
		event.consume();
		return (isValidElement((ViewElement<?>) event.getGestureSource()));
	}

	protected boolean handleReleased(MouseDragEvent event) {
		if (isValidElement((ViewElement<?>) event.getGestureSource())) {
			//Here we want to check if we dragged our question at all. If we didn't, just add it back where it was
			addChild((ViewElement<?>) event.getGestureSource(), event.getSceneX(), event.getSceneY());
			event.consume();
			return true;
		}
		return false;
	}
}
