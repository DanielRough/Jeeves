package com.jeeves.vpl.canvas.receivers;

import static com.jeeves.vpl.Constants.*;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.Typed;
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

	protected String receiveType;
	protected String value; //The value is for when we don't have an Expression in the receiver

	public ExpressionReceiver(String receiveType) {
		super();
		captureRect.setWidth(20);
		captureRect.setHeight(20);
		setReceiveType(receiveType);		
	}

	@Override
	public void addChild(ViewElement expression, double mouseX, double mouseY) {
		getChildren().add(expression);
		expression.setLayoutX(0);
		expression.setLayoutY(0);
		expression.setManaged(true);

		childList.add(expression);
		EventHandler<MouseEvent> removeEvent = new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) {
				ViewElement child = (ViewElement)arg0.getSource();
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
	public boolean isValidElement(ViewElement dragged) {

		if(dragged.getType() == ElementType.EXPRESSION || dragged.getType() == ElementType.VARIABLE){
			System.out.println("My type is " + getReceiveType() + " and vartype is " + ((Typed)dragged).getVarType());
			if(((Typed)dragged).getVarType() == getReceiveType())
					return true;
		}
		return false;
	}

	@Override
	public void removeChild(ViewElement expression) {
		childList.remove(0);
		captureRect.setOpacity(0.5);
		captureRect.toFront();
		expression.addAllHandlers();
		getChildren().remove(expression);
		captureRect.setWidth(20);
		autosize();
	}

	//Conditional formatting
	public void setReceiveType(String type) {
		this.receiveType = type;
		switch(type){
		case VAR_BOOLEAN: 
			captureRect.setArcWidth(20);
			captureRect.setArcHeight(20);
			captureRect.setFill(Color.DARKCYAN); break;
		case VAR_LOCATION: 			
			captureRect.setArcWidth(20);
			captureRect.setArcHeight(20);break;
		case VAR_NONE: 			
			captureRect.setFill(Color.BLACK); break;
		case VAR_ANY: 			
			captureRect.setFill(Color.BLACK);
			captureRect.setArcWidth(0);
			captureRect.setArcHeight(0);break;
		case VAR_NUMERIC: 
			captureRect.setFill(Color.LIMEGREEN); break;

		}	
	}

	public String getValue(){
		return value;
	}
	public String getReceiveType() {
		return receiveType;
	}

}