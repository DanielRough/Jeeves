package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.CHILD_COLOURS;
import static com.jeeves.vpl.Constants.getSaltString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.Main;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.firebase.FirebaseQuestion;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public abstract class QuestionView extends ViewElement<FirebaseQuestion> {
	public static QuestionView create(FirebaseQuestion question) {
		String classname = question.gettype();
		try {
			return (QuestionView) Class.forName(classname).getConstructor(FirebaseQuestion.class).newInstance(question); 
		} catch (Exception e) {
			System.exit(1);
		}
		return null;
	}
	private int colourCode = -1;
	@FXML
	public Label lblQuestion;
	private double originalWidth = 0;
	private Button btnDeleteQ;
	private Button btnEdit;
	@FXML
	private ImageView imgEntry;
	private boolean isChild = false;
	protected ObservableList<QuestionView> childQuestions;
	protected Pane optionsPane;
	private String questionId;
	protected QuestionView parentQuestion;
	private StringProperty questionTextProperty;
	HBox buttonBox;

	HBox surveynode;

	public abstract String getAnswerType();
	
	public QuestionView() throws InstantiationException, IllegalAccessException {
		super(FirebaseQuestion.class.newInstance(),FirebaseQuestion.class);
		}

	public QuestionView(FirebaseQuestion model) {
		super(model, FirebaseQuestion.class);
		
	}

	public StringProperty getQuestionTextProperty() {
		return questionTextProperty;
	}
	public void addButtons() {
		if (surveynode.getChildren().contains(buttonBox))
			return;
		btnEdit = new Button("Edit");
		btnEdit.setStyle("-fx-font-size:14px");
		btnDeleteQ = new Button("X");
		btnDeleteQ.setStyle("-fx-font-size:14px; -fx-font-weight:bold");
		buttonBox.getChildren().addAll(btnEdit, btnDeleteQ);
		buttonBox.setSpacing(10);

		surveynode.setPrefWidth(300);
		buttonBox.setPrefWidth(120);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		surveynode.getChildren().add(buttonBox);
	}

	public void addChildQuestion(QuestionView q) {
		childQuestions.add(q);
		q.indentQuestion();
		btnDeleteQ.setDisable(true); // shouldn't be able to delete a parent
		if (colourCode == -1) { // we ain't got no colour on the parent, add a
								// new colour to them both
			// find the minimum number that isn't there
			List<Integer> constraintNums = Constants.getConstraintNums();
			for (int i = 0; i < 6; i++) {
				if (!constraintNums.contains(i)) {
					colourCode = i;
					constraintNums.add(i);
					break;
				}
			}
			//let's start again
			if(colourCode == -1){
				constraintNums.clear();
				colourCode = 0;
			}
			setColour(colourCode);
			q.setColour(colourCode);

		} else {
			q.setColour(colourCode); // Colour the child the same colour as the
										// parent
		}

	}

	public abstract void addEventHandlers();

	@Override
	public void addListeners() {
		super.addListeners();
		EventHandler<MouseEvent> pressedHandler = arg0 -> 
				setOldIndex(((VBox) getParent()).getChildren().indexOf(getInstance())); // Bleugh
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
	}

	@Override
	public void fxmlInit() {
		this.type = ElementType.QUESTION;
		gui = Main.getContext();
		loadOptions();
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/QuestionView.fxml"));

		try {
			surveynode = surveyLoader.load();
			getChildren().add(surveynode);

			setImage(getImagePath());
			questionTextProperty = new SimpleStringProperty();

			buttonBox = new HBox();
			childQuestions = FXCollections.observableArrayList();

		} catch (Exception e) {
			System.exit(1);
		}
	}

	public String getAssignedVar() {
		return model.getassignedVar();
	}

	public ObservableList<QuestionView> getChildQuestions() {
		return childQuestions;
	}
	public Button getDeleteButton() {
		return btnDeleteQ;
	}

	public Button getEditButton() {
		return btnEdit;
	}

	public abstract String getImagePath();

	@Override
	public QuestionView getInstance() {
		return this;
	}


	@Override
	public FirebaseQuestion getModel() {
		return model;
	}

	public Pane getOptionsPane() {
		return optionsPane;
	}

	public String getParentConstraints() {
		return model.getconditionConstraints();
	}
	
	
	public QuestionView getParentQuestion(){
		return parentQuestion;
	}

	

	public String getQuestionId(){
		return this.questionId;
	}
	@SuppressWarnings("unchecked")
	public Map<String, Object> getQuestionOptions() {
		if(model.getparams().get("options") == null)
			return new HashMap<>();
		return (Map<String,Object>)model.getparams().get("options");
	}

	public String getQuestionText() {
		return model.getquestionText() != null ? model.getquestionText() : "";
	}

	public abstract String getQuestionType();

	public String getText() {
		return model.getquestionText();
	}


	public void setisMandatory(boolean mandatory){
		model.setisMandatory(mandatory);
	}
	
	public boolean isMandatory(){
		return model.getisMandatory();
	}
	public void indentQuestion() {

		Platform.runLater(() ->{
				lblQuestion.setMaxWidth(parentQuestion.lblQuestion.getWidth() - 30);
				setPrefWidth(parentQuestion.getWidth() - 30);
				buttonBox.setPadding(new Insets(0,20,0,0));
		});
	}
	
	public void unindent() {
		lblQuestion.setMaxWidth(100);
		setPrefWidth(originalWidth);
		buttonBox.setPadding(new Insets(0,0,0,0));


	}
	public boolean isChild() {
		return isChild;
	}

	public abstract void loadOptions();

	public void removeChildQuestion(QuestionView q) {
		childQuestions.remove(q);
		q.setColour(6);
		q.unindent();

		if (childQuestions.isEmpty()) { // If there are no constraints, and this
										// isn't already a child then we can
										// remove the colour
			Constants.getConstraintNums().remove((Integer) colourCode);
			setColour(6);
			colourCode = -1;
			btnDeleteQ.setDisable(false);

		}
	}

	public void setAssignedVar(String varname) {
		model.setAssignedVar(varname);
	}

	public void setColour(int colour) {
		this.colourCode = colour;
		surveynode.setStyle("-fx-background-color: " + CHILD_COLOURS[colourCode]);
		for (QuestionView children : childQuestions) {
			children.setColour(colourCode);
		}
	}

	@Override
	public void setData(FirebaseQuestion model) {
		super.setData(model);

		setQuestionText(model.getquestionText());
		setQuestionId(model.getquestionId());
		model.setquestionType(getQuestionType());
		if(model.getquestionId() == null)
			model.setquestionId(getSaltString());
		model.settype(getClass().getName());

	}

	public void setImage(String image) {
		imgEntry.setImage(new Image(getClass().getResourceAsStream(image)));
	}


	public void setParentConstraints(String constraints) {
		model.setconditionConstraints(constraints);
	}

	public void setParentQuestion(QuestionView q) {
		Platform.runLater(()->{
				if (originalWidth == 0)
					originalWidth = getWidth(); // Set the original width
		});
	
		if (parentQuestion != null) // If we currently have a parent question
			parentQuestion.removeChildQuestion(this);

		this.parentQuestion = q;
		if (q == null) {
			model.setconditionQuestion(null);
			isChild = false;
		} else {
			parentQuestion.addChildQuestion(this);
			model.setconditionQuestion(q.getModel());
			isChild = true;
		}
	}

	public void setQuestionId(String id){
		questionId = id;
	}
	public void setQuestionText(String text) {
		lblQuestion.setText(text);
		model.setquestionText(text);
	}

	public void setQuestionType(String type) {
		model.setquestionType(type);
	}

	public void showDelete() {
		btnEdit.setVisible(true);
		btnDeleteQ.setVisible(true);
	}

	public abstract void showEditOpts(Map<String, Object> opts);




}