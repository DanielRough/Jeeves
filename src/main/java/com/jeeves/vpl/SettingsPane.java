package com.jeeves.vpl;

import java.net.URL;

import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseProject;

import javafx.collections.ObservableList;
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

	private FirebaseProject currentproject;
	private Stage stage;
	@FXML private TextField txtStudyId;
	@FXML private Button btnStudyId;
	@FXML private Button btnPublic;
	@FXML private Label lblStudyId;
	@FXML private Label lblStudyStatus;
	@FXML private Label lblStatusDesc;
	@FXML private VBox vboxPublished;
	@FXML private VBox vboxUnpublished;
	private String notPublic;
	private String currentlyPublic;
	
	public SettingsPane(Main gui, Stage stage) {
		this.stage = stage;
	//	this.projects = firebase.getprojects();
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		notPublic = "Your study currently requires patients to have your study ID. To make your study freely available to anyone, click 'Go Public' below!";
		currentlyPublic = "Your study is now public! Anyone with the Jeeves app can now assign themselves to your study. To make your study available only with the study ID, click 'Go Private'";
		URL location = this.getClass().getResource("/SettingsPane.fxml");

		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
			currentproject = FirebaseDB.getOpenProject();
			if(!currentproject.getactive()){
				vboxPublished.setVisible(false);
				vboxUnpublished.setVisible(true);
				return;
			}
			String currentid = currentproject.getid();
			lblStudyId.setText(currentid);

			updatePublicBit();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void close(Event e){
		stage.close();
	}
	
	private void updatePublicBit(){
		boolean isPublic = currentproject.getisPublic();
		if(isPublic){
			lblStudyStatus.setText("PUBLIC");
			lblStudyStatus.setStyle("-fx-text-fill:green;-fx-font-weight: bold;");	
			lblStatusDesc.setText(currentlyPublic);
			btnPublic.setText("Go Private");
		}
		else{
			lblStudyStatus.setText("PRIVATE");
			lblStudyStatus.setStyle("-fx-text-fill:purple;-fx-font-weight: bold;");
			lblStatusDesc.setText(notPublic);
			btnPublic.setText("Go Public");
		}
	}
	@FXML
	public void updateStudyId(Event e){
		
		String newId = txtStudyId.getText();
		
		if(newId.length() < 3){
			makeInfoAlert("Jeeves","ID too short","New ID must be at least 3 characters long");
			return;
		}
		ObservableList<FirebaseProject> publicprojects = FirebaseDB.getInstance().getpublicprojects();
		for(FirebaseProject proj : publicprojects){
			if(proj.getid().equals(newId)){
				makeInfoAlert("Jeeves","ID exists","A project with this ID already exists");
			    return;
			}
		}
		
		//If we made it through, we can change our current project's study ID
		currentproject.setid(newId);
		lblStudyId.setText(newId);
		//and now we republish
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
	
	@FXML
	public void goPublic(Event e){
		if(!currentproject.getisPublic())
			currentproject.setisPublic(true);
		else
			currentproject.setisPublic(false);
		updatePublicBit();
		FirebaseDB.getInstance().publishStudy(currentproject);

	}
	@FXML
	public void endStudy(Event e){
		
	}
}
