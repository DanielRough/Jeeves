package com.jeeves.vpl.survey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.firebase.FirebaseVariable;
import com.jeeves.vpl.survey.questions.QuestionView;

public class QuestionEditor extends Pane{

	@FXML private VBox vboxOpts;
	@FXML private ComboBox<UserVariable> cboVars;

	@FXML private ComboBox<String> cboQuestionText;
	

	@FXML public TextField txtAnswer;
	@FXML public TextField txtNumAnswer;
	@FXML public RadioButton rdioTrue;
	@FXML public RadioButton rdioFalse;
	@FXML public ComboBox<String> cboMultiChoice;
	@FXML public ComboBox<String> cboLessMore;

	//These need to be used across multiple different QuestionView subclasses so it's nice and clean really
	@FXML private CheckBox chkAskOnCondition;
	@FXML private CheckBox chkAssignToVar;
	@FXML private Pane paneAssignToVar;

	@FXML private HBox hboxCondition;
	@FXML private HBox hboxSaveAs;
	@FXML private ImageView imgCondition;
	@FXML private ImageView imgSaveAs;

	@FXML private Pane paneCondition;
	@FXML private TextField txtQText;
	private ToggleGroup tgroup;

	private ChangeListener<String> freeTextListener;
	private ChangeListener<String> numTextListener;
	private ChangeListener<String> multiChoiceListener;
	private ChangeListener<String> lessMoreListener;
	private ChangeListener<Toggle> trueFalseListener;

	private ChangeListener<String> listener;
	private ChangeListener<String> cboQuestionTextListener;
//	private ArrayList<Pane> questionPanes;
	private ArrayList<Node> conditionPanes;
	private QuestionView selectedQuestion;

	private QuestionView conditionQuestion;
	private FirebaseQuestion selectedQuestionModel;
	private Main gui;
	private ObservableList<QuestionView> surveyQuestions;
	/**
	 * This method enables and disables relevant controls depending on whether
	 * the user wants to ask a question based on a condition
	 */

	public QuestionEditor(ObservableList<QuestionView> surveyQuestions){
		this.surveyQuestions = surveyQuestions;
		this.gui = Main.getContext();
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/QuestionEditor.fxml"));
		Pane surveynode;
		try {
			surveynode = (Pane) surveyLoader.load();
		
		this.getChildren().add(surveynode);
		tgroup = new ToggleGroup();
		rdioTrue.setToggleGroup(tgroup);
		rdioFalse.setToggleGroup(tgroup);
		cboLessMore.getItems().addAll("less than", "more than", "equal to");
		
		addListeners();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
	}

	private void addListeners(){
		gui.registerVarListener(new ListChangeListener<FirebaseVariable>() {
			@Override
			public void onChanged(Change<? extends FirebaseVariable> c) {
				ObservableList<FirebaseVariable> vars = gui.getVariables();
				cboVars.getItems().clear();
				vars.forEach(entry -> {
					if (entry.getisCustom()) {
						UserVariable uservar = new UserVariable(entry);
						cboVars.getItems().add(uservar);
						uservar.removeHander();
					}
				});			
				}
		});
		hboxCondition.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) {
				if(arg0.getEventType().equals(MouseEvent.MOUSE_ENTERED)){
					imgCondition.setVisible(true);
				}
				else if(arg0.getEventType().equals(MouseEvent.MOUSE_EXITED)){
					imgCondition.setVisible(false);
				}
			}

		});
		hboxSaveAs.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) {
				if(arg0.getEventType().equals(MouseEvent.MOUSE_ENTERED)){
					imgSaveAs.setVisible(true);
				}
				else if(arg0.getEventType().equals(MouseEvent.MOUSE_EXITED)){
					imgSaveAs.setVisible(false);
				}				
			}

		});
		cboVars.setCellFactory(new Callback<ListView<UserVariable>, ListCell<UserVariable>>() {
			@Override
			public ListCell<UserVariable> call(ListView<UserVariable> param) {

				return new ListCell<UserVariable>(){
					@Override
					public void updateItem(UserVariable item, boolean empty){
						super.updateItem(item, empty);
						if(!empty) {
							setText(item.getName());
							getStyleClass().add(item.getVarType());
							this.setAlignment(Pos.CENTER);
							this.setFont(Font.font("Calibri",FontWeight.BOLD,13.0));
							this.setTextFill(Color.WHITE);
							this.setTextAlignment(TextAlignment.CENTER);
							getStyleClass().remove("combo-box-base");
							setGraphic(null);
						}
					}
				};
			}
		});
		cboVars.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<UserVariable>(){

			@Override
			public void changed(ObservableValue<? extends UserVariable> observable, UserVariable oldValue,
					UserVariable newValue) {
				if(newValue != null)
					selectedQuestionModel.setAssignedVar(newValue.getName());
			}

		});

		txtNumAnswer.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent arg0) {
				if(arg0.getEventType().equals(KeyEvent.KEY_TYPED))
					try {
						Long.parseLong(arg0.getCharacter());
					} catch (NumberFormatException e) {						
						arg0.consume();
						return;
					}
			}
		});

		chkAskOnCondition.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (chkAskOnCondition.isSelected()) {
					paneCondition.getChildren().forEach(child -> {
						((Node) child).setDisable(false);
						;
					});
				} else {
					paneCondition.getChildren().forEach(child -> {
						((Node) child).setDisable(true);
						;
					});

					selectedQuestionModel.setCondition(null);
					txtAnswer.setText("");
					cboQuestionText.getSelectionModel().clearSelection();

				}			}
		});
		chkAssignToVar.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (chkAssignToVar.isSelected()) {
					paneAssignToVar.getChildren().forEach(child -> {
						((Node) child).setDisable(false);
						;
					});
					paneAssignToVar.setPrefHeight(Pane.USE_COMPUTED_SIZE);
				} else {
					paneAssignToVar.getChildren().forEach(child -> {
						((Node) child).setDisable(true);
						;
					});
					paneAssignToVar.setPrefHeight(0);
					selectedQuestionModel.setAssignedVar("");
				}			}
		});


		cboQuestionTextListener = new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int questionNo = cboQuestionText.getSelectionModel().getSelectedIndex();
				if (questionNo < 0)
					return;
				hideConditionPanes();
				QuestionView question = surveyQuestions.get(questionNo);
				removeConditionListeners();
			//	question.showCheckQOpts();
				addConditionListeners();
				conditionQuestion = question;
			}

		};
		cboQuestionText.getSelectionModel().selectedItemProperty().addListener(cboQuestionTextListener);
		paneAssignToVar.getChildren().forEach(child -> {
			((Node) child).setDisable(true);
			;
		});
		paneCondition.getChildren().forEach(child -> {
			((Node) child).setDisable(true);
			;
		});
		//grpSurveyEdit.setDisable(true);

//		questionPanes = new ArrayList<Pane>();
//		Collections.addAll(questionPanes,
//				paneScale/* ,paneMultChoiceS,paneMultChoiceM */);
		conditionPanes = new ArrayList<Node>();
		Collections.addAll(conditionPanes, txtAnswer, txtNumAnswer, cboLessMore, cboMultiChoice, rdioTrue,
				rdioFalse);
	} 

	public void removeConditionListeners(){
		txtAnswer.textProperty().removeListener(freeTextListener);
		txtNumAnswer.textProperty().addListener(numTextListener);
		cboMultiChoice.getSelectionModel().selectedItemProperty().addListener(multiChoiceListener);
		cboLessMore.getSelectionModel().selectedItemProperty().addListener(lessMoreListener);
		tgroup.selectedToggleProperty().addListener(trueFalseListener);
	}


	public void populateQuestion(QuestionView entry) {
//		setSelectedQuestion(entry);
		selectedQuestionModel = entry.getModel();
		populateQ(entry.getModel());
		vboxOpts.getChildren().remove(3,vboxOpts.getChildren().size()); //leave the qtext
		if(listener != null)
			txtQText.textProperty().removeListener(listener);
		listener = new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				entry.setQuestionText(newValue);
			}			
		};
		txtQText.textProperty().addListener(listener);

		txtQText.setText(selectedQuestionModel.getquestionText());
		vboxOpts.getChildren().add(entry.getOptionsPane());
		vboxOpts.getChildren().forEach(child->child.setVisible(true));
//		lstRegQ.getChildren().forEach(label -> {
//			label.getStyleClass().remove("borderedselected");
//		});
//		entry.getStyleClass().add("borderedselected");
	}

	private void handleUpdateCondition(String answer) {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("question", cboQuestionText.getSelectionModel().getSelectedIndex());
		condition.put("answer", answer);
		selectedQuestionModel.setCondition(condition);
	}

	private void hideConditionPanes() {
		conditionPanes.forEach(pane -> {
			pane.setVisible(false);
		});
	}

	@SuppressWarnings("unchecked")
	public void populateQ(FirebaseQuestion qEntry) {

		Map<String, Object> opts = null;
		Map<String, Object> qParams = qEntry.getparams(); // (Map<String,Object>)questionData.get("params");
		if (qParams != null) {
			opts = (Map<String, Object>) qParams.get("options");
			Map<String, Object> condition = (Map<String, Object>) qParams.get("condition");

			if (qEntry.getassignedVar() != null && !qEntry.getassignedVar().equals("")) {
				chkAssignToVar.setSelected(true);
				cboVars.getItems().forEach(variable->{
					if(variable.getName().equals(qEntry.getassignedVar())){
						cboVars.getSelectionModel().select(variable);
					}
				});
			} else
				chkAssignToVar.setSelected(false);

			cboQuestionText.getItems().clear();

			for (QuestionView entry : surveyQuestions) {
			//	if(!(qentries instanceof QuestionView))continue;
		//		QuestionView preventry = (QuestionView) qentries;
				if (entry.getModel().equals(qEntry))
					break;
				cboQuestionText.getItems().add(entry.getQuestionText());
			}

			hideConditionPanes();
			if (condition != null) {
				chkAskOnCondition.setSelected(true);

				if (condition.get("question") != null) {
					int questionno = Integer.parseInt(condition.get("question").toString());
					cboQuestionText.getSelectionModel().clearSelection();
					cboQuestionText.getSelectionModel().select(questionno);
					if (condition.get("answer") != null) {

						String scon = condition.get("answer").toString();
						if (conditionQuestion != null){
		//					conditionQuestion.handleCheckQ(scon);
						}
					}
				}

			} else {
				//selectedQuestion.handleCheckQ("");
				chkAskOnCondition.setSelected(false);
			}
		}
		if (selectedQuestion == null)
			return;
		selectedQuestion.showEditOpts(opts);
	}

	public void addConditionListeners(){
		freeTextListener = new ChangeListener<String>(){
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				handleUpdateCondition(txtAnswer.getText());
			}};
			numTextListener = new ChangeListener<String>(){
				@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					handleUpdateCondition(cboLessMore.getValue() + ";" + txtNumAnswer.getText());		
				}};
				multiChoiceListener = new ChangeListener<String>(){
					@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
						if(newValue != null)
							handleUpdateCondition(newValue);				
					}};
					lessMoreListener = new ChangeListener<String>(){
						@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
							if(newValue != null)
								handleUpdateCondition(cboLessMore.getValue() + ";" + txtNumAnswer.getText());				
						}};
						trueFalseListener = new ChangeListener<Toggle>(){
							@Override public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
								handleUpdateCondition(rdioTrue.isSelected() ? "true" : "false");				
							}};
							txtAnswer.textProperty().addListener(freeTextListener);
							txtNumAnswer.textProperty().addListener(numTextListener);
							cboMultiChoice.getSelectionModel().selectedItemProperty().addListener(multiChoiceListener);
							cboLessMore.getSelectionModel().selectedItemProperty().addListener(lessMoreListener);
							tgroup.selectedToggleProperty().addListener(trueFalseListener);
	}
}
