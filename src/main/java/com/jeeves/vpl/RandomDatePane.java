package com.jeeves.vpl;

import static com.jeeves.vpl.Constants.VAR_DATE;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeeves.vpl.canvas.receivers.DateReceiver;
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
public class RandomDatePane extends Pane{
	final Logger logger = LoggerFactory.getLogger(RandomDatePane.class);

	@FXML HBox hBoxEarliest;
	@FXML HBox hBoxLatest;
	@FXML Button btnAdd;
	private Stage stage;
	private FirebaseVariable var;
	private DateReceiver dateEarliest;
	private DateReceiver dateLatest;
	public RandomDatePane(Stage stage, FirebaseVariable var,boolean isNew) {
		this.stage = stage;
		this.var = var;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/RandomDate.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = fxmlLoader.load();
			getChildren().add(root);
			this.stage = stage;
			dateEarliest = new DateReceiver(VAR_DATE);
			dateLatest = new DateReceiver(VAR_DATE);
			hBoxEarliest.getChildren().add(dateEarliest);
			hBoxLatest.getChildren().add(dateLatest);
			if(!isNew) {
				List<String> options = var.getrandomOptions();
				dateEarliest.setText(options.get(0));
				dateLatest.setText(options.get(1));
				dateEarliest.setDisable(true);
				dateLatest.setDisable(true);
				btnAdd.setDisable(true);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
	}
	@FXML
	public void add(Event e) {
		ArrayList<String> list = new ArrayList<>();
		list.add(dateEarliest.getText());
		list.add(dateLatest.getText());
		var.setrandomOptions(list);
		FirebaseDB.getInstance().getOpenProject().getvariables().add(var);
		stage.close();
	}	
	@FXML
	public void close(Event e){
		stage.close();
	}
}
