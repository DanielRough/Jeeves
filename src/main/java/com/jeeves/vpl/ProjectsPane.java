package com.jeeves.vpl;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseProject;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ProjectsPane extends Pane {
	final Logger logger = LoggerFactory.getLogger(ProjectsPane.class);

	@FXML private Button btnCancel;
	@FXML private Button btnLoad;
	@FXML private ListView<FirebaseProject> lstProjects;
	private FirebaseProject selectedProject;
	private Main gui;
	private ObservableList<FirebaseProject> projects;
	private Stage stage;
	
	@FXML 
	private void close(Event e){
		stage.close();
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


			lstProjects.getSelectionModel().selectedItemProperty().addListener((o,v0,v1)->{
				btnLoad.setDisable(false);
				selectedProject = v1;
			});


		} catch (Exception e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
	}
	private class ProjectCell extends ListCell<FirebaseProject>{
		@FXML private Label lblProjectName;
		@FXML private Label lblLastUpdated;
		@FXML private Label lblStatus;
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
				if(project.getactive()){
					lblStatus.setText("Status: Running");
					lblStatus.setStyle("-fx-text-fill: green");
				}
				else{
					lblStatus.setText("Status: Unpublished");
					lblStatus.setStyle("-fx-text-fill: red");
				}
				FirebaseDB.getInstance().getpatients().forEach(patient->{
					String study = patient.getCurrentStudy();

					if(study != null && study.equals(project.getname()))
						patientCount++;
				});
				lblPatients.setText("Patients: " + patientCount);
				patientCount = 0;
			}

			setText(null);
			setGraphic(hbox);
		}

	}
}

