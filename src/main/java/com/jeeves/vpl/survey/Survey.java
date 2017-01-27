package com.jeeves.vpl.survey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import com.jeeves.vpl.MainController;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.firebase.FirebaseSurvey;

public class Survey extends ViewElement<FirebaseSurvey> {
	@FXML
	private CheckBox chkExpiry;
	@FXML
	private Label lblExpiry;
	@FXML
	private TextField txtExpiry;
	@FXML
	private TextField txtSurveyName;

	@FXML
	private VBox lstRegQ;
	@FXML
	private FlowPane flowPaneQuestionTypes;
	//private VBox vboxQuestionTypes;
	@FXML
	private TextField txtQText;
	@FXML
	private TextField txtSurveyDesc;
	@FXML
	private TextField txtDescription;
	@FXML
	private CheckBox chkAskOnCondition;
	@FXML
	private CheckBox chkAssignToVar;
	@FXML
	private Pane paneAssignToVar;
	@FXML
	private HBox hboxQuestionCondition;
	@FXML
	private Button btnAddChoiceOpt;
	@FXML
	private Pane paneScale;

	@FXML
	private ComboBox<String> cboQuestionText;
	@FXML
	public TextField txtAnswer;
	@FXML
	public TextField txtNumAnswer;
	@FXML
	public RadioButton rdioTrue;
	@FXML
	public RadioButton rdioFalse;
	@FXML
	public ComboBox<String> cboMultiChoice;
	@FXML
	public ComboBox<String> cboLessMore;

	//private DropShadow selshadow = new DropShadow();
	private DropShadow shadow = new DropShadow();
	private SurveyController controller;

	@FXML
	private TabPane paneSurveys;
	@FXML
	private Pane paneCondition;
	
	@FXML
	private ScrollPane paneScrollVars;
	// @FXML private VBox paneVariables;
	@FXML
	private Group grpSurveyEdit;
	@FXML
	private Label lblSaved;
	@FXML
	private TextField txtGeo;


	private ArrayList<Pane> questionPanes;
	private ArrayList<Node> conditionPanes;
	private EventHandler<MouseEvent> qTypeHandler;
	private QuestionView selectedQuestion;
	
	private QuestionView conditionQuestion;
	private FirebaseQuestion selectedQuestionModel;
	@FXML
	private ComboBox<String> cboSurveys;
	private String timeAlive = "";
	public boolean wasDragged;
	@FXML
	private VBox vboxOpts;
	@FXML
	private ComboBox<UserVariable> cboVars;
	private Tab parentTab;

	@FXML
	private Pane paneInfo;
	@FXML 
	private Label paneInfoDragged;
	@FXML
	private ScrollPane paneScroller;
	private boolean containsQs = false;
	void refreshVariables(){
		cboVars.getItems().clear();
		MainController.currentGUI.currentvariables.forEach(entry -> {
			if (entry.isCustom) {
				UserVariable uservar = new UserVariable(entry);
				cboVars.getItems().add(uservar);
				uservar.removeHander();
			}
		});
	}
	void addProperties() {

		MainController.currentGUI.currentvariables.addListener(new ListChangeListener<Object>() {
			@Override
			public void onChanged(Change<?> c) {
				refreshVariables();
			}
		});
		refreshVariables();
		
		chkExpiry.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (chkExpiry.isSelected()) {
					txtExpiry.setDisable(false);
					lblExpiry.setDisable(false);
				} else {
					txtExpiry.setText("");
					setExpiry(0);
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
					Long isValid = Long.parseLong(arg0.getCharacter());

				} catch (NumberFormatException e) {
					arg0.consume();
					return;
				}

			}
		});
		txtExpiry.textProperty().addListener(change -> {
			setExpiry(Long.parseLong(txtExpiry.getText()));
			getModel().setexpiryTime(Long.parseLong(txtExpiry.getText()));
		});

	}

	int prevIndex = -1;
	QuestionView dummyView;

	ToggleGroup tgroup;
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
			surveynode.setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					arg0.consume();
				}});
			tgroup = new ToggleGroup();
			rdioTrue.setToggleGroup(tgroup);
			rdioFalse.setToggleGroup(tgroup);
			cboLessMore.getItems().addAll("less than", "more than", "equal to");
			addConditionListeners();
			addQuestionBoxListeners();
			
			cboVars.setCellFactory(new Callback<ListView<UserVariable>, ListCell<UserVariable>>() {
				 @Override
				 public ListCell<UserVariable> call(ListView<UserVariable> param) {
				  
				  return new ListCell<UserVariable>(){
				   @Override
				   public void updateItem(UserVariable item, boolean empty){
				    super.updateItem(item, empty);
				    if(!empty) {
				     setText(item.getName());
				     getStyleClass().add(item.varType);
				     this.setAlignment(Pos.CENTER);
				     this.setFont(Font.font("Calibri",FontWeight.BOLD,13.0));
				     this.setTextFill(Color.WHITE);
				     this.setTextAlignment(TextAlignment.CENTER);
				     getStyleClass().remove("combo-box-base");
				     setGraphic(null);
				    }
				   }
				  };
				 }
				});
			cboVars.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<UserVariable>(){

				@Override
				public void changed(ObservableValue<? extends UserVariable> observable, UserVariable oldValue,
						UserVariable newValue) {
					if(newValue != null)
					selectedQuestionModel.setAssignedVar(newValue.getName());
				}
				
			});

			txtNumAnswer.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent arg0) {
					if(arg0.getEventType().equals(KeyEvent.KEY_TYPED))
					try {
						Long isValid = Long.parseLong(arg0.getCharacter());
					} catch (NumberFormatException e) {						
						arg0.consume();
						return;
					}
				}
			});
			
			chkAskOnCondition.selectedProperty().addListener(new ChangeListener<Boolean>() {
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
					handleCheckQuestion();
				}
			});
			chkAssignToVar.selectedProperty().addListener(new ChangeListener<Boolean>() {
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
					handleAssignToVar();
				}
			});


			cboQuestionTextListener = new ChangeListener<String>() {

				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					int questionNo = cboQuestionText.getSelectionModel().getSelectedIndex();
					if (questionNo < 0)
						return;
					hideConditionPanes();
					QuestionView question = (QuestionView)lstRegQ.getChildren().get(questionNo);
					removeConditionListeners();
					question.showCheckQOpts();
					addConditionListeners();
					conditionQuestion = question;
				}

			};
			cboQuestionText.getSelectionModel().selectedItemProperty().addListener(cboQuestionTextListener);
			paneAssignToVar.getChildren().forEach(child -> {
				((Node) child).setDisable(true);
				;
			});
			paneCondition.getChildren().forEach(child -> {
				((Node) child).setDisable(true);
				;
			});
			grpSurveyEdit.setDisable(true);

			questionPanes = new ArrayList<Pane>();
			Collections.addAll(questionPanes,
					paneScale/* ,paneMultChoiceS,paneMultChoiceM */);
			conditionPanes = new ArrayList<Node>();
			Collections.addAll(conditionPanes, txtAnswer, txtNumAnswer, cboLessMore, cboMultiChoice, rdioTrue,
					rdioFalse);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public Survey(SurveyController controller, FirebaseSurvey data) {
		super(data, FirebaseSurvey.class);
		this.controller = controller;
		this.name.setValue("New Survey");// = "New Survey";
	}
	
	public void addQuestionBoxListeners(){
		lstRegQ.addEventHandler(MouseDragEvent.ANY, new EventHandler<MouseDragEvent>(){
			@Override
			public void handle(MouseDragEvent event) {
				if(event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_ENTERED)){
					((QuestionView) event.getGestureSource()).wasDetected = true;

					dummyView = ((QuestionView) event.getGestureSource()).clone();
					dummyView.setVisible(false);
					lstRegQ.getChildren().add(dummyView);
				}
				else if(event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_EXITED)){
					lstRegQ.getChildren().remove(dummyView);
					lstRegQ.getChildren().forEach(child -> child.setMouseTransparent(false));
				}
				else if(event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_OVER)){
					wasDragged = true;
					if (!(event.getGestureSource() instanceof QuestionView))
						return;
					else {
						QuestionView view = (QuestionView) event.getGestureSource();
						view.wasDetected = true;
						selectedQuestion = null;
						Point2D mousePos = lstRegQ.sceneToLocal(new Point2D(event.getSceneX(), event.getSceneY()));
						double questionHeight = ((QuestionView) event.getGestureSource()).getHeight();
						int index = (int) (mousePos.getY() / questionHeight);
						if (index < 0)
							index = 0;
						if (index != prevIndex) {
							if (index >= lstRegQ.getChildren().size())
								index = lstRegQ.getChildren().size() - 1; 
							prevIndex = index;
							lstRegQ.getChildren().remove(dummyView);
							lstRegQ.getChildren().add(index, dummyView);
						}
						lstRegQ.getChildren().forEach(child -> child.setMouseTransparent(true));
					}

				}
				else if(event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_RELEASED)){
					if (!(event.getGestureSource() instanceof QuestionView))
						return;
					else {
						System.out.println("yaaaaaaa");

						int index = lstRegQ.getChildren().indexOf(dummyView);
						if (index == -1)
							index = ((QuestionView) event.getGestureSource()).getMyIndex();
						QuestionView view = (QuestionView) event.getGestureSource();
					//	if(lstRegQ.getChildren().contains(view))return;
					//	removeQ(view);
						lstRegQ.getChildren().add(index, QuestionView.create(view.getModel(), getMyInstance()));
						view.removeEventHandler(MouseEvent.ANY, qTypeHandler);
						lstRegQ.setPrefHeight(lstRegQ.getPrefHeight() + view.getHeight());
						view.isReadOnly = false;

						if (index >= getModel().getquestions().size())
							index -= 1;
						if(index > 0)
							getModel().getquestions().add(index, view.getModel());
						else
							getModel().getquestions().add(view.getModel());
						lstRegQ.getChildren().remove(dummyView);
						lstRegQ.getChildren().remove(paneInfo);
						populateQ(view.getModel());
						containsQs = true;
						paneScroller.setStyle("");

						view.setManaged(true);
						view.setMouseTransparent(false);
						view.showDelete();
						lstRegQ.getChildren().forEach(child -> {
							child.setMouseTransparent(false);
						});

					}
				}
			}
		});

	}
	public ChangeListener<String> freeTextListener;
	public ChangeListener<String> numTextListener;
	public ChangeListener<String> multiChoiceListener;
	public ChangeListener<String> lessMoreListener;
	public ChangeListener<Toggle> trueFalseListener;
	public void addConditionListeners(){
		freeTextListener = new ChangeListener<String>(){
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				handleUpdateCondition(txtAnswer.getText());
			}};
		numTextListener = new ChangeListener<String>(){
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				handleUpdateCondition(cboLessMore.getValue() + ";" + txtNumAnswer.getText());		
			}};
		multiChoiceListener = new ChangeListener<String>(){
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue != null)
				handleUpdateCondition(newValue);				
			}};
		lessMoreListener = new ChangeListener<String>(){
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue != null)
				handleUpdateCondition(cboLessMore.getValue() + ";" + txtNumAnswer.getText());				
			}};
		trueFalseListener = new ChangeListener<Toggle>(){
			@Override public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				handleUpdateCondition(rdioTrue.isSelected() ? "true" : "false");				
			}};
			txtAnswer.textProperty().addListener(freeTextListener);
			txtNumAnswer.textProperty().addListener(numTextListener);
			cboMultiChoice.getSelectionModel().selectedItemProperty().addListener(multiChoiceListener);
			cboLessMore.getSelectionModel().selectedItemProperty().addListener(lessMoreListener);
			tgroup.selectedToggleProperty().addListener(trueFalseListener);
	}
	
	public void removeConditionListeners(){
		txtAnswer.textProperty().removeListener(freeTextListener);
		txtNumAnswer.textProperty().addListener(numTextListener);
		cboMultiChoice.getSelectionModel().selectedItemProperty().addListener(multiChoiceListener);
		cboLessMore.getSelectionModel().selectedItemProperty().addListener(lessMoreListener);
		tgroup.selectedToggleProperty().addListener(trueFalseListener);
	}
	
	private ChangeListener<String> listener;
	
	public void populateQuestion(QuestionView entry) {
		setSelectedQuestion(entry);
		selectedQuestionModel = entry.getModel();
		populateQ(entry.getModel());
		vboxOpts.getChildren().remove(3,vboxOpts.getChildren().size()); //leave the qtext
		if(listener != null)
		txtQText.textProperty().removeListener(listener);
		listener = new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				entry.setQuestionText(newValue);
			}			
		};
		txtQText.textProperty().addListener(listener);
		
		txtQText.setText(selectedQuestionModel.getquestionText());
		vboxOpts.getChildren().add(entry.optionsPane);
		vboxOpts.getChildren().forEach(child->child.setVisible(true));
		lstRegQ.getChildren().forEach(label -> {
			label.getStyleClass().remove("borderedselected");
		});
		entry.getStyleClass().add("borderedselected");
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

		for (int i = 1; i <= QuestionView.NUMBER_OF_TYPES; i++) {
			FirebaseQuestion q = new FirebaseQuestion();
			q.setquestionType(i);
			QuestionView view = QuestionView.create(q, this);
			Pane imgPane = new Pane();
			ImageView myview = new ImageView(view.getImagePath());
			myview.setFitHeight(50);
			myview.setFitWidth(70);
			imgPane.setPrefWidth(70);
			imgPane.setPrefHeight(50);
			imgPane.getChildren().add(myview);
			view.setVisible(false);
			imgPane.getChildren().add(view);
			imgPane.setStyle("-fx-background-color: white");
			flowPaneQuestionTypes.getChildren().add(imgPane);
			view.setReadOnly();
			imgPane.addEventHandler(MouseEvent.ANY,new EventHandler<MouseEvent>(){
				public void handle(MouseEvent event){
					if(event.isSecondaryButtonDown()){event.consume();return;}
					setOnDragDetected(event1 ->{if(event1.isSecondaryButtonDown())return; view.startFullDrag();});
				if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
					MainController.currentGUI.getMainPane().getChildren().add(view);
					if(containsQs == false){
						paneScroller.setStyle("-fx-border-color: #71a4eb; -fx-border-width: 5");
						paneInfo.getChildren().forEach(child->child.setVisible(false));
						paneInfoDragged.setVisible(true);
						}
					view.setVisible(true);
					view.setMouseTransparent(true);
					view.setLayoutX(event.getSceneX());
					view.setLayoutY(event.getSceneY());//Should hopefully add it to the main pane
				//	view.setEffect(shadow);
				//	view.currentCanvas = MainController.currentGUI.getViewCanvas(); //I don't like this much
					setEffect(null);
				} else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
					view.setLayoutX(event.getSceneX());
					view.setLayoutY(event.getSceneY());
				
				} else if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
					setCursor(Cursor.HAND);
			    // 	setEffect(shadow);
			   //  	MainController.currentGUI.mnuFile.hide();
				} 
				else if (event.getEventType().equals(MouseEvent.MOUSE_EXITED)){
					setEffect(null);
				}
				else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
					MainController.currentGUI.getMainPane().getChildren().remove(view);
					imgPane.getChildren().add(view);
					view.setVisible(false);
					//draggable.setEffect(null);
				}
			}});
			
		//	view.setDraggable(QuestionView.create(q, this));

		}
	
		setExpiry(model.gettimeAlive());
		model.getquestions();
		List<FirebaseQuestion> questions = model.getquestions();

		if (questions == null)
			return;
		int index = 0;
		for (FirebaseQuestion newquestion : questions) {
			QuestionView question = QuestionView.create(newquestion, this);
			// new QuestionView(new);
			question.setData(newquestion, this);
			setSelectedQuestion(question);
			selectedQuestionModel = newquestion;
			lstRegQ.getChildren().add(question);
			question.showDelete();
			question.setMyIndex(index);
			populateQ(newquestion);
		}
		setSelectedQuestion(null);

		flowPaneQuestionTypes.getChildren().forEach(label -> {
			label.addEventHandler(MouseEvent.ANY, qTypeHandler);
		});
	}
	public Survey getMyInstance(){
		return this;
	}


	public void removeQ(QuestionView question) {
		if(model.getquestions().contains(question.getModel())){
		model.getquestions().remove(question.getModel());
		lstRegQ.getChildren().remove(question);
		}
	}

	@SuppressWarnings("unchecked")
	private void populateQ(FirebaseQuestion qEntry) {

		Map<String, Object> opts = null;
		Map<String, Object> qParams = qEntry.getparams(); // (Map<String,Object>)questionData.get("params");
		if (qParams != null) {
			opts = (Map<String, Object>) qParams.get("options");
			Map<String, Object> condition = (Map<String, Object>) qParams.get("condition");

			if (qEntry.getassignedVar() != null && !qEntry.getassignedVar().equals("")) {
				chkAssignToVar.setSelected(true);
				cboVars.getItems().forEach(variable->{
					if(variable.getName().equals(qEntry.getassignedVar())){
						cboVars.getSelectionModel().select(variable);
					}
				});
			} else
				chkAssignToVar.setSelected(false);

			cboQuestionText.getItems().clear();

			for (Node qentries : lstRegQ.getChildren()) {
				if(!(qentries instanceof QuestionView))continue;
				QuestionView preventry = (QuestionView) qentries;
				if (preventry.getModel().equals(qEntry))
					break;
				 cboQuestionText.getItems().add(preventry.getQuestionText());
			}

			hideConditionPanes();
			if (condition != null) {
				chkAskOnCondition.setSelected(true);

				if (condition.get("question") != null) {
					int questionno = Integer.parseInt(condition.get("question").toString());
					cboQuestionText.getSelectionModel().clearSelection();
					cboQuestionText.getSelectionModel().select(questionno);
					if (condition.get("answer") != null) {

						String scon = condition.get("answer").toString();
						if (conditionQuestion != null){
							conditionQuestion.handleCheckQ(scon);
						}
					}
				}

			} else {
				//selectedQuestion.handleCheckQ("");
				chkAskOnCondition.setSelected(false);
			}
		}
		if (selectedQuestion == null)
			return;
		selectedQuestion.showEditOpts(opts);
	}

	private void hideConditionPanes() {
		conditionPanes.forEach(pane -> {
			pane.setVisible(false);
		});
	}


	private ChangeListener cboQuestionTextListener;
	private void handleCheckQuestion() {
		if (chkAskOnCondition.isSelected()) {
			paneCondition.getChildren().forEach(child -> {
				((Node) child).setDisable(false);
				;
			});
		} else {
			paneCondition.getChildren().forEach(child -> {
				((Node) child).setDisable(true);
				;
			});

			selectedQuestionModel.setCondition(null);
			txtAnswer.setText("");
			cboQuestionText.getSelectionModel().clearSelection();

		}
	}

	/**
	 * This method enables and disables relevant controls depending on whether
	 * the user wants to ask a question based on a condition
	 */
	private void handleAssignToVar() {
		if (chkAssignToVar.isSelected()) {
			paneAssignToVar.getChildren().forEach(child -> {
				((Node) child).setDisable(false);
				;
			});
			paneAssignToVar.setPrefHeight(USE_COMPUTED_SIZE);
		} else {
			paneAssignToVar.getChildren().forEach(child -> {
				((Node) child).setDisable(true);
				;
			});
			paneAssignToVar.setPrefHeight(0);
			selectedQuestionModel.setAssignedVar("");
		}
	}

	private void handleUpdateCondition(String answer) {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("question", cboQuestionText.getSelectionModel().getSelectedIndex());
		condition.put("answer", answer);
		selectedQuestionModel.setCondition(condition);
	}

	public void setSelectedQuestion(QuestionView entry) {
		selectedQuestion = entry;
		if (entry == null) {
			grpSurveyEdit.setDisable(true);
		} else
			grpSurveyEdit.setDisable(false);
	}

	public void setTab(Tab tab) {
		this.parentTab = tab;

	}

	public Tab getParentTab(){
		return parentTab;
	}
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
	public void setExpiry(long expiryTime) {
		this.timeAlive = Long.toString(expiryTime);
	}

	public String getExpiry() {
		return timeAlive;
	}
}
