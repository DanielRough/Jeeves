package com.jeeves.vpl.canvas.uielements;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.StackPane;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.firebase.FirebaseUI;

public class UIButton extends UIElement{ // NO_UCD (unused code)
	@FXML private StackPane panePane;
	@FXML private Button btnButton;
	@Override
	public ViewElement getInstance() {
		return this;
	}

	@Override
	public Node[] getWidgets() {
		return new Node[]{btnButton};
	}
	

	public UIButton(){
		this(new FirebaseUI());
	}
	public UIButton(FirebaseUI data){
		super(data);
		this.name.setValue("BUTTON"); 
		btnButton.setMaxWidth(215);
		addListeners();
	}

	public String getText(){
		return btnButton.getText();
		
	}

	public void setData(FirebaseUI model) {
		setText(model.gettext());
	}

	@Override
	public String getViewPath() {
		return String.format("/uiButton.fxml", this.getClass().getSimpleName());
	}
	
	public void setText(String text){
		btnButton.setText(text);
		model.settext(text);
	}

	@Override
	public Control getChild() {
		return btnButton;
	}

	public StringProperty getTextProperty(){
		return btnButton.textProperty();
	}
	@Override
	protected void addListeners() {
		super.addListeners();
		btnButton.textProperty().addListener(new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
					model.settext(arg2);
			}
			
		});
	}
}
