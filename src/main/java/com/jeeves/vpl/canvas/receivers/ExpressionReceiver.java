package com.jeeves.vpl.canvas.receivers;

import static com.jeeves.vpl.Constants.VAR_ANY;
import static com.jeeves.vpl.Constants.VAR_BOOLEAN;
import static com.jeeves.vpl.Constants.VAR_LOCATION;
import static com.jeeves.vpl.Constants.VAR_BLUETOOTH;
import static com.jeeves.vpl.Constants.VAR_WIFI;
import static com.jeeves.vpl.Constants.VAR_NONE;
import static com.jeeves.vpl.Constants.VAR_NUMERIC;
import static com.jeeves.vpl.Constants.numberHandler;

import com.jeeves.vpl.Main;
import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.TextUtils;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.expressions.Typed;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.firebase.FirebaseExpression;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * This will be a class that allows nodes within it to be dragged back and forth
 * within the same pane, rather than adding them to the end all the time. It
 * should be easy to implement as it bears a very storng resemblance to the
 * ActionReceiver class
 *
 * @author Daniel
 */
@SuppressWarnings("rawtypes")

public class ExpressionReceiver extends Receiver {

	protected Expression containedExpression;
	// in the receiver
	protected double defaultOpacity;
	TextField numericTextField;

							protected String receiveType;
	protected String value; // The value is for when we don't have an Expression

	public ExpressionReceiver(String receiveType) {
		super();
		captureRect.setWidth(20);
		captureRect.setHeight(20);
		defaultOpacity = 0.5;
		numericTextField = new TextField();
		setReceiveType(receiveType);
		if (receiveType.equals(VAR_NUMERIC))
			defaultOpacity = 0.0; // it's different for numbers
	}

	public TextField getTextField(){
		return numericTextField;
	}
	@Override
	public void addChild(ViewElement expression, double mouseX, double mouseY) {
		if(((Expression)expression).getModel().getisValue()	){
			numericTextField.setText(((Expression)expression).getModel().getvalue());
			return;
		}
		getChildren().add(expression);
		expression.setLayoutX(0);
		expression.setLayoutY(0);
		expression.setManaged(true);
		containedExpression = (Expression) expression;
		childList.add(expression);
		EventHandler<MouseEvent> removeEvent = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				ViewElement child = (ViewElement) arg0.getSource();
				removeChild(child);

				child.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
			}

		};
		expression.addEventHandler(MouseEvent.MOUSE_PRESSED, removeEvent);
		expression.setMouseTransparent(false);
		getStyleClass().clear();
		expression.removeAllHandlers();
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
					Main.getContext().highlightMenu(ElementType.EXPRESSION,true);

				}
				if(event.getEventType().equals(MouseEvent.MOUSE_EXITED)){
					captureRect.setFill(Color.DARKCYAN);
					Main.getContext().highlightMenu(ElementType.EXPRESSION,false);

				}
			}
		
			
		});
		mentered = event -> {
			event.consume();
			if (!isValidElement((ViewElement) event.getGestureSource()))
				return;
			getStyleClass().add("drop_shadow");
		};
		mexited = event -> {
			event.consume();
			if (!isValidElement((ViewElement) event.getGestureSource()))
				return;
			getStyleClass().remove("drop_shadow");
		};
	}

	public ViewElement getChildExpression() {
		return containedExpression;
	}

	public FirebaseExpression getChildModel() {
		return containedExpression == null ? null : (FirebaseExpression) containedExpression.getModel();
	}

	public String getReceiveType() {
		return receiveType;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean isValidElement(ViewElement dragged) {
		if (dragged.getType() == ElementType.EXPRESSION || dragged.getType() == ElementType.VARIABLE) {

			if (((Typed) dragged).getVarType().equals(getReceiveType()) || getReceiveType().equals(VAR_ANY))
				return true;
		}
		return false;
	}

	@Override
	public void removeChild(ViewElement expression) {
		containedExpression = null;

		childList.remove(0);
		captureRect.setOpacity(defaultOpacity);
		captureRect.toFront();
		expression.addAllHandlers();
		getChildren().remove(expression);
		captureRect.setWidth(20);
		autosize();
	}

	// Conditional formatting
	public void setReceiveType(String type) {
		this.receiveType = type;
		switch (type) {
		case VAR_BOOLEAN:
			captureRect.setArcWidth(20);
			captureRect.setArcHeight(20);
			captureRect.setFill(Color.DARKCYAN);
			break;
		case VAR_LOCATION:
		case VAR_BLUETOOTH:
		case VAR_WIFI:
			captureRect.setArcWidth(20);
			captureRect.setArcHeight(20);
			captureRect.setFill(Color.THISTLE);
			break;
		case VAR_NONE:
			captureRect.setFill(Color.BLACK);
			break;
		case VAR_ANY:
			captureRect.setFill(Color.BLACK);
			captureRect.setArcWidth(0);
			captureRect.setArcHeight(0);
			break;
		case VAR_NUMERIC:
			captureRect.setOpacity(0);
			numericTextField.setPrefWidth(20);
			numericTextField.setMinHeight(20);
			numericTextField.setPrefHeight(20);
			numericTextField.getStyleClass().add("textfield");
			getChildren().add(numericTextField);

			numericTextField.toBack();
			captureRect.setOnMousePressed(handler -> {
				handler.consume();
				numericTextField.toFront();
				numericTextField.requestFocus();
			});

			this.getChildren().addListener(new ListChangeListener<Node>() {

				@Override
				public void onChanged(Change c) {
					c.next();
					if (c.wasAdded() && c.getAddedSubList().get(0) instanceof ViewElement) {
						numericTextField.setText("");
						numericTextField.setPrefWidth(20);
					}
				}

			});
			numericTextField.addEventFilter(KeyEvent.KEY_TYPED, numberHandler);
			ChangeListener<String> textChanged = new ChangeListener<String>() {

				@Override
				public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
					numericTextField.setPrefWidth(
							TextUtils.computeTextWidth(numericTextField.getFont(), numericTextField.getText(), 0.0D)
									+ 10);
					captureRect.setWidth(numericTextField.getPrefWidth() + 6);
					autosize();
					value = numericTextField.getText();
					FirebaseExpression model = new FirebaseExpression();
					model.setIsValue(true);
					model.setvalue(value);
					model.setVartype("");
					UserVariable var = UserVariable.create(model);
					addChild(var,0,0);
				}

			};
			numericTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
					if (arg2 == false)
						captureRect.toFront();
				}

			});
			numericTextField.textProperty().addListener(textChanged);
			break;

		}
	}

}