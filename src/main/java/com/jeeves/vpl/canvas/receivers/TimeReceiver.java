package com.jeeves.vpl.canvas.receivers;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import com.jeeves.vpl.TextUtils;
import com.jeeves.vpl.ViewElement;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TimeReceiver extends ExpressionReceiver {
	public class NewTimePane extends Pane {

		@FXML
		private TextField txtHours;
		@FXML
		private TextField txtMins;
		@FXML
		private Button btnOK;
		private EventHandler<MouseEvent> closeHandler;

		private String padWithZeroes(int number) {
			if (number > 9)
				return Integer.toString(number);
			else
				return "0" + Integer.toString(number);
		}
		
		public NewTimePane(Stage stage, TextField texty) {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setController(this);
			URL location = this.getClass().getResource("/PopupNewTime.fxml");
			fxmlLoader.setLocation(location);
			try {
				Node root = (Node) fxmlLoader.load();
				getChildren().add(root);
			} catch (Exception e) {
				e.printStackTrace();
			}
			btnOK.setOnAction(handler->{
				try{
				int hours = Integer.parseInt(txtHours.getText());
				int mins = Integer.parseInt(txtMins.getText());
				texty.setText(padWithZeroes(hours) + ":" + padWithZeroes(mins));
				}
				catch(NumberFormatException e){
					//do nothing
				}
				stage.close();
			});
			txtHours.addEventHandler(KeyEvent.KEY_PRESSED, handler->{
				if(handler.getCode() == KeyCode.ENTER)
					closeHandler.handle(null);
			});
			txtHours.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent arg0) {
					
					String ch = arg0.getCharacter();
					
					int x = 0;
					try {
						x = Integer.parseInt(ch);
						int hours = Integer.parseInt(txtHours.getText() + x);
						if (hours > 23) {
							arg0.consume();
							return;
						}
					} catch (NumberFormatException e) {
						arg0.consume();
						return;
					}

				}

			});
			txtHours.textProperty().addListener(listen -> {
				int hours = Integer.parseInt(txtHours.getText());
				if (hours > 2 || (txtHours.getText().length()==2))
					txtMins.requestFocus();
			});
			txtMins.addEventHandler(KeyEvent.KEY_PRESSED, handler->{
				if(handler.getCode() == KeyCode.ENTER)
					closeHandler.handle(null);
			});
			txtMins.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent arg0) {
					if(arg0.getCode().equals(KeyCode.ENTER))
						closeHandler.handle(null);
					if (txtMins.getText().length() == 2) {
						arg0.consume();
						return;
					}
					String ch = arg0.getCharacter();
					int x = 0;
					try {
						x = Integer.parseInt(ch);
						int mins = Integer.parseInt(txtMins.getText() + x);
						if (mins > 59) {
							arg0.consume();
							return;
						}
					} catch (NumberFormatException e) {
						arg0.consume();

						return;
					}
				}

			});
		}

		public TextField getTxtHours() {
			return txtHours;
		}

		public TextField getTxtMins() {
			return txtMins;
		}
	}

	protected TextField text;

	public TimeReceiver(String receiveType) {
		super(receiveType);
	}

	@Override
	public void defineHandlers() {
		mentered = event -> {
			if (handleEntered(event))
				captureRect.setOpacity(0.7);
		};
		mexited = event -> {
			if (handleExited(event))
				captureRect.setOpacity(0);
		};
		mreleased = event -> {
			if (!handleReleased(event))
				return;
			captureRect.setOpacity(0);
			EventHandler<MouseEvent> removeEvent = new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					ViewElement child = (ViewElement) arg0.getSource();
					captureRect.setOpacity(0);
					child.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
					captureRect.setWidth(text.getPrefWidth());
				}
			};
			((ViewElement) event.getGestureSource()).addEventHandler(MouseEvent.MOUSE_PRESSED, removeEvent);
		};

	}

	public String getText() {
		String clockText = text.getText();
		String[] hoursmins = clockText.split(":");
		String value = Integer.toString((Integer.parseInt(hoursmins[0]) * 60 + Integer.parseInt(hoursmins[1]))*60000);
		return value;
	}

	//
	public TextField getTextField() {
		return text;
	}

	private String padWithZeroes(int number) {
		if (number > 9)
			return Integer.toString(number);
		else
			return "0" + Integer.toString(number);
	}

	@Override
	public void removeChild(ViewElement expression) {
		super.removeChild(expression);
		captureRect.setOpacity(0);
		captureRect.setWidth(text.getPrefWidth());
	}

	@Override
	public void setReceiveType(String type) {
		this.receiveType = type;
		captureRect.setFill(Color.DARKCYAN);
		captureRect.setArcWidth(20);
		captureRect.setArcHeight(20);
		captureRect.setOpacity(0);
		text = new TextField();
		getChildren().add(text);
		text.toBack();
		text.setMinHeight(20);
		text.setPrefHeight(20);
		text.getStyleClass().add("timevar");
		captureRect.setOnMouseClicked(event -> {
			text.requestFocus();
			text.setEditable(false);
		});
		text.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (arg2 == false) {
					captureRect.toFront();
				} else {
					captureRect.requestFocus();
					 final Stage myDialog = new Stage();

					NewTimePane pane = new NewTimePane(myDialog,text);
				        myDialog.initModality(Modality.APPLICATION_MODAL);
				        Scene myDialogScene = new Scene(pane);
				        myDialog.setScene(myDialogScene);
						Point2D screenpoint = text.localToScreen(new Point2D(text.getLayoutX(), text.getLayoutY()));
				        myDialog.setX(screenpoint.getX()+50);
				        myDialog.setY(screenpoint.getY());
				        myDialog.initStyle(StageStyle.TRANSPARENT);
				        myDialog.setIconified(false);

				        myDialog.show();
				}
			}

		});
		text.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				text.setPrefWidth(TextUtils.computeTextWidth(text.getFont(), text.getText(), 0.0D) + 20);
				captureRect.setWidth(text.getPrefWidth());
			}
		});
		text.setText("00:00");

		autosize();
	}

	public void setText(String newtext) {
		try {
			long midnightMillis = Long.parseLong(newtext);
			int midnightMinutes = (int)midnightMillis/60000;
			text.setText(padWithZeroes(midnightMinutes/60) + ":" + padWithZeroes(midnightMinutes%60));
		} catch (NumberFormatException e) {
			text.setText(newtext);
		}
	}

}
