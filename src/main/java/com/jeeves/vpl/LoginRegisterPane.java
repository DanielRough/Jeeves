package com.jeeves.vpl;
import static com.jeeves.vpl.Constants.REG_ERROR;
import static com.jeeves.vpl.Constants.TITLE;
import static com.jeeves.vpl.Constants.PRIVATE_COLL;
import static com.jeeves.vpl.Constants.makeInfoAlert;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.shiro.subject.Subject;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jeeves.vpl.firebase.FirebaseDB;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Class for logging in/registering. Communicates with the Firebase database to determine
 * whether the email/password combination exists
 * @author DJR
 *
 */
public class LoginRegisterPane extends Pane{
	private Stage stage;
	@FXML private TextField txtLogEmail;
	@FXML private PasswordField txtPassword;
	@FXML private TextField txtRegEmail;
	@FXML private PasswordField txtRegPassword;
	@FXML private PasswordField txtRegPasswordConfirm;
	@FXML private Label lblError;
	@FXML private VBox vboxShadow;
	@FXML private VBox vboxLoading;
	@FXML private Label lblLoading;
	@FXML private HBox hboxError;
	Subject currentUser;

	public LoginRegisterPane(Stage stage) throws IOException {
		this.stage = stage;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/LoginRegister.fxml");
		fxmlLoader.setLocation(location);
		Node root = fxmlLoader.load();
		getChildren().add(root);
		//default values for now
		txtLogEmail.setText("");
		txtPassword.setText("");

	}

	@SuppressWarnings("deprecation")
	@FXML 
	private void validate(Event e){
		String email = txtRegEmail.getText();
		String password = txtRegPassword.getText();
		String passwordconfirm = txtRegPasswordConfirm.getText();
		CreateRequest request = null;
		if(email == null || email.isEmpty() ||  !EmailValidator.getInstance().isValid(email)){
			makeInfoAlert(TITLE,REG_ERROR,
					"Please enter a valid email address");
			return;
		}
		if(password == null || password.isEmpty()){
			makeInfoAlert(TITLE,REG_ERROR,
					"Please enter a password");
			return;
		}
		if(passwordconfirm == null || passwordconfirm.isEmpty() || !password.equals(passwordconfirm)){
			makeInfoAlert(TITLE,REG_ERROR,
					"Make sure your passwords match!");
			return;
		}
		vboxShadow.setVisible(true);
		vboxLoading.setVisible(true);
		lblLoading.setText("Registering");
		//This should all be fine with validation above, but it's in a try-catch just in case...
		try{
			request = new CreateRequest()
					.setEmail(email)
					.setEmailVerified(false)
					.setPassword(password)
					.setDisplayName(email)
					.setDisabled(false);
		}
		catch(Exception err){
			makeInfoAlert(TITLE,
					"Registration failed", "Sorry, that didn't work. " +
							err.getMessage());
			vboxShadow.setVisible(false);
			vboxLoading.setVisible(false);
			return;
		}

		ApiFuture<UserRecord> userRecord = FirebaseAuth.getInstance().createUserAsync(request);
		ApiFutures.addCallback(userRecord, new ApiFutureCallback<UserRecord>() {

			@Override
			public void onSuccess(UserRecord result) {
				Platform.runLater(new Runnable() {

					public void run() {
						vboxShadow.setVisible(false);
						vboxLoading.setVisible(false);
						makeInfoAlert(TITLE,"Success","Successfully registered! You can now log into Jeeves");
						FirebaseDB.getInstance().putUserCredentials(email, password);
						txtLogEmail.setText(email);
						txtPassword.setText(password);
						txtRegEmail.clear();
						txtRegPassword.clear();
						txtRegPasswordConfirm.clear();
					}
				});
			}

			@Override
			public void onFailure(Throwable t) {
				Platform.runLater(new Runnable() {

					public void run() {
						vboxShadow.setVisible(false);
						vboxLoading.setVisible(false);
						makeInfoAlert(TITLE,"Registration failed", "Sorry, that didn't work. "
								+ "A user with this email address already exists!");
					}
				});	

			}

		});

	}

	@FXML
	private void login(Event e) throws InterruptedException, ExecutionException{
		vboxShadow.setVisible(true);
		vboxLoading.setVisible(true);
		hboxError.setVisible(false);
		lblLoading.setText("Logging you in");
		String username = txtLogEmail.getText();
		String password = txtPassword.getText();

		authenticate(username,password);
	}
	//Need to do some proper authentication in here. 
//	public void authenticate(String email, String password) throws IOException {
//		// Fetch the service account key JSON file contents
//		FileInputStream serviceAccount = new FileInputStream("path/to/serviceAccount.json");
//
//		// Initialize the app with a custom auth variable, limiting the server's access
//		Map<String, Object> auth = new HashMap<String, Object>();
//		auth.put("uid", "my-service-worker");
//
//		// Initialize the app with a service account, granting admin privileges
//		FirebaseOptions options = new FirebaseOptions.Builder()
//		    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//		    .setDatabaseUrl("https://<databaseName>.firebaseio.com")
//		    .setDatabaseAuthVariableOverride(auth)
//		    .build();
//		FirebaseApp.initializeApp(options);
//
//		// As an admin, the app has access to read and write all data, regardless of Security Rules
//		DatabaseReference ref = FirebaseDatabase.getInstance()
//		    .getReference("restricted_access/secret_document");
//		ref.addListenerForSingleValueEvent(new ValueEventListener() {
//		  @Override
//		  public void onDataChange(DataSnapshot dataSnapshot) {
//		    Object document = dataSnapshot.getValue();
//		    System.out.println(document);
//		  }
//
//		  @Override
//		  public void onCancelled(DatabaseError error) {
//		  }
//		});
//	}
	public void authenticate(String email, String password) throws InterruptedException, ExecutionException {
		UserRecord userRecord;
		hboxError.setVisible(false);
		try {
			userRecord = FirebaseAuth.getInstance().getUserByEmailAsync(email).get();
		}
		catch(Exception e) {
			hboxError.setVisible(true);
			lblError.setText("Error logging in. Check your password and Internet connection");			
			vboxShadow.setVisible(false);
			vboxLoading.setVisible(false);
			return;
		}
		String uid = userRecord.getUid();
		DatabaseReference	dbRef = FirebaseDatabase.getInstance().getReference();
		DatabaseReference privateRef =  dbRef.child(PRIVATE_COLL).child(uid);
		privateRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

				Platform.runLater(()->{
					@SuppressWarnings("unchecked")
					Map<String,Object> value = (Map<String,Object>)dataSnapshot.getValue();
					//It's a bit annoying, but this is the only bit of the project that gets updated from the Android side!
					try {
						String token = value.get("token").toString();
						if(token.equals(password)) {
							FirebaseDB.getInstance().setCurrentUserEmail(txtLogEmail.getText());
							stage.hide();
						}
					}
					catch(NullPointerException e) {
						lblError.setText("Sorry, this account is no longer active.");									
					}
					vboxShadow.setVisible(false);
					vboxLoading.setVisible(false);
					hboxError.setVisible(true);					
					lblError.setText("Error logging in. Check your password and Internet connection");			
				});


			}

			@Override
			public void onCancelled(DatabaseError arg0) {
				vboxShadow.setVisible(false);
				vboxLoading.setVisible(false);
				hboxError.setVisible(true);
				lblError.setText("Error logging in. Check your password and Internet connection");					
			}
		});


	}
	
	//Listener for login action when button is focused and Enter is pressed
	@FXML
	private void checkEnterLogin(Event e) throws InterruptedException, ExecutionException {
		if(((KeyEvent)e).getCode().equals(KeyCode.ENTER)) {
			login(e);
		}		
	}

	//Listener for register action when button is focused and Enter is pressed
	@FXML
	private void checkEnterRegister(Event e) throws InterruptedException, ExecutionException {
		if(((KeyEvent)e).getCode().equals(KeyCode.ENTER)) {
			validate(e);
		}
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
