package com.jeeves.vpl;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.tasks.Task;
import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseProject;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginRegisterPane extends Pane{
	private FirebaseProject selectedProject;
	private FirebaseDB firebase;
	private Main gui;
	private ObservableList<FirebaseProject> projects;
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
//	this.firebase = firebase;
		this.gui = gui;
		this.stage = stage;
	//	this.projects = firebase.getprojects();
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);

		URL location = this.getClass().getResource("/LoginRegister.fxml");

		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);

		} catch (Exception e) {
			e.printStackTrace();
		}
//		//1.
//	    Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory("shiro.ini");
//
//	    //2.
//	    org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
//
//	    //3.
//	    SecurityUtils.setSecurityManager(securityManager);
//
//		currentUser = SecurityUtils.getSubject();
//		
//		//Shiro security stuff
//		Session session = currentUser.getSession();
//		session.setAttribute( "someKey", "aValue" );
		
	}
	
	@FXML 
	public void validate(Event e){
		String email = txtEmail.getText();
		String password = txtRegPassword.getText();
		String firstName = txtFirstName.getText();
		String lastName = txtLastName.getText();
		CreateRequest request = null;
		try{
			request = new CreateRequest()
			    .setEmail(email)
			    .setEmailVerified(false)
			    .setPassword(password)
			    .setDisplayName(firstName + " " + lastName)
//			    .setPhotoUrl("http://www.example.com/12345678/photo.png")
			    .setDisabled(false);
		}
		catch(Exception err){
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Registration failed");
			alert.setHeaderText(null);
			alert.setContentText("Sorry, that didn't work. " + err.getMessage());
			alert.showAndWait();
			return;
		}
			Task<UserRecord> task = FirebaseAuth.getInstance().createUser(request)
			    .addOnSuccessListener(userRecord -> {
			      // See the UserRecord reference doc for the contents of userRecord.
			    	Platform.runLater(new Runnable(){
			    		public void run(){
			    			Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Registration successful");
							alert.setHeaderText(null);
							alert.setContentText("Successfully registered new user. You can now log in to Jeeves!");
							alert.showAndWait();
			    		}
			    	});
			    	
			      addUserToConfig(email,password,userRecord.getUid());
			    })
			    .addOnFailureListener(err -> {
			    	Platform.runLater(new Runnable(){
			    		public void run(){
			    			Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Registration failed");
							alert.setHeaderText(null);
							alert.setContentText("Sorry, that didn't work. " + err.getMessage());
							alert.showAndWait();
			    		}
			    	});
			    	
			      System.err.println("Error creating new user: " + err.getMessage());
			    });
			
	}
	
	
	public void addUserToConfig(String email, String password,String uid){
		Sha256Hash sha256Hash = new Sha256Hash(password);
		//Edit our config file
		try {
	
			Files.write(Paths.get("shiro.ini"), System.getProperty("line.separator").getBytes(), StandardOpenOption.APPEND);
		    Files.write(Paths.get("shiro.ini"), (email + " = " + sha256Hash.toHex() + "," + uid).getBytes(), StandardOpenOption.APPEND);

		}catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}
	@FXML
	public void login(Event e){
	    //Must reset security manager, I think?
	    Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory("shiro.ini");

	    //2.
	    org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();

	    //3.
	    SecurityUtils.setSecurityManager(securityManager);
		String username = txtUsername.getText();
		String password = txtPassword.getText();
		currentUser = SecurityUtils.getSubject();
		if ( !currentUser.isAuthenticated() ) {
		    //collect user principals and credentials in a gui specific manner
		    //such as username/password html form, X509 certificate, OpenID, etc.
		    //We'll use the username/password example here since it is the most common.
		    //(do you know what movie this is from? ;)
			Sha256Hash sha256Hash = new Sha256Hash(password);
//			System.out.println(sha256Hash.toHex());
		    UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		    //this is all you have to do to support 'remember me' (no config - built in!):
		    token.setRememberMe(true);
		    try {
		        currentUser.login( token );
		        System.out.println("User [" + currentUser.getPrincipal() + "] logged in successfully.");
		        stage.close();
		        //if no exception, that's it, we're done!
		    }catch (UnknownAccountException uae) {
               lblError.setText("There is no user with username of " + token.getPrincipal());
            } catch (IncorrectCredentialsException ice) {
            	lblError.setText("Password for account " + token.getPrincipal() + " was incorrect!");
            } catch (LockedAccountException lae) {
            	lblError.setText("The account for username " + token.getPrincipal() + " is locked.  " +
                        "Please contact your administrator to unlock it.");
            }	
	}
		
		//		// Fetch the service account key JSON file contents
//		FileInputStream serviceAccount = new FileInputStream("path/to/serviceAccountCredentials.json");
//
//		FirebaseOptions options = new FirebaseOptions.Builder()
//		    .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
//		    .setDatabaseUrl("https://databaseName.firebaseio.com")
//		    .setDatabaseAuthVariableOverride(null)
//		    .build();
//		FirebaseApp.initializeApp(options);
//
//		// The app only has access to public data as defined in the Security Rules
//		DatabaseReference ref = FirebaseDatabase
//		    .getInstance()
//		    .getReference("/public_resource");
//		ref.addListenerForSingleValueEvent(new ValueEventListener() {
//		    @Override
//		    public void onDataChange(DataSnapshot dataSnapshot) {
//		        String res = dataSnapshot.getValue();
//		        System.out.println(res);
//		    }
//		});
//		mAuth.ver
//		mAuth.signInWithEmailAndPassword(email, password)
//        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithEmail:success");
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    updateUI(user);
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithEmail:failure", task.getException());
//                    Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                            Toast.LENGTH_SHORT).show();
//                    updateUI(null);
//                }
//
//                // ...
//            }
     //   });
	}

	@FXML
	public void close(Event e){
		stage.close();
		System.exit(0);
	}
	
	@FXML
	public void showGlow(Event e){
		Node image = (Node)e.getSource();
		image.getStyleClass().add("drop_shadow");
	}
	
	@FXML
	public void hideGlow(Event e){
		Node image = (Node)e.getSource();
		image.getStyleClass().remove("drop_shadow");
	}
}
