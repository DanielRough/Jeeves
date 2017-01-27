package com.jeeves.vpl.canvas.receivers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import com.jeeves.vpl.ActionHolder;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.actions.Action;
import com.jeeves.vpl.canvas.ifsloops.Control;

/**
 * A list of actions to be executed either in a trigger or as part of a
 * conditional statement. Each action list has a pane where the action panes are
 * contained, and a 'bracket' polygon surrounding it.
 *
 * @author Daniel
 */
public class ActionReceiver extends Pane implements IReceiver {
	private VBox actions;
	private Action parentAction;
	private Polygon brackets;
	private Path receiverPath; // The black bit
	private Path highlightPath;
	private Rectangle captureRect; // The zone in which events are registered
	private ObservableList<ViewElement> childList = FXCollections.observableArrayList();
	private EventHandler<MouseDragEvent> mexited;
	private EventHandler<MouseDragEvent> mentered;
	private EventHandler<MouseDragEvent> mreleased;
	private int hoveredIndex = -1; // If we've dragged over a particular child
									// in the list

	public ActionReceiver getInstance() {
		return this;
	}

	public VBox getActions() {
		return actions;
	}

	public void setParentAction(Action parentAction) {
		this.parentAction = parentAction;
	}

	public Action getParentAction() {
		return parentAction;
	}

	public ActionReceiver() {
		super();
		brackets = new Polygon();
		actions = new VBox();
		actions.setPadding(new Insets(10, 0, 0, 12));
		getChildren().add(brackets);
		getChildren().add(actions);
		actions.setLayoutX(13);
		actions.setLayoutY(0);
		brackets.getPoints().addAll(new Double[] { 0.0, 0.0, 180.0, 0.0, 180.0, 10.0, 25.0, 10.0, 25.0, 19.0, 11.0,
				19.0, 11.0, 31.0, 25.0, 31.0, 25.0, 40.0, 180.0, 40.0, 180.0, 50.0, 0.0, 50.0 });
		actions.setMaxWidth(100);
		captureRect = new Rectangle();
		getChildren().add(captureRect);
		captureRect.setLayoutY(0);
		captureRect.setLayoutX(0);
		captureRect.toBack();
		captureRect.setWidth(180);
		captureRect.setFill(Color.DARKCYAN);
		captureRect.setOpacity(0.3);
		actions.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				captureRect.setHeight((double) newValue + 30);
			}
		});
		getStylesheets().add(ViewElement.class.getResource("/ButtonsDemo.css").toExternalForm());
		setMaxWidth(100);
		setPickOnBounds(false);
		actions.setPickOnBounds(false);
		brackets.getStyleClass().add("rich-blue");
		receiverPath = new Path();
		highlightPath = new Path();
		receiverPath.setStroke(Color.BLACK);
		highlightPath.setStroke(Color.ORANGE);
		highlightPath.setStrokeWidth(3);
		receiverPath.getElements().addAll(new MoveTo(180.0, 10.0), new LineTo(25.0, 10.0), new LineTo(25, 19),
				new LineTo(11, 19), new LineTo(11, 31), new LineTo(25, 31), new LineTo(25, 40));
		getChildren().add(receiverPath);
		getChildren().add(highlightPath);
		
		
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
			if (isValidElement((ViewElement) event.getGestureSource()))
				event.consume();
			addElement((ViewElement) event.getGestureSource());
		};
		captureRect.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, mentered);
		captureRect.addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, mexited);
		captureRect.addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, mreleased);
	}

	public void addElement(ViewElement elem) {
		highlightPath.getElements().clear();
		getStyleClass().remove("hoverover");
		if (!isValidElement((ViewElement) elem))
			return;
		addChild(elem, 0, 0);
		//elem.toFront();
	}

	/**
	 * Add an action to the receiver
	 */
	@Override
	public void addChild(ViewElement child, double mouseX, double mouseY) {
		Action newChild = (Action) child;
		actions.getChildren().add(newChild); //Why don't I do thisx?
		newChild.setReceiver(this);
		if (hoveredIndex > -1) {
			childList.add(hoveredIndex, newChild);

		} else {
			childList.add(newChild);
		}
		child.setLayoutX(0);
		child.setLayoutY(0);
		child.setManaged(true); // So it sits in the appropriate place
		child.setMouseTransparent(false);

		redrawLine(); // Need to call this to update
		ChangeListener<Number> actionsHeightListener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if ((double) newValue - (double) oldValue < 10) {
					redrawLine();
					return;
				} // MERCILESS HACK BUT COULD BE ONTO SOMETHING
				//actions.getChildren().clear();
				//actions.getChildren().addAll(childList);

				redrawLine();

			}
		};
		ChangeListener<Number> childHeightListener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if ((double) newValue - (double) oldValue < 30) {
					redrawLine();
					return;
				} 
				//actions.getChildren().clear();
				//actions.getChildren().addAll(childList);
				redrawLine();

			}
		};
		actions.heightProperty().addListener(actionsHeightListener);
		child.heightProperty().addListener(childHeightListener);
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
		}); // Because the children are over it so it needs to be fired
			// explicitly
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
				{
					if (getActions().contains(event.getX() + 20, event.getY()) && event.isPrimaryButtonDown()) {
						mentered.handle(event);
					} else {
						mexited.handle(event);
					}
				}
			});
			// And the line above should hopefully re-add the highlighting
			childReceiver.setOnMouseDragReleased(event -> highlightPath.getElements().clear());
		}
		if (child.getHeight() > 0) {
			redrawLine();
			actions.getChildren().clear();
			actions.getChildren().addAll(childList);
		}

	}

	private void redrawLine() {
		receiverPath.getElements().removeAll(receiverPath.getElements()); 
		receiverPath.getElements().addAll(new MoveTo(25.0, 10.0), new LineTo(25, 10));
		LineTo lastline = new LineTo();
		double yPos = 0;
		for (ViewElement child : childList) {
			lastline = (LineTo) receiverPath.getElements().get(receiverPath.getElements().size() - 1);
			lastline.getX();
			yPos = lastline.getY();
			receiverPath.getElements().addAll(new MoveTo(180, yPos), new LineTo(25, yPos), new LineTo(25, yPos + 9),
					new LineTo(11, yPos + 9), new LineTo(11, yPos + 21), new LineTo(25, yPos + 21),
					new LineTo(25, yPos + 30));
			if (child.getHeight() > 30) {
				LineTo newLine = new LineTo(25.0, yPos + 30 + (child.getHeight() - 30));
				receiverPath.getElements().remove(receiverPath.getElements().size() - 1);
				receiverPath.getElements().add(newLine); // Add any additional
															// line space
			}
		}

		lastline = (LineTo) receiverPath.getElements().get(receiverPath.getElements().size() - 1);
		lastline.getX();
		yPos = lastline.getY();
		receiverPath.getElements().addAll(new MoveTo(180, yPos), new LineTo(25, yPos), new LineTo(25, yPos + 9),
				new LineTo(11, yPos + 9), new LineTo(11, yPos + 21), new LineTo(25, yPos + 21),
				new LineTo(25, yPos + 30));
		hoveredIndex = -1;

	}

	public void addStyle(String style) {
		brackets.getStyleClass().clear();
		brackets.getStyleClass().add(style);
	}

	/**
	 * Get the list of actions within this receiver
	 */
	@Override
	public ObservableList<ViewElement> getChildElements() {
		return childList;
	}

	/**
	 * Bit of magic number embedding here...This just moves the brackets up and
	 * down on receiving new elements
	 * 
	 * @param heightChange
	 */
	public void heightChanged(double heightChange) {
		ObservableList<Double> points = brackets.getPoints();
		int length = points.size();
		ObservableList<Double> bottombracket = FXCollections.observableList(points.subList(length - 16, length));
		for (int yPoint = 1; yPoint < 16; yPoint += 2)
			bottombracket.set(yPoint, bottombracket.get(yPoint) + heightChange);
	//	FXCollections.observableList(points.subList(6, 18));
	}

	/**
	 * Convenience method to check whether what we've dragged over the receiver
	 * is a valid action
	 */
	@Override
	public boolean isValidElement(ViewElement dragged) {
		if (!(dragged instanceof Action))
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
		child.setOnMouseDragReleased(null); // Because the children are over it
											// so it needs to be fired
											// explicitly
		child.setOnMouseDragEntered(null);
		ObservableList<Double> points = brackets.getPoints();

		actions.getChildren().remove(child);
		childList.remove(child);
		if (child instanceof Control) {
			((Control) child).setActionHolder(null);
		}
		((Action) child).setReceiver(null);
		getChildren().remove(child);
		int length = points.size();
		FXCollections.observableList(points.subList(length - 16, length));
		if (childList.size() == 0) // Add the initial bit back in
			receiverPath.getElements().addAll(new MoveTo(180.0, 10.0), new LineTo(25.0, 10.0), new LineTo(25, 19),
					new LineTo(11, 19), new LineTo(11, 31), new LineTo(25, 31), new LineTo(25, 40));
		redrawLine(); // Need to call this to update
		captureRect.autosize();
		actions.autosize();

	}
}
