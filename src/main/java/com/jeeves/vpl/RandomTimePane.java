package com.jeeves.vpl;

import static com.jeeves.vpl.Constants.VAR_CLOCK;

import java.net.URL;
import java.util.ArrayList;

import com.jeeves.vpl.canvas.receivers.DateReceiver;
import com.jeeves.vpl.canvas.receivers.TimeReceiver;
import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class RandomTimePane extends Pane{

	@FXML HBox hBoxEarliest;
	@FXML HBox hBoxLatest;
	@FXML Button btnAdd;
	private Stage stage;
	private FirebaseVariable var;
	private TimeReceiver timeEarliest;
	private TimeReceiver timeLatest;
	
	public RandomTimePane(Stage stage, FirebaseVariable var) {
		this.stage = stage;
		this.var = var;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/RandomTime.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
			this.stage = stage;
			timeEarliest = new TimeReceiver(VAR_CLOCK);
			timeLatest = new TimeReceiver(VAR_CLOCK);
			hBoxEarliest.getChildren().add(timeEarliest);
			hBoxLatest.getChildren().add(timeLatest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void add(Event e) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(timeEarliest.getText());
		list.add(timeLatest.getText());
		var.setrandomOptions(list);
		FirebaseDB.getOpenProject().getvariables().add(var);
		stage.close();
	}
	@FXML
	public void close(Event e){
		stage.close();
	}
}
