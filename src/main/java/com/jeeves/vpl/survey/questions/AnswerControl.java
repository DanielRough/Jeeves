package com.jeeves.vpl.survey.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.Main;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.actions.Action;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.canvas.receivers.QuestionReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.survey.ConditionEditor;
import com.jeeves.vpl.survey.QuestionEditor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * Just like Survey doesn't extend Trigger and Action doesn't extend Question, I dont think this should extend Control 
 * 
 * @author Daniel
 *
 */
public class AnswerControl extends Question implements SurveyElement{
	public static final String NAME = "Question Condition";
	protected QuestionReceiver childReceiver;
	private static final String UPDATE = "update";
	private static final String UPDATED = "updated";
	private ConditionEditor condEditor;
	protected ObservableList<Question> childQuestions;
	@FXML
	private Pane pane;
	@FXML
	protected HBox evalbox;
	@FXML
	private ComboBox<Question> cboChoice;
	private List<Question> prevQuestions;
	
	private ChangeListener<Question> listener;
	private Question conditionQuestion;
	protected ExpressionReceiver exprreceiver;
	protected ObservableMap<String, Object> params;
	public ObservableMap<String, Object> getparams() {
		return params;
	}
	
	public void updateChoiceBox(List<Question> prevQs) {
		this.prevQuestions.clear();
		this.prevQuestions.addAll(prevQs);
//		this.prevQuestions = prevQuestions;
		System.out.println("We updatin this " + this + " with questions " + prevQuestions);
		cboChoice.getSelectionModel().selectedItemProperty().removeListener(listener);
		cboChoice.getItems().clear();
		cboChoice.getItems().addAll(prevQuestions);
		cboChoice.getSelectionModel().selectedItemProperty().addListener(listener);
		if(conditionQuestion == null) {
			condEditor.populate(null); //Clear the condition widgets
			//return;
		}
		for(int i = 0; i < prevQuestions.size(); i++) {
			String qtext = prevQuestions.get(i).getQuestionText();
			if(conditionQuestion != null && qtext.equals(conditionQuestion.getQuestionText())) {
				cboChoice.getSelectionModel().select(prevQuestions.get(i));
		//		break;
			}
		}
		//I think this needs to be in here to propagate changes from parent receivers to their children
		for(int i = 0; i < childReceiver.getChildElements().size(); i++) {
			Question q = (Question)childReceiver.getChildElements().get(i);
			if(q.getType() == ElementType.CTRL_QUESTION) {
				System.out.println("TIS INDEED a control question");
				((AnswerControl)q).updateChoiceBox(prevQuestions);
			}
			else if(!prevQuestions.contains(q)){ //ought to stop duplicates
				prevQuestions.add(q);
				System.out.println("Adding " + q.getQuestionText() + "(" + q +  ") to list " + prevQuestions);
			}
		}
		condEditor.populate(null); //Clear the condition widgets
	}
	
	public QuestionReceiver getMyReceiver() {
		return childReceiver;
	}
	public AnswerControl(String name) {
		this(new FirebaseQuestion(name));
	}

	public AnswerControl(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public void fxmlInit() {
		this.type = ElementType.CTRL_QUESTION;
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/AnswerControl.fxml"));

		try {
			Node root = surveyLoader.load();
			getChildren().add(root);
			childQuestions = FXCollections.observableArrayList();
			questionTextProperty = new SimpleStringProperty();
			condEditor = new ConditionEditor(this);
			evalbox.getChildren().add(condEditor);

		} catch (Exception e) {
			System.exit(1);
		}
		childQuestions = FXCollections.observableArrayList();
		childReceiver = new QuestionReceiver();
		getChildren().add(childReceiver);
		childReceiver.setLayoutY(pane.getPrefHeight());

		pane.setPrefHeight(USE_COMPUTED_SIZE);
		childReceiver.getBrackets().getStyleClass().clear();
		childReceiver.getBrackets().getStyleClass().add("if_control");
		setPickOnBounds(false);
		
		prevQuestions = new ArrayList<Question>();
		Callback<ListView<Question>, ListCell<Question>> factory = lv -> new ListCell<Question>() {
		    @Override
		    protected void updateItem(Question item, boolean empty) {
		        super.updateItem(item, empty);
		        setText(empty ? "" : item.getQuestionText());
		    }
		};
		cboChoice.setCellFactory(factory);
		cboChoice.setButtonCell(factory.call(null));
		listener = new ChangeListener<Question>() {
			@Override
			public void changed(ObservableValue<? extends Question> arg0, Question arg1, Question arg2) {
				conditionQuestion = arg2;
				System.out.println("Condition question change");
				condEditor.populate(arg2);
			}
		};
		cboChoice.getSelectionModel().selectedItemProperty().addListener(listener);
	}

	public ObservableList<Question> getChildQuestions() {
		return childQuestions;
	}
	
	@Override
	public void addListeners() {
		super.addListeners();
		params = FXCollections.observableHashMap();
		if (params != null) {
			params.addListener(
						(javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change) ->{
					if (change.wasAdded()) {
						model.getparams().put(change.getKey(), change.getValueAdded());
					} else {
						model.getparams().remove(change.getKey());
					}
				}
			);
		}
		
		childReceiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
			arg0.next();
			if (arg0.wasAdded()) {
				ViewElement<?> added = (ViewElement<?>) arg0.getAddedSubList().get(0);
				if (model.getchildQuestions() == null)
					model.setchildQuestions(new ArrayList<FirebaseQuestion>());
				int index = childReceiver.getChildElements().indexOf(added);
				model.getchildQuestions().add(index, (FirebaseQuestion) added.getModel());
				addQuestion(index,(Question)added);
			} 
			//ArrayList<Question> newActions = new ArrayList<>();

			model.getchildQuestions().clear();
			params.put(UPDATE,UPDATED);
			params.remove(UPDATE);
			childReceiver.getChildElements().forEach(element -> {
				Question myaction = (Question)element;
				//newActions.add(myaction);
				model.getchildQuestions().add((FirebaseQuestion) element.getModel());
				
				//DJR got rid of this for now as it was causing problems
//				myaction.getparams().addListener((
//							javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change)-> {
//						//Merciless hack to update parent receiver
//						params.put(UPDATE,UPDATED);
//						params.remove(UPDATE);
//					}
//
//				);
			});
			//Should put this in the AnswerControl class but I'm not in the mood
			List<Question> qSoFar = new ArrayList<Question>();
			qSoFar.addAll(getInstance().prevQuestions);
			for(int i = 0; i < childReceiver.getChildElements().size(); i++) {
				Question q = (Question)childReceiver.getChildElements().get(i);
				if(q.getType() == ElementType.CTRL_QUESTION) {
					((AnswerControl)q).updateChoiceBox(qSoFar);
				}
				else if(!qSoFar.contains(q)){ //ought to stop duplicates
					qSoFar.add(q);
				}
			}
			
		});
		//Clear the previous question box when we remove this
		EventHandler newHandler = event->{
			cboChoice.getItems().clear();
			if(prevQuestions != null)
				prevQuestions.clear();
		};
		addEventHandler(MouseEvent.MOUSE_PRESSED, newHandler);

//		exprreceiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
//			if (!exprreceiver.getChildElements().isEmpty()) {
//				ViewElement<?> child = exprreceiver.getChildElements().get(0);
//				Expression variable = ((Expression) child);
//				
//				//Here, whenever the parameters change, we remove and re-add it to the model.
//				//This triggers a change in the getVars() of the Action, which in turn triggers a change in the trigger
//				//Basically by adjusting the parameters of an expression in our action, we update the whole trigger config. Woohoo!
//				variable.getparams().addListener((
//							javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change) ->{
//						vars.clear();
//						vars.add(0,variable.getModel());
//						params.put(UPDATE,UPDATED);
//						params.remove(UPDATE);	
//					}
//					
//				);
//				vars.clear();
//				vars.add(0, variable.getModel());				
//				model.setcondition((FirebaseExpression) child.getModel());
//			} else {
//				vars.clear();
//
//				model.setcondition(null);
//			}
//		});
	//	if(exprreceiver.getChildExpression() == null)return;
	//	ViewElement child = exprreceiver.getChildElements().get(0);
	//	Expression variable = ((Expression) child);
		
		//Here, whenever the parameters change, we remove and re-add it to the model.
		//This triggers a change in the getVars() of the Action, which in turn triggers a change in the trigger
		//Basically by adjusting the parameters of an expression in our action, we update the whole trigger config. Woohoo!
//		variable.getparams().addListener(
//					(javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change)-> {
//				vars.clear();
//				vars.add(0,variable.getModel());
//				params.put(UPDATE,UPDATED);
//				params.remove(UPDATE);	
//			}
//			
//		);
	}

	public void showEditPane(Question view) {
		Stage stage = new Stage(StageStyle.UNDECORATED);
		QuestionEditor editor = new QuestionEditor(stage);
		editor.populateQuestion(view);
		stage.setScene(new Scene(editor));
		stage.setTitle("Edit question");
		stage.initModality(Modality.NONE);
		stage.initOwner(Main.getContext().getStage());
		stage.show();
		stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if(arg2.booleanValue() == false) {
					stage.close();
				}
			}
			
		});
	}
	//Copied largely from the Survey class with a couple wee differences
	public void addQuestion(int index, Question view) {
		//surveyQuestions.add(index, view);
		if(view.getQuestionText().isEmpty() && view.getType() != ElementType.CTRL_QUESTION) {
			showEditPane(view);
		}
		view.getEditButton().setOnAction(event -> {
			showEditPane(view);
		});

		view.getQuestionTextProperty().addListener((o,v0,v1) -> {
				if(!v1.isEmpty()) {
					//Merciless hack to update parent receiver
					params.put(UPDATE,UPDATED);
					params.remove(UPDATE);				}
		});

	}
	
	public void setParentConstraints(FirebaseQuestion conditionQuestion, String constraints) {
		model.setconditionQuestion(conditionQuestion);
		model.setconditionConstraints(constraints);
	}
	
	public String getParentConstraints() {
		return model.getconditionConstraints();
	}
	
	public Question getParentQuestion(){
		return conditionQuestion;
	}
	
	@Override
	public void setData(FirebaseQuestion model) {
		super.setData(model);
		childQuestions = FXCollections.observableArrayList();
		if (model.getchildQuestions() != null) {
			List<FirebaseQuestion> onReceive = new ArrayList<>(model.getchildQuestions()); 
			for (FirebaseQuestion question : onReceive) {
				Question myaction = Question.create(question);
				childQuestions.add(myaction);
				//I think the Y coordinate has to be high so it gets tacked onto the end...
				//Yup that worked. Wow.
				childReceiver.addChild(myaction, 0, 10000);
			}
		}
		FirebaseQuestion conditionQuestion = model.getconditionQuestion();
//		FirebaseExpression condition = model.getcondition();
		if (conditionQuestion == null)
			return;
		Question conditionQ = Question.create(conditionQuestion);
		cboChoice.getSelectionModel().select(conditionQ);
		condEditor.populate(conditionQ);
//		Expression expr = Expression.create(condition);
	//	exprreceiver.addChild(expr, 0, 0);
	}
	@Override
	public String getAnswerType() {
		return null;
	}

	@Override
	public void addEventHandlers() {		
	}

	@Override
	public String getImagePath() {
		return null;
	}

	@Override
	public String getQuestionType() {
		return null;
	}

	@Override
	public void loadOptions() {		
	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {		
	}

	public AnswerControl getInstance() {
		return this;
	}
}
