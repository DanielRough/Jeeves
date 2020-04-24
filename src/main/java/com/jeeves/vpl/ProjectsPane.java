package com.jeeves.vpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseProject;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ProjectsPane extends Pane {
	final Logger logger = LoggerFactory.getLogger(ProjectsPane.class);

	@FXML private Button btnCancel;
	@FXML private Button btnLoad;
	@FXML private Button btnDelete;
	@FXML private ListView<FirebaseProject> lstProjects;
	private FirebaseProject selectedProject;
	private ChangeListener<? super FirebaseProject> listener; 

	private Main gui;
	private ObservableList<FirebaseProject> projects;
	private Stage stage;
	
	@FXML 
	private void close(Event e){
		stage.close();
	}

	@FXML
	private void loadFile(Event e) {
//		final FileChooser fileChooser = new FileChooser();
//
//		Bucket bucket = StorageClient.getInstance().bucket();
//		File file = fileChooser.showOpenDialog(null);
//		if (file != null) {
//			txtImage.setText(file.getAbsolutePath());
//			imgImage.setImage(new Image(getClass().getResourceAsStream(getImagePath())));
//			Map<String, Object> audioOpts = new HashMap<>();
//			audioOpts.put("fullpath", file.getAbsolutePath());
//			audioOpts.put(AUDIOSTR, file.getName());
//			model.getparams().put("options",audioOpts);
//			try {
//				BlobId blobId = BlobId.of(bucket.getName(), file.getName());
//				BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("audio/*").build();
//				Blob blob = storage.create(blobInfo,new FileInputStream(file));
//			} catch (FileNotFoundException e1) {
//				logger.error(e1.getMessage(),e1.fillInStackTrace());
//			} 
//
//		}	
	}
	
	@FXML
	private void deleteProject(Event e) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete project");
		alert.setHeaderText(null);
		alert.setContentText("Really delete project " + selectedProject.getname() + "? This can't be undone");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK){
		//	FirebaseDB.getInstance().getprojects().remove(selectedProject);
			lstProjects.getSelectionModel().selectedItemProperty().removeListener(listener);
			lstProjects.setItems(null);
			FirebaseDB.getInstance().deleteProject(selectedProject);
			stage.close();
		//	

		} else {
//			stage.close();
		}

	}
	@FXML
	private void loadProject(Event e){
		gui.setCurrentProject(selectedProject);
		stage.close();
	}
	public ProjectsPane(Main gui,Stage stage) {
		this.gui = gui;
		this.stage = stage;
		this.projects = FirebaseDB.getInstance().getprojects();
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		
		URL location = this.getClass().getResource("/ProjectPane.fxml");

		fxmlLoader.setLocation(location);
		
		listener = (o,v0,v1)->{
			btnLoad.setDisable(false);
			btnDelete.setDisable(false);
			selectedProject = v1;
		};;
		try {
			Node root = fxmlLoader.load();
			getChildren().add(root);
			lstProjects.setItems(projects);
			lstProjects.setCellFactory(projectsView -> new ProjectCell());

			//Wait for 5 seconds before declaring that no projects are available
			new Thread(() -> {
				try {
					Thread.sleep(5000);
					if(projects.isEmpty()) {
						Platform.runLater(()->
						lstProjects.setPlaceholder(new Label("No projects currently available."))
						);		        
					}
				}
				catch (Exception e){
					logger.error(e.getMessage(),e.fillInStackTrace());
				}
			}).start();

			lstProjects.setPlaceholder(new Label("Loading projects..."));


			lstProjects.getSelectionModel().selectedItemProperty().addListener(listener);


		} catch (Exception e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
	}

	private class ProjectCell extends ListCell<FirebaseProject>{
		@FXML private Label lblProjectName;
		@FXML private Label lblLastUpdated;
		@FXML private Label lblPatients;
		@FXML private HBox hbox;
		FXMLLoader fxmlLoader;
		int patientCount = 0;

		@Override
		protected void updateItem(FirebaseProject project, boolean empty) {
			super.updateItem(project, empty);

			if(empty || project == null) {

				setText(null);
				setGraphic(null);

			} else {
				if (fxmlLoader == null) {
					fxmlLoader = new FXMLLoader(getClass().getResource("/ProjectCell.fxml"));
					fxmlLoader.setController(this);

					try {
						fxmlLoader.load();
					} catch (IOException e) {
						logger.error(e.getMessage(),e.fillInStackTrace());
					}

				}

				lblProjectName.setText(project.getname());
				DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
				Date date = new Date();
				date.setTime(project.getlastUpdated());
				String dateStr = formatter.format(date);
				lblLastUpdated.setText(dateStr);
				FirebaseDB.getInstance().getpatients().forEach(patient->{
					String study = patient.getCurrentStudy();

					if(study != null && study.equals(project.getname()))
						patientCount++;
				});
				lblPatients.setText("Users: " + patientCount);
				patientCount = 0;
			}

			setText(null);
			setGraphic(hbox);
		}

	}
}

