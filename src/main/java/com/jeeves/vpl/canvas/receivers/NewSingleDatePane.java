package com.jeeves.vpl.canvas.receivers;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

class NewSingleDatePane extends Pane{
	public Stage stage;
	public TextField texty;
	@FXML private DatePicker pckPicker;
	public NewSingleDatePane(Stage stage, TextField texty){
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = getClass().getResource("/newsingledate.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);	
			this.stage = stage;
			this.texty = texty;
			pckPicker.setValue(LocalDate.parse(texty.getText(),DateTimeFormatter.ofPattern("dd/MM/yy")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		//getStylesheets().add(ViewElement.class.getResource("ButtonsDemo.css").toExternalForm());
	}
	
	@FXML
	public void closePane(Event e){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
		if(pckPicker.getValue() != null)
		{
		String formattedString = pckPicker.getValue().format(formatter);
		texty.setText(formattedString);
		}
		stage.hide();
	}
}
