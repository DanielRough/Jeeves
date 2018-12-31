package com.jeeves.vpl.survey;

import static com.jeeves.vpl.Constants.VAR_CATEGORY;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.firebase.FirebaseVariable;
import com.jeeves.vpl.survey.questions.QuestionView;

import javafx.beans.value.ChangeListener;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

public class QuestionEditor extends Pane {
	final Logger logger = LoggerFactory.getLogger(QuestionEditor.class);

	@FXML
	private ComboBox<UserVariable> cboVars;
	@FXML
	private CheckBox chkAssignToVar;
	private ConditionEditor conditionEditor;

	@FXML
	private HBox hboxCondition;
	@FXML
	private HBox hboxSaveAs;
	@FXML
	private ImageView imgSaveAs;
	private ChangeListener<String> listener;
	@FXML
	private Pane paneAssignToVar;
	@FXML
	private Pane paneNoOpts;
	private QuestionView selectedQuestion;
	private ObservableList<QuestionView> surveyQuestions;

	@FXML
	private TextField txtQText;
	@FXML
	private VBox vboxOpts;
	@FXML private CheckBox chkMandatory;

	ListChangeListener<QuestionView> childQListener;

	public QuestionEditor(ObservableList<QuestionView> currentelements) {
		this.surveyQuestions = currentelements;
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/QuestionEditor.fxml"));
		VBox surveynode;
		try {
			surveynode = surveyLoader.load();
			this.getChildren().add(surveynode);
			conditionEditor = new ConditionEditor();
			surveynode.getChildren().add(1, conditionEditor);
		} catch (IOException e) {
			System.exit(1);
		}

		Constants.getOpenProject().registerVarListener(c-> 
				populateVarBox()
		);
		populateVarBox();

		cboVars.getSelectionModel().selectedItemProperty().addListener((o,v0,v1)-> {
				if (v1 != null) {
					selectedQuestion.setAssignedVar(v1.getName());
					if(v1.getModel().getvartype().equals(VAR_CATEGORY)) {
						//This is a bit nasty, but if the variable is of type 'category' then we need to update
						//its possible values in the global  hashmap
						String[] optsArray = new String[0];
						Constants.getCategoryOpts().put(v1.getName(), selectedQuestion.getQuestionOptions().values().toArray(optsArray));
						if(v0 != null)
							Constants.getCategoryOpts().remove(v0.getName());
					}
				}
			

		});

		chkMandatory.selectedProperty().addListener((o,v0,v1)->
				selectedQuestion.setisMandatory(v1));

		chkAssignToVar.selectedProperty().addListener((arg0,arg1,arg2)-> {
				if (chkAssignToVar.isSelected()) {
					paneAssignToVar.getChildren().forEach(child -> 
						child.setDisable(false)
					);
					paneAssignToVar.setPrefHeight(Region.USE_COMPUTED_SIZE);
				} else {
					paneAssignToVar.getChildren().forEach(child -> 
						child.setDisable(true)
					);
					paneAssignToVar.setPrefHeight(0);
					selectedQuestion.setAssignedVar("");
				}
			
		});
	}

	public QuestionView getSelectedQuestion() {
		return selectedQuestion;
	}

	public void populateQuestion(QuestionView entry) {
		if (entry.equals(selectedQuestion))
			return; // Forget it, we've already selected this quesiton
		selectedQuestion = entry;
		populateVarBox();



		chkMandatory.setSelected(entry.isMandatory());
	
		if (entry.getAssignedVar() != null && !entry.getAssignedVar().equals("")) {
			chkAssignToVar.setSelected(true);
			cboVars.getItems().forEach(variable -> {
				if (variable.getName().equals(entry.getAssignedVar())) {
					cboVars.getSelectionModel().select(variable);
				}
			});
		} else {
			chkAssignToVar.setSelected(false);
		}

		Map<String, Object> opts = entry.getQuestionOptions();
		TreeMap<String,Object> sortedmap = new TreeMap<>(opts);
		selectedQuestion.showEditOpts(sortedmap);
		conditionEditor.setDisable(!entry.getChildQuestions().isEmpty());

		vboxOpts.getChildren().remove(3, vboxOpts.getChildren().size());
		if (listener != null)
			txtQText.textProperty().removeListener(listener);
		listener = (o,v0,v1) -> 
				entry.setQuestionText(v1);

		txtQText.setOnKeyReleased(handler->
			entry.getQuestionTextProperty().setValue(txtQText.getText()) //This might work?
		);
		txtQText.textProperty().addListener(listener);
		txtQText.setText(selectedQuestion.getText());
		if (entry.getOptionsPane() != null)
			vboxOpts.getChildren().add(entry.getOptionsPane());
		else
			vboxOpts.getChildren().add(paneNoOpts);
		vboxOpts.getChildren().forEach(child -> child.setVisible(true));
		conditionEditor.clear();

		for (QuestionView question : surveyQuestions) {
			if (question.equals(entry))
				break;
			else if (!question.isChild())	
				conditionEditor.addOption(question);
		}

		conditionEditor.populate(entry);
	}

	public void populateVarBox() {
		ObservableList<FirebaseVariable> vars = Constants.getOpenProject().getObservableVariables();

		cboVars.getItems().clear();
		if (selectedQuestion == null)
			return;
		vars.forEach(entry -> {
				UserVariable uservar = new UserVariable(entry);
				if(uservar.getVarType().equals(selectedQuestion.getAnswerType())) {
					cboVars.getItems().add(uservar);
				}
				
		});
		Callback<ListView<UserVariable>, ListCell<UserVariable>> cellFactory = param -> 
				new ListCell<UserVariable>() {
					@Override
					public void updateItem(UserVariable item, boolean empty) {
						super.updateItem(item, empty);
						if (!empty) {
							setText(item.getName());
							getStyleClass().clear();
							getStyleClass().add(item.getVarType());
							this.setBorder(null);
							this.setAlignment(Pos.CENTER);
							this.setFont(Font.font("Calibri", FontWeight.BOLD, 12.0));
							this.setTextFill(Color.BLACK);
							this.setTextAlignment(TextAlignment.CENTER);
							setGraphic(null);
						}
					}
				};
			
		cboVars.setCellFactory(cellFactory);
		cboVars.setButtonCell(cellFactory.call(null));

	}

	public void refresh() {
		if (selectedQuestion == null)
			return;
		conditionEditor.setDisable(!selectedQuestion.getChildQuestions().isEmpty());
	}

}
