package com.jeeves.vpl;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	final Logger logger = LoggerFactory.getLogger(RandomCategoryPane.class);

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
			Node root = fxmlLoader.load();
			getChildren().add(root);
			this.stage = stage;
			lstList.selectionModelProperty().addListener(changed->
				btnDelete.setDisable(false)
			);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
	}
	@FXML
	public void add(Event e) {
		var.setrandomOptions(lstList.getItems());
		FirebaseDB.getInstance().getOpenProject().getvariables().add(var);
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
