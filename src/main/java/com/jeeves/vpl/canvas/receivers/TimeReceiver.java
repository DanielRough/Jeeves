package com.jeeves.vpl.canvas.receivers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

public class TimeReceiver extends ExpressionReceiver{

	public TimeReceiver(String receiveType) {
		super(receiveType);
	}

	public String getText() {
			String clockText = text.getText();
			String[] hoursmins = clockText.split(":");
			return Integer.toString(Integer.parseInt(hoursmins[0])*60 + Integer.parseInt(hoursmins[1]));
	}
	
	public void setTextFromActual(String hours, String mins){
		int hrs,mns;
		try{
		hrs = Integer.parseInt(hours);
		}
		catch(NumberFormatException e){
			hrs=0;
		}
		try{
			mns = Integer.parseInt(mins);
		}
		catch(NumberFormatException e){
			mns=0;
		}
		text.setText(padWithZeroes(hrs) + ":" + padWithZeroes(mns));
	}
	public void setText(String newtext) {
			try{
			int totalmins = Integer.parseInt(newtext);
			int hours = totalmins/60;
			int mins = totalmins%60;
			text.setText(padWithZeroes(hours) + ":" + padWithZeroes(mins));
			}
			catch(NumberFormatException e){
				text.setText(newtext);
			}
		}
	
	public void setReceiveType(String type) {
		captureRect.setFill(Color.TRANSPARENT);
		captureRect.setArcWidth(20);
		captureRect.setArcHeight(20);
		captureRect.setOpacity(0);

		text.toBack();

		text.setPrefWidth(20);
		text.getStyleClass().add("timevar");
		captureRect.setOnMouseClicked(event -> {
			text.requestFocus();
			text.setEditable(false);
		});
		text.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (arg2 == false){
					captureRect.toFront();
				}
				else{
					Popup pop = new Popup();
					NewTimePane pane = new NewTimePane();
					pop.getContent().add(pane);
					Point2D screenpoint = text.localToScreen(new Point2D(text.getLayoutX(),text.getLayoutY()));
					pop.setX(screenpoint.getX());
					pop.setY(screenpoint.getY());
					pop.setAutoHide(true);
					pop.setHideOnEscape(true);
					pane.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>(){

						@Override
						public void handle(MouseEvent arg0) {
							if(arg0.getEventType().equals(MouseEvent.MOUSE_EXITED))
								pop.hide();
						}
						
					});
					TextField hours = pane.getTxtHours();
					TextField mins = pane.getTxtMins();
					hours.textProperty().addListener(listener->{
						setTextFromActual(hours.getText(),mins.getText());
					});
					mins.textProperty().addListener(listener->{
						setTextFromActual(hours.getText(),mins.getText());
					});
					pop.show(text,screenpoint.getX(),screenpoint.getY());

				}
				}

		});
		text.textProperty().addListener(new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				text.setPrefWidth(TextUtils.computeTextWidth(text.getFont(), text.getText(), 0.0D) + 15);
				captureRect.setWidth(text.getPrefWidth() + 6);
				value = text.getText();
			}	
		});
		text.setText("00:00");

		autosize();
	}
}


