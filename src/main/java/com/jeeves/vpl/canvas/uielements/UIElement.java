package com.jeeves.vpl.canvas.uielements;

import static com.jeeves.vpl.Constants.elemNames;

import java.io.IOException;
import java.util.List;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.firebase.FirebaseUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class UIElement extends ViewElement<FirebaseUI> {
	public static UIElement create(FirebaseUI exprmodel) {
		String trigname = exprmodel.getname();
		String classname = "com.jeeves.vpl.canvas.uielements." + elemNames.get(trigname);		try {
			return (UIElement) Class.forName(classname).getConstructor(FirebaseUI.class).newInstance(exprmodel);
		} catch (Exception e) {
			System.exit(1);
		}
		return null;
	}
	protected boolean dragged = false;
	protected boolean previouslyAdded = false;
	protected TextField editField;
	
	public void setPreviouslyAdded(boolean prevAdded) {
		this.previouslyAdded = prevAdded;
	}
	public boolean getPreviouslyAdded() {
		return previouslyAdded;
	}
	public UIElement() throws InstantiationException, IllegalAccessException {
		super(FirebaseUI.class.newInstance(),FirebaseUI.class);
	}

	public UIElement(FirebaseUI data) {
		super(data, FirebaseUI.class);
	}

	@Override
	public void fxmlInit() {
		this.type = ElementType.UIELEMENT;
		this.model = new FirebaseUI();
		FXMLLoader fxmlLoader = new FXMLLoader();
		editField = new TextField();
		fxmlLoader.setController(this);
		fxmlLoader.setLocation(getClass().getResource("/" + getClass().getSimpleName() + ".fxml"));
		try {
			Node root = fxmlLoader.load();
			getChildren().add(root);
		} catch (IOException exception) {
			System.exit(1);
		}
	}

	public abstract Control getChild();
	public abstract void setText(String text);
	public abstract String getText();


	
	@Override
	public void setData(FirebaseUI element) {
		super.setData(element);
		if (element.gettext() != null)
			setText(element.gettext());
	}

	/**
	 * Method called when a new UI Element is added
	 * @param childList
	 */
	public void update(List<ViewElement> childList) {
		Stage stage = new Stage(StageStyle.UNDECORATED);
		String currentname = getText();
		UIPopupPane root = new UIPopupPane(stage,childList,currentname);
		root.init(this);
		stage.setScene(new Scene(root));
		stage.setTitle("Add property");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(this.getScene().getWindow());
		stage.showAndWait();
	}
}
