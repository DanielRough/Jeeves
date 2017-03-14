package com.jeeves.vpl;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import com.jeeves.vpl.canvas.receivers.ActionReceiver;
import com.jeeves.vpl.canvas.uielements.UIElement;

public class ViewCanvas extends Group{ // NO_UCD (use default)
	private Rectangle bigRect = new Rectangle(-10000, -10000, 20000, 20000);
	private ObservableList<ViewElement> currentChildren = FXCollections.observableArrayList();
	private boolean mousepressed = false;
	private double translatedX = 0;
	private double translatedY = 0;
	private double pressedX = 0;
	private double pressedY = 0;
	private Point2D minPoint;
	private double minX = 0;
	private double minY = 0;
	private Pane rectPane = new Pane();
	
	private ActionReceiver receiver;
	private double initialHeight;
	private ActionHolder parent;

	
	private boolean isMouseOver;
	public ViewCanvas(){
	}
	
	public void setIsMouseOver(boolean mouse){
		this.isMouseOver = mouse;
	}
	public boolean getIsMouseOver(){
		return isMouseOver;
	}
	void addChildrenListener(ListChangeListener<Node> list){
		getChildren().addListener(list);
	}
	public EventHandler<MouseEvent> mouseHandler;
	void addEventHandlers(){
		EventHandler<ScrollEvent> scrollHandler = event -> {
			if(mousepressed)return; //Does dodgy things when you try to click and scroll simultaneously
			event.consume();
			Point2D newpoint = localToScreen(0, 0);
			System.out.println("NEW POINT IS " + newpoint.getX() + "," + newpoint.getY());
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
			if ((newpoint.getX() > minX && deltaY > 0) || (newpoint.getY() > minY) && deltaY > 0)
				return;
			if (getScaleX() > 5 && deltaY < 0)
				return;
			if (getScaleX() < 0.5 && deltaY > 0)
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
		
		EventHandler<MouseDragEvent> dragHandler = event -> {
			
			if(event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_ENTERED)){
				isMouseOver = true;
			//	System.out.println("WHAT ABOUT THIS");
			}
			else if(event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_EXITED)){
			//	System.out.println("THIS HAS HAPPENED");
				isMouseOver = false;
			}
			if (event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_RELEASED)) {
				System.out.println("AAAARGH");
				if (event.getGestureSource() instanceof ViewElement && !(event.getGestureSource() instanceof UIElement) && !(getChildren().contains(event.getGestureSource()))) {
					ViewElement dragged = (ViewElement) event.getGestureSource();
					System.out.println("WTF");
					if (dragged instanceof ViewElement){
						Point2D releasePoint = sceneToLocal(event.getSceneX(),event.getSceneY());
						addChild(dragged, releasePoint.getX(), releasePoint.getY());
					}
				}
			}
		};
		this.addEventFilter(MouseDragEvent.ANY, dragHandler);
		if(mouseHandler != null)
		getParent().removeEventHandler(MouseEvent.ANY, mouseHandler);

		//A mouse handler that allows us to pan the canvas
		mouseHandler = event -> {
			if(event.isSecondaryButtonDown()){
				event.consume();
				return;
			}
			if(event.getEventType().equals(MouseEvent.MOUSE_ENTERED)){
				isMouseOver = true;
				System.out.println("MY parent is " + getParent());

			}
			else if(event.getEventType().equals(MouseEvent.MOUSE_MOVED)){
				isMouseOver = true;
		}
			else if(event.getEventType().equals(MouseEvent.MOUSE_EXITED)){
				isMouseOver = false;
			}
			else if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
				System.out.println("MY parent is " + getParent());
				isMouseOver = true;
				requestFocus();
				mousepressed = true;
				System.out.println("PRESSED");
				pressedX = event.getX();
				pressedY = event.getY();
				System.out.println(localToScreen(0,0).getX());
				if (minX == 0) {
					minPoint = localToScreen(parentToLocal(0,0)); //Dumb
					minX = minPoint.getX();
					minY = minPoint.getY();
				}
				translatedX = getTranslateX();
				translatedY = getTranslateY();
			} else if (mousepressed && event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
				Point2D newpoint = localToScreen(0, 0);
				if(newpoint == null)System.out.println("NULL NULL");
				if (newpoint.getX() > minX && event.getX() - pressedX >= 0) {
					pressedX = event.getX();
					pressedY = event.getY();
					return;
				}
				if (newpoint.getY() > minY && event.getY() - pressedY >= 0) {
					pressedX = event.getX();
					pressedY = event.getY();
					return;
				}
				setTranslateX(translatedX + event.getX() - pressedX);
				setTranslateY(translatedY + event.getY() - pressedY);
			}
			else if(event.getEventType().equals(MouseEvent.MOUSE_RELEASED))
				mousepressed = false;
			event.consume();
		};
		
		//System.out.println("My parent is " + getParent());
		getParent().addEventHandler(MouseEvent.ANY, mouseHandler);
		setTranslateX(-5000);
		setTranslateY(-5000);
		this.toBack();
		
		bigRect = new Rectangle(0, 0, 20000, 20000);
		bigRect.setFill(Color.TRANSPARENT);
		rectPane.getChildren().add(bigRect);
		getChildren().add(rectPane);
		rectPane.getStyleClass().add("blueprint");
		rectPane.toBack();
		System.out.println("My parent EVERYWHEREis " + getParent());

	}


	void addChild(ViewElement child, double mouseX, double mouseY) {
		child.setMouseTransparent(false);
		if (!getChildren().contains(child)) {
			child.setLayoutX(mouseX);
			child.setLayoutY(mouseY);
			child.setPosition(new Point2D(mouseX,mouseY));
			getChildren().add(child);
			child.toFront();
			currentChildren.add(child);
			System.out.println("My parent HERE is " + getParent());

			child.addEventHandler(MouseEvent.MOUSE_PRESSED, event->isMouseOver = true); //When we press a child on the canvas, this ensures that 'isMouseOver' is still set to true

		}

	}

	void removeChild(ViewElement child) {

		getChildren().remove(child);
		currentChildren.remove(child);
		System.out.println("My parent THERE is " + getParent());

	}
}
