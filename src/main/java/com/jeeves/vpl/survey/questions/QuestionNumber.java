package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.survey.QuestionEditor.*;

import java.util.Map;

import javafx.scene.layout.Pane;

import com.jeeves.vpl.firebase.FirebaseQuestion;

public class QuestionNumber extends QuestionView {

//	public QuestionView clone(){
//		return new QuestionNumber(super.getModel());
//	}
//	public QuestionNumber(FirebaseQuestion model) {
//		super(model);
//		setImage("/img/icons/imgnumeric.png");
//	//	setQuestionText("Numeric");
//		//this.description = "User chooses a number";
//	}
	public String getLabel(){
		return "Enter a numeric value into a text box";
	}
	public String getImagePath(){
		return "/img/icons/imgnumeric.png";
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


//	@Override
//	public void showCheckQOpts() {
//		clearFields();
//		cboLessMore.setVisible(true);
//		txtNumAnswer.setVisible(true);
//		cboLessMore.getSelectionModel().clearSelection();
//		txtNumAnswer.clear();
//	}
//	@Override
//	public void handleCheckQ(String scon) {
//		System.out.println("scon is " + scon);
//
//		if(!scon.isEmpty()){
//		//	cboLessMore.getSelectionModel().select(scon);
//			System.out.println("scon is " + scon);
//			String[] components = scon.split(";");
//			addToCboLessMore(components[0]);
//			System.out.println("Components[0] is " + components[0]);
//			if(components.length>1)
//				setNumAnswerProperty(components[1]);
//		//	txtNumAnswer.setText(components[1]);	
//		}
//		else{
////			cboLessMore.getSelectionModel().clearSelection();
////			txtNumAnswer.clear();
//			clearFields();
//		}
//		}

	@Override
	public void showEditOpts(Map<String,Object> opts) {
		// TODO Auto-generated method stub
		
	}

}
