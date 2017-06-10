package com.jeeves.vpl;

import java.net.URL;
import java.util.Optional;

import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseProject;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SaveAsPane extends Pane { // NO_UCD (use default)
	@FXML
	private Button btnSave;
	private Main currentGUI;
	private FirebaseDB firebase;
	private FirebaseProject project;
	private Stage stage;
	@FXML
	private TextField txtSaveAsName;

	public SaveAsPane(Main gui, Stage stage, FirebaseProject project, FirebaseDB firebase) {
		this.firebase = firebase;
		this.currentGUI = gui;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/PopupSaveAs.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
			this.stage = stage;
			this.project = project;
		} catch (Exception e) {
			e.printStackTrace();
		}
		txtSaveAsName.textProperty().addListener(listen -> {
			if (txtSaveAsName.getText().equals(""))
				btnSave.setDisable(true);
			else
				btnSave.setDisable(false);
		});
	}

	@FXML
	public void handleCloseClick(Event e) {
		stage.close();
	}
	boolean exists = false;

	@FXML
	public void handleSaveAsClick(Event e) {
		String oldname = project.getname();
		String newname = txtSaveAsName.getText();
		firebase.getprojects().forEach(proj->{if(proj.getname().equals(newname)){
			exists = true;	
		}});
		if(exists){
			exists = false;
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Name conflict");
			alert.setHeaderText(null);
			alert.setContentText("A study with name " + newname + " already exists. Overwrite this study?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
				project.setname(newname);
				firebase.saveProject(oldname, project);
				currentGUI.setNewProject(false);
				stage.close();
			} else {
				stage.close();
				return;
			}
		}
		else{
			project.setname(newname);
			firebase.saveProject(oldname, project);
			currentGUI.setNewProject(false);
			stage.close();
		}
		
	}
}
