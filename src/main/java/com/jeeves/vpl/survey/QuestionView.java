package com.jeeves.vpl.survey;

import java.util.Map;

import com.jeeves.vpl.MainController;
import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.survey.questions.QuestionDateTime;
import com.jeeves.vpl.survey.questions.QuestionLikert;
import com.jeeves.vpl.survey.questions.QuestionLocation;
import com.jeeves.vpl.survey.questions.QuestionMultMany;
import com.jeeves.vpl.survey.questions.QuestionMultSingle;
import com.jeeves.vpl.survey.questions.QuestionNumber;
import com.jeeves.vpl.survey.questions.QuestionText;
import com.jeeves.vpl.survey.questions.QuestionTrueFalse;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

	public abstract class QuestionView extends AnchorPane{
		@FXML private ImageView imgEntry;
		@FXML private Label lblQuestion;
		@FXML private Button btnDeleteQ;
		@FXML private Button btnEdit;
		protected FirebaseQuestion model;	
		public static final int NUMBER_OF_TYPES = 8;
		private static final int MULT_MANY = 3;
		private static final int MULT_SINGLE = 2;
		private static final int OPEN_ENDED = 1;
		private static final int SCALE = 4;
		private static final int DATETIME = 5;
		private static final int GEO = 6;
		private static final int BOOLEAN = 7;
		private static final int NUMERIC = 8;
		private int questionType;
		public String imagePath;
		protected Survey mySurvey;
		public String description; 
		public int myIndex;
		boolean isReadOnly;
		public Pane optionsPane;
		public EventHandler<MouseEvent> draggedHandler;
		public EventHandler<MouseEvent> mainHandler;
		public EventHandler<MouseEvent> viewElementHandler;
		private Pane parent;
		public EventHandler<MouseEvent> releasedHandler;
		private QuestionView draggable;
		public int getMyIndex(){
			return myIndex;
		}
		public void setMyIndex(int myIndex){
			this.myIndex = myIndex;
		}
		
	     public void setReadOnly(){
	    	 isReadOnly = true;

	     }
	     
	     public String getDescription(){
	    	 return description;
	     }
	     
	     public void showDelete(){
	    	 btnEdit.setVisible(true);
	    	 btnDeleteQ.setVisible(true);
	     }
		public static QuestionView create(FirebaseQuestion question, Survey mySurvey){
			switch((int)question.getquestionType()){
			case OPEN_ENDED:
				return new QuestionText(question,mySurvey);
			case MULT_SINGLE:
				return new QuestionMultSingle(question,mySurvey);
			case MULT_MANY:
				return new QuestionMultMany(question,mySurvey);
			case NUMERIC:
				return new QuestionNumber(question,mySurvey);
			case SCALE:
				return new QuestionLikert(question,mySurvey);
			case GEO:
				return new QuestionLocation(question,mySurvey);
			case BOOLEAN:
				return new QuestionTrueFalse(question,mySurvey);
			case DATETIME:
				return new QuestionDateTime(question,mySurvey);
			}
			System.out.println("OH NO");
			return null;
		}
		public abstract void showEditOpts(Map<String,Object> opts);
		
		public abstract void showCheckQOpts();
		public abstract void handleCheckQ(String scon);
		public abstract void addEventHandlers();

		public abstract void loadOptions();
		
		public QuestionView(int questionType, Survey survey){
		loadOptions();
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/questionentry.fxml"));
		HBox surveynode;
		this.mySurvey = survey;
		this.questionType = questionType;
		System.out.println("Here question type is " + questionType);
		
		try {
			 surveynode = (HBox) surveyLoader.load();
				this.getChildren().add(surveynode);
				AnchorPane.setLeftAnchor(surveynode, 0.0);
				AnchorPane.setRightAnchor(surveynode, 0.0);

				
				model = new FirebaseQuestion();
				model.setquestionType(questionType);

				//setQuestionType(questionType);
				getStyleClass().add("bordered");
				btnDeleteQ.setOnMouseReleased(new EventHandler<MouseEvent>(){

					@Override
					public void handle(MouseEvent event) {
						Stage stage = new Stage(StageStyle.UNDECORATED);
						QuestionDeletePane root = new QuestionDeletePane(getInstance(),stage);
						stage.setScene(new Scene(root));
						stage.initModality(Modality.APPLICATION_MODAL);
						stage.showAndWait();
					}
					
				});
				btnEdit.setOnMouseReleased(new EventHandler<MouseEvent>(){

					@Override
					public void handle(MouseEvent event) {
						mySurvey.populateQuestion(getInstance());
					}
					
				});
				addListeners();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

		public QuestionView getInstance(){
			return this;
		}
		public ImageView getImage(){
			return imgEntry;
		}
		public abstract String getImagePath();
		
		public void removeFromSurvey(){
			mySurvey.removeQ(this);
		}
		public int getQuestionType(){
			return questionType;
		}
		public FirebaseQuestion getModel(){
			return model;
		}
		public void setData(FirebaseQuestion model,Survey mySurvey){
			this.model = model;
			this.mySurvey = mySurvey;
			setQuestionText(model.getquestionText());
		}
		public QuestionView(QuestionView model) {
			this(model.getQuestionType(),model.mySurvey);
			System.out.println("woooha");
			this.setDisable(true);
		}

		public QuestionView(FirebaseQuestion model, Survey survey){
			this((int)model.getquestionType(),survey);

		}
		public abstract QuestionView clone();
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
			return model.getquestionText();
		}
		public boolean wasDetected = false;
		public boolean wasDragged = false;
//		public void setDraggable(QuestionView draggable){
//			this.draggable = draggable;
//		}
//		public QuestionView getDraggable(){
//			return draggable;
//		}
		public void addListeners() {
			draggedHandler = event -> {
				if (event.isSecondaryButtonDown()){
					event.consume();
					return;
				}
			//	event.consume();
				startFullDrag();
				setMouseTransparent(true);
			};

			mainHandler = new EventHandler<MouseEvent>() {
				private int index;
				@Override
				public void handle(MouseEvent event) {

					if (event.isSecondaryButtonDown()){				
						return;
					}

					// An event for when we press the mouse
					else if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
						event.consume();
						requestFocus();
						System.out.println("HELOOOOOO");
						if(!isReadOnly)
							parent = ((VBox)getParent());
						else
							parent = ((Pane)getParent());
						if(parent == null)System.out.println("parent is null");
						if(getModel() == null)System.out.println("Model is null");
//						if(isReadOnly){
//						QuestionView duplicate = QuestionView.create(getModel(), mySurvey);
//						duplicate.setReadOnly();
//						parent.getChildren().add(parent.getChildren().indexOf(getInstance()),duplicate);
//						}
					    index = parent.getChildren().indexOf(getInstance());
						MainController.currentGUI.getMainPane().getChildren().add(getInstance());
						setLayoutX(event.getSceneX());
						setLayoutY(event.getSceneY());
					}
					// An event for dragging the element about
					else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
						if(event.isSecondaryButtonDown()){return;};
						requestFocus();
						toFront();
						setManaged(false);
						wasDragged = true;
						setMouseTransparent(true);
						setLayoutX(event.getSceneX());
						setLayoutY(event.getSceneY());
					} 
					else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
						
						event.consume();
						if(event.getButton().equals(MouseButton.SECONDARY))return;

						else{
							if((!isReadOnly && wasDragged == false) || wasDetected == false)
								parent.getChildren().add(index,getInstance());
							MainController.currentGUI.getMainPane().getChildren().remove(getInstance());
						setCursor(Cursor.HAND);
						setManaged(true);
						setMouseTransparent(false);
						}
						wasDragged = false;
						wasDetected = false;
					} 
					else if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)){
						setCursor(Cursor.HAND);
						MainController.currentGUI.mnuFile.hide();
					}}
			};

			
			addEventHandler(MouseEvent.ANY,mainHandler);
			setOnDragDetected(draggedHandler);
		}
	}