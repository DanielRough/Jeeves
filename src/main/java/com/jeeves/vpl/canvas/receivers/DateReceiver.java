package com.jeeves.vpl.canvas.receivers;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateUtils;

import com.jeeves.vpl.TextUtils;
import com.jeeves.vpl.ViewElement;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DateReceiver extends ExpressionReceiver {
	//private long epochMillis;

	public class NewSingleDatePane extends Pane {
		@FXML
		private DatePicker pckPicker;
		public Stage stage;
		public TextField texty;

		public NewSingleDatePane(Stage stage, TextField texty) {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setController(this);
			URL location = getClass().getResource("/PopupNewDate.fxml");
			fxmlLoader.setLocation(location);
			try {
				Node root = (Node) fxmlLoader.load();
				getChildren().add(root);
				this.stage = stage;
				this.texty = texty;
				pckPicker.setValue(LocalDate.parse(texty.getText(), DateTimeFormatter.ofPattern("dd/MM/yy")));
			} catch (Exception e) {
				e.printStackTrace();
			}
			// getStylesheets().add(ViewElement.class.getResource("Styles.css").toExternalForm());
		}

		@FXML
		public void closePane(Event e) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
			
			if (pckPicker.getValue() != null) {
				String formattedString = pckPicker.getValue().format(formatter);
				texty.setText(formattedString);
			}
			stage.hide();
		}
	}

	protected TextField text;

	public DateReceiver(String receiveType) {
		super(receiveType);
	}

	@Override
	public void defineHandlers() {
		mentered = event -> {
			if (handleEntered(event))
				getStyleClass().add("drop_shadow");
		};
		mexited = event -> {
			if (handleExited(event))
				getStyleClass().remove("drop_shadow");
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
			try {
				long epochMillis = DateUtils.parseDate(text.getText(), "dd/MM/yy").getTime();
				return Long.toString(epochMillis + 7200000);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				return "0";
			}
	
//		return text.getText();
	}

	public TextField getTextField() {
		return text;
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
		//
		text = new TextField();
		getChildren().add(text);
		text.toBack();
		text.setPrefWidth(20);
		text.setMinHeight(20);
		text.setPrefHeight(20);
		text.getStyleClass().add("datevar");
		captureRect.setOnMousePressed(event -> {
			event.consume();
			Stage stage = new Stage(StageStyle.UNDECORATED);
			NewSingleDatePane root = new NewSingleDatePane(stage, text);
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Edit dates");
			stage.initModality(Modality.APPLICATION_MODAL);
			Bounds txtBounds = text.localToScreen(text.getBoundsInLocal());
			stage.setX(txtBounds.getMinX());
			stage.setY(txtBounds.getMinY());
			stage.showAndWait();
		});

		text.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				text.setPrefWidth(TextUtils.computeTextWidth(text.getFont(), text.getText(), 0.0D) + 20);
				captureRect.setWidth(text.getPrefWidth());
				// value = text.getText();
			}
		});
		DateFormat df = new SimpleDateFormat("dd/MM/yy");
		Date dateobj = new Date();
		text.setText(df.format(dateobj));

		autosize();
	}

	public void setText(String newtext) {
			long epochMillis = Long.parseLong(newtext);
			final Date date = new Date(epochMillis); //I have no idea why it takes a day off things :S
			final String ISO_FORMAT = "dd/MM/yy";
			final SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
			final TimeZone utc = TimeZone.getTimeZone("Europe/London");
			sdf.setTimeZone(utc);
			text.setText(sdf.format(date));
		
	}
//	public void setText(String newtext) {
//		try {
//			int totalmins = Integer.parseInt(newtext);
//			int hours = totalmins / 60;
//			int mins = totalmins % 60;
//			text.setText(padWithZeroes(hours) + ":" + padWithZeroes(mins));
//		} catch (NumberFormatException e) {
//			text.setText(newtext);
//		}
//	}
//
//	public void setTextFromActual(String day, String month, String year) {
////		text.setText(year);
////		int hrs, mns;
////		try {
////			hrs = Integer.parseInt(hours);
////		} catch (NumberFormatException e) {
////			hrs = 0;
////		}
////		try {
////			mns = Integer.parseInt(mins);
////		} catch (NumberFormatException e) {
////			mns = 0;
////		}
//	//	text.setText(padWithZeroes(hrs) + ":" + padWithZeroes(mns));
//	}
}
