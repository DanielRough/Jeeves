package com.jeeves.vpl;
import static com.jeeves.vpl.Constants.PRIVATE_COLL;
import static com.jeeves.vpl.Constants.makeInfoAlert;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.shiro.subject.Subject;

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

	public LoginRegisterPane(Main gui, Stage stage) {
		this.stage = stage;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);

		URL location = this.getClass().getResource("/LoginRegister.fxml");

		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);

			//default values for now
			txtUsername.setText("danielrough@hotmail.com");
			txtPassword.setText("command22");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML 
	private void validate(Event e){
		String email = txtEmail.getText();
		String password = txtRegPassword.getText();
		String passwordconfirm = txtRegPasswordConfirm.getText();
		String firstName = txtFirstName.getText();
		String lastName = txtLastName.getText();
		CreateRequest request = null;
		if(firstName == null || firstName.isEmpty()){
			makeInfoAlert("Jeeves","Registration error","Please enter your first name");
			return;
		}
		if(lastName == null || lastName.isEmpty()){
			makeInfoAlert("Jeeves","Registration error","Please enter your last name");
			return;
		}
		if(email == null || email.isEmpty() ||  !EmailValidator.getInstance().isValid(email)){
			makeInfoAlert("Jeeves","Registration error","Please enter a valid email address");
			return;
		}
		if(password == null || password.isEmpty()){
			makeInfoAlert("Jeeves","Registration error","Please enter a password");
			return;
		}
		if(passwordconfirm == null || passwordconfirm.isEmpty() || !password.equals(passwordconfirm)){
			makeInfoAlert("Jeeves","Registration error","Make sure your passwords match!");
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
			makeInfoAlert("Jeeves","Registration failed", "Sorry, that didn't work. " + err.getMessage());
			err.printStackTrace();
			return;
		}

		FirebaseAuth.getInstance().createUser(request)
		.addOnSuccessListener(userRecord -> {
			Platform.runLater(new Runnable(){
				public void run(){
					//Not very secure tbh
					FirebaseDB.getInstance().putUserCredentials(email,password);
					makeInfoAlert("Jeeves","Registration successful","Successfully registered " + email + ". You can now log in to Jeeves!");
				}
			});

		//	addUserToConfig(email,password,userRecord.getUid());
		})
		//It's not always that the user already exists, but...almost always.
		.addOnFailureListener(err -> {
			
			Platform.runLater(new Runnable(){
				public void run(){
					makeInfoAlert("Jeeves","Registration failed", "Sorry, that didn't work. A user with this email address already exists!");
				}
			});
		});

	}

	@FXML
	private void login(Event e){
		String username = txtUsername.getText();
		String password = txtPassword.getText();
		authenticate(username,password);
	}
	public void authenticate(String email, String password) {
		UserRecord userRecord;
	try {
			
			userRecord = FirebaseAuth.getInstance().getUserByEmailAsync(email).get();
			String uid = userRecord.getUid();
		   DatabaseReference	dbRef = FirebaseDatabase.getInstance().getReference();
		   DatabaseReference privateRef =  dbRef.child(PRIVATE_COLL).child(uid);
		  // stage.hide();
		   
			privateRef.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					
					Platform.runLater(new Runnable() {
						public void run() {
							Map<String,Object> value = (Map<String,Object>)dataSnapshot.getValue();
							//It's a bit annoying, but this is the only bit of the project that gets updated from the Android side!
							String token = value.get("token").toString();
							if(token.equals(password)) {
								FirebaseDB.currentUserEmail = txtUsername.getText();
								stage.hide();
							}

							lblError.setText("Error logging in. Check your password and Internet connection");			
						}
					});
							
					
				}
			
				@Override
				public void onCancelled(DatabaseError arg0) {
					lblError.setText("Error logging in. Check your password and Internet connection");					
				}
			});
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
