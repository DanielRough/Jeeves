package com.jeeves.vpl.survey;

import java.net.URL;
import com.jeeves.vpl.firebase.FirebaseSurvey;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

public class SurveyPane extends Pane {

	private Survey currentSurvey;
	@FXML
	private Pane paneNoSurveys;

	@FXML
	private TabPane paneSurveys;

	// private ObservableList<FirebaseSurvey> currentsurveys;

	public SurveyPane() {
		// this.currentsurveys = currentsurveys;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/NoSurveys.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// getStylesheets().add(ViewElement.class.getResource("Styles.css").toExternalForm());
		// loadSurveys();
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
		paneSurveys.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

			@Override
			public void changed(ObservableValue<? extends Tab> arg0, Tab arg1, Tab arg2) {
				if (arg2 == null)
					return;
				currentSurvey = (Survey) arg2.getContent();
				// FirebaseSurvey surveyModel = currentSurvey.getModel();
				if (currentSurvey == null) {
					return;
				}
			}

		});

	}

	// private void loadSurveys(){
	// paneSurveys.getTabs().clear();
	// currentsurveys.forEach(survey->{
	// Tab surveytab = new Tab(survey.gettitle());
	// Survey surveyView = new Survey(survey);
	// surveytab.setContent(surveyView);
	// paneSurveys.getTabs().add(surveytab);
	// surveyView.setTab(surveytab);
	//
	// });
	// if(currentsurveys.size() > 0){
	// paneSurveys.getSelectionModel().clearAndSelect(0); //select the first
	// survey
	// paneNoSurveys.setVisible(false);
	// }
	// else{
	// paneNoSurveys.setVisible(true);
	// }
	// }

	@FXML
	public void handleNewSurveyClick(Event e) {
		FirebaseSurvey survey = new FirebaseSurvey();
		Survey surveyview = new Survey(new FirebaseSurvey());
		surveyview.setData(survey);
		survey.settitle("New survey");
		addSurvey(surveyview);
		// grpSurveyGroup.setVisible(true);
		// currentsurveys.add(survey);
		// surveyview.parentTab.textProperty().addListener(event->{ //WHen the
		// survey's name gets changed we want to alert all triggers and
		// expressions that use it
		// currentsurveys.remove(survey);
		// currentsurveys.add(survey);
		// });
	}
}
