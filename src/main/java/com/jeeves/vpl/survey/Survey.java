package com.jeeves.vpl.survey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.QuestionReceiver;
import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.firebase.FirebaseSurvey;
import com.jeeves.vpl.survey.questions.QuestionView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
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
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
public class Survey extends ViewElement<FirebaseSurvey> {
	@FXML private CheckBox chkExpiry;
	@FXML private Label lblExpiry;
	@FXML private TextField txtExpiry;
	@FXML private TextField txtSurveyName;
	@FXML private TextField txtSurveyDesc;
	@FXML private TextField txtDescription;
	@FXML private HBox hboxQuestionCondition;
	@FXML private Button btnAddChoiceOpt;
	@FXML private Pane paneScale;
	@FXML private TabPane paneSurveys;
	@FXML private ScrollPane paneScrollVars;
	@FXML private Group grpSurveyEdit;
	@FXML private Label lblSaved;
	@FXML private TextField txtGeo;
	@FXML private ComboBox<String> cboSurveys;
	@FXML private Pane paneReceiver;
	@FXML private Pane paneEditor;
	@FXML private Pane paneDropRect;

	private DropShadow shadow = new DropShadow();
	private String name;
	private EventHandler<MouseEvent> qTypeHandler;
	private Tab parentTab;

	private ObservableList<QuestionView> surveyQuestions = FXCollections.observableArrayList();
//	private ObservableList<FirebaseQuestion> currentelements = FXCollections.observableList(new ArrayList<FirebaseQuestion>());

	private QuestionReceiver receiver;
	private QuestionEditor editor; 

	private static int lastQuestionId = 0;
	public Survey(SurveyPane controller, FirebaseSurvey data) {
		super(data, FirebaseSurvey.class);
		this.setName("New Survey");
		editor = new QuestionEditor(surveyQuestions);
		paneEditor.getChildren().add(editor);
	}

	private void addProperties() {
		chkExpiry.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (chkExpiry.isSelected()) {
					txtExpiry.setDisable(false);
				//	lblExpiry.setDisable(false);
				} else {
					txtExpiry.setText("");
					getModel().setexpiryTime(0);
					txtExpiry.setDisable(true);
				//	lblExpiry.setDisable(true);
				}
			}
		});


		txtSurveyName.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				setName(txtSurveyName.getText());
				getModel().setname(txtSurveyName.getText());
				parentTab.setText(txtSurveyName.getText());
				if (txtSurveyName.getText().isEmpty()) {
					setName("New Survey");
					getModel().setname("New Survey");
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



	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	public void fxmlInit() {
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/Survey.fxml"));
		Pane surveynode;
		shadow = new DropShadow();
		shadow.setWidth(25);
		shadow.setHeight(25);
		shadow.setRadius(15);
		shadow.setSpread(0.2);
		shadow.setColor(Color.LIGHTBLUE);
		Bloom bloom = new Bloom();
		bloom.setThreshold(0.75);
		qTypeHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				Pane entry = (Pane)event.getSource();
				if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
					entry.setEffect(shadow);
					entry.setEffect(bloom);
					setCursor(Cursor.HAND);

				} else if (event.getEventType().equals(MouseEvent.MOUSE_EXITED)) {
					entry.setEffect(null);
					setCursor(Cursor.DEFAULT);
				}
			}

		};
		try {
			surveynode = (Pane) surveyLoader.load();

			this.getChildren().add(surveynode);
			addProperties();
			addQuestionBoxListeners();

			surveynode.setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					arg0.consume();
				}});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void addQuestion(int index, QuestionView view){
		surveyQuestions.add(index ,view);
		view.removeEventHandler(MouseEvent.ANY, qTypeHandler);
		view.setReadOnly(false);
		view.addButtons();
		view.getEditButton().setOnAction(event->{editor.populateQuestion(view);paneEditor.setDisable(false);});
		view.getDeleteButton().setOnAction(event->{
			Stage stage = new Stage(StageStyle.UNDECORATED);
			QuestionDeletePane root = new QuestionDeletePane(this,view,stage);
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
		});

	}

	public void removeQuestion(QuestionView question) {
		if(model.getquestions().contains(question.getModel())){
			model.getquestions().remove(question.getModel());
			receiver.removeChild(question);
			if(question.getParentQuestion() != null)
				question.getParentQuestion().removeChildQuestion(question);	


		}
		//Time to referesh
		editor.refresh();
	}
	
	public void addQuestionBoxListeners(){
		receiver = new QuestionReceiver(paneReceiver.getPrefWidth(),paneReceiver.getPrefHeight());
		receiver.getChildElements().addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> arg0) {
			//	currentelements.clear();
				model.getquestions().clear();
				
				for(int i = 0; i < receiver.getChildElements().size(); i++){
					ViewElement child = receiver.getChildElements().get(i);
					model.getquestions().add((FirebaseQuestion)child.getModel());
				//	currentelements.add((FirebaseQuestion)child.getModel());
					((FirebaseQuestion)child.getModel()).setquestionId(lastQuestionId++);
					addQuestion(i,(QuestionView)child);
				}
				
				//We also want to notify our question editor
				arg0.next();
				if(arg0.wasAdded()){
					editor.populateQuestion((QuestionView)arg0.getAddedSubList().get(0));
					paneEditor.setDisable(false);
				}
				else{
					QuestionView removed = (QuestionView)arg0.getRemoved().get(0);
					if(editor.getSelectedQuestion() != null && editor.getSelectedQuestion().equals(removed)){
						paneEditor.getChildren().remove(editor);
						editor = new QuestionEditor(surveyQuestions);
						paneEditor.getChildren().add(editor);
						paneEditor.setDisable(true);
					}
				}
				
				
			}

		});
		paneReceiver.getChildren().add(receiver);
		receiver.addDummyView(paneDropRect, 0); 
	}



	public void setData(FirebaseSurvey model) {
		super.setData(model);
		this.model = model;
		addProperties();
		txtSurveyName.setText(model.getname());
		if (model.getexpiryTime() > 0) {
			chkExpiry.setSelected(true);
			txtExpiry.setText(Long.toString(model.getexpiryTime()));
		}

		model.getquestions();
		List<FirebaseQuestion> questions = model.getquestions();

		if (questions == null)
			return;

		int index = 0;
		for (FirebaseQuestion newquestion : questions) {
			QuestionView question = QuestionView.create(newquestion);
			question.setData(newquestion);
			addQuestion(index,question);
			question.showDelete();
		//	editor.populateQ(newquestion);
			index++;
		}
		//setSelectedQuestion(null);

		//		flowPaneQuestionTypes.getChildren().forEach(label -> {
		//			label.addEventHandler(MouseEvent.ANY, qTypeHandler);
		//		});
	}
	//	public Survey getMyInstance(){
	//		return this;
	//	}
	//
	//


	public void setTab(Tab tab) {
		this.parentTab = tab;
	}

	public Tab getParentTab(){
		return parentTab;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ViewElement getInstance() {
		return this;
	}
	@Override
	public String toString() {
		return getName();
	}
	@Override
	public Node[] getWidgets() {
		return new Node[] {};
	}
	@Override
	public FirebaseSurvey getModel() {
		return model;
	}
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
