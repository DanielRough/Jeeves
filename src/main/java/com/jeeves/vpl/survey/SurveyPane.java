package com.jeeves.vpl.survey;


import static com.jeeves.vpl.Constants.getSaltString;

import java.net.URL;
import java.util.Optional;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.firebase.FirebaseProject;
import com.jeeves.vpl.firebase.FirebaseSurvey;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;

public class SurveyPane extends Pane {

	@FXML
	private Button btnDelete;
	@FXML
	private Pane paneNoSurveys;
	@FXML
	private TabPane paneSurveys;
	private ObservableList<FirebaseSurvey> currentsurveys;

	public ObservableList<Tab> getSurveyTabs(){
		return paneSurveys.getTabs();
	}
	public void registerSurveyListener(ListChangeListener<FirebaseSurvey> listener){
		currentsurveys.addListener(listener);
	}
	public SurveyPane() {
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/NoSurveys.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = fxmlLoader.load();
			getChildren().add(root);
		} catch (Exception e) {
			System.exit(1);
		}
		Constants.getConstraintNums().clear();
		currentsurveys = FXCollections.observableArrayList();
		addSurveyListeners();
	}

	public void addSurvey(Survey s) {
		paneNoSurveys.setVisible(false);
		Tab tab = new Tab();
		s.setTab(tab);
		tab.setContent(s);
		tab.setText(s.getModel().gettitle());
		paneSurveys.getTabs().add(tab);
		paneSurveys.getSelectionModel().clearAndSelect(paneSurveys.getTabs().indexOf(tab));

	}

	public void addSurveyListeners() {
		paneSurveys.getSelectionModel().selectedItemProperty().addListener((arg0,arg1,arg2)->{
				if (arg2 == null) {
					btnDelete.setVisible(false);
					return;			
				}
				btnDelete.setVisible(true);
		});
	}


	@FXML
	public void handleNewSurveyClick(Event e) {
		FirebaseSurvey survey = new FirebaseSurvey();
		Survey surveyview = new Survey(new FirebaseSurvey());
		surveyview.setData(survey);
		survey.setsurveyId(getSaltString());
		survey.settitle("New survey");
		Constants.getOpenProject().add(surveyview,0);
		addSurvey(surveyview);

	}
	
	@FXML
	public void handleDeleteSurveyClick(Event e) {
		FirebaseProject openProject = Constants.getOpenProject();
		Survey toDelete = (Survey)paneSurveys.getSelectionModel().getSelectedItem().getContent();
		String surveyname = toDelete.getModel().gettitle();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Jeeves");
		alert.setHeaderText("Delete Survey");
		alert.setContentText("Do you really want to delete survey " + surveyname + "?");
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get().equals(ButtonType.OK)) {
			paneSurveys.getTabs().remove(paneSurveys.getSelectionModel().getSelectedItem());
			openProject.remove(toDelete);
			//openProject.getsurveys().remove(index)
			if(paneSurveys.getTabs().isEmpty()) {
				paneNoSurveys.setVisible(true);
			}
		}
	}
}
