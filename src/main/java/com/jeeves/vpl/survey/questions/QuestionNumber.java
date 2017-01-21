package com.jeeves.vpl.survey.questions;

import java.io.IOException;
import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.survey.QuestionView;
import com.jeeves.vpl.survey.Survey;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class QuestionNumber extends QuestionView {

	public QuestionView clone(){
		return new QuestionNumber(super.getModel(),mySurvey);
	}
	public QuestionNumber(FirebaseQuestion model, Survey survey) {
		super(model,survey);
		setImage("/img/icons/imgnumeric.png");
		setQuestionText("Numeric");
	}

	public void loadOptions(){
		optionsPane = new Pane();

	}
	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub
//		mySurvey.txtNumAnswer.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){
//
//				@Override
//				public void handle(KeyEvent arg0) {
//					try{
//						Long isValid = Long.parseLong(arg0.getCharacter());
//						if(mySurvey.txtNumAnswer.getText().length()>0)
//						handleUpdateCondition(mySurvey.cboLessMore.getValue()+";"+mySurvey.txtNumAnswer.getText());
//					}
//					catch(NumberFormatException e){
//						arg0.consume();
//						return;
//					}	
//				}
//			});
	}


	@Override
	public void showCheckQOpts() {
		mySurvey.cboLessMore.setVisible(true);
		mySurvey.txtNumAnswer.setVisible(true);
		mySurvey.cboLessMore.getSelectionModel().clearSelection();
		mySurvey.txtNumAnswer.clear();

	}
	@Override
	public void handleCheckQ(String scon) {
		System.out.println("scon is " + scon);

		if(!scon.isEmpty()){
			mySurvey.cboLessMore.getSelectionModel().select(scon);
			System.out.println("scon is " + scon);
			String[] components = scon.split(";");
			mySurvey.cboLessMore.setValue(components[0]);
			System.out.println("Components[0] is " + components[0]);
			if(components.length>1)
			mySurvey.txtNumAnswer.setText(components[1]);	
		}
		else{
			mySurvey.cboLessMore.getSelectionModel().clearSelection();
			mySurvey.txtNumAnswer.clear();
		}
		}

	@Override
	public void showEditOpts(Map<String,Object> opts) {
		// TODO Auto-generated method stub
		
	}

}
