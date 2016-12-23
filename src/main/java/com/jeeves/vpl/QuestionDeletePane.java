package com.jeeves.vpl;

import java.net.URL;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class QuestionDeletePane extends Pane{
	private QuestionView parent;
	private Stage stage;

	@FXML
	public void cancel(Event e){
		
		stage.close();

	}
	
	@FXML
	public void delete(Event e){
		parent.removeFromSurvey();
		stage.close();

	}
	public QuestionDeletePane(QuestionView parent, Stage stage){
		this.parent = parent;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/qdelete.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);	
			this.stage = stage;
		//	this.currentGUI = gui;
			//this.project = project;
		} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
