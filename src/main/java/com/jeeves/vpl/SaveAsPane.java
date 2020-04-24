package com.jeeves.vpl;

import java.net.URL;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseProject;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SaveAsPane extends Pane { // NO_UCD (use default)
	final Logger logger = LoggerFactory.getLogger(SaveAsPane.class);

	@FXML private Button btnSave;
	@FXML private TextField txtSaveAsName;

	private Stage stage;
	private boolean exists;

	public SaveAsPane(Stage stage) {		
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/PopupSaveAs.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root =fxmlLoader.load();
			getChildren().add(root);
			this.stage = stage;
		} catch (Exception e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
		txtSaveAsName.textProperty().addListener(listen -> 
				btnSave.setDisable(txtSaveAsName.getText().equals(""))
		);
	}

	@FXML
	public void close(Event e){
		stage.close();
	}

	@FXML
	public void handleSaveAsClick(Event e) {
		FirebaseProject openProject = FirebaseDB.getInstance().getOpenProject();
		FirebaseDB firebase = FirebaseDB.getInstance();
		String oldname = openProject.getname();
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
			if (result.isPresent() && result.get() == ButtonType.OK){
				openProject.setname(newname);
				Main.getContext().setNameLabel(newname); 
				firebase.saveProject(oldname, openProject);
				stage.close();
			} else {
				stage.close();
			}
		}
		else{
			Main.getContext().setNameLabel(newname); 
			openProject.setname(newname);
			firebase.saveProject(oldname, openProject);
			stage.close();
		}
		
	}
	@FXML
	public void showGlow(Event e){
		ImageView image = (ImageView)e.getSource();
		image.getStyleClass().add("drop_shadow");
	}
	@FXML
	public void hideGlow(Event e){
		ImageView image = (ImageView)e.getSource();
		image.getStyleClass().remove("drop_shadow");
	}
}
