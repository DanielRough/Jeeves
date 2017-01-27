package com.jeeves.vpl.survey.questions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.survey.QuestionView;
import com.jeeves.vpl.survey.Survey;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class QuestionMultSingle extends QuestionView{

	public QuestionView clone(){
		return new QuestionMultSingle(super.getModel(),mySurvey);
	}
	public QuestionMultSingle(FirebaseQuestion model, Survey survey) {
		super(model,survey);
		System.out.println("HIYAAAAAAAAA");
		setImage("/img/icons/imgsingle.png");
		setQuestionText("Select One");
	}
	public String getImagePath(){
		return "/img/icons/imgsingle.png";
	}
	@FXML private Pane paneMultChoiceS;
	@FXML private VBox paneChoiceOptsS;

	@FXML private Button btnAddOptS;
	@FXML private ScrollPane paneOptionsS;
	
	public void loadOptions(){
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/multSopts.fxml"));
		 try {
			 optionsPane = (Pane) surveyLoader.load();
			 addEventHandlers();
		 }
		 catch(IOException e){
			 System.out.println("Goodbye");
		 }
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
						model.setOptions(qOptions);
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
					model.setOptions(qOptions);
			}
		});
	}

	@Override
	public void addEventHandlers() {
		// TODO Auto-generated method stub
		btnAddOptS.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				handleAddOpt(paneChoiceOptsS,"");//Add a blank options
			}
		});
	}


	@Override
	public void showCheckQOpts() {
		
		mySurvey.cboMultiChoice.setVisible(true);
		mySurvey.cboMultiChoice.getItems().clear();
		Map<String,Object> opts = (Map<String,Object>)model.getparams().get("options");

		if(opts != null)
			opts.values().forEach(mval ->{mySurvey.cboMultiChoice.getItems().add(mval.toString());});		
		mySurvey.cboMultiChoice.getSelectionModel().clearSelection();

		
	}
	@Override
	public void handleCheckQ(String scon) {
//		Map<String,Object> opts = (Map<String,Object>)model.getparams().get("options");
//		mySurvey.cboMultiChoice.getItems().clear();
//		System.out.println("EEERHMAGERD SCON IS " + scon);
//
//		if(opts != null)
//			opts.values().forEach(mval ->{mySurvey.cboMultiChoice.getItems().add(mval.toString());});		
		if(!scon.isEmpty())
			mySurvey.cboMultiChoice.getSelectionModel().select(scon);
		else
			mySurvey.cboMultiChoice.getSelectionModel().clearSelection();
	}

	@Override
	public void showEditOpts(Map<String,Object> opts) {
		paneChoiceOptsS.getChildren().clear();
		if(opts == null)return;
		for (Object opt : opts.values()){
			handleAddOpt(paneChoiceOptsS,opt.toString());
		}		
	}

}
