package com.jeeves.vpl;

import java.net.URL;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.jeeves.vpl.firebase.FirebaseDB;

class StudentIDGetter extends Pane{

	@FXML private TextField txtUsername;
	@FXML public Button btnOK;
	private FirebaseDB firebase;
	public Stage stage; 
	
	public StudentIDGetter(Stage stage, FirebaseDB firebase){
		this.firebase = firebase; 
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/studentid.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);	
			txtUsername.textProperty().addListener(listen->{if(txtUsername.getText().equals(""))btnOK.setDisable(true);else btnOK.setDisable(false);});
			this.stage = stage;
		} catch (Exception e) {
				e.printStackTrace();
			}
		//getStylesheets().add(ViewElement.class.getResource("ButtonsDemo.css").toExternalForm());
		

	}
	
	@FXML
	public void save(Event e){
			String id = txtUsername.getText();
			firebase.setUsername(id);
			stage.close();
	}
}
