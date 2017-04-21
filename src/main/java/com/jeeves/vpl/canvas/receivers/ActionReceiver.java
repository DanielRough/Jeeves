package com.jeeves.vpl.canvas.receivers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;

import com.jeeves.vpl.ActionHolder;
import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.actions.Action;

/**
 * A list of actions to be executed either in a trigger or as part of a
 * conditional statement. Each action list has a pane where the action panes are
 * contained, and a 'bracket' polygon surrounding it.
 *
 * @author Daniel
 */
@SuppressWarnings("rawtypes")
public class ActionReceiver extends Receiver{
	private VBox actions;
	private Polygon brackets;
	private Path receiverPath; // The black bit
	private Path highlightPath;
	private ActionReceiver parentReceiver;
	private int hoveredIndex = -1; 

	public ActionReceiver getInstance() {
		return this;
	}

	public VBox getActions() {
		return actions;
	}

	public void setParentReceiver(ActionReceiver parentReceiver){
		this.parentReceiver = parentReceiver;
	}
	public Polygon getBrackets(){
		return brackets;
	}

	public ActionReceiver() {
		super();
		brackets = new Polygon();
		actions = new VBox();
		actions.setPadding(new Insets(10, 0, 0, 12));
		getChildren().add(brackets);
		getChildren().add(actions);
		actions.setLayoutX(13);
		captureRect.setHeight(40);
		actions.setLayoutY(0);
		brackets.getPoints().addAll(new Double[] { 0.0, 0.0, 180.0, 0.0, 180.0, 10.0, 25.0, 10.0, 25.0, 19.0, 11.0,
				19.0, 11.0, 31.0, 25.0, 31.0, 25.0, 40.0, 180.0, 40.0, 180.0, 50.0, 0.0, 50.0 });
		actions.setMaxWidth(100);
		captureRect.setWidth(180);
		setMaxWidth(100);
		setPickOnBounds(false);
		actions.setPickOnBounds(false);
		brackets.getStyleClass().add("trigger");
		receiverPath = new Path();
		highlightPath = new Path();
		receiverPath.setStroke(Color.BLACK);
		highlightPath.setStroke(Color.ORANGE);
		highlightPath.setStrokeWidth(3);
		receiverPath.getElements().addAll(new MoveTo(180.0, 10.0), new LineTo(25.0, 10.0), new LineTo(25, 19),
				new LineTo(11, 19), new LineTo(11, 31), new LineTo(25, 31), new LineTo(25, 40));
		getChildren().add(receiverPath);
		getChildren().add(highlightPath);

	}

	@Override
	public void defineHandlers(){

		mentered = event -> {
			event.consume();
			if (!isValidElement((ViewElement) event.getGestureSource()))
				return;
			int lenElements = receiverPath.getElements().size();
			highlightPath.getElements().addAll(receiverPath.getElements().subList(lenElements - 7, lenElements));

		};
		mexited = event -> {
			event.consume();
			if (!isValidElement((ViewElement) event.getGestureSource()))
				return;
			highlightPath.getElements().clear();
		};
		mreleased = event -> {
			if (isValidElement((ViewElement) event.getGestureSource())){
				addChild((ViewElement) event.getGestureSource(),0,0);
				event.consume();

			}
		};
	}

	/**
	 * Add an action to the receiver
	 */
	@Override
	public void addChild(ViewElement child, double mouseX, double mouseY) {
		highlightPath.getElements().clear();
		Action newChild = (Action) child;
		if(newChild.getType() == ElementType.CTRL_ACTION){
			((ActionHolder)newChild).getMyReceiver().setParentReceiver(this);
		}

		if (hoveredIndex > -1) {
			actions.getChildren().add(hoveredIndex, newChild); 
			childList.add(hoveredIndex, newChild);
		} else {
			childList.add(newChild);
			actions.getChildren().add(newChild);
		}
		heightChanged(child.getHeight());
		
		child.setManaged(true); // So it sits in the appropriate place
		child.setMouseTransparent(false);

		EventHandler<MouseEvent> removeEvent = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent arg0) {
				ViewElement child = (ViewElement)arg0.getSource();
				removeChild(child);
				child.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
			}
		};
		child.addEventHandler(MouseEvent.MOUSE_PRESSED, removeEvent);

		child.setOnMouseDragExited(event -> {
			hoveredIndex = -1;
			highlightPath.getElements().clear();
			if (getActions().contains(event.getX() + 20, event.getY()) && event.isPrimaryButtonDown()) {
				mentered.handle(event);
			} else {
				mexited.handle(event);
			}
		});
		child.setOnMouseDragReleased(event -> {
			event.consume();
			captureRect.fireEvent(event);
		}); 
		child.setOnMouseDragEntered(event -> {
			event.consume();
			this.hoveredIndex = childList.indexOf(child);
			highlightPath.getElements().clear();
			int childOrder = childList.indexOf(child);
			if (childOrder < 0)
				return;
			if (!isValidElement((ViewElement) event.getGestureSource()))
				return;
			highlightPath.getElements()
			.addAll(receiverPath.getElements().subList((childOrder * 7 + 2), (childOrder * 7 + 8)));

		});
		// Is this an ActionHolder, like a control? If so, need a reference to
		// it for proper highlighting
		if (child instanceof ActionHolder) {
			ActionReceiver childReceiver = ((ActionHolder) child).getMyReceiver();
			childReceiver.setOnMouseDragEntered(event -> mexited.handle(event));
			childReceiver.setOnMouseDragExited(event -> {
					if (getActions().contains(event.getX() + 20, event.getY()) && event.isPrimaryButtonDown()) 
						mentered.handle(event);
					 else 
						mexited.handle(event);
				}
			);
			// And the line above should hopefully re-add the highlighting
			childReceiver.setOnMouseDragReleased(event -> highlightPath.getElements().clear());
		}
	}

	private void redrawLine() {

		receiverPath.getElements().removeAll(receiverPath.getElements()); 
		receiverPath.getElements().addAll(new MoveTo(25.0, 10.0), new LineTo(25, 10));
		LineTo lastline = new LineTo();
		double yPos = 0;
		for (Pane child : childList) {
			lastline = (LineTo) receiverPath.getElements().get(receiverPath.getElements().size() - 1);
			yPos = lastline.getY();
			receiverPath.getElements().addAll(new MoveTo(180, yPos), new LineTo(25, yPos), new LineTo(25, yPos + 9),
					new LineTo(11, yPos + 9), new LineTo(11, yPos + 21), new LineTo(25, yPos + 21),
					new LineTo(25, yPos + 30));
			if (child.getHeight() > 30) {
				receiverPath.getElements().remove(receiverPath.getElements().size() - 1);
				receiverPath.getElements().add(new LineTo(25.0, yPos + 30 + (child.getHeight() - 30))); 
			}
		}

		lastline = (LineTo) receiverPath.getElements().get(receiverPath.getElements().size() - 1);
		yPos = lastline.getY();
		receiverPath.getElements().addAll(new MoveTo(180, yPos), new LineTo(25, yPos), new LineTo(25, yPos + 9),
				new LineTo(11, yPos + 9), new LineTo(11, yPos + 21), new LineTo(25, yPos + 21),
				new LineTo(25, yPos + 30));
		hoveredIndex = -1;

	}

	@Override
	public ObservableList<ViewElement> getChildElements() {
		return childList;
	}

	public void heightChanged(double heightChange) {
		captureRect.setHeight(captureRect.getHeight() + heightChange);
		ObservableList<Double> points = brackets.getPoints();
		int length = points.size();
		ObservableList<Double> bottombracket = FXCollections.observableList(points.subList(length - 16, length));
		for (int yPoint = 1; yPoint < 16; yPoint += 2)
			bottombracket.set(yPoint, bottombracket.get(yPoint) + heightChange);
		redrawLine();
		
		if(parentReceiver != null){parentReceiver.heightChanged(heightChange);}
		heightProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> arg0,Number arg1, Number arg2) {
				if(parentReceiver == null)return;
				Platform.runLater(new Runnable(){
					public void run(){
						parentReceiver.redrawLine();
						parentReceiver.requestLayout(); 	
					}
				});			
			}});
	}

	/**
	 * Convenience method to check whether what we've dragged over the receiver
	 * is a valid action
	 */
	@Override
	public boolean isValidElement(ViewElement dragged) {
		if (!(dragged.getType() == ElementType.ACTION || dragged.getType() == ElementType.CTRL_ACTION))
			return false;
		return true;
	}

	/**
	 * Remove an action from the receiver, means that the bottom bracket has to
	 * be moved up, and the old slot removed
	 */
	@Override
	public void removeChild(ViewElement child) {

		child.setOnMouseDragExited(null);
		child.setOnMouseDragReleased(null); 
		child.setOnMouseDragEntered(null);
		childList.remove(child);
		actions.getChildren().remove(child);
		ObservableList<Double> points = brackets.getPoints();
		int length = points.size();
		FXCollections.observableList(points.subList(length - 16, length));
		if (childList.size() == 0) // Add the initial bit back in
			receiverPath.getElements().addAll(new MoveTo(180.0, 10.0), new LineTo(25.0, 10.0), new LineTo(25, 19),
					new LineTo(11, 19), new LineTo(11, 31), new LineTo(25, 31), new LineTo(25, 40));
		heightChanged(-child.getHeight());


	}
}
