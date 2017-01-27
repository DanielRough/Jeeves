package com.jeeves.vpl;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class CalendarFromTo extends Pane{
	@FXML private Label lblFrom;
	@FXML private Label lblTo;
	@FXML private ImageView imgCalFrom;
	@FXML private ImageView imgCalTo;
	
	public CalendarFromTo(){
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/calfromto.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);	
			imgCalFrom.setImage(new Image("/img/icons/calenda.png"));
			imgCalTo.setImage(new Image("/img/icons/calenda.png"));

			} catch (Exception e) {
				e.printStackTrace();
			}
//		getStylesheets().add(ViewElement.class.getResource("ButtonsDemo.css").toExternalForm());
	}
	public void setCalDates(LocalDate from, LocalDate to){
		long fromval = from.toEpochDay();
		long toval = to.toEpochDay();
		 String fromstr = from.format(DateTimeFormatter.ofPattern("MMM dd"));
		  String tostr = to.format(DateTimeFormatter.ofPattern("MMM dd"));
		lblFrom.setText(fromstr);
		lblTo.setText(tostr);
	}
}
