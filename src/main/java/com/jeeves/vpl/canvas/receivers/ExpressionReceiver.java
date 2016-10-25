package com.jeeves.vpl.canvas.receivers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.expressions.UserVariable;

/**
 * This will be a class that allows nodes within it to be dragged back and forth
 * within the same pane, rather than adding them to the end all the time. It
 * should be easy to implement as it bears a very storng resemblance to the
 * ActionReceiver class
 *
 * @author Daniel
 */
public class ExpressionReceiver extends StackPane implements IReceiver {

	private String receiveType;
	protected Rectangle captureRect;
	public TextField text = new TextField(); 
	public String value; //The value is for when we don't have an Expression in the receiver
	protected ObservableList<ViewElement> childList = FXCollections.observableArrayList();

	public ExpressionReceiver getInstance() {
		return this;
	}

	public TextField getTextField(){
		return text;
	}
	public String getText() {
			return text.getText();
	}

	public String padWithZeroes(int number){
		if(number>9)
			return Integer.toString(number);
		else
			return "0" + Integer.toString(number);
	}
	public void setText(String newtext) {
		text.setText(newtext);
	}

	public ExpressionReceiver(String receiveType) {
		super();
		getChildren().add(text);
		text.setPrefWidth(20);
		this.receiveType = receiveType;
		this.setAlignment(Pos.CENTER);
		captureRect = new Rectangle();
		getChildren().add(captureRect);
		captureRect.setWidth(20);
		captureRect.setHeight(20);
		setReceiveType(this.receiveType);
		
		captureRect.setOpacity(0.5);
		childList.add(null); // So its size is always 1
		EventHandler<MouseDragEvent> mentered = event -> {
			setPickOnBounds(true);
			if (!isValidElement((ViewElement) event.getGestureSource()))
				return;
			captureRect.setOpacity(1);
			captureRect.setFill(Color.ORANGE);

		};
		EventHandler<MouseDragEvent> mexited = event -> {
			event.consume();
			if (!isValidElement((ViewElement) event.getGestureSource()))
				return;
			captureRect.setOpacity(0.5);
			setReceiveType(this.receiveType); //Defaults it back to what it was
		};
		EventHandler<MouseDragEvent> mreleased = event -> {
			if (isValidElement((ViewElement) event.getGestureSource()))

			event.consume();
			if (!isValidElement((ViewElement) event.getGestureSource()))
				return;
			captureRect.setOpacity(0.5);
			setReceiveType(this.receiveType); //Defaults it back to what it was


			ViewElement dragged = (ViewElement) event.getGestureSource();
			addChild(dragged, 0, 0);
			dragged.toFront();
		};
		captureRect.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, mentered);
		captureRect.addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, mexited);
		captureRect.addEventFilter(MouseDragEvent.MOUSE_DRAG_RELEASED, mreleased);
	}

	@Override
	public void addChild(ViewElement expression, double mouseX, double mouseY) {
		getChildren().add(expression);
		((Expression) expression).setReceiver(this);
		expression.setManaged(true);
		text.toBack();
		captureRect.toBack();
		childList.set(0, expression);
		expression.setMouseTransparent(false);
		getStyleClass().clear();
		text.setPrefWidth(expression.getWidth());
		captureRect.setWidth(expression.getWidth());
		expression.setOnDragDetected(null);
		expression.setOnMouseReleased(null);


	}

	@Override
	public ObservableList<ViewElement> getChildElements() {
		return childList;
	}

	@Override
	public boolean isValidElement(ViewElement dragged) {
		//TEMPORARY FIX
		if(getReceiveType().equals(Expression.VAR_CLOCK))
			return false;
		//TEMPORARY FIX
		if (childList.get(0) != null && !(childList.get(0) instanceof UserVariable)) {
			return false;
		}
		if (!(dragged instanceof Expression))
			return false;
		if (dragged instanceof Expression && !((Expression) dragged).varType.equals(getReceiveType())
				&& !(getReceiveType().equals(Expression.VAR_ANY))){
			return false;
		}
		return true;
	}

	@Override
	public void removeChild(ViewElement expression) {
		childList.set(0, null);
		captureRect.setOpacity(0.5);
		captureRect.toFront();
		((Expression) expression).setReceiver(null);
		expression.addEventHandler(MouseEvent.ANY, expression.mainHandler);
		expression.setOnDragDetected(expression.draggedHandler);
		expression.setOnMouseReleased(expression.releasedHandler);
		getChildren().remove(expression);
		text.setPrefWidth(TextUtils.computeTextWidth(text.getFont(), text.getText(), 0.0D) + 15);
		captureRect.setWidth(text.getPrefWidth() + 6);
		autosize();
	}

	public void setReceiveType(String type) {
		receiveType = type;
		if (receiveType.equals(Expression.VAR_BOOLEAN)) {
			captureRect.setArcWidth(20);
			captureRect.setArcHeight(20);
			captureRect.setFill(Color.DARKCYAN);
			text.setVisible(false);

		} 

		if (receiveType.equals(Expression.VAR_LOCATION)){
			captureRect.setArcWidth(20);
			captureRect.setArcHeight(20);
			text.setVisible(false);

		}
		if (receiveType.equals(Expression.VAR_NONE)){
			captureRect.setFill(Color.BLACK);
		}
		if (receiveType.equals(Expression.VAR_ANY)){
			captureRect.setFill(Color.BLACK);
			captureRect.setArcWidth(0);
			captureRect.setArcHeight(0);
			text.setVisible(true);

		}
		if (receiveType.equals(Expression.VAR_NUMERIC)) {
			captureRect.setFill(Color.LIMEGREEN);
			this.setOnMouseDragged(event -> {
				event.consume();
			}); // STOPS IT DISAPPEARING WHEN DRAGGED
			text.toBack();
			text.setPrefWidth(20);
			captureRect.setOnMousePressed(event -> {
				event.consume();
				text.toFront();
				text.requestFocus();
			});
			ChangeListener<String> textChanged = new ChangeListener<String>() {

				@Override
				public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
					text.setPrefWidth(TextUtils.computeTextWidth(text.getFont(), text.getText(), 0.0D) + 10);
					captureRect.setWidth(text.getPrefWidth() + 6);
					autosize();
					value = text.getText();
				}

			};
			text.focusedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
					if (arg2 == false)
						captureRect.toFront();
				}

			});
			text.textProperty().addListener(textChanged);
		}
		if (receiveType.equals(Expression.VAR_LOCATION)) {
			captureRect.setFill(Color.RED);
			this.setOnMouseDragged(event -> {
				event.consume();
			}); // STOPS IT DISAPPEARING WHEN DRAGGED
			
		}
	}

	public String getValue(){
		return value;
	}
	public String getReceiveType() {
		return receiveType;
	}

}