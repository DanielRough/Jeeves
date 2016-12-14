package com.jeeves.vpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.firebase.FirebaseSurvey;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class Survey extends ViewElement<FirebaseSurvey>{
	@FXML private CheckBox chkExpiry;
	@FXML private Label lblExpiry;
	@FXML private TextField txtExpiry;
	@FXML private TextField txtSurveyName;
	
	@FXML private VBox lstRegQ;
	
	@FXML private RadioButton rdioRegMultMany;
	@FXML private RadioButton rdioRegMultSingle;
	@FXML private RadioButton rdioRegOpen;
	@FXML private RadioButton rdioRegScale;
	@FXML private RadioButton rdioRegNum;
	@FXML private RadioButton rdioRegBool;
	@FXML private RadioButton rdioRegDate;
	@FXML private RadioButton rdioRegGeo;
	
///	@FXML private TextField txtMin;
//	@FXML private TextField txtMax;
//	@FXML private TextField txtScaleMax;
	@FXML private RadioButton rdioButton5;
	@FXML private RadioButton rdioButton7;
	@FXML private TextField txtLikert1;
	@FXML private TextField txtLikert2;
	@FXML private TextField txtLikert3;
	@FXML private TextField txtLikert4;
	@FXML private TextField txtLikert5;
	@FXML private TextField txtLikert6;
	@FXML private TextField txtLikert7;
	private TextField[] fields;
	@FXML private TextArea txtRegQ;
	@FXML private TextField txtSurveyDesc;
	@FXML private TextField txtDescription;
	@FXML private CheckBox chkAskOnCondition;
	@FXML private CheckBox chkAssignToVar;
	@FXML private Pane paneAssignToVar;
	@FXML private HBox hboxQuestionCondition;
	@FXML private Button btnAddChoiceOpt;
	@FXML private Pane paneScale;

	@FXML private ComboBox<String> cboQuestionText;
	@FXML private TextField txtAnswer;
	private DropShadow selshadow = new DropShadow();


	@FXML private Pane paneMultChoiceS;
	@FXML private VBox paneChoiceOptsS;

	@FXML private Button btnAddOptS;
	@FXML private ScrollPane paneOptionsS;

	@FXML private Pane paneMultChoiceM;
	@FXML private VBox paneChoiceOptsM;
	@FXML private Button btnAddOptM;
	@FXML private ScrollPane paneOptionsM;
	@FXML private TabPane paneSurveys;
	@FXML private Pane paneCondition;
	@FXML private Button btnAddVariable;
	
	@FXML private ScrollPane paneScrollVars;
	@FXML private VBox paneVariables;
	@FXML private Group grpSurveyEdit;
	@FXML private Label lblSaved;
	@FXML private TextField txtGeo;
	
	@FXML private CheckBox chkAssignScore;
	private ArrayList<Pane> questionPanes;
	private EventHandler<MouseEvent> qHandler;
	private QuestionView selectedQuestion;
	private FirebaseQuestion selectedQuestionModel;
	@FXML private ComboBox<String> cboSurveys;
	private String timeAlive = "";
	private static final int MULT_MANY = 3;
	private static final int MULT_SINGLE = 2;
	private static final int OPEN_ENDED = 1;
	private static final int SCALE = 4;
	private static final int DATETIME = 5;
	private static final int GEO = 6;
	private static final int BOOLEAN = 7;
	private static final int NUMERIC = 8;
	




	@Override
	public String toString(){
		return getName();
	}
	public Tab parentTab;
	

	public void setSelectedQuestion(QuestionView entry){
		selectedQuestion = entry;
		if(entry == null){
			grpSurveyEdit.setDisable(true);
		}
		else
			grpSurveyEdit.setDisable(false);
	}
	public void setTab(Tab tab){
		this.parentTab = tab;

	}
	

	void addProperties(){
		DropShadow shadow = new DropShadow();
		 shadow.setWidth(25);
		 shadow.setHeight(25);
		 shadow.setRadius(15);
		 shadow.setSpread(0.8);
		 shadow.setColor(Color.LIGHTBLUE);
		 paneVariables.getChildren().clear();
		 EventHandler<MouseEvent> variablehandler = new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent event) {
					UserVariable uservar = ((UserVariable)event.getSource());
					if(event.getEventType().equals(MouseEvent.MOUSE_ENTERED)){
						if(uservar.getEffect()== null || !uservar.getEffect().equals(selshadow))
						uservar.setEffect(shadow);
					}
					else if(event.getEventType().equals(MouseEvent.MOUSE_DRAGGED))
						event.consume();
					else if(event.getEventType().equals(MouseEvent.MOUSE_EXITED)){
						if(uservar.getEffect()== null || !uservar.getEffect().equals(selshadow))
						uservar.setEffect(null);
					}
					else if(event.getEventType().equals(MouseEvent.MOUSE_CLICKED)){
						uservar.setLayoutX(0);
						uservar.setLayoutY(0);
						uservar.setManaged(true);
						paneVariables.getChildren().forEach(child->child.setEffect(null));
						uservar.setEffect(selshadow);
						if(selectedQuestionModel != null)
						selectedQuestionModel.setAssignedVar(uservar.getName());
						System.out.println("Set assigned var to " + uservar.getName());
					}
				}
			};
		 MainController.currentGUI.currentvariables.addListener(new ListChangeListener<Object>(){
			
			@Override
			public void onChanged(Change<?> c) {
				paneVariables.getChildren().clear();
				MainController.currentGUI.currentvariables.forEach(entry->{
					if(entry.isCustom){
						UserVariable uservar = new UserVariable(entry);
					paneVariables.getChildren().add(uservar);
					uservar.removeHander();
					uservar.addEventHandler(MouseEvent.ANY, variablehandler);
					}
					});
			}
			 
		 });
			MainController.currentGUI.currentvariables.forEach(entry->{
				if(entry.isCustom){
					UserVariable uservar = new UserVariable(entry);
				uservar.removeHander();
				paneVariables.getChildren().add(uservar);
				uservar.addEventHandler(MouseEvent.ANY, variablehandler);
				}
				});
			chkExpiry.selectedProperty().addListener(new ChangeListener<Boolean>(){
				
				@Override
				public void changed(
						ObservableValue<? extends Boolean> arg0,
						Boolean arg1, Boolean arg2) {
					if(chkExpiry.isSelected()){
						txtExpiry.setDisable(false);
						lblExpiry.setDisable(false);
					}
					else{
						txtExpiry.setText("");
						setExpiry(0);
						getModel().setexpiryTime(0);
						txtExpiry.setDisable(true);
						lblExpiry.setDisable(true);
					}
				}
				
			});
			
			
			txtSurveyName.setOnKeyReleased(new EventHandler<KeyEvent>(){

				@Override
				public void handle(KeyEvent event) {
					setName(txtSurveyName.getText());
					getModel().setname(txtSurveyName.getText());
					parentTab.setText(txtSurveyName.getText());
					if(txtSurveyName.getText().isEmpty()){
						setName("New Survey");
						getModel().setname("New Survey");
						parentTab.setText("New Survey");
					}
					
				}
				
			});
//			txtSurveyName.focusedProperty().addListener((obs,oldval,newval)->{
//				
//			});
//			
			txtExpiry.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent arg0) {
				try{
					Long isValid = Long.parseLong(arg0.getCharacter());
					
				}
				catch(NumberFormatException e){
					arg0.consume();
					return;
				}	
				
			
			}
		});
			txtExpiry.textProperty().addListener(change->{
				setExpiry(Long.parseLong(txtExpiry.getText()));
				getModel().setexpiryTime(Long.parseLong(txtExpiry.getText()));
			});
	}
	
	public Survey(){
		this(new FirebaseSurvey());
	}
	
	public void fxmlInit(){
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/survey.fxml"));
		Pane surveynode;
		DropShadow shadow = new DropShadow();
		 shadow.setWidth(25);
		 shadow.setHeight(25);
		 shadow.setRadius(15);
		 shadow.setSpread(0.2);
		 shadow.setColor(Color.LIGHTBLUE);
		 Bloom bloom = new Bloom();

	     bloom.setThreshold(0.75);

	    qHandler = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
		//		if(event.getSource() instanceof Button)return;
				QuestionView entry = (QuestionView)event.getSource();
				if(event.getEventType().equals(MouseEvent.MOUSE_ENTERED)){
					entry.setEffect(shadow);
					entry.setEffect(bloom);
					setCursor(Cursor.HAND);

				}
				else if(event.getEventType().equals(MouseEvent.MOUSE_CLICKED)){
				//	paneVariables.getChildren().forEach(child->child.setEffect(null)); //remove any variable highlighting

					setSelectedQuestion(entry);
					selectedQuestionModel = entry.getModel();
					populateQ(entry.getModel());	
					lstRegQ.getChildren().forEach(label->{
					label.getStyleClass().remove("borderedselected");});
					entry.getStyleClass().add("borderedselected");
				}
				else if(event.getEventType().equals(MouseEvent.MOUSE_EXITED)){
					entry.setEffect(null);
					setCursor(Cursor.DEFAULT);
				}

			}
			
		};
		try {
			 surveynode = (Pane) surveyLoader.load();
				this.getChildren().add(surveynode);
				addProperties();
				surveynode.setOnMouseDragged(new EventHandler<MouseEvent>(){

					@Override
					public void handle(MouseEvent arg0) {
						arg0.consume();
					}
					
				});
				chkAskOnCondition.selectedProperty().addListener(new ChangeListener<Boolean>(){
					public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
						handleCheckQuestion();
					}
				});
				chkAssignToVar.selectedProperty().addListener(new ChangeListener<Boolean>(){
					public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
						handleAssignToVar();
					}
				});
				 txtRegQ.setOnKeyReleased((event)->{selectedQuestion.setQuestionText(txtRegQ.getText());});
				 cboQuestionText.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)->{
					 if(newValue != null)
					 handleUpdateCondition();
				 });

				 txtAnswer.setOnKeyReleased((event)->{handleUpdateCondition();});
					paneAssignToVar.getChildren().forEach(child->{((Node)child).setDisable(true);;});
					paneCondition.getChildren().forEach(child->{((Node)child).setDisable(true);;});
					grpSurveyEdit.setDisable(true);
				 
				btnAddOptS.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent e){
						handleAddOpt(paneChoiceOptsS,"");//Add a blank options
					}
				});
				btnAddOptM.setOnAction(new EventHandler<ActionEvent>(){
					public void handle(ActionEvent e){
						handleAddOpt(paneChoiceOptsM,"");//Add a blank options
					}
				});
				
			    fields = new TextField[]{txtLikert1,txtLikert2,txtLikert3,txtLikert4,txtLikert5,txtLikert6,txtLikert7};
			    for(TextField field : fields){
			    	field.setText("");
			    }
				final ToggleGroup group = new ToggleGroup();
				rdioButton5.setToggleGroup(group);
				rdioButton7.setToggleGroup(group);
				rdioButton7.selectedProperty().addListener(new ChangeListener<Boolean>(){

					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
							Boolean newValue) {
						if(newValue){
							txtLikert6.setVisible(true);
							txtLikert7.setVisible(true);
							handleUpdateScale();
						}						
					}
					
				});
				rdioButton5.selectedProperty().addListener(new ChangeListener<Boolean>(){

					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
							Boolean newValue) {
						if(newValue){
							txtLikert6.setVisible(false);
							txtLikert7.setVisible(false);
							handleUpdateScale();
						}
					}
					
				});
				for(TextField field : fields){
					field.textProperty().addListener(change->handleUpdateScale());
				}
				questionPanes = new ArrayList<Pane>();
				 Collections.addAll(questionPanes,paneScale,paneMultChoiceS,paneMultChoiceM);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
	
	public Survey(FirebaseSurvey data) {
		super(data,FirebaseSurvey.class);
		this.name.setValue("New Survey");// = "New Survey";
		
	}

	@Override
	public ViewElement getInstance() {
		return this;
	}

	public void setExpiry(long expiryTime){
		this.timeAlive = Long.toString(expiryTime);
	}
	public String getExpiry(){
		return timeAlive;
	}

	public void setData(FirebaseSurvey model) {
		super.setData(model);
		this.model = model;
		selshadow = new DropShadow();
		addProperties();
		txtSurveyName.setText(model.getname());
		if(model.getexpiryTime()>0){
			chkExpiry.setSelected(true);
			txtExpiry.setText(Long.toString(model.getexpiryTime()));
		}
			setExpiry(model.gettimeAlive());
		model.getquestions();
		List<FirebaseQuestion> questions = model.getquestions();

		if(questions == null)return;
		for(FirebaseQuestion newquestion : questions){
			QuestionView question = new QuestionView((int)newquestion.getquestionType(),newquestion.questionText);
			question.setData(newquestion);
			setSelectedQuestion(question);
			selectedQuestionModel = newquestion;
			lstRegQ.getChildren().add(question);
			populateQ(newquestion);
		}
		setSelectedQuestion(null);
		selshadow.setWidth(25);
		 selshadow.setHeight(25);
		 selshadow.setRadius(15);
		 selshadow.setSpread(0.8);
		 selshadow.setColor(Color.ORANGE);
		

		lstRegQ.getChildren().forEach(label->{
			label.addEventFilter(MouseEvent.ANY, qHandler);});
	}

	@Override
	public Node[] getWidgets() {
		return new Node[]{};
	}

	/**
	 * Given a JSON representation of a question, this populates the survey pane with the question's parameters
	 * @param qJson JSON representation of a Question
	 */
	@SuppressWarnings("unchecked")
	private void populateQ(FirebaseQuestion qEntry) {
		 selshadow.setWidth(25);
		 selshadow.setHeight(25);
		 selshadow.setRadius(15);
		 selshadow.setSpread(0.8);
		 selshadow.setColor(Color.ORANGE);
		paneChoiceOptsS.getChildren().clear();
		paneChoiceOptsM.getChildren().clear();
		Map<String,Object> opts = null;
		int questionType = (int)qEntry.getquestionType(); //Integer.parseInt(questionData.get("questionType").toString());
		
		String questionText = qEntry.getquestionText(); //questionData.get("questionText").toString();
		Map<String,Object> qParams = qEntry.getparams(); //(Map<String,Object>)questionData.get("params");
		if(qParams != null){
		
		Map<String,Object> condition = (Map<String,Object>)qParams.get("condition");
	
		if(qEntry.getassignedVar() != null && !qEntry.getassignedVar().equals("")){
		String assignedvar = qEntry.getassignedVar();
		chkAssignToVar.setSelected(true);
		paneVariables.getChildren().forEach(child->child.setEffect(null));
		for(Node variable : paneVariables.getChildren()){
			if(((UserVariable)variable).getName().equals(assignedvar)){
				System.out.println("FOUND IT FOUND IT");
				((UserVariable)variable).setEffect(selshadow);
				selectedQuestionModel.setAssignedVar(((UserVariable)variable).getName());
				double height = ((paneVariables.getChildren().indexOf(variable)*28+50)/(paneVariables.getHeight()));
				paneScrollVars.setVvalue(height);
				break;
			}
		};
	}
	else
		chkAssignToVar.setSelected(false);
		
		boolean assigntoscore = qParams.get("assignToScore") == null ? false : Boolean.parseBoolean(qParams.get("assignToScore").toString());
		cboQuestionText.getItems().clear();
		txtAnswer.clear();
		for(Node qentries : lstRegQ.getChildren()){
			QuestionView preventry = (QuestionView)qentries;
			if(preventry.getModel().equals(qEntry))break;
			cboQuestionText.getItems().add(preventry.getQuestionText());
		}
		if(condition != null){
			chkAskOnCondition.setSelected(true);
			if(condition.get("answer") != null){
			String value = condition.get("answer").toString();
			txtAnswer.setText(value);
			}
			if(condition.get("question") != null){
				int question = Integer.parseInt(condition.get("question").toString());
				cboQuestionText.getSelectionModel().clearSelection();
				cboQuestionText.getSelectionModel().select(question);
			}
		}
		else{
			chkAskOnCondition.setSelected(false);
		}
			
		if(assigntoscore == true){
			chkAssignScore.setSelected(true);
		}
		else
			chkAssignScore.setSelected(false);

		opts = (Map<String,Object>)qParams.get("options");
		}
		chkAssignScore.setDisable(true);
		txtRegQ.setText(questionText);

		switch (questionType) {
		case OPEN_ENDED:
			showOpenEnded(null);
			rdioRegOpen.setSelected(true);
			break;
		case NUMERIC:
			showNumeric(null);
			rdioRegNum.setSelected(true);
			break;
		case BOOLEAN:
			showBoolean(null);
			rdioRegBool.setSelected(true);
			break;
		case DATETIME:
			showDate(null);
			rdioRegDate.setSelected(true);
			break;
		case GEO:
			showGeo(null);
			rdioRegGeo.setSelected(true);
			break; 
		case MULT_SINGLE:
			showMultChoiceS(null);
			if(opts != null)
			for (Object opt : opts.values())
				handleAddOpt(paneChoiceOptsS,opt.toString());
			rdioRegMultSingle.setSelected(true);
			break;
		case MULT_MANY:
			showMultChoiceM(null);
			if(opts!= null)
			for (Object opt : opts.values())
				handleAddOpt(paneChoiceOptsM,opt.toString());
			rdioRegMultMany.setSelected(true);
			break;
		case SCALE:
			showScale(null);
			rdioRegScale.setSelected(true);
			if(opts == null){
				rdioButton5.setSelected(true);
				for(TextField field : fields){
					field.setText("");
				}
			}
			else{
				if(opts.containsKey("number")){
					String number = opts.get("number").toString();
					if(number.equals("5"))rdioButton5.setSelected(true);
					else if(number.equals("7"))rdioButton7.setSelected(true);
				}
				if(opts.containsKey("labels")){
					ArrayList<String> labels = (ArrayList<String>)opts.get("labels");
					int count = 0;
					for(String label : labels){
						fields[count].setText(label);
						count++;
					}
				}
			}
				break;
		}
	}


	private void hidePanes(){
		chkAssignScore.setDisable(true);
		questionPanes.forEach(pane->{
			pane.setPrefHeight(0); 
			pane.setVisible(false);
		});
	}

	@FXML
	public void handleAssignScore(Event e){
		selectedQuestionModel.setAssign(chkAssignScore.isSelected()); //This is getting silly
	}
	@FXML
	public void showMultChoiceS(Event e) { // NO_UCD (unused code)
		hidePanes();
		paneMultChoiceS.setPrefHeight(USE_COMPUTED_SIZE);paneMultChoiceS.setVisible(true);
		selectedQuestion.setQuestionType(MULT_SINGLE);
	}
	@FXML
	public void showMultChoiceM(Event e) { // NO_UCD (unused code)
		hidePanes();
		paneMultChoiceM.setPrefHeight(USE_COMPUTED_SIZE);paneMultChoiceM.setVisible(true);
		selectedQuestion.setQuestionType(MULT_MANY);	
		}
	@FXML
	public void showDate(Event e) { // NO_UCD (unused code)
		hidePanes(); selectedQuestion.setQuestionType(DATETIME);	
	}
	@FXML
	public void showGeo(Event e) { // NO_UCD (unused code)
		hidePanes(); selectedQuestion.setQuestionType(GEO);	
	}
	@FXML
	public void showOpenEnded(Event e) { // NO_UCD (unused code)
		hidePanes(); selectedQuestion.setQuestionType(OPEN_ENDED);	
	}
	@FXML
	public void showBoolean(Event e){
		hidePanes(); selectedQuestion.setQuestionType(BOOLEAN);	
	}
	@FXML
	public void showNumeric(Event e){
		hidePanes(); selectedQuestion.setQuestionType(NUMERIC);	
	}
	@FXML
	public void showScale(Event e) { // NO_UCD (unused code)
		hidePanes();
		paneScale.setPrefHeight(USE_COMPUTED_SIZE);paneScale.setVisible(true);
		chkAssignScore.setDisable(false);

		selectedQuestion.setQuestionType(SCALE);	
		handleUpdateScale(); //Updates the scale value in case the user doesn't touch it
	}

	@FXML
	public void addQ(Event e) { // NO_UCD (unused code)
			QuestionView newEntry = new QuestionView(OPEN_ENDED,"");
			lstRegQ.getChildren().add(newEntry);
			setSelectedQuestion(newEntry);
			selectedQuestionModel = newEntry.getModel();
			newEntry.addEventFilter(MouseEvent.ANY, qHandler);
			lstRegQ.getChildren().forEach(label->{
				
			label.getStyleClass().remove("borderedselected");});
			newEntry.getStyleClass().add("borderedselected");
			getModel().getquestions().add(newEntry.getModel());
			populateQ(newEntry.getModel());
		}
		private void handleCheckQuestion(){
			if(chkAskOnCondition.isSelected()){
				paneCondition.getChildren().forEach(child->{((Node)child).setDisable(false);;});
			}
			else{
				paneCondition.getChildren().forEach(child->{((Node)child).setDisable(true);;});
				
				selectedQuestionModel.setCondition(null);
				txtAnswer.setText("");
				cboQuestionText.getSelectionModel().clearSelection();

			}
		}
		

		/**
		 * This method enables and disables relevant controls depending on whether the user wants to ask a
		 * question based on a condition
		 */
		private void handleAssignToVar(){
			if(chkAssignToVar.isSelected()){
				paneAssignToVar.getChildren().forEach(child->{((Node)child).setDisable(false);;});
				paneAssignToVar.setPrefHeight(USE_COMPUTED_SIZE);
			}
			else{
				paneAssignToVar.getChildren().forEach(child->{((Node)child).setDisable(true);;});
				paneAssignToVar.setPrefHeight(0);
				selectedQuestionModel.setAssignedVar("");
				System.out.println("I find myself here");
				paneVariables.getChildren().forEach(child->child.setEffect(null));

			}
		}
		
		
		private void handleUpdateCondition(){
			String questiontext = cboQuestionText.getSelectionModel().getSelectedItem();
			String questionanswer = txtAnswer.getText();
			Map<String,Object> condition = new HashMap<String,Object>();
			condition.put("question", cboQuestionText.getSelectionModel().getSelectedIndex());
			condition.put("answer", questionanswer);
			selectedQuestionModel.setCondition(condition);;
		}
		private void handleUpdateScale(){
		//	String toText =  (txtScaleMax.getText().equals("") ? "0" : txtScaleMax.getText());
			String number = rdioButton5.isSelected() ? "5" : "7";
			Map<String,Object> qScaleVals = new HashMap<String,Object>();
			qScaleVals.put("number", number);
			ArrayList<String> labels = new ArrayList<String>();
			for(TextField field : fields){
				labels.add(field.getText());
				
			}
			qScaleVals.put("labels",labels);

			selectedQuestionModel.setOptions(qScaleVals);

		}
		/**
		 * Add an option to a multiple choice question
		 * @param s The option text
		 */
		public void handleAddOpt(VBox choices, String s) { // NO_UCD (unused code)
			HBox optionBox = new HBox();
			optionBox.setSpacing(2);
			TextField choice = new TextField();
			choice.setText(s);
			Button remove = new Button();
			remove.setText("X");
			remove.setOnAction(new EventHandler<ActionEvent>(){
				public void handle(ActionEvent e){
					choices.getChildren().remove(optionBox);
					Map<String,Object> qOptions = new HashMap<String,Object>();
					int optcount = 1;
					for(Node opt : choices.getChildren()){
						HBox optbox = (HBox)opt;
						TextField opttext = (TextField)optbox.getChildren().get(0);
							qOptions.put("option"+Integer.toString(optcount++),opttext.getText());
							selectedQuestionModel.setOptions(qOptions);
					}
				}
			});		

			optionBox.getChildren().addAll(choice,remove);
			choices.getChildren().add(optionBox);
			choice.setOnKeyReleased((event)->{
				Map<String,Object> qOptions = new HashMap<String,Object>();
				int optcount = 1;
				for(Node opt : choices.getChildren()){
					HBox optbox = (HBox)opt;
					TextField opttext = (TextField)optbox.getChildren().get(0);
						qOptions.put("option"+Integer.toString(optcount++),opttext.getText());
						selectedQuestionModel.setOptions(qOptions);
				}
			});
		}

		@FXML
		private void handleAddProperty(Event e){
			   Stage stage = new Stage(StageStyle.UNDECORATED);
			   VariablePane root= new VariablePane(stage);
				stage.setScene(new Scene(root));
				   stage.setTitle("Add property");
				   
				   stage.initModality(Modality.APPLICATION_MODAL);
				   stage.initOwner(btnAddVariable.getScene().getWindow());
				   stage.showAndWait();
				   
				   stage.setOnHiding(new EventHandler<WindowEvent>(){

					@Override
					public void handle(WindowEvent event) {
						addProperties(); 
					}
				   });
				   
			   
		}
		@Override
		public FirebaseSurvey getModel() {
			return model;
		}

}
