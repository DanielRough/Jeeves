package com.jeeves.vpl.survey;

import static com.jeeves.vpl.Constants.BOOLEAN;
import static com.jeeves.vpl.Constants.DATE;
import static com.jeeves.vpl.Constants.TIME;
import static com.jeeves.vpl.Constants.GEO;
import static com.jeeves.vpl.Constants.NUMERIC;
import static com.jeeves.vpl.Constants.SCALE;
import static com.jeeves.vpl.Constants.VAR_BOOLEAN;
import static com.jeeves.vpl.Constants.VAR_CLOCK;
import static com.jeeves.vpl.Constants.VAR_DATE;
import static com.jeeves.vpl.Constants.VAR_LOCATION;
import static com.jeeves.vpl.Constants.VAR_NUMERIC;

import java.io.IOException;
import java.util.Map;

import com.jeeves.vpl.Main;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.firebase.FirebaseVariable;
import com.jeeves.vpl.survey.questions.QuestionView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

	@FXML
	private ComboBox<UserVariable> cboVars;
	@FXML
	private CheckBox chkAssignToVar;
	private ConditionEditor conditionEditor;
	private Main gui;

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

	ListChangeListener<QuestionView> childQListener;

	public QuestionEditor(ObservableList<QuestionView> currentelements) {
		this.surveyQuestions = currentelements;
		this.gui = Main.getContext();
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/QuestionEditor.fxml"));
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
				populateVarBox();

			}
		});
		populateVarBox();

		cboVars.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<UserVariable>() {

			@Override
			public void changed(ObservableValue<? extends UserVariable> observable, UserVariable oldValue,
					UserVariable newValue) {
				if (newValue != null)
					selectedQuestion.setAssignedVar(newValue.getName());
			}

		});

		chkAssignToVar.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (chkAssignToVar.isSelected()) {
					paneAssignToVar.getChildren().forEach(child -> {
						child.setDisable(false);
					});
					paneAssignToVar.setPrefHeight(Region.USE_COMPUTED_SIZE);
				} else {
					paneAssignToVar.getChildren().forEach(child -> {
						child.setDisable(true);
					});
					paneAssignToVar.setPrefHeight(0);
					selectedQuestion.setAssignedVar("");
				}
			}
		});
	}

	public QuestionView getSelectedQuestion() {
		return selectedQuestion;
	}

	public void populateQuestion(QuestionView entry) {
		if (entry == null)
			return;
		if (entry.equals(selectedQuestion))
			return; // Forget it, we've already selected this quesiton
		selectedQuestion = entry;
		
//		entry.
		populateVarBox();

		
		Map<String, Object> opts = entry.getQuestionOptions();
	//	if(opts != null)
			selectedQuestion.showEditOpts(opts);

		if (entry.getAssignedVar() != null && !entry.getAssignedVar().equals("")) {
			chkAssignToVar.setSelected(true);
			cboVars.getItems().forEach(variable -> {
				if (variable.getName().equals(entry.getAssignedVar())) {
					cboVars.getSelectionModel().select(variable);
				}
			});
		} else
			chkAssignToVar.setSelected(false);

		if (entry.getChildQuestions().isEmpty())
			conditionEditor.setDisable(false);
		else
			conditionEditor.setDisable(true);

		if (selectedQuestion == null)
			return;
		vboxOpts.getChildren().remove(2, vboxOpts.getChildren().size());
		if (listener != null)
			txtQText.textProperty().removeListener(listener);
		listener = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				entry.setQuestionText(newValue);

			}
		};
		txtQText.setOnKeyReleased(handler->{
			entry.questionTextProperty.setValue(txtQText.getText()); //This might work?
		});
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
			else if (question.isChild())
				continue; // Don't add the ones that are conditionally asked.
							// Currently can't nest conditions
			conditionEditor.addOption(question);
		}

		conditionEditor.populate(entry);
	}

	public void populateVarBox() {
		ObservableList<FirebaseVariable> vars = gui.getVariables();

		cboVars.getItems().clear();
		if (selectedQuestion == null)
			return;
		vars.forEach(entry -> {
			if (entry.getisCustom()) {
				UserVariable uservar = new UserVariable(entry);

				// Only add valid variables that we can assign it to
				switch (selectedQuestion.getQuestionType()) {
				case SCALE:
					if (uservar.getVarType().equals(VAR_NUMERIC)) {
						cboVars.getItems().add(uservar);
					}
					break;
				case GEO:
					if (uservar.getVarType().equals(VAR_LOCATION)) {
						cboVars.getItems().add(uservar);
					}
					break;
				case BOOLEAN:
					if (uservar.getVarType().equals(VAR_BOOLEAN)) {
						cboVars.getItems().add(uservar);
					}
					break;
				case NUMERIC:
					if (uservar.getVarType().equals(VAR_NUMERIC)) {
						cboVars.getItems().add(uservar);
					}
					break;
				case DATE:
					if (uservar.getVarType().equals(VAR_DATE)) {
						cboVars.getItems().add(uservar);
					}
					break;
				case TIME:
					if (uservar.getVarType().equals(VAR_CLOCK)) {
						cboVars.getItems().add(uservar);
					}
					break;
				}
				uservar.removeHander();

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
			}
		};
		cboVars.setCellFactory(cellFactory);
		cboVars.setButtonCell(cellFactory.call(null));

	}

	public void refresh() {
		if (selectedQuestion == null)
			return;
		if (selectedQuestion.getChildQuestions().isEmpty())
			conditionEditor.setDisable(false);
		else
			conditionEditor.setDisable(true);
	}

}
