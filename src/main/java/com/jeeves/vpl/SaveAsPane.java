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

public class SaveAsPane extends Pane{ // NO_UCD (use default)
	private Stage stage;
//	private Main currentGUI;
	private FirebaseProject project;
	@FXML private TextField txtSaveAsName;
	@FXML private Button btnSave;
	private FirebaseDB firebase;
	
	@FXML
	public void handleSaveAsClick(Event e){
		String oldname = project.getname();
		project.setname(txtSaveAsName.getText());
		//currentGUI.tabCanvas.setText(txtSaveAsName.getText() + " Configuration");
		firebase.addProject(oldname,project);
		//currentGUI.setNewProject(false);

		stage.close();
	}
	@FXML
	public void handleCloseClick(Event e){
		stage.close();
	}
	public SaveAsPane(Main gui, Stage stage,FirebaseProject project, FirebaseDB firebase) {
		this.firebase = firebase;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/PopupSaveAs.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);	
			this.stage = stage;
			//this.currentGUI = gui;
			this.project = project;
		} catch (Exception e) {
				e.printStackTrace();
			}
	//	getStylesheets().add(ViewElement.class.getResource("Styles.css").toExternalForm());
		txtSaveAsName.textProperty().addListener(listen->{if(txtSaveAsName.getText().equals(""))btnSave.setDisable(true);else btnSave.setDisable(false);});
	}
}
