package com.jeeves.vpl.survey;

import static com.jeeves.vpl.Constants.getSaltString;

//DJR 12th April okay so superficially at least, this class needs to be EXACTLY like the Trigger class,
//and QuestionViews need to be like Actions, I think...if that's what we're going for. 
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.Constants;
import com.jeeves.vpl.Main;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.actions.Action;
import com.jeeves.vpl.canvas.receivers.ActionReceiver;
import com.jeeves.vpl.canvas.receivers.QuestionReceiver;
import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.firebase.FirebaseSurvey;
import com.jeeves.vpl.survey.questions.AnswerControl;
import com.jeeves.vpl.survey.questions.Question;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Survey extends ViewElement<FirebaseSurvey> {

	private ObservableList<Question> surveyQuestions;
	private StringProperty title;
	private QuestionReceiver childReceiver;

	@FXML
	public TextField txtTitle;
	@FXML
	public CheckBox chkExpiry;
	@FXML
	public Label lblFirst;
	@FXML
	public Label lblThird;
	@FXML
	public TextField txtSecond;
	
	public Survey(FirebaseSurvey data) {
		super(data, FirebaseSurvey.class);
		this.model = data;
		int actionNumber = 0;
		if (model.getsurveyId() == null && Constants.shouldUpdateTriggers()){
			model.setsurveyId(getSaltString());
		}
//			if (surveyQuestions != null) {
//			for(Question a : surveyQuestions){
//				childReceiver.addChildAtIndex(a, actionNumber++);
//			}
//			}
	}
	
	private ListChangeListener<Node> receiverListener;
	@Override
	public void addListeners() {
		super.addListeners();

//		//If any new action is added
//		childReceiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
//			if (/*!loading && */Constants.shouldUpdateTriggers()){
//					model.setsurveyId(getSaltString());//(getSaltString()); // Need to update ID if
//			}
//			childReceiver.getChildElements().forEach(this::addChildListener);
//		});
		
		receiverListener = (ListChangeListener.Change<? extends Node> arg0) ->{
				//new question, better update the survey ID!
				getModel().setsurveyId(getSaltString());
				arg0.next();
				if (arg0.wasAdded()) {
					ViewElement<?> added = (ViewElement<?>) arg0.getAddedSubList().get(0);
					int index = childReceiver.getChildElements().indexOf(added);
					model.getquestions().add(index, (FirebaseQuestion) added.getModel());
					addQuestion(index,(Question)added);
				} else {
					Question removed = (Question)arg0.getRemoved().get(0);
					surveyQuestions.remove(removed);
					model.getquestions().remove(removed.getModel());
				}
				
				//Should put this in the AnswerControl class but I'm not in the mood
				List<Question> qSoFar = new ArrayList<Question>();
				for(int i = 0; i < childReceiver.getChildElements().size(); i++) {
					Question q = (Question)childReceiver.getChildElements().get(i);
					if(q.getType() == ElementType.CTRL_QUESTION) {
						((AnswerControl)q).updateChoiceBox(qSoFar);
					}
					else {
						qSoFar.add(q);
					}
				}
		};
	
		childReceiver.getChildElements().addListener(receiverListener);
	}

	public void addQuestion(int index, Question view) {
		surveyQuestions.add(index, view);
		if(view.getQuestionText().isEmpty() && view.getType() != ElementType.CTRL_QUESTION) {
			showEditPane(view);
		}

		view.getEditButton().setOnAction(event -> {
			showEditPane(view);
		});

		view.getQuestionTextProperty().addListener((o,v0,v1) -> {
				if(!v1.isEmpty()) {
					getModel().setsurveyId(getSaltString()); //question text changed, again survey ID needs to be updated
				}
		});

	}

	public void showEditPane(Question view) {
		Stage stage = new Stage(StageStyle.UNDECORATED);
		QuestionEditor editor = new QuestionEditor(stage);
		editor.populateQuestion(view);
		stage.setScene(new Scene(editor));
		stage.setTitle("Edit question");
		stage.initModality(Modality.NONE);
		stage.initOwner(Main.getContext().getStage());
		stage.show();
		stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if(arg2.booleanValue() == false) {
					stage.close();
				}
			}
			
		});
	}
	@Override
	public void fxmlInit() {
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/SurveyOrganiser.fxml"));
		Pane root;

		surveyQuestions = FXCollections.observableArrayList();
		title = new SimpleStringProperty();
		
		try {
			root = surveyLoader.load();
			
			getChildren().add(root);
			childReceiver = new QuestionReceiver();
			getChildren().add(childReceiver);

			double layouty = Math.max(((Pane) root).getPrefHeight(), ((Pane) root).getMinHeight());
			childReceiver.setLayoutY(layouty);
			setPickOnBounds(false);
			((Pane) root).heightProperty().addListener(listen -> {
				double layout = ((Pane) root).getHeight();
				childReceiver.setLayoutY(layout);
			});
	    	getMyReceiver().getBrackets().getStyleClass().remove("trigger");
	    	getMyReceiver().getBrackets().getStyleClass().add("schedule");
			getChildren().forEach(child -> child.setPickOnBounds(false));
			addProperties();
			
		} catch (IOException e) {
			System.exit(1);
		}
	}

	@Override
	public ViewElement<FirebaseSurvey> getInstance() {
		return this;
	}
	public ActionReceiver getMyReceiver() {
		return childReceiver;
	}
	@Override
	public FirebaseSurvey getModel() {
		return model;
	}

	public ObservableList<Question> getQuestions() {
		return surveyQuestions;
	}

	public StringProperty getTitle() {
		return title;
	}
	public void addChildListener(ViewElement element) {
		Question myaction = (Question)element;
		//model.getactions().add((FirebaseAction) element.getModel());
		myaction.getQuestionTextProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				model.setsurveyId(getSaltString());//(getSaltString()); // Again, update,
				
			}
		});
	}

	@Override
	public void setData(FirebaseSurvey model) {
		super.setData(model);
		this.model = model;
		//addProperties();
		txtTitle.setText(model.gettitle());
		//THIS IS TO STOP THEM RENAMING SURVEYS ONCE THE THING IS PUBLISHED
				if(FirebaseDB.getInstance().getOpenProject().getactive() && model.gettitle() != null)
					txtTitle.setDisable(true);
		
		//removeProperties();
		if (model.getexpiryTime() > 0) {
			chkExpiry.setSelected(true);
			txtSecond.setText(Long.toString(model.getexpiryTime()));
		}
		addProperties(); //Straddle this to avoid it updating the salt string
		
		List<FirebaseQuestion> questions = model.getquestions();
		if (questions == null)
			return;
		int index = 0;
		childReceiver.getChildElements().removeListener(receiverListener);
		for (FirebaseQuestion newquestion : questions) {
			Question question = Question.create(newquestion);
			question.setData(newquestion);
			surveyQuestions.add(question);
			childReceiver.addChildAtIndex(question, index);
		//	addQuestion(index++, question);
			
		}
		childReceiver.getChildElements().addListener(receiverListener);
	}

	public void setTitle(String name) {
		title.set(name);
	}

	private void removeProperties() {
		chkExpiry.selectedProperty().removeListener(chkExpiryListener);
	}
	ChangeListener<Boolean> chkExpiryListener;
	ChangeListener<Boolean> chkFastTranslationListener;
	private void addProperties() {
		
		txtTitle.setOnKeyReleased(event -> {
				setTitle(txtTitle.getText());
				getModel().settitle(txtTitle.getText());
				if (txtTitle.getText() == null || txtTitle.getText().isEmpty()) {
					setTitle("New survey");
					getModel().settitle("New survey");
				}
		});

		txtSecond.addEventHandler(KeyEvent.KEY_TYPED,arg0 -> {
				try {
					Long.parseLong(arg0.getCharacter());
				} catch (NumberFormatException e) {
					arg0.consume();
				}
		});
		txtSecond.textProperty().addListener(change -> 
			getModel().setexpiryTime(Long.parseLong(txtSecond.getText()))
			);
	}

	@FXML
	public void showHideExpiry(Event e) {
		getModel().setsurveyId(getSaltString());
		if(chkExpiry.isSelected()) {
			lblFirst.setVisible(true);
			txtSecond.setVisible(true);
			lblThird.setVisible(true);
		}
		else {
			lblFirst.setVisible(false);
			txtSecond.setVisible(false);
			lblThird.setVisible(false);			
		}
	}
}
