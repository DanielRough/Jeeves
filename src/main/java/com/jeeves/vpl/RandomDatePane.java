package com.jeeves.vpl;

import java.net.URL;
import java.util.ArrayList;

import com.jeeves.vpl.canvas.receivers.DateReceiver;
import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseVariable;
import static com.jeeves.vpl.Constants.VAR_DATE;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
public class RandomDatePane extends Pane{

	@FXML HBox hBoxEarliest;
	@FXML HBox hBoxLatest;
	@FXML Button btnAdd;
	private Stage stage;
	private FirebaseVariable var;
	private DateReceiver dateEarliest;
	private DateReceiver dateLatest;
	public RandomDatePane(Stage stage, FirebaseVariable var) {
		this.stage = stage;
		this.var = var;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/RandomDate.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
			this.stage = stage;
			dateEarliest = new DateReceiver(VAR_DATE);
			dateLatest = new DateReceiver(VAR_DATE);
			hBoxEarliest.getChildren().add(dateEarliest);
			hBoxLatest.getChildren().add(dateLatest);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@FXML
	public void add(Event e) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(dateEarliest.getText());
		list.add(dateLatest.getText());
		var.setrandomOptions(list);
		FirebaseDB.getOpenProject().getvariables().add(var);
		stage.close();
	}	
	@FXML
	public void close(Event e){
		stage.close();
	}
}
