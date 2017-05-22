package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.CHILD_COLOURS;
import static com.jeeves.vpl.Constants.CONSTRAINT_NUMS;

import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.Main;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.firebase.FirebaseQuestion;

public abstract class QuestionView extends ViewElement<FirebaseQuestion> {
	public static QuestionView create(FirebaseQuestion question) {
		String classname = question.gettype();

		try {
			switch ((int) question.getquestionType()) {

			}
			return (QuestionView) Class.forName(classname).getConstructor(FirebaseQuestion.class).newInstance(question); // It's
																															// a
																															// plain
																															// Action
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	// public String colourCode = "white";
	public int colourCode = -1;
	@FXML
	public Label lblQuestion;
	public int oldIndex = 0;
	public double originalWidth = 0;
	private Button btnDeleteQ;
	private Button btnEdit;
	private String imagePath;
	@FXML
	private ImageView imgEntry;
	private boolean isChild = false;
	// protected FirebaseQuestion model;
	protected ObservableList<QuestionView> childQuestions;
	protected Pane optionsPane;

	protected QuestionView parentQuestion;

	HBox buttonBox;

	// protected String parentConstraints;
	HBox surveynode;

	public QuestionView() {
		super(FirebaseQuestion.class);
	}

	public QuestionView(FirebaseQuestion model) {
		super(model, FirebaseQuestion.class);
	}

	public void addButtons() {
		if (surveynode.getChildren().contains(buttonBox))
			return;
		btnEdit = new Button("Edit");
		btnDeleteQ = new Button("DELETE");
		btnEdit.setOnAction(action -> {
		});
		btnDeleteQ.setOnAction(action -> {
		});
		buttonBox.getChildren().addAll(btnEdit, btnDeleteQ);
		buttonBox.setSpacing(10);

		surveynode.setPrefWidth(300);
		buttonBox.setPrefWidth(120);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		surveynode.getChildren().add(buttonBox);
	}

	public void addChildQuestion(QuestionView q) {
		childQuestions.add(q);
		q.indent();
		btnDeleteQ.setDisable(true); // shouldn't be able to delete a parent
		if (colourCode == -1) { // we ain't got no colour on the parent, add a
								// new colour to them both
			// find the minimum number that isn't there
			for (int i = 0; i < 6; i++) {
				if (!CONSTRAINT_NUMS.contains(i)) {
					colourCode = i;
					CONSTRAINT_NUMS.add(i);
					break;
				}
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
		EventHandler<MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				setOldIndex(((VBox) getParent()).getChildren().indexOf(getInstance())); // Bleugh
			}

		};
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
			surveynode = (HBox) surveyLoader.load();
			getChildren().add(surveynode);
			model = new FirebaseQuestion();
			model.setquestionType(getQuestionType());
			setImage(getImagePath());
			setQuestionText(getLabel());
			// addListeners();
			buttonBox = new HBox();
			childQuestions = FXCollections.observableArrayList();
			model.settype(getClass().getName());

		} catch (Exception e) {
			e.printStackTrace();
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

	public abstract String getLabel();

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

	public QuestionView getParentQuestion() {
		return parentQuestion;
	}

	public Map<String, Object> getQuestionOptions() {
		return model.getOptions();
	}

	public String getQuestionText() {
		return model.getquestionText() != null ? model.getquestionText() : "";
	}

	public abstract int getQuestionType();

	public String getText() {
		return model.getquestionText();
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] {};
	}

	public void indent() {

		lblQuestion.setMaxWidth(parentQuestion.lblQuestion.getWidth() - 30);
		setPrefWidth(parentQuestion.getWidth() - 30);

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
			CONSTRAINT_NUMS.remove((Integer) colourCode);
			setColour(6);
			colourCode = -1;
			btnDeleteQ.setDisable(false);

		}
	}

	public void setAssignedVar(String varname) {
		model.setAssignedVar(varname);
	}

	public void setColour(int colour) {
		// this.colourCode = colour;
		this.colourCode = colour;
		surveynode.setStyle("-fx-background-color: " + CHILD_COLOURS[colourCode]);
		for (QuestionView children : childQuestions) {
			children.setColour(colourCode);
		}
	}

	@Override
	public void setData(FirebaseQuestion model) {
		this.model = model;
		setQuestionText(model.getquestionText());
	}

	public void setImage(String image) {
		imgEntry.setImage(new Image(getClass().getResourceAsStream(image)));
	}

	public void setOldIndex(int index) {
		this.oldIndex = index;
	}

	public void setParentConstraints(String constraints) {
		model.setconditionConstraints(constraints);
	}

	public void setParentQuestion(QuestionView q) {
		if (originalWidth == 0)
			originalWidth = getWidth(); // Set the original width

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

	public void setQuestionText(String text) {
		lblQuestion.setText(text);
		model.setquestionText(text);
	}

	public void setQuestionType(int type) {
		model.setquestionType(type);
		setImage(imagePath);
	}

	public void setReadOnly(boolean readOnly) {
		isReadOnly = readOnly;
	}

	public void showDelete() {
		btnEdit.setVisible(true);
		btnDeleteQ.setVisible(true);
	}

	public abstract void showEditOpts(Map<String, Object> opts);

	public void unindent() {
		lblQuestion.setMaxWidth(100);
		setPrefWidth(originalWidth);

	}

}