package com.jeeves.vpl.canvas.receivers;

import java.util.Arrays;

import com.jeeves.vpl.ActionHolder;
import com.jeeves.vpl.Main;
import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ViewElement;

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

/**
 * A list of actions to be executed either in a trigger or as part of a
 * conditional statement. Each action list has a pane where the action panes are
 * contained, and a 'bracket' polygon surrounding it.
 *
 * @author Daniel
 */
@SuppressWarnings("rawtypes")
public class ActionReceiver extends Receiver {
	private Polygon brackets;
	private Path receiverPath; // The black bit
	private Path highlightPath;
	private ActionReceiver parentReceiver;
	private double[] defaultBracketValues;
	public ActionReceiver getInstance() {
		return this;
	}

	public void setParentReceiver(ActionReceiver parentReceiver) {
		this.parentReceiver = parentReceiver;
	}

	public Polygon getBrackets() {
		return brackets;
	}

	public ActionReceiver() {
		super();
		brackets = new Polygon();
		elements = new VBox();
		elements.setPadding(new Insets(10, 0, 0, 12));
		getChildren().add(brackets);
		getChildren().add(elements);
		elements.setLayoutX(13);
		captureRect.setHeight(40);
		elements.setLayoutY(0);
		defaultBracketValues = new double[]{0.0, 0.0, 180.0, 0.0, 180.0, 10.0, 25.0, 10.0, 25.0, 19.0, 11.0,
				19.0, 11.0, 31.0, 25.0, 31.0, 25.0, 40.0, 180.0, 40.0, 180.0, 50.0, 0.0, 50.0};

		Double[] wrappedDoubles = Arrays.stream(defaultBracketValues)
                .boxed()
                .toArray(Double[]::new);			
		brackets.getPoints().addAll(wrappedDoubles);
		elements.setMaxWidth(100);
		captureRect.setWidth(180);
		setMaxWidth(100);
		setPickOnBounds(false);
		elements.setPickOnBounds(false);
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
//
//	@Override
//	public void addChild(ViewElement child, double x, double y) {
//		// TODO Auto-generated method stub
//		super.addChild(child, x, y);
//	}
//	
	@Override
	public void addChildAtIndex(ViewElement child, int index){
		super.addChildAtIndex(child, index);
		addChildHandlers(child);
		if (child.getType() == ElementType.CTRL_ACTION) {
			((ActionHolder) child).getMyReceiver().setParentReceiver(this);
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				heightChanged(child.getHeight());
			}
		});
	}

	@Override
	public void defineHandlers() {
		super.defineHandlers();
		
		this.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent event) {
				event.consume();
				if(event.getEventType().equals(MouseEvent.MOUSE_ENTERED)){
					captureRect.setFill(Color.CYAN);
					Main.getContext().highlightMenu(ElementType.ACTION,true);

				}
				if(event.getEventType().equals(MouseEvent.MOUSE_EXITED)){
					captureRect.setFill(Color.DARKCYAN);
					Main.getContext().highlightMenu(ElementType.ACTION,false);

				}
			}
		
			
		});
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
	}

	private void addChildHandlers(ViewElement child) {

		child.setOnMouseDragExited(event -> {
			hoveredIndex = -1;
			highlightPath.getElements().clear();
			if (elements.contains(event.getX() + 20, event.getY()) && event.isPrimaryButtonDown()) {
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
		captureRect.setHeight(yPos + 30);
		
		ObservableList<Double> points = brackets.getPoints();
		int length = points.size();
		ObservableList<Double> bottombracket = FXCollections.observableList(points.subList(length - 16, length));
		for (int yPoint = 1; yPoint < 16; yPoint += 2)
			bottombracket.set(yPoint, defaultBracketValues[yPoint] + yPos+10);
	}

	@Override
	public ObservableList<ViewElement> getChildElements() {
		return childList;
	}

	private void heightChanged(double heightChange) {
//		captureRect.setHeight(captureRect.getHeight() + heightChange);
//		ObservableList<Double> points = brackets.getPoints();
//		int length = points.size();
//		ObservableList<Double> bottombracket = FXCollections.observableList(points.subList(length - 16, length));
//		for (int yPoint = 1; yPoint < 16; yPoint += 2)
//			bottombracket.set(yPoint, bottombracket.get(yPoint) + heightChange);
		redrawLine();

		if (parentReceiver != null) {
			parentReceiver.heightChanged(heightChange);
		}
		heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				if (parentReceiver == null)
					return;
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						parentReceiver.redrawLine();
						parentReceiver.requestLayout();
					}
				});
			}
		});
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

	@Override
	public void removeChild(ViewElement child) {
		child.setOnMouseDragExited(null);
		child.setOnMouseDragReleased(null);
		child.setOnMouseDragEntered(null);
		child.setManaged(false);
		if (child.getType() == ElementType.CTRL_ACTION) {
			((ActionHolder) child).getMyReceiver().setParentReceiver(null);
		}
		elements.getChildren().remove(child);
		childList.remove(child);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				heightChanged(-child.getHeight());
			}
		});
	}
}
