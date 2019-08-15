package com.jeeves.vpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.jeeves.vpl.firebase.FirebaseDB;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Class for logging in/registering. Communicates with the Firebase database to determine
 * whether the email/password combination exists
 * @author DJR
 *
 */
public class LoginRegisterPane extends Pane{
	private Stage stage;
	@FXML private TextField txtJson;
	@FXML private TextField txtStorage;	
	@FXML private Button btnJson;
	@FXML private Button btnStorage;
	@FXML private Label lblError;
	@FXML private VBox vboxShadow;
	@FXML private VBox vboxLoading;
	@FXML private Label lblLoading;
	@FXML private HBox hboxError;
	private boolean bothLoaded = false;
	public void doFileCheck() {
		File f = new File(Constants.FILEPATH);
		File f2 = new File(Constants.STORAGEPATH);
		
		if(f.exists() && f2.exists()) {
			bothLoaded = true;
			JsonParser parser = new JsonParser();
			JsonElement fileStuff;
			try {
				fileStuff = parser.parse(new JsonReader(new FileReader(f)));
				String projid = fileStuff.getAsJsonObject().get("project_id").getAsString();
				InputStream resource = new FileInputStream(Constants.FILEPATH);
				FirebaseOptions options = new FirebaseOptions.Builder()
						.setCredentials(GoogleCredentials.fromStream(resource))
						.setDatabaseUrl("https://" + projid+".firebaseio.com")
					    .setStorageBucket(projid+".appspot.com")
						.build();
				FirebaseApp.initializeApp(options);
				resource.close();
				stage.hide();
				FirebaseDB.getInstance().firebaseLogin();

			} catch (IOException | JsonIOException | JsonSyntaxException e) {
				e.printStackTrace();
			} 
		}
	}
	
	public boolean shouldLoad() {
		return !bothLoaded;
	}
	public LoginRegisterPane(Stage stage) throws IOException {
		this.stage = stage;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/LoginRegister.fxml");
		fxmlLoader.setLocation(location);
		Node root = fxmlLoader.load();
		getChildren().add(root);
		btnJson.setOnAction(e -> {
			final FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = 
                    new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
            fileChooser.getExtensionFilters().add(extFilter);
			File file = fileChooser.showOpenDialog(null);
			if (file != null) {
				txtJson.setText(file.getAbsolutePath());
				JsonParser parser = new JsonParser();
				try {
					JsonElement fileStuff = parser.parse(new JsonReader(new FileReader(txtJson.getText())));
					FileWriter writer = new FileWriter(Constants.FILEPATH);
					writer.write(fileStuff.toString());
					writer.close();
					doFileCheck();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}	
		});
		btnStorage.setOnAction(e -> {
			final FileChooser fileChooser = new FileChooser();
			 // Set extension filter
            FileChooser.ExtensionFilter extFilter = 
                    new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
            fileChooser.getExtensionFilters().add(extFilter);
			File file = fileChooser.showOpenDialog(null);
			if (file != null) {
				txtStorage.setText(file.getAbsolutePath());
				JsonParser parser = new JsonParser();
				try {
					JsonElement fileStuff = parser.parse(new JsonReader(new FileReader(txtStorage.getText())));
					FileWriter writer = new FileWriter(Constants.STORAGEPATH);
					writer.write(fileStuff.toString());
					writer.close();
					doFileCheck();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}	
		});	
		doFileCheck();
	}

	@FXML
	private void close(Event e){
		stage.close();
		System.exit(0);
	}

	@FXML
	private void showGlow(Event e){
		Node image = (Node)e.getSource();
		image.getStyleClass().add("drop_shadow");
	}

	@FXML
	private void hideGlow(Event e){
		Node image = (Node)e.getSource();
		image.getStyleClass().remove("drop_shadow");
	}
}
