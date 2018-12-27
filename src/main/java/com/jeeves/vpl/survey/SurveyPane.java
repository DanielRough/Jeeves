package com.jeeves.vpl.survey;

import static com.jeeves.vpl.Constants.CONSTRAINT_NUMS;

import java.net.URL;

import com.jeeves.vpl.firebase.FirebaseSurvey;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
		CONSTRAINT_NUMS.clear();
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
		paneSurveys.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

			@Override
			public void changed(ObservableValue<? extends Tab> arg0, Tab arg1, Tab arg2) {
				if (arg2 == null)
					return;
				currentSurvey = (Survey) arg2.getContent();
				if (currentSurvey == null) {
					return;
				}
			}

		});

	}


	@FXML
	public void handleNewSurveyClick(Event e) {
		FirebaseSurvey survey = new FirebaseSurvey();
		Survey surveyview = new Survey(new FirebaseSurvey());
		surveyview.setData(survey);
		survey.settitle("New survey");
		currentsurveys.add(survey);

		addSurvey(surveyview);

	}
}
