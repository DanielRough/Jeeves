package com.jeeves.vpl;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import com.jeeves.vpl.canvas.receivers.ActionReceiver;

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
	Pane rectPane = new Pane();
	
	protected ActionReceiver receiver;
	protected double initialHeight;
	protected ActionHolder parent;

	
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
	
	void addEventHandlers(){

		EventHandler<MouseDragEvent> dragHandler = event -> {
			
			if(event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_ENTERED)){
				isMouseOver = true;
			}
			else if(event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_EXITED)){
				isMouseOver = false;
			}
			if (event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_RELEASED)) {
				if (!(getChildren().contains(event.getGestureSource()))) {
					ViewElement dragged = (ViewElement) event.getGestureSource();
					if (dragged instanceof ViewElement){
						Point2D releasePoint = sceneToLocal(event.getSceneX(),event.getSceneY());
						addChild(dragged, releasePoint.getX(), releasePoint.getY());
					}
				}
			}
		};
		this.addEventHandler(MouseDragEvent.ANY, dragHandler);

		//A mouse handler that allows us to pan the canvas
		EventHandler<MouseEvent> mouseHandler = event -> {
			if(event.isSecondaryButtonDown()){
				event.consume();
				return;
			}
			if(event.getEventType().equals(MouseEvent.MOUSE_ENTERED)){
				isMouseOver = true;
			}
			else if(event.getEventType().equals(MouseEvent.MOUSE_MOVED)){
				isMouseOver = true;
		}
			else if(event.getEventType().equals(MouseEvent.MOUSE_EXITED)){
				isMouseOver = false;
			}
			else if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
				isMouseOver = true;
				requestFocus();
				mousepressed = true;
				pressedX = event.getX();
				pressedY = event.getY();
				if (minX == 0) {
					minPoint = localToScreen(parentToLocal(0,0)); //Dumb
					minX = minPoint.getX();
					minY = minPoint.getY();
				}
				translatedX = getTranslateX();
				translatedY = getTranslateY();
			} else if (mousepressed && event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
				Point2D newpoint = localToScreen(0, 0);
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
			child.addEventHandler(MouseEvent.MOUSE_PRESSED, event->isMouseOver = true); //When we press a child on the canvas, this ensures that 'isMouseOver' is still set to true

		}

	}

	void removeChild(ViewElement child) {

		getChildren().remove(child);
		currentChildren.remove(child);

	}
}
