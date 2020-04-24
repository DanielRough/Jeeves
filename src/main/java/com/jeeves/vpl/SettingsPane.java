package com.jeeves.vpl;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseProject;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static com.jeeves.vpl.Constants.*;

public class SettingsPane extends Pane{
	final Logger logger = LoggerFactory.getLogger(SettingsPane.class);

	private FirebaseProject currentproject;
	private Stage stage;
	@FXML private TextField txtStudyId;
	@FXML private Button btnStudyId;
	@FXML private Label lblStudyId;
	@FXML private Label lblStudyStatus;
	@FXML private Label lblStatusDesc;
	@FXML private VBox vboxPublished;
	@FXML private VBox vboxUnpublished;
	
	public SettingsPane(Stage stage) {
		this.stage = stage;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/SettingsPane.fxml");

		fxmlLoader.setLocation(location);
		try {
			Node root = fxmlLoader.load();
			getChildren().add(root);
			currentproject = FirebaseDB.getInstance().getOpenProject();
			if(!currentproject.getactive()){
				vboxPublished.setVisible(false);
				vboxUnpublished.setVisible(true);
				return;
			}
			String currentid = currentproject.getid();
			lblStudyId.setText(currentid);

		} catch (Exception e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
	}
	
	@FXML
	public void close(Event e){
		stage.close();
	}
	
	@FXML
	public void updateStudyId(Event e){
		
		String newId = txtStudyId.getText();
		
		if(newId.length() < 3){
			makeInfoAlert("Jeeves","ID too short","New ID must be at least 3 characters long");
			return;
		}
		currentproject.setid(newId);
		lblStudyId.setText(newId);
		FirebaseDB.getInstance().publishStudy(currentproject);
		
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
