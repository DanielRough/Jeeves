package com.jeeves.vpl.survey;


import static com.jeeves.vpl.Constants.getSaltString;

import java.net.URL;
import java.util.Optional;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.QuestionCanvas;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class SurveyPane extends Pane {

	@FXML
	private ScrollPane paneScroll;
	@FXML
	private Pane paneCanvas;
	private QuestionCanvas canvas;
	
	public QuestionCanvas getCanvas() {
		return canvas;
	}
	public void setCanvas(QuestionCanvas canvas) {
		this.canvas = canvas;
	}

	private ObservableList<FirebaseSurvey> currentsurveys;

	public void registerSurveyListener(ListChangeListener<FirebaseSurvey> listener){
		currentsurveys.addListener(listener);
	}
	public SurveyPane() {
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/SurveyPane.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = fxmlLoader.load();
			getChildren().add(root);
		} catch (Exception e) {
			System.exit(1);
		}
		Constants.getConstraintNums().clear();
		currentsurveys = FXCollections.observableArrayList();
		canvas = new QuestionCanvas(paneCanvas.getPrefWidth(), paneCanvas.getPrefHeight());

		paneCanvas.getChildren().add(canvas);
		canvas.addEventHandlers();
		this.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				paneScroll.setPrefWidth(arg2.intValue() -30);
			
			}
		});
	}
	public void addSurvey(Survey s) {
		canvas.addChild(s, 270, 175); //seems to work nicely
		s.setParentPane(canvas);
		s.addEventHandler(MouseEvent.ANY, s.mainHandler);
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
	
}
