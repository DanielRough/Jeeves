package com.jeeves.vpl.survey;

import java.net.URL;

import com.jeeves.vpl.survey.questions.QuestionView;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class QuestionDeletePane extends Pane {
	private QuestionView question;
	private Stage stage;
	private Survey survey;

	public QuestionDeletePane(Survey survey, QuestionView question, Stage stage) {
		this.question = question;
		this.survey = survey;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/PopupDeleteQuestion.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
			this.stage = stage;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void cancel(Event e) {
		stage.close();
	}

	@FXML
	public void delete(Event e) {
		survey.removeQuestion(question);
		// parent.removeFromSurvey();
		stage.close();

	}
}
