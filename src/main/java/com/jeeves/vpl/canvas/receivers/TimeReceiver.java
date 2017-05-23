package com.jeeves.vpl.canvas.receivers;

import java.net.URL;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

import com.jeeves.vpl.TextUtils;
import com.jeeves.vpl.ViewElement;

public class TimeReceiver extends ExpressionReceiver {
	public class NewTimePane extends Pane {

		// private Stage stage;
		@FXML
		private TextField txtHours;
		@FXML
		private TextField txtMins;

		public NewTimePane() {
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
				if (hours > 2)
					txtMins.requestFocus();
			});
			txtMins.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent arg0) {
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
			((ViewElement) event.getSource()).addEventHandler(MouseEvent.MOUSE_PRESSED, removeEvent);
		};

	}

	public String getText() {
		String clockText = text.getText();
		String[] hoursmins = clockText.split(":");
		String value = Integer.toString(Integer.parseInt(hoursmins[0]) * 60 + Integer.parseInt(hoursmins[1]));
		return value;
	}

	//
	public TextField getTextField() {
		return text;
	}

	public String padWithZeroes(int number) {
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
					Popup pop = new Popup();
					NewTimePane pane = new NewTimePane();
					pop.getContent().add(pane);
					Point2D screenpoint = text.localToScreen(new Point2D(text.getLayoutX(), text.getLayoutY()));
					pop.setX(screenpoint.getX());
					pop.setY(screenpoint.getY());
					pop.setAutoHide(true);
					pop.setHideOnEscape(true);
					pane.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent arg0) {
							if (arg0.getEventType().equals(MouseEvent.MOUSE_EXITED))
								pop.hide();
						}

					});
					TextField hours = pane.getTxtHours();
					TextField mins = pane.getTxtMins();
					hours.textProperty().addListener(listener -> {
						setTextFromActual(hours.getText(), mins.getText());
					});
					mins.textProperty().addListener(listener -> {
						setTextFromActual(hours.getText(), mins.getText());
					});
					pop.show(text, screenpoint.getX(), screenpoint.getY());

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
			int totalmins = Integer.parseInt(newtext);
			int hours = totalmins / 60;
			int mins = totalmins % 60;
			text.setText(padWithZeroes(hours) + ":" + padWithZeroes(mins));
		} catch (NumberFormatException e) {
			text.setText(newtext);
		}
	}

	public void setTextFromActual(String hours, String mins) {
		int hrs, mns;
		try {
			hrs = Integer.parseInt(hours);
		} catch (NumberFormatException e) {
			hrs = 0;
		}
		try {
			mns = Integer.parseInt(mins);
		} catch (NumberFormatException e) {
			mns = 0;
		}
		text.setText(padWithZeroes(hrs) + ":" + padWithZeroes(mns));
	}
}
