package com.jeeves.vpl.firebase;

import static com.jeeves.vpl.Constants.DB_URL;
import static com.jeeves.vpl.Constants.PATIENTS_COLL;
import static com.jeeves.vpl.Constants.PRIVATE_COLL;
import static com.jeeves.vpl.Constants.PROJECTS_COLL;
import static com.jeeves.vpl.Constants.PUBLIC_COLL;
import static com.jeeves.vpl.Constants.SERVICE_JSON;
import static com.jeeves.vpl.Constants.generateProjectID;
import static com.jeeves.vpl.Constants.makeInfoAlert;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jeeves.vpl.Main;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
@SuppressWarnings("deprecation")

public class FirebaseDB {
	final Logger logger = LoggerFactory.getLogger(FirebaseDB.class);
	private static final String TOKEN = "token";
	private static final String TOKENS = "tokens";
	private DatabaseReference dbRef;
	private DatabaseReference privateRef;
	private DatabaseReference publicRef;
	private String currentUserId;
	private String currentUserEmail;
	DatabaseReference connectedRef;
	private String projectKey;
	private ObservableList<FirebasePatient> newpatients = FXCollections.observableArrayList();
	private ObservableList<FirebaseProject> newprojects = FXCollections.observableArrayList();
	
	private ObservableList<FirebaseProject> publicprojects = FXCollections.observableArrayList();
	private static FirebaseDB instance;
	private FirebaseProject openProject;
	private String uid;
	private Map<String,String> projectTokens = new HashMap<>();
	

	public static FirebaseDB getInstance(){
		if(instance == null)
			instance = new FirebaseDB();
		return instance;
	}
	public FirebaseProject getOpenProject(){
		return openProject;
	}
	public void setOpenProject(FirebaseProject project){
		openProject = project;
		openProject.addStuff();
		//Not particularly nice that I have to put the reference for survey data in here
		if(privateRef == null || project.getname() == null)return;

		DatabaseReference globalRef = privateRef.child(PROJECTS_COLL).child(project.getname());
		projectKey =projectTokens.get(project.getname()+ TOKEN);
		System.out.println("PROJECT KEY IS " + projectKey);
		DatabaseReference surveyDataRef = globalRef.child("surveydata");

		surveyDataRef.addValueEventListener(new ValueEventListener(){
			@Override
			public void onCancelled(DatabaseError arg0) {
				logger.error(arg0.getMessage(),arg0.getDetails());
			}
			Map<String,Map<String,FirebaseSurveyEntry>> surveymap = new HashMap<>();
			Map<String,FirebaseSurveyEntry> entrymap = new HashMap<>();
			
			@Override
			public void onDataChange(DataSnapshot arg0) {
				for(DataSnapshot listofentries : arg0.getChildren()) {
					entrymap = new HashMap<>();
					String surveyname = listofentries.getKey();
					for(DataSnapshot entry : listofentries.getChildren()) {
						FirebaseSurveyEntry surveyentry = entry.getValue(FirebaseSurveyEntry.class);
						entrymap.put(entry.getKey(), surveyentry);
					}
					surveymap.put(surveyname, entrymap);
					
				}
				openProject.setsurveydata(surveymap);
			}
		});
	
	}
	public void getUserCredentials(){	
		ApiFuture<UserRecord> task = FirebaseAuth.getInstance().getUserByEmailAsync(getCurrentUserEmail());
		ApiFutures.addCallback(task, new ApiFutureCallback<UserRecord>() {
			    @Override
			    public void onSuccess(UserRecord result) {
			    	currentUserId = result.getUid();
			    	System.out.println("UID IS " + currentUserId);
					FirebaseApp.getInstance().delete();
					firebaseLogin();			    }

			    @Override
			    public void onFailure(Throwable t) {
					makeInfoAlert("Jeeves","Registration failed", "Sorry, that didn't work. A user with this email address already exists!");
			    }
			  });		
	}

	//Signs us into the database with restricted access based on the generated userid
	public void firebaseLogin(){
		Map<String, Object> auth = new HashMap<>();
		uid = currentUserId;
		auth.put("uid", currentUserId);
		try {
			InputStream resource = FirebaseDB.class.getResourceAsStream(SERVICE_JSON);
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(resource))
					.setDatabaseUrl(DB_URL)
					.setDatabaseAuthVariableOverride(auth)
				    .setStorageBucket("jeeves-27914.appspot.com")
					.build();
			FirebaseApp.initializeApp(options);
			resource.close();
			
			addListeners(); //listen on the database AS OUR CURRENT USER
		} catch (IOException e) {
			logger.error("Error",e.fillInStackTrace());
			System.exit(1);
		}

	}
	public FirebaseDB() {
		try {
			InputStream stream = FirebaseDB.class.getResourceAsStream(SERVICE_JSON);
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(stream))
					.setDatabaseUrl(DB_URL)
					.build();
			FirebaseApp.initializeApp(options);
		} catch (IOException e) {
			System.exit(1);
		}

	}


	public void putUserCredentials(String email, String password) {
		try {
			UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmailAsync(email).get();
			String userId = userRecord.getUid();
			dbRef = FirebaseDatabase.getInstance().getReference();
			privateRef =  dbRef.child(PRIVATE_COLL).child(userId);
			privateRef.child(TOKEN).setValueAsync(password);
		} catch (ExecutionException e1) {
			System.exit(1);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public String getProjectToken() {
		System.out.println("it's can" + projectKey);
		return projectKey;
	}
	public void addListeners() {
	
		dbRef = FirebaseDatabase.getInstance().getReference();
		privateRef =  dbRef.child(PRIVATE_COLL).child(currentUserId);
		System.out.println("REF is " + privateRef.toString());
		DatabaseReference patientsRef = privateRef.child("patients");
		publicRef = dbRef.child(PUBLIC_COLL);
		//We now have listeners for individual patients rather than just clearing and adding them all every time an
		//update happens. This should make the patients pane function more cleanly
		patientsRef.addChildEventListener(new ChildEventListener() {
		    @Override
		    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
		    	System.out.println("ADDING PATIENT");
		    	System.out.println(dataSnapshot.getKey());
		        FirebasePatient newPost = dataSnapshot.getValue(FirebasePatient.class);
		        System.out.println("Did it");
		        newpatients.add(newPost);
		    }

		    @Override
		    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
		    	FirebasePatient changedPatient = dataSnapshot.getValue(FirebasePatient.class);
		    	newpatients.add(changedPatient);
		    }

		    @Override
		    public void onChildRemoved(DataSnapshot dataSnapshot) {
		    	FirebasePatient removed = dataSnapshot.getValue(FirebasePatient.class);
		    	newpatients.remove(removed);
				logger.debug(dataSnapshot.toString());
		    }
		    @Override
		    public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
				logger.debug(dataSnapshot.toString());
		    }

		    @Override
		    public void onCancelled(DatabaseError arg0) {
				logger.error(arg0.getMessage(),arg0.getDetails());
		    }
		});
		privateRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onCancelled(DatabaseError arg0) {
				logger.error(arg0.getMessage(),arg0.getDetails());
			}
			@Override
			public void onDataChange(DataSnapshot arg0) {
				System.out.println("HEre we go");
				FirebasePrivate appdata = arg0.getValue(FirebasePrivate.class);
				System.out.println("oh it worked");
				newprojects.clear();

				if (appdata != null) {
					Map<String, FirebaseProject> projects = appdata.getprojects();
					if (projects != null)
						for (Map.Entry<String,FirebaseProject> entry : projects.entrySet()) {
							newprojects.add(projects.get(entry.getKey()));
							
						}
				}
				System.out.println("I wonder...");
				System.out.println(appdata);
				projectTokens = appdata.gettokens();
			}
		});
		publicRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onCancelled(DatabaseError arg0) {
				logger.error(arg0.getMessage(),arg0.getDetails());
			}

			@Override
			public void onDataChange(DataSnapshot arg0) {
				FirebasePublic publicdata = arg0.getValue(FirebasePublic.class);
				publicprojects.clear();
				Map<String,FirebaseProject> projects = publicdata.getprojects();
				if (projects != null) {
					publicprojects.addAll(projects.values());
				}
			}
		});
		connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
		connectedRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				boolean connected = snapshot.getValue(Boolean.class);
				Platform.runLater(()->Main.getContext().updateConnectedStatus(connected));
				
			}

			@Override
			public void onCancelled(DatabaseError arg0) {
				logger.error(arg0.getMessage(),arg0.getDetails());
			}
		});
	}

	public boolean addPatient(FirebasePatient object) {
		DatabaseReference globalRef = privateRef.child(PATIENTS_COLL).child(object.getUid());
		globalRef.setValueAsync(object);
		return true;
	}

	public void sendPatientFeedback(FirebasePatient patient, String feedback){
        final DatabaseReference firebaseFeedback =  privateRef.child(PATIENTS_COLL).child(patient.getName()).child("feedback").child(Long.toString(System.currentTimeMillis()));
        firebaseFeedback.setValueAsync("You: " + feedback);
	}

	KeyPairGenerator kpg;
    KeyPair kp;
    PublicKey publicKey;
    PrivateKey privateKey;
    byte [] encryptedBytes;
    byte [] decryptedBytes;
    Cipher cipher;
    Cipher cipher1;
    String encrypted;
    String decrypted;

	public boolean saveProject(String oldname, FirebaseProject object) {
		if (oldname == null){
			try {
				kpg = KeyPairGenerator.getInstance("RSA");
				kpg.initialize(2048);
		        kp = kpg.genKeyPair();
		        publicKey = kp.getPublic();
		 
		        privateKey = kp.getPrivate();
		        object.setpubKey(Base64.encodeBase64String(publicKey.getEncoded()));
		        privateRef.child(TOKENS).child(object.getname()+TOKEN).setValueAsync(Base64.encodeBase64String(privateKey.getEncoded()));
//		        privateRef.child(object.getname()+TOKEN).setValueAsync(Base64.encodeBase64String(privateKey.getEncoded()));
		  
		        
			} catch (NoSuchAlgorithmException e) {
				System.exit(1);
			}
	        

			String projid = generateProjectID();
			object.setid(projid);
			object.setresearcherno(uid);

		}

		object.setlastUpdated(System.currentTimeMillis());
		privateRef.child(PROJECTS_COLL).child(object.getname()).setValueAsync(object);
		
		if(object.getactive()){
			publicRef.child(PROJECTS_COLL).child(object.getname()).setValueAsync(object);		}
		return true;
	}

	public ObservableList<FirebasePatient> getpatients() {
		return newpatients;
	}

	public ObservableList<FirebaseProject> getprojects() {
		return newprojects;
	}

	/**
	 * 'Publish' the study by putting it in the 'public' branch of the Firebase Database.
	 * It first updates the 'active' field of the study to true. It then writes this study spec to both the private and public branches
	 * @param project
	 */
	public void publishStudy(FirebaseProject project){
		DatabaseReference privateProjectRef = privateRef.child(PROJECTS_COLL).child(project.getname());
		project.setactive(true);
		privateProjectRef.setValueAsync(project);
		DatabaseReference globalRef = publicRef.child(PROJECTS_COLL).child(project.getname());
		globalRef.setValueAsync(project);
	}
	public String getCurrentUserEmail() {
		return currentUserEmail;
	}
	public void setCurrentUserEmail(String currentUserEmail) {
		this.currentUserEmail = currentUserEmail;
	}

}
