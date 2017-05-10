package com.jeeves.vpl.survey;

import java.io.IOException;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import com.jeeves.vpl.Main;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.firebase.FirebaseVariable;
import com.jeeves.vpl.survey.questions.QuestionView;

public class QuestionEditor extends Pane {

	@FXML private VBox vboxOpts;
	@FXML private ComboBox<UserVariable> cboVars;
	@FXML private CheckBox chkAssignToVar;
	@FXML private Pane paneAssignToVar;

	@FXML private HBox hboxCondition;
	@FXML private HBox hboxSaveAs;
	@FXML private ImageView imgSaveAs;
	@FXML private TextField txtQText;
	@FXML private Pane paneNoOpts;
	private ConditionEditor conditionEditor;
	private ChangeListener<String> listener;
	private QuestionView selectedQuestion;
	//private QuestionView conditionQuestion;
//	private FirebaseQuestion selectedQuestionModel;
	private Main gui;
	private ObservableList<QuestionView> surveyQuestions;
	

	public QuestionView getSelectedQuestion(){
		return selectedQuestion;
	}
	public QuestionEditor(ObservableList<QuestionView> currentelements) {
		this.surveyQuestions = currentelements;
		this.gui = Main.getContext();
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader
		.setLocation(getClass().getResource("/QuestionEditor.fxml"));
		VBox surveynode;
		try {
			surveynode = (VBox) surveyLoader.load();
			this.getChildren().add(surveynode);
			conditionEditor = new ConditionEditor();
			surveynode.getChildren().add(1, conditionEditor);
		} catch (IOException e) {
			e.printStackTrace();
		}

		gui.registerVarListener(new ListChangeListener<FirebaseVariable>() {
			@Override
			public void onChanged(Change<? extends FirebaseVariable> c) {
				ObservableList<FirebaseVariable> vars = gui.getVariables();
				UserVariable selected = (cboVars.getSelectionModel()
						.getSelectedItem());
				cboVars.getItems().clear();
				vars.forEach(entry -> {
					if (entry.getisCustom()) {
						UserVariable uservar = new UserVariable(entry);
						cboVars.getItems().add(uservar);
						uservar.removeHander();

					}
				});
				cboVars.getSelectionModel().select(selected);
			}
		});
		Callback<ListView<UserVariable>, ListCell<UserVariable>> cellFactory = new Callback<ListView<UserVariable>, ListCell<UserVariable>>() {
			@Override
			public ListCell<UserVariable> call(ListView<UserVariable> param) {

				return new ListCell<UserVariable>() {
					@Override
					public void updateItem(UserVariable item, boolean empty) {
						super.updateItem(item, empty);
						if (!empty) {
							setText(item.getName());
							getStyleClass().add(item.getVarType());
							this.setAlignment(Pos.CENTER);
							this.setFont(Font.font("Calibri", FontWeight.BOLD, 12.0));
							this.setTextFill(Color.BLACK);
							this.setTextAlignment(TextAlignment.CENTER);
							setGraphic(null);
						}
					}
				};
			}
		};
		cboVars.setCellFactory(cellFactory);
		cboVars.setButtonCell(cellFactory.call(null));
		cboVars.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<UserVariable>() {

			@Override
			public void changed(
					ObservableValue<? extends UserVariable> observable,
					UserVariable oldValue, UserVariable newValue) {
				if (newValue != null)
					selectedQuestion.setAssignedVar(newValue
							.getName());
				cboVars.setButtonCell(cellFactory.call(null));
			}

		});

		chkAssignToVar.selectedProperty().addListener(
				new ChangeListener<Boolean>() {
					public void changed(
							ObservableValue<? extends Boolean> arg0,
							Boolean arg1, Boolean arg2) {
						if (chkAssignToVar.isSelected()) {
							paneAssignToVar.getChildren().forEach(child -> {
								((Node) child).setDisable(false);
							});
							paneAssignToVar
							.setPrefHeight(Pane.USE_COMPUTED_SIZE);
						} else {
							paneAssignToVar.getChildren().forEach(child -> {
								((Node) child).setDisable(true);
							});
							paneAssignToVar.setPrefHeight(0);
							selectedQuestion.setAssignedVar("");
						}
					}
				});
	}


	ListChangeListener<QuestionView> childQListener;
	
	public void refresh(){
		if(selectedQuestion == null)return;
		if(selectedQuestion.getChildQuestions().isEmpty())
			conditionEditor.setDisable(false);
		else
			conditionEditor.setDisable(true);
	}
	
	public void populateQuestion(QuestionView entry) {
		if(entry == null)return;
		if(entry.equals(selectedQuestion))return; //Forget it, we've already selected this quesiton
		selectedQuestion = entry;
		Map<String, Object> opts = entry.getQuestionOptions();
		if (entry.getAssignedVar() != null && !entry.getAssignedVar().equals("")) {
			chkAssignToVar.setSelected(true);
			cboVars.getItems().forEach(variable -> {
				if (variable.getName().equals(entry.getAssignedVar())) {
					cboVars.getSelectionModel().select(variable);
				}
			});
		} else
			chkAssignToVar.setSelected(false);

		if(entry.getChildQuestions().isEmpty())
			conditionEditor.setDisable(false);
		else
			conditionEditor.setDisable(true);
				
		if (selectedQuestion == null)
			return;
		selectedQuestion.showEditOpts(opts);
		vboxOpts.getChildren().remove(2, vboxOpts.getChildren().size()); 
		if (listener != null)
			txtQText.textProperty().removeListener(listener);
		listener = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				entry.setQuestionText(newValue);
			}
		};
		txtQText.textProperty().addListener(listener);
		txtQText.setText(selectedQuestion.getText());
		if(entry.getOptionsPane() != null)
			vboxOpts.getChildren().add(entry.getOptionsPane());
		else
			vboxOpts.getChildren().add(paneNoOpts);
		vboxOpts.getChildren().forEach(child -> child.setVisible(true));
		conditionEditor.clear();
		
		
		for (QuestionView question : surveyQuestions) {
			if (question.equals(entry))
				break;
			else if (question.isChild())
				continue; //Don't add the ones that are conditionally asked. Currently can't nest conditions
			conditionEditor.addOption(question);
		}
	
		conditionEditor.populate(entry);
	}



}
