package com.jeeves.vpl.survey;

import static com.jeeves.vpl.Constants.*;

import java.io.IOException;
import java.util.List;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.QuestionReceiver;
import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.firebase.FirebaseSurvey;
import com.jeeves.vpl.survey.questions.QuestionView;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Survey extends ViewElement<FirebaseSurvey> {
	@FXML
	private Button btnAddChoiceOpt;
	@FXML
	private ComboBox<String> cboSurveys;
	@FXML
	private CheckBox chkExpiry;
	@FXML 
	private CheckBox chkFastTranslation;
	private QuestionEditor editor;
	@FXML
	private Group grpSurveyEdit;
	@FXML
	private HBox hboxQuestionCondition;
	@FXML
	private Label lblExpiry;
	@FXML
	private Label lblSaved;
	@FXML
	private Pane paneDropRect;
	@FXML
	private Pane paneEditor;
	@FXML
	private Pane paneReceiver;
	@FXML
	private Pane paneScale;
	@FXML
	private ScrollPane paneScrollVars;
	@FXML
	private TabPane paneSurveys;
	private Tab parentTab;
	private QuestionReceiver receiver;

	private ObservableList<QuestionView> surveyQuestions;
	private StringProperty title;
	@FXML
	private TextField txtDescription;

	@FXML
	private TextField txtExpiry;

	@FXML
	private TextField txtGeo;
	@FXML
	private TextField txtSurveyDesc;

	@FXML
	private TextField txtSurveyName;

	public Survey(FirebaseSurvey data) {
		super(data, FirebaseSurvey.class);
		this.model = data;

	}
	private ListChangeListener<Node> receiverListener;
	@Override
	public void addListeners() {
		receiverListener = new ListChangeListener<Node>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> arg0) {
				//new question, better update the survey ID!
				getModel().setsurveyId(getSaltString());
				arg0.next();
				if (arg0.wasAdded()) {
					ViewElement added = (ViewElement) arg0.getAddedSubList().get(0);
					int index = receiver.getChildElements().indexOf(added);
					System.out.println("MAYBE");
					model.getquestions().add(index, (FirebaseQuestion) added.getModel());
					addQuestion(index,(QuestionView)added);
					editor.populateQuestion((QuestionView) arg0.getAddedSubList().get(0));
					paneEditor.setDisable(false);
				} else {
					QuestionView removed = (QuestionView) arg0.getRemoved().get(0);
					surveyQuestions.remove(removed);
					System.out.println("LIKELY NOT");
					model.getquestions().remove(removed.getModel());

					if (editor.getSelectedQuestion() != null && editor.getSelectedQuestion().equals(removed)) {
						paneEditor.getChildren().remove(editor);
						editor = new QuestionEditor(surveyQuestions);
						paneEditor.getChildren().add(editor);
						paneEditor.setDisable(true);
					}
				}

			}

		};
		receiver.getChildElements().addListener(receiverListener);
		paneReceiver.getChildren().add(receiver);
		receiver.addDummyView(paneDropRect, 0);
	}

	public void addQuestion(int index, QuestionView view) {
		surveyQuestions.add(index, view);
		editor.populateQuestion(view);

		view.addButtons();
		view.getEditButton().setOnAction(event -> {
			editor.populateQuestion(view);
			paneEditor.setDisable(false);
		});
		view.questionTextProperty.addListener(new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(!newValue.isEmpty())
				getModel().setsurveyId(getSaltString()); //question text changed, again survey ID needs to be updated
			}
			
		});
		view.getDeleteButton().setOnAction(event -> {
			Stage stage = new Stage(StageStyle.UNDECORATED);
			QuestionDeletePane root = new QuestionDeletePane(this, view, stage);
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
		});

	}

	@Override
	public void fxmlInit() {
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/survey.fxml"));
		Pane surveynode;

		surveyQuestions = FXCollections.observableArrayList();
		title = new SimpleStringProperty();
		editor = new QuestionEditor(surveyQuestions);

		try {
			surveynode = (Pane) surveyLoader.load();

			this.getChildren().add(surveynode);
			paneEditor.getChildren().add(editor);
			addProperties();
			receiver = new QuestionReceiver(paneReceiver.getPrefWidth(), paneReceiver.getPrefHeight());
			surveynode.setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					arg0.consume();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ViewElement getInstance() {
		return this;
	}

	@Override
	public FirebaseSurvey getModel() {
		return model;
	}

	public ObservableList<QuestionView> getQuestions() {
		return surveyQuestions;
	}

	public StringProperty getTitle() {
		return title;
	}


	public void removeQuestion(QuestionView question) {

		if (model.getquestions().contains(question.getModel())) {
			model.getquestions().remove(question.getModel());
			receiver.removeChild(question);
			if (question.getParentQuestion() != null)
				question.getParentQuestion().removeChildQuestion(question);

		}
		editor.refresh();
	}

	@Override
	public void setData(FirebaseSurvey model) {
		super.setData(model);
		this.model = model;
		addProperties();
		txtSurveyName.setText(model.gettitle());
		//THIS IS TO STOP THEM RENAMING SURVEYS ONCE THE THING IS PUBLISHED
				if(FirebaseDB.getOpenProject().getactive() && model.gettitle() != null)
					txtSurveyName.setDisable(true);
		if (model.getexpiryTime() > 0) {
			chkExpiry.setSelected(true);
			txtExpiry.setText(Long.toString(model.getexpiryTime()));
		}

		
		List<FirebaseQuestion> questions = model.getquestions();

		if (questions == null)
			return;

		int index = 0;
		receiver.getChildElements().removeListener(receiverListener);
		for (FirebaseQuestion newquestion : questions) {
			QuestionView question = QuestionView.create(newquestion);
			question.setData(newquestion);
			
			receiver.addChildAtIndex(question, index);
			addQuestion(index++, question);
			
			//New stuff to set the parent question in here
			FirebaseQuestion condition = newquestion.getconditionQuestion();
			if(condition != null){
				for(QuestionView q : this.surveyQuestions)
					//TODO: Replace with IDs
					if(q.getQuestionId().equals(condition.getquestionId())){
						question.setParentQuestion(q);
					}
			}
		}
		receiver.getChildElements().addListener(receiverListener);
	}


	public void setTab(Tab tab) {
		this.parentTab = tab;
	}

	public void setTitle(String name) {
		title.set(name);
	}

	private void addProperties() {
		chkExpiry.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (chkExpiry.isSelected()) {
					txtExpiry.setDisable(false);
				} else {
					txtExpiry.setText("");
					getModel().setexpiryTime(0);
					txtExpiry.setDisable(true);
				}
				getModel().setsurveyId(getSaltString()); //question text changed, again survey ID needs to be updated

			}
		});

		chkFastTranslation.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				getModel().setfastTransition(chkFastTranslation.isSelected());
				getModel().setsurveyId(getSaltString()); //question text changed, again survey ID needs to be updated

			}
		});
		
		txtSurveyName.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				setTitle(txtSurveyName.getText());
				getModel().settitle(txtSurveyName.getText());
				parentTab.setText(txtSurveyName.getText());
				if (txtSurveyName.getText() == null || txtSurveyName.getText().isEmpty()) {
					setTitle("New Survey");
					getModel().settitle("New Survey");
					parentTab.setText("New Survey");
				}
			}
		});

		txtExpiry.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent arg0) {
				try {
					Long.parseLong(arg0.getCharacter());

				} catch (NumberFormatException e) {
					arg0.consume();
					return;
				}

			}
		});
		txtExpiry.textProperty().addListener(change -> {
			getModel().setexpiryTime(Long.parseLong(txtExpiry.getText()));
		});

	}

}
