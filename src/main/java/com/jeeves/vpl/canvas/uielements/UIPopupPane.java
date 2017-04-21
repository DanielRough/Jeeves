package com.jeeves.vpl.canvas.uielements;

import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

class UIPopupPane extends Pane{

	private UIElement element;
	@FXML private Button btnOkay;
	@FXML private Button btnCancel;
	@FXML private TextField txtText;
public UIPopupPane(Stage stage) {
		
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/UIPopup.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);	
			} catch (Exception e) {
				e.printStackTrace();
			}
		txtText.requestFocus();
		btnCancel.setOnAction(click->{stage.hide();});
		btnOkay.setOnAction(click->{element.setText(txtText.getText()); stage.hide();});
		txtText.setOnKeyReleased(keyevent->{if(keyevent.getCode().toString().equals("ENTER")){element.setText(txtText.getText()); stage.hide();}});
	}

public void init(UIElement element){
	this.element = element;
	txtText.setText(element.getText());
}
}
