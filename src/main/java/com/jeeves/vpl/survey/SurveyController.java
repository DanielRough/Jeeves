package com.jeeves.vpl.survey;

import java.net.URL;

import com.jeeves.vpl.MainController;
import com.jeeves.vpl.firebase.FirebaseSurvey;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

public class SurveyController extends Pane{
	
	@FXML private Pane paneNoSurveys;
	@FXML private TabPane paneSurveys;
	
	private Survey currentSurvey;
	
	private ObservableList<FirebaseSurvey> currentsurveys;
	
	public SurveyController(ObservableList<FirebaseSurvey> currentsurveys){
		this.currentsurveys = currentsurveys;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/SurveyPane.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);	
			} catch (Exception e) {
				e.printStackTrace();
			}
	//	getStylesheets().add(ViewElement.class.getResource("ButtonsDemo.css").toExternalForm());
		loadSurveys();
		addSurveyListeners();
	}
	@FXML
	public void handleNewSurveyClick(Event e){
		Tab tab = new Tab();
		FirebaseSurvey survey = new FirebaseSurvey();
		Survey surveyview = new Survey(this,new FirebaseSurvey());
		surveyview.setTab(tab);
		surveyview.setData(survey);
		tab.setContent(surveyview);
		tab.setText("New survey");
		paneNoSurveys.setVisible(false);
	//	grpSurveyGroup.setVisible(true);
		paneSurveys.getTabs().add(tab);
		survey.setname("New survey");
		currentsurveys.add(survey);
//		surveyview.parentTab.textProperty().addListener(event->{ //WHen the survey's name gets changed we want to alert all triggers and expressions that use it
//			currentsurveys.remove(survey);
//			currentsurveys.add(survey);
//		});
		paneSurveys.getSelectionModel().clearAndSelect(paneSurveys.getTabs().indexOf(tab));
	}
	
	private void loadSurveys(){
		paneSurveys.getTabs().clear();
		currentsurveys.forEach(survey->{
			Tab surveytab = new Tab(survey.getname());
			Survey surveyView = new Survey(this,survey);
			surveytab.setContent(surveyView);
			paneSurveys.getTabs().add(surveytab);
			surveyView.setTab(surveytab);

		});
		if(currentsurveys.size() > 0){
		paneSurveys.getSelectionModel().clearAndSelect(0); //select the first survey
		paneNoSurveys.setVisible(false);
		}
		else{
			paneNoSurveys.setVisible(true);
		}
		}
	
	public void addSurveyListeners(){
		paneSurveys.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>(){

			@Override
			public void changed(ObservableValue<? extends Tab> arg0, Tab arg1,
					Tab arg2) {
					if(arg2 == null)return;
					currentSurvey = (Survey)arg2.getContent();
					FirebaseSurvey surveyModel = currentSurvey.getModel();
					if(currentSurvey == null){return;}
			}
			
		});

		

	}
}
