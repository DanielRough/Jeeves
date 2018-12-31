package com.jeeves.vpl;

import java.util.List;

import com.jeeves.vpl.canvas.uielements.UIElement;
import com.jeeves.vpl.survey.questions.QuestionView;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

@SuppressWarnings("rawtypes")

public class ViewCanvas extends DragPane { 
	EventHandler<MouseEvent> mouseHandler;
	private EventHandler<MouseDragEvent> dragHandler;
	//For making the scrollable background 'blueprint' image
	private Pane rectPane = new Pane();

	private double minX = 0;
	private double minY = 0;
	private boolean mousepressed = false;
	private double pressedX = 0;
	private double pressedY = 0;
	private double translatedX = 0;
	private double translatedY = 0;

	private void makeDragHandler() {
		 dragHandler = event -> {
			if (event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_RELEASED)
					&& event.getGestureSource() instanceof ViewElement && !(event.getGestureSource() instanceof UIElement)
						&& !(event.getGestureSource() instanceof QuestionView)
						&& !(getChildren().contains(event.getGestureSource()))
						&& (ViewElement) event.getGestureSource() instanceof ViewElement) {
					ViewElement dragged = (ViewElement) event.getGestureSource();
						addChild(dragged, event.getSceneX(), event.getSceneY());
					}
				
		};
	}
	private void makeMouseHandler(){
		// A mouse handler that allows us to pan the canvas
		mouseHandler = event -> {
			if (event.isSecondaryButtonDown()) {
				event.consume();
				return;
			}
			if (minX == 0) {
				minX = localToScreen(parentToLocal(0, 0)).getX();
				minY = localToScreen(parentToLocal(0, 0)).getY();
			}
			if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
				requestFocus();
				mousepressed = true;
				pressedX = event.getX();
				pressedY = event.getY();

				translatedX = getTranslateX();
				translatedY = getTranslateY();
			} else if (mousepressed && event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
				Point2D newpoint = localToScreen(0, 0);
				if ((newpoint.getX() > minX && event.getX() - pressedX >= 0) || (newpoint.getY() > minY && event.getY() - pressedY >= 0)) {
					pressedX = event.getX();
					pressedY = event.getY();
					return;
				}
				setTranslateX(translatedX + event.getX() - pressedX);
				setTranslateY(translatedY + event.getY() - pressedY);
			} else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
				mousepressed = false;
			}
			event.consume();
		};
	}
	
	@Override
	public void addChild(ViewElement child, double mouseX, double mouseY) {

		child.setMouseTransparent(false);
		if (!getChildren().contains(child)) {

			Point2D canvasPoint = sceneToLocal(new Point2D(mouseX, mouseY));
			child.setLayoutX(canvasPoint.getX());
			child.setLayoutY(canvasPoint.getY());
			child.setPosition(canvasPoint);
			getChildren().add(child);
			child.toFront();
		}

	}

	@Override
	public void removeChild(ViewElement child) {
		getChildren().remove(child);
	}

	void addChildrenListener() {
		getChildren().addListener((javafx.collections.ListChangeListener.Change<? extends Node> arg0)->{
			while (arg0.next()) {
				if (arg0.wasAdded()) {
					List<?> addedlist = arg0.getAddedSubList();
					Constants.getOpenProject().add((ViewElement<?>) addedlist.get(0));
				} else if (arg0.wasRemoved()) {
					List<?> removedlist = arg0.getRemoved();
					Constants.getOpenProject().remove((ViewElement<?>) removedlist.get(0));

				}
			}
		});
	}

	void addEventHandlers() {
		EventHandler<ScrollEvent> scrollHandler = event -> {
			if (mousepressed)
				return; // Does dodgy things when you try to click and scroll
						// simultaneously
			event.consume();
			Point2D newpoint = localToScreen(0, 0);
			double mousex = event.getX();
			double mousey = event.getY();
			final Point2D zoomPos = localToParent(mousex, mousey);
			final Bounds bounds = this.getBoundsInParent();
			final double groupWidth = bounds.getWidth();
			final double groupHeight = bounds.getHeight();

			double zoomFactor = 0.9;
			double deltaY = event.getDeltaY();

			if (deltaY < 0)
				zoomFactor = 2.0 - zoomFactor;
			if ((newpoint.getX() > minX && deltaY > 0)
				|| (getScaleX() > 5 && deltaY < 0)
				|| (getScaleX() < 0.5 && deltaY > 0)
				|| (newpoint.getY() > minY && deltaY > 0))
				return;
			final double dw = groupWidth * (zoomFactor - 1);
			final double xr = 2 * (groupWidth / 2 - (zoomPos.getX() - bounds.getMinX())) / groupWidth;
			final double dh = groupHeight * (zoomFactor - 1);
			final double yr = 2 * (groupHeight / 2 - (zoomPos.getY() - bounds.getMinY())) / groupHeight;

			setScaleX(getScaleX() * zoomFactor);
			setScaleY(getScaleY() * zoomFactor);
			setTranslateX(getTranslateX() + xr * dw / 2);
			setTranslateY(getTranslateY() + yr * dh / 2);
		};
		setOnScroll(scrollHandler);

		makeDragHandler();
		this.addEventFilter(MouseDragEvent.ANY, dragHandler);
		if(mouseHandler != null)
			getParent().removeEventHandler(MouseEvent.ANY, mouseHandler);
		makeMouseHandler();
		getParent().addEventHandler(MouseEvent.ANY, mouseHandler);
		setTranslateX(-5000);
		setTranslateY(-5000);
		this.toBack();
		Rectangle bigRect = new Rectangle(0, 0, 20000, 20000);
		bigRect.setFill(Color.TRANSPARENT);
		rectPane.getChildren().add(bigRect);
		getChildren().add(rectPane);
		rectPane.getStyleClass().add("blueprint");
		rectPane.toBack();
		
		addChildrenListener();
	}
}
