package com.jeeves.vpl;

import java.util.HashMap;

import com.jeeves.vpl.canvas.receivers.ElementReceiver;
import com.jeeves.vpl.firebase.FirebaseQuestion;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

	class QuestionView extends Pane{
		@FXML private ImageView imgEntry;
		@FXML private Label lblQuestion;
		@FXML private Button btnDeleteQ;
		private FirebaseQuestion model;		
		private static final int MULT_MANY = 3;
		private static final int MULT_SINGLE = 2;
		private static final int OPEN_ENDED = 1;
		private static final int SCALE = 4;
		private static final int DATETIME = 5;
		private static final int GEO = 6;
		private static final int BOOLEAN = 7;
		private static final int NUMERIC = 8;
		private int questionType;
		private Survey mySurvey;
		public int myIndex;
		public EventHandler<MouseEvent> draggedHandler;
		public EventHandler<MouseEvent> mainHandler;
		public EventHandler<MouseEvent> releasedHandler;
		
		public int getMyIndex(){
			return myIndex;
		}
		public void setMyIndex(int myIndex){
			this.myIndex = myIndex;
		}
		private static HashMap<Integer,String> questionImages = new HashMap<Integer,String>();
		 static
		    {
			 questionImages.put(DATETIME, "/img/icons/imgdate.png");
			 questionImages.put(GEO, "/img/icons/imggeo.png");
			 questionImages.put(MULT_MANY, "/img/icons/imgmany.png");
			 questionImages.put(MULT_SINGLE, "/img/icons/imgsingle.png");
			 questionImages.put(OPEN_ENDED, "/img/icons/imgfreetext.png");
			 questionImages.put(SCALE, "/img/icons/imgscale.png");
			 questionImages.put(NUMERIC, "/img/icons/imgnumeric.png");
			 questionImages.put(BOOLEAN, "/img/icons/imgbool.png");
		    }
		public QuestionView(int questionType, String questionText, Survey survey){
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/questionentry.fxml"));
		Pane surveynode;
		this.mySurvey = survey;
		this.questionType = questionType;

		try {
			 surveynode = (Pane) surveyLoader.load();
				this.getChildren().add(surveynode);
				imgEntry.setImage(new Image(getClass().getResourceAsStream(questionImages.get(questionType))));
				lblQuestion.setText(questionText);
				model = new FirebaseQuestion();
				model.setquestionType(questionType);
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
				addListeners();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
		public void showDelete(){
			btnDeleteQ.setVisible(true);
		}
		public void hideDelete(){
			btnDeleteQ.setVisible(false);
		}
		public QuestionView getInstance(){
			return this;
		}
		
		public void removeFromSurvey(){
			mySurvey.removeQ(this);
		}
		public int getQuestionType(){
			return questionType;
		}
		public FirebaseQuestion getModel(){
			return model;
		}
		public void setData(FirebaseQuestion model){
			this.model = model;
		}
		public QuestionView(QuestionView model) {
			this(model.getQuestionType(),model.getQuestionText(),model.mySurvey);
			this.setDisable(true);
		}

		
		public void setImage(String image){
			imgEntry.setImage(new Image(getClass().getResourceAsStream(image)));

		}
		
		public void setQuestionType(int type){
			model.setquestionType(type);
			setImage(questionImages.get(type));
		}

		public String getQuestionText(){
			return lblQuestion.getText();
		}
		public void setQuestionText(String text){
			lblQuestion.setText(text);
			model.setquestionText(text);
			//question.setQuestionText(text);
		}

		public void addListeners() {
			draggedHandler = event -> {
				if (event.isSecondaryButtonDown()){
					event.consume();
					return;
				}
				event.consume();
				startFullDrag();
				setMouseTransparent(true);
			};
			releasedHandler = event -> setMouseTransparent(false);
			mainHandler = new EventHandler<MouseEvent>() {
				private double x, y, mouseX, mouseY;
				@Override
				public void handle(MouseEvent event) {
					//If we right click
					if (event.isSecondaryButtonDown()){				
						return;
					}
					// An event for when we press the mouse
					else if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
						requestFocus();
//					//	currentCanvas.setIsMouseOver(true);
//						Point2D canvasPoint = currentCgetanvas.sceneToLocal(new Point2D(event.getSceneX(),event.getSceneY()));
//						if(getReceiver() != null && (!(getReceiver() instanceof ElementReceiver))){ //This does not apply to Element Receivers
//							getReceiver().removeChild(getInstance());
//
//						currentCanvas.addChild(getInstance(), canvasPoint.getX(), canvasPoint.getY());
//						
//						}
					//	event.consume();
					//	mySurvey.removeQ(getInstance());
					//	contextMenu.hide();
			//			toFront();

						Point2D parentPoint = getParent().sceneToLocal(event.getSceneX(),event.getSceneY());
						x = getLayoutX(); y = getLayoutY();
						mouseX = parentPoint.getX(); mouseY = parentPoint.getY();
					}
					// An event for dragging the element about
					else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
						if(event.isSecondaryButtonDown()){return;};
						requestFocus();
						toFront();
						setManaged(false);

						setMouseTransparent(true);
						Point2D parentPoint = getParent().sceneToLocal(event.getSceneX(),event.getSceneY());
						double offsetX = parentPoint.getX() - mouseX; double offsetY = parentPoint.getY() - mouseY;
						x += offsetX; y += offsetY;
						setLayoutX(x); setLayoutY(y);
						mouseX = parentPoint.getX(); mouseY = parentPoint.getY();
						
						event.consume();
					} 
					//When the mouse is released
					else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
						if(event.getButton().equals(MouseButton.SECONDARY))return;
//						if(MainController.currentGUI.isOverTrash(event.getSceneX(), event.getSceneY())){
//							currentCanvas.removeChild(getInstance());
//							if(getInstance().getReceiver() != null){
//								getInstance().getReceiver().removeChild(getInstance()); //Make sure it's totally gotten rid of
//							}
//						}
						else{
					//	setPosition((new Point2D(getLayoutX(), getLayoutY())));
						setCursor(Cursor.HAND);
						setManaged(true);
						setMouseTransparent(false);
						}
					} 
					else if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)){
						setCursor(Cursor.HAND);
						MainController.currentGUI.mnuFile.hide();
					}}
			};

			
			addEventHandler(MouseEvent.ANY,mainHandler);
			setOnDragDetected(draggedHandler);
			setOnMouseReleased(releasedHandler);			
		}
	}