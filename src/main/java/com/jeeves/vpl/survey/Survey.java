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
//	@FXML private VBox lstRegQ;
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
//	@FXML private Pane paneInfo;
//	@FXML private Label paneInfoDragged;
//	@FXML private ScrollPane paneScroller;
	@FXML private ComboBox<String> cboSurveys;
	@FXML private Pane paneReceiver;
	@FXML private Pane paneEditor;
	@FXML private Pane paneDropRect;
	private DropShadow shadow = new DropShadow();
	private String name;
	private EventHandler<MouseEvent> qTypeHandler;
	//private String timeAlive = "";
	private Tab parentTab;
	private boolean containsQs = false;
	private int prevIndex = -1;
//	private QuestionView dummyView;
	private ObservableList<QuestionView> surveyQuestions = FXCollections.observableArrayList();
	private ObservableList<FirebaseQuestion> currentelements = FXCollections.observableList(new ArrayList<FirebaseQuestion>());

	private QuestionReceiver receiver;
	private QuestionEditor editor; 
	public Survey(SurveyPane controller, FirebaseSurvey data) {
		super(data, FirebaseSurvey.class);
		this.setName("New Survey");// = "New Survey";
		editor = new QuestionEditor(surveyQuestions);
		paneEditor.getChildren().add(editor);
	}
	private void addProperties() {
	
	
		chkExpiry.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (chkExpiry.isSelected()) {
					txtExpiry.setDisable(false);
					lblExpiry.setDisable(false);
				} else {
					txtExpiry.setText("");
				//	setExpiry(0);
					getModel().setexpiryTime(0);
					txtExpiry.setDisable(true);
					lblExpiry.setDisable(true);
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
	//		setExpiry(Long.parseLong(txtExpiry.getText()));
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
		surveyLoader.setLocation(getClass().getResource("/survey.fxml"));
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
		//		QuestionView entry = (QuestionView) event.getSource();
				if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
//					if (!entry.equals(selectedQuestion)) {
						entry.setEffect(shadow);
						entry.setEffect(bloom);
//					}
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}


	public void addQuestion(int index, QuestionView view){

//		lstRegQ.getChildren().add(index, view);
		surveyQuestions.add(index ,view);
		view.removeEventHandler(MouseEvent.ANY, qTypeHandler);
	//	lstRegQ.setPrefHeight(lstRegQ.getPrefHeight() + view.getHeight());
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
	int counter = 0;
	public void addQuestionBoxListeners(){
		receiver = new QuestionReceiver(paneReceiver.getPrefWidth(),paneReceiver.getPrefHeight());
		receiver.getChildElements().addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> arg0) {
				currentelements.clear();
			//	currentproject.getuidesign().clear();
				model.getquestions().clear();
				
				for(int i = 0; i < receiver.getChildElements().size(); i++){
					ViewElement child = receiver.getChildElements().get(i);
					model.getquestions().add((FirebaseQuestion)child.getModel());
					currentelements.add((FirebaseQuestion)child.getModel());
//					((FirebaseQuestion)child.getModel()).getMyTextProperty().addListener(change->{
//						receiver.getChildElements().remove(child);
//						receiver.getChildElements().add(child);
					addQuestion(counter,(QuestionView)child);
				}
				
			}

		});
		paneReceiver.getChildren().add(receiver);
		receiver.addDummyView(paneDropRect, 0); //Our dummy 'drop this here' rectangle thing
		
//		lstRegQ.addEventHandler(MouseDragEvent.ANY, new EventHandler<MouseDragEvent>(){
//			@Override
//			public void handle(MouseDragEvent event) {
//				if(event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_ENTERED)){
//			     	lstRegQ.getStyleClass().add("drop_shadow");
//
//				}
//				else if(event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_EXITED)){
//					lstRegQ.getStyleClass().remove("drop_shadow");
//
//				}
//				else if(event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_OVER)){
//					if (!(event.getGestureSource() instanceof QuestionView))
//						return;
//					else {
//						Point2D mousePos = lstRegQ.sceneToLocal(new Point2D(event.getSceneX(), event.getSceneY()));
//						double questionHeight = ((QuestionView) event.getGestureSource()).getHeight();
//						int index = (int) (mousePos.getY() / questionHeight);
//						if (index < 0)
//							index = 0;
//						if (index != prevIndex) {
//							if (index >= lstRegQ.getChildren().size())
//								index = lstRegQ.getChildren().size() - 1; 
//							prevIndex = index;
////							lstRegQ.getChildren().remove(dummyView);
////							lstRegQ.getChildren().add(index, dummyView);
//						}
//						lstRegQ.getChildren().forEach(child -> child.setMouseTransparent(true));
//					}
//
//				}
//				else if(event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_RELEASED)){
//					if (!(event.getGestureSource() instanceof QuestionView))
//						return;
//					else {
//						int index = 0;
//						for (int i = 0; i < lstRegQ.getChildren().size(); i++) {
//							Point2D point = lstRegQ.sceneToLocal(event.getSceneX(), event.getSceneY());
//							System.out.println("release point is " + point.getX() + "," + point.getY());
//							Pane elem = ((Pane) lstRegQ.getChildren().get(i));
//							double max = (elem.getLayoutY() + elem.getHeight());
//							System.out.println("this max is " + max);
//							if (point.getY() < max) {
//								break;
//							}
//							index = i+1;
//
//						}
////						getChildren().remove(paneInfo);
////						paneInfoDragged.setVisible(false);
////						getChildren().remove(paneInfoDragged);
//						//int index = lstRegQ.getChildren().indexOf(dummyView);
//						QuestionView view = (QuestionView) event.getGestureSource();
//						if (index >= lstRegQ.getChildren().size()){
//							index = lstRegQ.getChildren().size()-1;
//						}
//						addQuestion(index, view);
//						EventHandler<MouseEvent> removeHandler = new EventHandler<MouseEvent>() {
//							public void handle(MouseEvent e) {
//								e.consume();
//								if (e.isSecondaryButtonDown()) {
//									return;
//								}
//				
//								removeChild(view);
//								view.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
//							}
//						};
//				
//						view.addEventHandler(MouseEvent.MOUSE_PRESSED, removeHandler);
//						if(view.getQuestionText().isEmpty())
//							view.setQuestionText(view.getLabel());
//						if (index >= getModel().getquestions().size())
//							index -= 1;
//						if(index > 0)
//							getModel().getquestions().add(index, view.getModel());
//						else
//							getModel().getquestions().add(view.getModel());
////						lstRegQ.getChildren().remove(dummyView);
////						lstRegQ.getChildren().remove(paneInfo);
//						editor.populateQ(view.getModel());
//						containsQs = true;
//	//					paneScroller.setStyle("");
//
//						view.setManaged(true);
//						view.setMouseTransparent(false);
////						lstRegQ.getChildren().forEach(child -> {
////							child.setMouseTransparent(false);
////						});
//
//					}
//				}
//			}
//		});

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

//		for (int i = 1; i <= NUMBER_OF_TYPES; i++) {
//			FirebaseQuestion q = new FirebaseQuestion();
//			q.setquestionType(i);
//			QuestionView view = QuestionView.create(q);
//		//	flowPaneQuestionTypes.getChildren().add(view);
//			QuestionView draggable = QuestionView.create(q);
//		//	ViewElement<FirebaseQuestion> draggable = ViewElement.create(view.getClass().getName());
//			view.setReadOnly();
//			view.setDraggable(draggable); // DJRNEW
////			setElementParent(draggable);
////			view.setReadOnly();
////			element.setHandler(viewElementHandler);
//			view.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
//
//				@Override
//				public void handle(MouseEvent arg0) {
//					// TODO Auto-generated method stub
//					if(containsQs == false){
//						paneScroller.setStyle("-fx-border-color: #71a4eb; -fx-border-width: 5");
//						paneInfo.getChildren().forEach(child->child.setVisible(false));
//						paneInfoDragged.setVisible(true);
//						}
//				}
//			});
//		}
//			Pane imgPane = new Pane();
//			ImageView myview = new ImageView(view.getImagePath());
//			myview.setFitHeight(50);
//			myview.setFitWidth(70);
//			imgPane.setPrefWidth(70);
//			imgPane.setPrefHeight(50);
//			imgPane.getChildren().add(myview);
//			view.setVisible(false);
//			imgPane.getChildren().add(view);
//			imgPane.setStyle("-fx-background-color: white");
//			flowPaneQuestionTypes.getChildren().add(imgPane);
//			view.setReadOnly(true);
//			imgPane.addEventHandler(MouseEvent.ANY,new EventHandler<MouseEvent>(){
//				public void handle(MouseEvent event){
//					if(event.isSecondaryButtonDown()){event.consume();return;}
//					setOnDragDetected(event1 ->{if(event1.isSecondaryButtonDown())return; view.startFullDrag(); System.out.println("VIEW DRAG STARTED");});
//				if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
//					gui.getMainPane().getChildren().add(view);
//					if(containsQs == false){
//						paneScroller.setStyle("-fx-border-color: #71a4eb; -fx-border-width: 5");
//						paneInfo.getChildren().forEach(child->child.setVisible(false));
//						paneInfoDragged.setVisible(true);
//						}
//					view.setVisible(true);
//					view.setMouseTransparent(true);
//					view.setLayoutX(event.getSceneX());
//					view.setLayoutY(event.getSceneY());//Should hopefully add it to the main pane
//				//	view.setEffect(shadow);
//				//	view.currentCanvas = MainController.currentGUI.getViewCanvas(); //I don't like this much
//					setEffect(null);
//				} else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
//					view.setLayoutX(event.getSceneX());
//					view.setLayoutY(event.getSceneY());
//				
//				} else if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
//					setCursor(Cursor.HAND);
//			    // 	setEffect(shadow);
//			   //  	MainController.currentGUI.mnuFile.hide();
//				} 
//				else if (event.getEventType().equals(MouseEvent.MOUSE_EXITED)){
//					setEffect(null);
//				}
//				else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
//					gui.getMainPane().getChildren().remove(view);
//					imgPane.getChildren().add(view);
//					view.setVisible(false);
//					//draggable.setEffect(null);
//				}
//			}});
			
		//	view.setDraggable(QuestionView.create(q, this));

		
	

		//setQuestionType(questionType);
	//	getStyleClass().add("bordered");
//		btnDeleteQ.setOnMouseReleased(new EventHandler<MouseEvent>(){
//
//			@Override
//			public void handle(MouseEvent event) {
//				Stage stage = new Stage(StageStyle.UNDECORATED);
//				QuestionDeletePane root = new QuestionDeletePane(getInstance(),stage);
//				stage.setScene(new Scene(root));
//				stage.initModality(Modality.APPLICATION_MODAL);
//				stage.showAndWait();
//			}
//
//		});
//		btnEdit.setOnMouseReleased(new EventHandler<MouseEvent>(){
//
//			@Override
//			public void handle(MouseEvent event) {
//		//		mySurvey.populateQuestion(getInstance());
//			}
//
//		});
	//	setExpiry(model.gettimeAlive());
		model.getquestions();
		List<FirebaseQuestion> questions = model.getquestions();

		if (questions == null)
			return;
//		if(!questions.isEmpty())
//			lstRegQ.getChildren().clear(); //Get rid of intoductory pane
		int index = 0;
		for (FirebaseQuestion newquestion : questions) {
			QuestionView question = QuestionView.create(newquestion);
			// new QuestionView(new);
			question.setData(newquestion);
			//setSelectedQuestion(question);
//			lstRegQ.getChildren().add(question);
//			surveyQuestions.add(question);
			addQuestion(index,question);
			question.showDelete();
		//	question.setMyIndex(index);
			editor.populateQ(newquestion);
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
	public void removeQuestion(QuestionView question) {
		if(model.getquestions().contains(question.getModel())){
		model.getquestions().remove(question.getModel());
		System.out.println("WHAAAAAAT");
		receiver.removeChild(question);
		}
	}

//
//
//
//
//
//
//	public void setSelectedQuestion(QuestionView entry) {
//		if (entry == null) {
//			grpSurveyEdit.setDisable(true);
//		} else
//			grpSurveyEdit.setDisable(false);
//	}

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
//	public void setExpiry(long expiryTime) {
//		this.timeAlive = Long.toString(expiryTime);
//	}
//
//	public String getExpiry() {
//		return timeAlive;
//	}
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
