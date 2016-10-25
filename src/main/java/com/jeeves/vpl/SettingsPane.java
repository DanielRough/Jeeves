package com.jeeves.vpl;

import java.net.URL;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.jeeves.vpl.firebase.FirebaseProject;

public class SettingsPane extends Pane{ // NO_UCD (use default)
	private Stage stage;
	private FirebaseProject project;
	@FXML private TextField txtSaveAsName;

	@FXML
	public void handleSaveAsClick(Event e){
		String researcherNo = txtSaveAsName.getText();
		project.setresearcherno(researcherNo);
		stage.close();
	}
	@FXML
	public void handleCloseClick(Event e){
		stage.close();
	}
	public SettingsPane(Stage stage,FirebaseProject project) {
		
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/settingspopup.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);	
			this.stage = stage;
			this.project = project;
			String researcherno = project.getresearcherno();
			if(researcherno != null)
				txtSaveAsName.setText(researcherno);
		} catch (Exception e) {
				e.printStackTrace();
			}
	//	getStylesheets().add(ViewElement.class.getResource("ButtonsDemo.css").toExternalForm());
	}
}
