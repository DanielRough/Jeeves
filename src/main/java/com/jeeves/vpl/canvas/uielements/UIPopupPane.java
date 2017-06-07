package com.jeeves.vpl.canvas.uielements;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.jeeves.vpl.ViewElement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

class UIPopupPane extends Pane {

	@FXML
	private Button btnCancel;
	@FXML
	private Button btnOkay;
	private UIElement element;
	@FXML
	private TextField txtText;

	public UIPopupPane(Stage stage, List<ViewElement> currentChildren) {

		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/uiPopup.fxml");
		fxmlLoader.setLocation(location);

		//Hacky stuff to ensure we don't duplicate any names
		ArrayList<String> currentnames = new ArrayList<String>();
		for(ViewElement child : currentChildren){
			UIElement uichild = (UIElement)child;
			if(uichild instanceof UIButton){
				currentnames.add(uichild.getText());
			}
		}

		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
		txtText.requestFocus();
		btnCancel.setOnAction(click -> {
			stage.hide();
		});
		btnOkay.setOnAction(click -> {
			if(element instanceof UIButton && currentnames.contains(txtText.getText())){
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Duplicate button names");
				alert.setHeaderText(null);
				alert.setContentText("All buttons must have unique names");
				alert.showAndWait();
				return;
			}
			element.setText(txtText.getText());
			stage.hide();
		});
		txtText.setOnKeyReleased(keyevent -> {
			if (keyevent.getCode().toString().equals("ENTER")) {
				if(element instanceof UIButton && currentnames.contains(txtText.getText())){
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Duplicate button names");
					alert.setHeaderText(null);
					alert.setContentText("All buttons must have unique names");
					alert.showAndWait();
					return;
				}
				element.setText(txtText.getText());
				stage.hide();
			}
		});
	}

	public void init(UIElement element) {
		this.element = element;
		txtText.setText(element.getText());
	}
}
