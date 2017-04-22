package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.BOOLEAN;
import static com.jeeves.vpl.Constants.DATETIME;
import static com.jeeves.vpl.Constants.GEO;
import static com.jeeves.vpl.Constants.MULT_MANY;
import static com.jeeves.vpl.Constants.MULT_SINGLE;
import static com.jeeves.vpl.Constants.NUMERIC;
import static com.jeeves.vpl.Constants.OPEN_ENDED;
import static com.jeeves.vpl.Constants.SCALE;

import java.util.Map;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import com.jeeves.vpl.Main;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.canvas.actions.Action;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.survey.Survey;
public abstract class QuestionView extends ViewElement<FirebaseQuestion>{
	@FXML private ImageView imgEntry;
	@FXML private Label lblQuestion;
	private Button btnDeleteQ;
	private Button btnEdit;
	protected FirebaseQuestion model;	

	public Button getEditButton(){
		return btnEdit;
	}
	public Button getDeleteButton(){
		return btnDeleteQ;
	}
	private int questionType;
	private String imagePath;
	private boolean isReadOnly = true; //true by default
	protected Pane optionsPane;
	private EventHandler<MouseEvent> draggedHandler;
	private EventHandler<MouseEvent> mainHandler;
	private Pane parent;
	private Main gui;
	private boolean previouslyAdded = false;
	HBox surveynode;
	HBox buttonBox;
	public Pane getOptionsPane(){
		return optionsPane;
	}

	public boolean wasAdded(){
		return previouslyAdded;
	}
	public void setAddedFlag(){
		previouslyAdded = true;
	}
	public void addButtons(){
		if(surveynode.getChildren().contains(buttonBox))return;
	//	HBox buttonBox = new HBox();
		btnEdit = new Button("Edit");
		btnDeleteQ = new Button("DELETE");
		btnEdit.setOnAction(action->{});
		btnDeleteQ.setOnAction(action->{});
		buttonBox.getChildren().addAll(btnEdit,btnDeleteQ);
		buttonBox.setSpacing(10);
		
		surveynode.setPrefWidth(300);
		buttonBox.setPrefWidth(120);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		surveynode.getChildren().add(buttonBox);
	}
	public abstract void showEditOpts(Map<String,Object> opts);
	public abstract void addEventHandlers();
	public abstract void loadOptions();
	public abstract String getLabel();
	public abstract String getImagePath();
	
	public void setReadOnly(boolean readOnly){
		isReadOnly = readOnly;
		QuestionText text = new QuestionText();

	}
	@Override
	public Node[] getWidgets() {
		return new Node[]{};
	}
	public void fxmlInit(){
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
			model.setquestionType(questionType);
			setImage(getImagePath());
			setQuestionText(getLabel());
			addListeners();
			buttonBox = new HBox();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void showDelete(){
		btnEdit.setVisible(true);
		btnDeleteQ.setVisible(true);
	}
	public static QuestionView create(FirebaseQuestion question){
		String classname = question.gettype();
		try {
			return (QuestionView)Class.forName(classname).getConstructor(FirebaseQuestion.class).newInstance(question); //It's a plain Action
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public QuestionView(){
		super(FirebaseQuestion.class);
	}

	public QuestionView getInstance(){
		return this;
	}

	public int getQuestionType(){
		return questionType;
	}
	
	public FirebaseQuestion getModel(){
		return model;
	}
	
	public void setData(FirebaseQuestion model){
		this.model = model;
		setQuestionText(model.getquestionText());
	}
	public QuestionView(FirebaseQuestion model) {
		super(model,FirebaseQuestion.class);
	}

	public void setImage(String image){
		imgEntry.setImage(new Image(getClass().getResourceAsStream(image)));
	}

	public void setQuestionType(int type){
		model.setquestionType(type);
		setImage(imagePath);
	}

	public void setQuestionText(String text){
		lblQuestion.setText(text);
		model.setquestionText(text);
	}

	public String getQuestionText(){
		return model.getquestionText() != null ? model.getquestionText() : "";
	}

}