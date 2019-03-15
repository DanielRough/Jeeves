package com.jeeves.vpl;

import static com.jeeves.vpl.Constants.VAR_CLOCK;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	final Logger logger = LoggerFactory.getLogger(RandomTimePane.class);

	@FXML HBox hBoxEarliest;
	@FXML HBox hBoxLatest;
	@FXML Button btnAdd;
	private Stage stage;
	private FirebaseVariable var;
	private TimeReceiver timeEarliest;
	private TimeReceiver timeLatest;
	public RandomTimePane(Stage stage, FirebaseVariable var, boolean isNew) {
		this.stage = stage;
		this.var = var;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/RandomTime.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = fxmlLoader.load();
			getChildren().add(root);
			this.stage = stage;
			timeEarliest = new TimeReceiver(VAR_CLOCK);
			timeLatest = new TimeReceiver(VAR_CLOCK);
			hBoxEarliest.getChildren().add(timeEarliest);
			hBoxLatest.getChildren().add(timeLatest);
			if(!isNew) {
				List<String> options = var.getrandomOptions();
				timeEarliest.setText(options.get(0));
				timeLatest.setText(options.get(1));
				timeEarliest.setDisable(true);
				timeLatest.setDisable(true);
				btnAdd.setDisable(true);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
	}
	
	@FXML
	public void add(Event e) {
		ArrayList<String> list = new ArrayList<>();
		list.add(timeEarliest.getText());
		list.add(timeLatest.getText());
		var.setrandomOptions(list);
		FirebaseDB.getInstance().getOpenProject().getvariables().add(var);
		stage.close();
	}
	@FXML
	public void close(Event e){
		stage.close();
	}
}
