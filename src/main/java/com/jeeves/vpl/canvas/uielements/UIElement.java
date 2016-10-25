package com.jeeves.vpl.canvas.uielements;

import java.io.IOException;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.ElementReceiver;
import com.jeeves.vpl.canvas.receivers.IReceiver;
import com.jeeves.vpl.firebase.FirebaseUI;

public abstract class UIElement extends ViewElement<FirebaseUI>{
	private ElementReceiver receiver;
	public boolean previouslyAdded = false;
	
	public UIElement(FirebaseUI data){
		super(data,FirebaseUI.class);
		this.model = data;
	}
	
	public void fxmlInit(){
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
	public static UIElement create(FirebaseUI exprmodel){
		String classname = exprmodel.gettype();
		
		try {
			return (UIElement)Class.forName(classname).getConstructor(FirebaseUI.class).newInstance(exprmodel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public void setData(FirebaseUI element){
		super.setData(element);
		setText(element.gettext());
	}
	public abstract String getText();
	public boolean dragged = false;
	public void setReceiver(ElementReceiver receiver){
		this.receiver = receiver;
	}
	
	@Override
	public IReceiver getReceiver(){
		return receiver;
	}

	public abstract String getViewPath();
	public abstract void setText(String text);
	public abstract Control getChild();
	public abstract StringProperty getTextProperty();
	public void update(){
		 Stage stage = new Stage(StageStyle.UNDECORATED);
		   UIPopupPane root= new UIPopupPane(stage);
		   root.init(this);
			stage.setScene(new Scene(root));
			   stage.setTitle("Add property");
			   
			   stage.initModality(Modality.APPLICATION_MODAL);
			   stage.initOwner(this.getScene().getWindow());
			   stage.showAndWait();
	}
}
