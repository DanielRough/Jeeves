package com.jeeves.vpl.survey;

import static com.jeeves.vpl.Constants.BOOLEAN;
import static com.jeeves.vpl.Constants.DATETIME;
import static com.jeeves.vpl.Constants.GEO;
import static com.jeeves.vpl.Constants.MULT_MANY;
import static com.jeeves.vpl.Constants.MULT_SINGLE;
import static com.jeeves.vpl.Constants.NUMERIC;
import static com.jeeves.vpl.Constants.OPEN_ENDED;
import static com.jeeves.vpl.Constants.SCALE;
import static com.jeeves.vpl.Constants.VAR_CLOCK;
import static com.jeeves.vpl.Constants.VAR_DATE;
import static com.jeeves.vpl.Constants.numberHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import com.jeeves.vpl.canvas.receivers.DateReceiver;
import com.jeeves.vpl.canvas.receivers.TimeReceiver;
import com.jeeves.vpl.survey.questions.QuestionView;
public class ConditionEditor extends Pane{
	@FXML private ImageView imgCondition;
	@FXML private CheckBox chkAskOnCondition;
	@FXML public TextField txtAnswer;
	@FXML public TextField txtNumAnswer;
	@FXML public RadioButton rdioTrue;
	@FXML public RadioButton rdioFalse;
	@FXML private ComboBox<String> cboQuestionText; //Can we embed the question information into here?
	@FXML public ComboBox<String> cboMultiChoice;
	@FXML public ComboBox<String> cboLessMore;
	@FXML private ComboBox<String> cboBeforeAfter;
	@FXML private Pane paneDateTimeReceiver;
	@FXML private Pane paneCondition;
	@FXML private HBox hboxDateOpts;
	private DateReceiver dateReceiver;
	private TimeReceiver timeReceiver;
	private ArrayList<Node> conditionPanes;
	private QuestionView currentQuestion;
	private Map<String,QuestionView> conditionQuestions;
	private ToggleGroup tgroup;
	public ConditionEditor() throws IOException{
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/ConditionEditor.fxml"));
		Pane surveynode;
		try {
			surveynode = (Pane) surveyLoader.load();

			this.getChildren().add(surveynode);
			tgroup = new ToggleGroup();
			rdioTrue.setToggleGroup(tgroup);
			rdioFalse.setToggleGroup(tgroup);
			cboLessMore.getItems().addAll("less than", "more than", "equal to");
			cboBeforeAfter.getItems().addAll("before", "after");
			dateReceiver = new DateReceiver(VAR_DATE);
			timeReceiver = new TimeReceiver(VAR_CLOCK);
			addListeners();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	ChangeListener<String> cboQuestionListener;
	private void addListeners(){
		chkAskOnCondition.selectedProperty().addListener((ob,old,nval) -> {	
			if (chkAskOnCondition.isSelected()) {
				paneCondition.getChildren().forEach(child -> {((Node) child).setDisable(false); });
			} 
			else {
				disable();
		}});
		
		cboQuestionListener = (ob,old,nval) -> {
			if(nval == null)return;
			updateCondition("");
			populate(currentQuestion);
		};
		cboQuestionText.getSelectionModel().selectedItemProperty().addListener(cboQuestionListener);
		
		txtNumAnswer.addEventHandler(KeyEvent.ANY,numberHandler);
		txtAnswer.textProperty().addListener((ob,old,nval) -> {updateCondition(txtAnswer.getText());});
		txtNumAnswer.textProperty().addListener((ob,old,nval)-> {updateCondition(cboLessMore.getValue() + ";"+ txtNumAnswer.getText());});
		cboMultiChoice.getSelectionModel().selectedItemProperty().addListener((ob,old,nval) -> {updateCondition(nval);});
		cboLessMore.getSelectionModel().selectedItemProperty().addListener((ob,old,nval) -> {updateCondition(cboLessMore.getValue() + ";" + txtNumAnswer.getText());});
		tgroup.selectedToggleProperty().addListener((ob,old,nval) -> {updateCondition(rdioTrue.isSelected() ? "true" : "false");});
		timeReceiver.getTextField().textProperty().addListener((ob,old,nval)->{updateCondition(cboBeforeAfter.getValue() + ";"+ timeReceiver.getText());});
		dateReceiver.getTextField().textProperty().addListener((ob,old,nval)->{updateCondition(cboBeforeAfter.getValue() + ";"+ dateReceiver.getText());});


		conditionPanes = new ArrayList<Node>();
		conditionQuestions = new HashMap<String,QuestionView>();
		Collections.addAll(conditionPanes, txtAnswer, txtNumAnswer,hboxDateOpts,
				cboLessMore, cboMultiChoice, cboBeforeAfter, paneDateTimeReceiver, rdioTrue, rdioFalse);
	}


	private void updateCondition(String answer) {
		String selection = cboQuestionText.getValue();
		currentQuestion.setParentQuestion(conditionQuestions.get(selection));
		currentQuestion.setParentConstraints(answer);
	}

	private void hideConditionPanes() {
		conditionPanes.forEach(pane -> {
			pane.setVisible(false);
		});
	}
	public void clear() {
		cboQuestionText.getItems().clear();		
	}
	public void addOption(QuestionView entry) {
		conditionQuestions.put(entry.getText(), entry);
		cboQuestionText.getItems().add(entry.getText());
	}
	
	public void populate(QuestionView question) {
		this.currentQuestion = question;
	
		QuestionView conditionQuestion = question.getParentQuestion();
		
		if(conditionQuestion == null){ //If we don't have a condition question, we're done here
			chkAskOnCondition.setSelected(false);
			return;
		}

		chkAskOnCondition.setSelected(true);
		cboQuestionText.getSelectionModel().selectedItemProperty().removeListener(cboQuestionListener);
		cboQuestionText.getSelectionModel().select(conditionQuestion.getText());
		cboQuestionText.getSelectionModel().selectedItemProperty().addListener(cboQuestionListener);

		showConstraints();
	}
	
	public void showConstraints(){
		hideConditionPanes();
		QuestionView conditionQuestion = currentQuestion.getParentQuestion();
		String conditionConstraints = currentQuestion.getParentConstraints();
		switch((int)conditionQuestion.getQuestionType()){
		case OPEN_ENDED: 
			txtAnswer.setVisible(true); 
			txtAnswer.setText(conditionConstraints); break;
		case NUMERIC: 
		case SCALE: 
			txtNumAnswer.setVisible(true); 
			cboLessMore.setVisible(true);
			String[] constraints = conditionConstraints.split(";");
			if(constraints.length > 1){
			cboLessMore.setValue(constraints[0]);
			txtNumAnswer.setText(constraints[1]);
			}
			 break;
		case MULT_SINGLE: 
		case MULT_MANY:
			cboMultiChoice.setVisible(true);
			Map<String,Object> opts = conditionQuestion.getQuestionOptions();
			cboMultiChoice.getItems().clear();
			for(Object opt : opts.values()){
					String option = opt.toString();
					cboMultiChoice.getItems().add(option);
			}
			cboMultiChoice.getSelectionModel().select(conditionConstraints);
			break;
		case DATETIME: 
			hboxDateOpts.setVisible(true);
			Map<String,Object> myopts = conditionQuestion.getQuestionOptions();
			paneDateTimeReceiver.getChildren().clear();
			System.out.println("opts are " + myopts.toString());
			boolean useDate=false,useTime = false;
			if(myopts.containsKey("useDate"))
				useDate = (boolean)myopts.get("useDate");
			if(myopts.containsKey("useTime"))
				useTime = (boolean)myopts.get("useTime");
			if(useDate == false && useTime == true)
					paneDateTimeReceiver.getChildren().add(timeReceiver);
				else
					paneDateTimeReceiver.getChildren().add(dateReceiver);


			cboBeforeAfter.setVisible(true);
			paneDateTimeReceiver.setVisible(true);
			break;
		case BOOLEAN: 
			rdioTrue.setVisible(true);
			rdioFalse.setVisible(true);
			rdioTrue.setSelected(true);
			if(conditionConstraints.length() >0)
				tgroup.selectToggle(Boolean.parseBoolean(conditionConstraints) ? rdioTrue : rdioFalse);
			break;
		case GEO: break;
		}
	}
	public void disable() {
		System.out.println("Err.....");
		hideConditionPanes();
		currentQuestion.setParentQuestion(null);
		paneCondition.getChildren().forEach(child -> {
		((Node) child).setDisable(true);
	});
	}

}
