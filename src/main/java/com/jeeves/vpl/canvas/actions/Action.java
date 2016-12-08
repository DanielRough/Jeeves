package com.jeeves.vpl.canvas.actions;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.firebase.FirebaseAction;

/**
 * Actions that are performed on firing Triggers
 * @author Daniel
 *
 */
public abstract class Action extends ViewElement<FirebaseAction> {
	protected ObservableMap<String,Object> params = FXCollections.observableHashMap();

	public static String[] actionNames = {
			"com.jeeves.vpl.canvas.actions.PromptAction",
			"com.jeeves.vpl.canvas.actions.SendTextAction",
			"com.jeeves.vpl.canvas.actions.SpeakerAction",
			"com.jeeves.vpl.canvas.actions.SurveyAction",
			"com.jeeves.vpl.canvas.actions.UpdateAction",
			"com.jeeves.vpl.canvas.actions.WaitingAction",
	};
	
	public static Action create(FirebaseAction exprmodel){
		String classname = exprmodel.gettype();
		try {
			return (Action)Class.forName(classname).getConstructor(FirebaseAction.class).newInstance(exprmodel); //It's a plain Action
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected Action(FirebaseAction data) {
		super(data,FirebaseAction.class);
		
	}

	public void fxmlInit(){
		setInitHeight(30);
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		fxmlLoader.setLocation(getClass().getResource(getViewPath()));
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
	@Override
	public Action getInstance() {
		return this;
	}
	public abstract String getViewPath();

	public void setData(FirebaseAction data){
		super.setData(data);
	}

	protected void addListeners(){
		super.addListeners();
	}
}
