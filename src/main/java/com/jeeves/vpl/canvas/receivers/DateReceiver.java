package com.jeeves.vpl.canvas.receivers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DateReceiver extends ExpressionReceiver{


	public DateReceiver(String receiveType) {
		super(receiveType);
	}

	public String getText() {
			return text.getText();
	}
	
	public void setText(String newtext) {
			text.setText(newtext);
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
			Stage stage = new Stage(StageStyle.UNDECORATED);
			NewSingleDatePane root = new NewSingleDatePane(stage,text);
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Edit dates");
			stage.initModality(Modality.APPLICATION_MODAL);
			Bounds txtBounds = text.localToScreen(text.getBoundsInLocal());
			stage.setX(txtBounds.getMinX());
			stage.setY(txtBounds.getMinY());
			stage.showAndWait();	
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
		DateFormat df = new SimpleDateFormat("dd/MM/yy");
		Date dateobj = new Date();
		text.setText(df.format(dateobj));

		autosize();
	}
}


