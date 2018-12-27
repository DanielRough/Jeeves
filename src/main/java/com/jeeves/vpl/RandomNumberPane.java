package com.jeeves.vpl;

import java.net.URL;
import java.util.ArrayList;

import org.apache.poi.ss.formula.functions.T;

import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class RandomNumberPane extends Pane {

	@FXML private TextField txtMin;
	@FXML private TextField txtMax;
	@FXML private TextField txtStep;
	@FXML private Button btnAdd;
	
	private Stage stage;
	private FirebaseVariable var;
	public RandomNumberPane(Stage stage,FirebaseVariable var) {
		this.stage = stage;
		this.var = var;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/RandomNumber.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
			this.stage = stage;
			txtMin.setOnKeyTyped(Constants.numberHandler);
			txtMax.setOnKeyTyped(Constants.numberHandler);
			txtStep.setOnKeyTyped(Constants.numberHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@FXML
	public void add(Event e) {
		if(txtMin.getText().isEmpty() || txtMax.getText().isEmpty() || txtStep.getText().isEmpty())
			Constants.makeInfoAlert("Error", "You have empty fields","Please make sure you enter values for all fields");
		else{
			ArrayList<String> list = new ArrayList<String>();
			list.add(txtMin.getText());
			list.add(txtMax.getText());
			list.add(txtStep.getText());
			var.setrandomOptions(list);
			FirebaseDB.getOpenProject().getvariables().add(var);
			stage.close();
		}
	}	
	@FXML
	public void close(Event e){
		stage.close();
	}
}
