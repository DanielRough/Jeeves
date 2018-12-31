package com.jeeves.vpl;
import static com.jeeves.vpl.Constants.PRIVATE_COLL;
import static com.jeeves.vpl.Constants.makeInfoAlert;
import static com.jeeves.vpl.Constants.TITLE;
import static com.jeeves.vpl.Constants.REG_ERROR;

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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginRegisterPane extends Pane{
	private Stage stage;
	@FXML private TextField txtUsername;
	@FXML private PasswordField txtPassword;
	@FXML private TextField txtFirstName;
	@FXML private TextField txtLastName;
	@FXML private TextField txtEmail;
	@FXML private PasswordField txtRegPassword;
	@FXML private PasswordField txtRegPasswordConfirm;
	@FXML private Label lblError;
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
		txtUsername.setText("drrough@mail.com");
		txtPassword.setText("command22");

	}

	@SuppressWarnings("deprecation")
	@FXML 
	private void validate(Event e){
		String email = txtEmail.getText();
		String password = txtRegPassword.getText();
		String passwordconfirm = txtRegPasswordConfirm.getText();
		String firstName = txtFirstName.getText();
		String lastName = txtLastName.getText();
		CreateRequest request = null;
		if(firstName == null || firstName.isEmpty()){
			makeInfoAlert(TITLE,REG_ERROR,"Please enter your first name");
			return;
		}
		if(lastName == null || lastName.isEmpty()){
			makeInfoAlert(TITLE,REG_ERROR,"Please enter your last name");
			return;
		}
		if(email == null || email.isEmpty() ||  !EmailValidator.getInstance().isValid(email)){
			makeInfoAlert(TITLE,REG_ERROR,"Please enter a valid email address");
			return;
		}
		if(password == null || password.isEmpty()){
			makeInfoAlert(TITLE,REG_ERROR,"Please enter a password");
			return;
		}
		if(passwordconfirm == null || passwordconfirm.isEmpty() || !password.equals(passwordconfirm)){
			makeInfoAlert(TITLE,REG_ERROR,"Make sure your passwords match!");
			return;
		}
		//This should all be fine with validation above, but it's in a try-catch just in case...
		try{
			request = new CreateRequest()
					.setEmail(email)
					.setEmailVerified(false)
					.setPassword(password)
					.setDisplayName(firstName + " " + lastName)
					.setDisabled(false);
		}
		catch(Exception err){
			makeInfoAlert(TITLE,"Registration failed", "Sorry, that didn't work. " + err.getMessage());
			return;
		}
		ApiFuture<UserRecord> userRecord = FirebaseAuth.getInstance().createUserAsync(request);
		ApiFutures.addCallback(userRecord, new ApiFutureCallback<UserRecord>() {
			@Override
			public void onSuccess(UserRecord result) {
				makeInfoAlert(TITLE,"Success","Successfully registered! You can now log into Jeeves");
			}

			@Override
			public void onFailure(Throwable t) {
				makeInfoAlert(TITLE,"Registration failed", "Sorry, that didn't work. "
						+ "A user with this email address already exists!");
			}
		});
		FirebaseAuth.getInstance().createUserAsync(request);

	}

	@FXML
	private void login(Event e) throws InterruptedException, ExecutionException{
		String username = txtUsername.getText();
		String password = txtPassword.getText();
		authenticate(username,password);
	}
	public void authenticate(String email, String password) throws InterruptedException, ExecutionException {
		UserRecord userRecord;
		userRecord = FirebaseAuth.getInstance().getUserByEmailAsync(email).get();
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
					String token = value.get("token").toString();
					if(token.equals(password)) {
						FirebaseDB.getInstance().setCurrentUserEmail(txtUsername.getText());
						stage.hide();
					}

					lblError.setText("Error logging in. Check your password and Internet connection");			
				});


			}

			@Override
			public void onCancelled(DatabaseError arg0) {
				lblError.setText("Error logging in. Check your password and Internet connection");					
			}
		});


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
