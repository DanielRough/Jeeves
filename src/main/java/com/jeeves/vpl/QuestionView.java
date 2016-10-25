package com.jeeves.vpl;

import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import com.jeeves.vpl.firebase.FirebaseQuestion;

	class QuestionView extends Pane{
		@FXML private ImageView imgEntry;
		@FXML private Label lblQuestion;
		private FirebaseQuestion model;		
		private static final int MULT_MANY = 3;
		private static final int MULT_SINGLE = 2;
		private static final int OPEN_ENDED = 1;
		private static final int SCALE = 4;
		private static final int DATETIME = 5;
		private static final int GEO = 6;
		private static final int BOOLEAN = 7;
		private static final int NUMERIC = 8;
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
		public QuestionView(int questionType, String questionText){
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/questionentry.fxml"));
		Pane surveynode;
		

		try {
			 surveynode = (Pane) surveyLoader.load();
				this.getChildren().add(surveynode);
				imgEntry.setImage(new Image(getClass().getResourceAsStream(questionImages.get(questionType))));
				lblQuestion.setText(questionText);
				model = new FirebaseQuestion();
				model.setquestionType(questionType);
				getStyleClass().add("bordered");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
		
		public FirebaseQuestion getModel(){
			return model;
		}
		public void setData(FirebaseQuestion model){
			this.model = model;
		}
		public QuestionView() {
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
	}