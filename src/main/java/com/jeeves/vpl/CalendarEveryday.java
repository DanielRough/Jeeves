package com.jeeves.vpl;

import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class CalendarEveryday extends Pane{

	@FXML private ImageView imgCalendar;
	
	public CalendarEveryday(){
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/calevery.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);	
			imgCalendar.setImage(new Image("/img/icons/calenda.png"));

			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
