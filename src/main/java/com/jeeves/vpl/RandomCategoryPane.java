package com.jeeves.vpl;

import java.net.URL;

import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class RandomCategoryPane extends Pane{

	@FXML private Button btnAdd;
	@FXML private Button btnAddOption;
	@FXML private Button btnDelete;
	@FXML private TextField txtOption;
	@FXML private ListView<String> lstList;
	private Stage stage;
	private FirebaseVariable var;
	
	public RandomCategoryPane(Stage stage,FirebaseVariable var) {
		this.stage = stage;
		this.var = var;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/RandomCategory.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
			this.stage = stage;
			lstList.selectionModelProperty().addListener(changed->{
				btnDelete.setDisable(false);
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@FXML
	public void add(Event e) {
		var.setrandomOptions(lstList.getItems());;
		FirebaseDB.getOpenProject().getvariables().add(var);
		stage.close();
	}	
	@FXML
	public void close(Event e){
		stage.close();
	}
	
	@FXML
	public void addOption(Event e) {
		String text = txtOption.getText();
		if(text.length() != 0) {
			lstList.getItems().add(text);
		}
	}
	
	@FXML
	public void deleteOption(Event e) {
		String selected = lstList.getSelectionModel().getSelectedItem();
		lstList.getItems().remove(selected);
		lstList.getSelectionModel().clearSelection();
		btnDelete.setDisable(true);
	}
}
