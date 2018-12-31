package com.jeeves.vpl.survey;

import static com.jeeves.vpl.Constants.BOOLEAN;
import static com.jeeves.vpl.Constants.DATE;
import static com.jeeves.vpl.Constants.TIME;
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

public class ConditionEditor extends Pane {
	@FXML
	public ComboBox<String> cboLessMore;
	@FXML
	public ComboBox<String> cboMultiChoice;
	@FXML
	public RadioButton rdioFalse;
	@FXML
	public RadioButton rdioTrue;
	@FXML
	public TextField txtAnswer;
	@FXML
	public TextField txtNumAnswer;
	@FXML
	private ComboBox<String> cboBeforeAfter;
	@FXML
	private ComboBox<String> cboQuestionText; // Can we embed the question
	@FXML
	private CheckBox chkAskOnCondition;
	private ArrayList<Node> conditionPanes;
	private Map<String, QuestionView> conditionQuestions;
	private QuestionView currentQuestion;
	private DateReceiver dateReceiver;
	@FXML
	private HBox hboxDateOpts;
	@FXML
	private ImageView imgCondition;
	@FXML
	private Pane paneCondition;
	@FXML
	private HBox paneDateTimeReceiver;
	private ToggleGroup tgroup;
	private TimeReceiver timeReceiver;

	ChangeListener<String> cboQuestionListener;

	public ConditionEditor() throws IOException {
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/ConditionEditor.fxml"));
		Pane surveynode;
		try {
			surveynode = surveyLoader.load();

			this.getChildren().add(surveynode);
			tgroup = new ToggleGroup();
			rdioTrue.setToggleGroup(tgroup);
			rdioFalse.setToggleGroup(tgroup);
			cboLessMore.getItems().addAll("less than", "more than", "equal to");
			cboBeforeAfter.getItems().addAll("before", "after");
			dateReceiver = new DateReceiver(VAR_DATE);
			timeReceiver = new TimeReceiver(VAR_CLOCK);
			addListeners();
		} catch (IOException e) {
			System.exit(1);
		}
	}

	public void addOption(QuestionView entry) {
		conditionQuestions.put(entry.getText(), entry);
		cboQuestionText.getItems().add(entry.getText());
	}

	public void clear() {
		cboQuestionText.getItems().clear();
	}

	public void disable() {
		hideConditionPanes();
		currentQuestion.setParentQuestion(null);
		paneCondition.getChildren().forEach(child -> 
			child.setDisable(true)
		);
	}

	public void populate(QuestionView question) {
		this.currentQuestion = question;

		QuestionView conditionQuestion = question.getParentQuestion();

		if (conditionQuestion == null) { // If we don't have a condition
											// question, we're done here
			chkAskOnCondition.setSelected(false);
			return;
		}

		chkAskOnCondition.setSelected(true);
		cboQuestionText.getSelectionModel().selectedItemProperty().removeListener(cboQuestionListener);
		cboQuestionText.getSelectionModel().select(conditionQuestion.getText());
		cboQuestionText.getSelectionModel().selectedItemProperty().addListener(cboQuestionListener);

		showConstraints();
	}

	public void showConstraints() {
		hideConditionPanes();
		QuestionView conditionQuestion = currentQuestion.getParentQuestion();
		String conditionConstraints = currentQuestion.getParentConstraints();
		switch (conditionQuestion.getQuestionType()) {
		case OPEN_ENDED:
			txtAnswer.setVisible(true);
			txtAnswer.setText(conditionConstraints);
			break;
		case NUMERIC:
		case SCALE:
			txtNumAnswer.setVisible(true);
			cboLessMore.setVisible(true);
			cboLessMore.getSelectionModel().clearAndSelect(0); //Default
			txtNumAnswer.setText("5"); //Default
			updateScaleConstraints(conditionConstraints);
			break;
		case MULT_SINGLE:
		case MULT_MANY:
			cboMultiChoice.setVisible(true);
			Map<String, Object> opts = conditionQuestion.getQuestionOptions();
			cboMultiChoice.getItems().clear();
			for (Object opt : opts.values()) {
				String option = opt.toString();
				cboMultiChoice.getItems().add(option);
			}
			updateMultConstraints(conditionConstraints);
			break;
		case DATE:
			hboxDateOpts.setVisible(true);
			paneDateTimeReceiver.getChildren().clear();
			paneDateTimeReceiver.getChildren().add(dateReceiver);
			cboBeforeAfter.setVisible(true);
			paneDateTimeReceiver.setVisible(true);
			updateDateConstraints(conditionConstraints);
			break;
		case TIME:
			hboxDateOpts.setVisible(true);
			paneDateTimeReceiver.getChildren().clear();
			paneDateTimeReceiver.getChildren().add(timeReceiver);
			cboBeforeAfter.setVisible(true);
			paneDateTimeReceiver.setVisible(true);
			updateTimeConstraints(conditionConstraints);
			break;
		case BOOLEAN:
			rdioTrue.setVisible(true);
			rdioFalse.setVisible(true);
			updateBooleanConstraints(conditionConstraints);
			break;
		case GEO:
			break;
		default:
			break;
		}
	}

	private void updateMultConstraints(String conditionConstraints) {
		if (conditionConstraints.length() > 0)
			cboMultiChoice.getSelectionModel().select(conditionConstraints);
		else
			cboMultiChoice.getSelectionModel().clearAndSelect(0); //default
	}
	private void updateScaleConstraints(String conditionConstraints) {
		String[] constraints = conditionConstraints.split(";");
		if (constraints.length > 1) {
			cboLessMore.setValue(constraints[0]);
			txtNumAnswer.setText(constraints[1]);
		}
	}
	private void updateDateConstraints(String conditionConstraints) {
		String[] dateconstraints = conditionConstraints.split(";");
		if (dateconstraints.length > 1) {
			dateReceiver.setText(dateconstraints[1]);
			cboBeforeAfter.setValue(dateconstraints[0]);
		}
		else
			cboBeforeAfter.getSelectionModel().clearAndSelect(0); //give it a default hopefully?
	}
	private void updateTimeConstraints(String conditionConstraints) {
		String[] timeconstraints = conditionConstraints.split(";");
		if (timeconstraints.length > 1) {
			timeReceiver.setText(timeconstraints[1]);
			cboBeforeAfter.setValue(timeconstraints[0]);
		}
		else
			cboBeforeAfter.getSelectionModel().clearAndSelect(0); //give it a default hopefully?
	}
	private void updateBooleanConstraints(String conditionConstraints) {
		if(tgroup.getSelectedToggle()!=null) {
			tgroup.getSelectedToggle().setSelected(false);
		}
		rdioTrue.setSelected(true);
		if (conditionConstraints.length() > 0) {
			tgroup.selectToggle(Boolean.parseBoolean(conditionConstraints) ? rdioTrue : rdioFalse);
		}
	}
	private void addListeners() {
		chkAskOnCondition.selectedProperty().addListener((ob, old, nval) -> {
			if (chkAskOnCondition.isSelected()) {
				paneCondition.getChildren().forEach(child -> {
					child.setDisable(false);
					cboQuestionText.getSelectionModel().clearSelection();
				});
			} else {
				disable();
				cboQuestionText.getSelectionModel().clearSelection();

			}
		});

		cboQuestionListener = (ob, old, nval) -> {
			if (nval == null)
				return;
			updateCondition("");
			populate(currentQuestion);
		};
		cboQuestionText.getSelectionModel().selectedItemProperty().addListener(cboQuestionListener);

		txtNumAnswer.addEventHandler(KeyEvent.ANY, numberHandler);
		txtAnswer.textProperty().addListener((ob, old, nval) -> 
			updateCondition(txtAnswer.getText()));
		txtNumAnswer.textProperty().addListener((ob, old, nval) ->
			updateCondition(cboLessMore.getValue() + ";" + txtNumAnswer.getText())
		);
		cboMultiChoice.getSelectionModel().selectedItemProperty().addListener((ob, old, nval) -> 
			updateCondition(nval)
		);
		cboLessMore.getSelectionModel().selectedItemProperty().addListener((ob, old, nval) -> 
			updateCondition(cboLessMore.getValue() + ";" + txtNumAnswer.getText())
		);
		cboBeforeAfter.getSelectionModel().selectedItemProperty().addListener((ob, old, nval) -> {
			if(paneDateTimeReceiver.getChildren().contains(dateReceiver)){
				updateCondition(cboBeforeAfter.getValue() + ";" + dateReceiver.getText());
			}
			else{
				updateCondition(cboBeforeAfter.getValue() + ";" + timeReceiver.getText());
			}
		});
		tgroup.selectedToggleProperty().addListener((ob, old, nval) -> 
			updateCondition(rdioTrue.isSelected() ? "true" : "false")
		);
		timeReceiver.getTextField().textProperty().addListener((ob, old, nval) -> 
			updateCondition(cboBeforeAfter.getValue() + ";" + timeReceiver.getText()));
		dateReceiver.getTextField().textProperty().addListener((ob, old, nval) ->
			updateCondition(cboBeforeAfter.getValue() + ";" + dateReceiver.getText())
		);

		conditionPanes = new ArrayList<>();
		conditionQuestions = new HashMap<>();
		Collections.addAll(conditionPanes, txtAnswer, txtNumAnswer, hboxDateOpts, cboLessMore, cboMultiChoice,
				cboBeforeAfter, paneDateTimeReceiver, rdioTrue, rdioFalse);
	}

	private void hideConditionPanes() {
		conditionPanes.forEach(pane -> 
			pane.setVisible(false)
			);
	}

	private void updateCondition(String answer) {
		String selection = cboQuestionText.getValue();
		currentQuestion.setParentQuestion(conditionQuestions.get(selection));
		currentQuestion.setParentConstraints(answer);
	}

}
