package com.jeeves.vpl;

import java.net.URL;

import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseProject;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
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

	@FXML
	public void handleSaveAsClick(Event e) {
		String oldname = project.getname();
		project.setname(txtSaveAsName.getText());
		firebase.addProject(oldname, project);
		currentGUI.setNewProject(false);
		stage.close();
	}
}
