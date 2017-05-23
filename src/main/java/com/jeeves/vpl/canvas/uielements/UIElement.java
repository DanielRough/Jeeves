package com.jeeves.vpl.canvas.uielements;

import java.io.IOException;
import java.util.List;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.firebase.FirebaseUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class UIElement extends ViewElement<FirebaseUI> {
	public static UIElement create(FirebaseUI exprmodel) {
		String classname = exprmodel.gettype();
		try {
			return (UIElement) Class.forName(classname).getConstructor(FirebaseUI.class).newInstance(exprmodel);
		} catch (Exception e) {
			return new UIButton(); // for safety purposes
		}
	}
	public boolean dragged = false;

	// private ElementReceiver receiver;
	public boolean previouslyAdded = false;

	public UIElement() {
		super(FirebaseUI.class);
	}

	public UIElement(FirebaseUI data) {
		super(data, FirebaseUI.class);
		this.model = data;
	}

	@Override
	public void fxmlInit() {
		this.type = ElementType.UIELEMENT;
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

	public abstract Control getChild();

	// public abstract StringProperty getTextProperty();
	public abstract String getText();

	public abstract String getViewPath();

	@Override
	public void setData(FirebaseUI element) {
		super.setData(element);
		if (element.gettext() != null)
			setText(element.gettext());
	}

	public abstract void setText(String text);

	public void update(List<ViewElement> childList) {
		Stage stage = new Stage(StageStyle.UNDECORATED);
		UIPopupPane root = new UIPopupPane(stage,childList);
		root.init(this);
		stage.setScene(new Scene(root));
		stage.setTitle("Add property");

		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(this.getScene().getWindow());
		stage.showAndWait();
	}
}
