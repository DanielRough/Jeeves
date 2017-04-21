package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.BOOLEAN;
import static com.jeeves.vpl.Constants.DATETIME;
import static com.jeeves.vpl.Constants.GEO;
import static com.jeeves.vpl.Constants.MULT_MANY;
import static com.jeeves.vpl.Constants.MULT_SINGLE;
import static com.jeeves.vpl.Constants.NUMERIC;
import static com.jeeves.vpl.Constants.OPEN_ENDED;
import static com.jeeves.vpl.Constants.SCALE;

import java.util.Map;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import com.jeeves.vpl.Main;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.canvas.actions.Action;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.survey.Survey;
public abstract class QuestionView extends ViewElement<FirebaseQuestion>{
	@FXML private ImageView imgEntry;
	@FXML private Label lblQuestion;
	private Button btnDeleteQ;
	private Button btnEdit;
//	@FXML private AnchorPane mainPane;
	protected FirebaseQuestion model;	
//	public abstract QuestionView clone();

	public Button getEditButton(){
		return btnEdit;
	}
	private int questionType;
	private String imagePath;
//	private Survey mySurvey;
//	private String description; 
	//	private int myIndex;
	private boolean isReadOnly = true; //true by default
	protected Pane optionsPane;
	private EventHandler<MouseEvent> draggedHandler;
	private EventHandler<MouseEvent> mainHandler;
	//	private EventHandler<MouseEvent> viewElementHandler;
	private Pane parent;
	//	private EventHandler<MouseEvent> releasedHandler;
	//	private QuestionView draggable;
	private Main gui;
	private boolean previouslyAdded = false;
	HBox surveynode;
	HBox buttonBox;
	public Pane getOptionsPane(){
		return optionsPane;
	}

	public boolean wasAdded(){
		return previouslyAdded;
	}
	public void setAddedFlag(){
		previouslyAdded = true;
	}
	public void addButtons(){
		if(surveynode.getChildren().contains(buttonBox))return;
	//	HBox buttonBox = new HBox();
		btnEdit = new Button("Edit");
		btnDeleteQ = new Button("X");
		buttonBox.getChildren().addAll(btnEdit,btnDeleteQ);
		buttonBox.setSpacing(10);
		
//		AnchorPane.setRightAnchor(buttonBox, 5.0);
//		AnchorPane.setTopAnchor(buttonBox,7.0);
//		AnchorPane.setBottomAnchor(buttonBox,5.0);
//		AnchorPane.setLeftAnchor(buttonBox,220.0);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		surveynode.getChildren().add(buttonBox);
		surveynode.setPrefWidth(300);
	}
	public abstract void showEditOpts(Map<String,Object> opts);
//	public abstract void showCheckQOpts();
//	public abstract void handleCheckQ(String scon);
	public abstract void addEventHandlers();
	public abstract void loadOptions();
	//		public int getMyIndex(){
	//			return myIndex;
	//		}
	//		public void setMyIndex(int myIndex){
	//			this.myIndex = myIndex;
	//		}

	public void setReadOnly(boolean readOnly){
		isReadOnly = readOnly;
		QuestionText text = new QuestionText();

	}
	@Override
	public Node[] getWidgets() {
		// TODO Auto-generated method stub
		return new Node[]{};
	}
	public void fxmlInit(){
		this.type = ElementType.QUESTION;
		gui = Main.getContext();
		loadOptions();
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/QuestionView.fxml"));
		//this.mySurvey = survey;
	//	this.questionType = questionType;

		try {
			surveynode = (HBox) surveyLoader.load();
			getChildren().add(surveynode);
//			AnchorPane.setLeftAnchor(surveynode, 0.0);
//			AnchorPane.setRightAnchor(surveynode, 0.0);


			model = new FirebaseQuestion();
			model.setquestionType(questionType);
			setImage(getImagePath());
			setQuestionText(getLabel());
			addListeners();
			buttonBox = new HBox();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
//
//	public String getDescription(){
//		return description;
//	}

	public void showDelete(){
		btnEdit.setVisible(true);
		btnDeleteQ.setVisible(true);
	}
	public static QuestionView create(FirebaseQuestion question){
		String classname = question.gettype();
		try {
			return (QuestionView)Class.forName(classname).getConstructor(FirebaseQuestion.class).newInstance(question); //It's a plain Action
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
//		switch((int)question.getquestionType()){
//		case OPEN_ENDED:
//			return new QuestionText(question);
//		case MULT_SINGLE:
//			return new QuestionMultSingle(question);
//		case MULT_MANY:
//			return new QuestionMultMany(question);
//		case NUMERIC:
//			return new QuestionNumber(question);
//		case SCALE:
//			return new QuestionLikert(question);
//		case GEO:
//			return new QuestionLocation(question);
//		case BOOLEAN:
//			return new QuestionTrueFalse(question);
//		case DATETIME:
//			return new QuestionDateTime(question);
//		}
//		//	System.out.println("OH NO");
//		return null;
	}


	public QuestionView(){
		super(FirebaseQuestion.class);

	}

//	public QuestionView(int questionType){
//		super(FirebaseQuestion.class);
//		
//	}

	public QuestionView getInstance(){
		return this;
	}
//	public ImageView getImage(){
//		return imgEntry;
//	}
	public abstract String getLabel();
	public abstract String getImagePath();

//	public void removeFromSurvey(){
//		mySurvey.removeQ(this);
//	}
	public int getQuestionType(){
		return questionType;
	}
	public FirebaseQuestion getModel(){
		return model;
	}
	public void setData(FirebaseQuestion model){
		this.model = model;
		setQuestionText(model.getquestionText());
	}
	public QuestionView(FirebaseQuestion model) {
		super(model,FirebaseQuestion.class);
	//	System.out.println("woooha");
	//	this.setDisable(true);
	}

//	public QuestionView(FirebaseQuestion model, Survey survey){
//		this((int)model.getquestionType());
//
//	}
	public void setImage(String image){
		System.out.println("Image is " + image);
		imgEntry.setImage(new Image(getClass().getResourceAsStream(image)));

	}

	public void setQuestionType(int type){
		model.setquestionType(type);
		setImage(imagePath);
	}

	public void setQuestionText(String text){
		lblQuestion.setText(text);
		model.setquestionText(text);
	}

	public String getQuestionText(){
		return model.getquestionText() != null ? model.getquestionText() : "";
	}

//
//	public void addListeners() {
//		System.out.println("LISTENERS TO BE ADDED");
////		draggedHandler = event -> {
////			if (event.isSecondaryButtonDown()){
////				event.consume();
////				return;
////			}
////			//	event.consume();
////			System.out.println("Started a full drag");
////			startFullDrag();
////			setMouseTransparent(true);
////		};
//
////		mainHandler = new EventHandler<MouseEvent>() {
////		//	private int index;
////			@Override
////			public void handle(MouseEvent event) {
////				if(event.isSecondaryButtonDown()){event.consume();return;}
////				setOnDragDetected(event1 ->{if(event1.isSecondaryButtonDown())return; System.out.println("WASSUP");startFullDrag();});
////				if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
////					event.consume();
////					requestFocus();
////					System.out.println("HELOOOOOO");
//////					if(!isReadOnly)
//////						parent = ((VBox)getParent());
//////					else
////					if(isReadOnly)
////						parent = ((Pane)getParent());
//////					if(parent == null)System.out.println("parent is null");
//////					if(getModel() == null)System.out.println("Model is null");
////											if(isReadOnly){
////											QuestionView duplicate = QuestionView.create(getModel());
////									//		duplicate.setReadOnly();
////											System.out.println("DOES THIS EVEN HAPPEN");
////											parent.getChildren().add(parent.getChildren().indexOf(getInstance()),duplicate);
////											}
////				//	index = getParent().getChildren().indexOf(getInstance());
////					//gui.getMainPane().getChildren().add(getInstance());
//////					setLayoutX(event.getSceneX());
//////					setLayoutY(event.getSceneY());
////				}
////				// An event for dragging the element about
////				else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
////					if(event.isSecondaryButtonDown()){return;};
////					requestFocus();
////					toFront();
////					setManaged(false);
////					wasDragged = true;
////					setStyle("-fx-background-color: white");
////					setMouseTransparent(true);
////					setLayoutX(event.getSceneX());
////					setLayoutY(event.getSceneY());
////				} 
////				else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
////					event.consume();
////					if(event.getButton().equals(MouseButton.SECONDARY))return;
////
////					else{
//////						if((!isReadOnly && wasDragged == false) || wasDetected == false)
//////							parent.getChildren().add(index,getInstance());
////						gui.getMainPane().getChildren().remove(getInstance());
////						setCursor(Cursor.HAND);
////						setManaged(true);
////						setMouseTransparent(false);
////					}
////					wasDragged = false;
////					wasDetected = false;
////				} 
////				else if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)){
////					setCursor(Cursor.HAND);
////					gui.hideMenu();
////				}}
////		};
//
//
//	//	addEventHandler(MouseEvent.ANY,mainHandler);
//	//	setOnDragDetected(draggedHandler);
//	}
}