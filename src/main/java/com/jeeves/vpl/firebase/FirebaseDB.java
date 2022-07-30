package com.jeeves.vpl.firebase;

import static com.jeeves.vpl.Constants.PATIENTS_COLL;
import static com.jeeves.vpl.Constants.PROJECTS_COLL;
import static com.jeeves.vpl.Constants.generateProjectID;
import static com.jeeves.vpl.Constants.makeInfoAlert;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jeeves.vpl.Constants;
import com.jeeves.vpl.Main;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FirebaseDB {
	
	public static class User {

		  public String date_of_birth;
		  public String full_name;
		  public String nickname;

		  public User(String dateOfBirth, String fullName) {
		    // ...
		  }

		  public User(String dateOfBirth, String fullName, String nickname) {
		    // ...
		  }

		}
	final Logger logger = LoggerFactory.getLogger(FirebaseDB.class);
	private static final String TOKEN = "token";
	private static final String TOKENS = "tokens";
	private DatabaseReference dbRef;
	DatabaseReference connectedRef;
	private String projectKey;
	private ObservableList<FirebasePatient> newpatients = FXCollections.observableArrayList();
	private ObservableList<FirebaseProject> newprojects = FXCollections.observableArrayList();
	
	private static FirebaseDB instance;
	private FirebaseProject openProject;
	private String uid;
	private Map<String,String> projectTokens = new HashMap<>();
	private DatabaseReference patientsRef;

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
		if(project.getname() == null)return;
		DatabaseReference globalRef = dbRef.child(PROJECTS_COLL).child(project.getname());
		projectKey =projectTokens.get(project.getname());
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
					System.out.println("Added " + surveyname + " To the survey map");
					
				}
				openProject.setsurveydata(surveymap);
			}
		});
	
	}

	//Signs us into the database with restricted access based on the generated userid
	public void firebaseLogin(){
			addListeners(); //listen on the database AS OUR CURRENT USER
	}
	public FirebaseDB() {
	}

	public String getProjectToken() {
		return projectKey;
	}
	public DatabaseReference getPatientsRef() {
		return patientsRef;
	}
	public void addListeners() {
	
		dbRef = FirebaseDatabase.getInstance().getReference();
		 patientsRef = dbRef.child(Constants.PATIENTS_COLL);

		patientsRef.addChildEventListener(new ChildEventListener() {
		    @Override
		    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
		        FirebasePatient newPost = dataSnapshot.getValue(FirebasePatient.class);
		        newPost.setUid(dataSnapshot.getKey());
		        newpatients.add(newPost);
		    }

		    @Override
		    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
		    	FirebasePatient changedPatient = dataSnapshot.getValue(FirebasePatient.class);
		    	newpatients.forEach(p->{
		    		if(p.getName() == changedPatient.getName()) {
		    			int index = newpatients.indexOf(p);
		    			newpatients.remove(p);
		    			newpatients.add(index,changedPatient);
		    			return;
		    		}
		    	});
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

		dbRef.addValueEventListener(new ValueEventListener() {		
			@Override
			public void onCancelled(DatabaseError arg0) {
				logger.error(arg0.getMessage(),arg0.getDetails());
			}
			@Override
			public void onDataChange(DataSnapshot arg0) {
				FirebasePrivate appdata = arg0.getValue(FirebasePrivate.class);
				newprojects.clear();

				if (appdata != null) {
					Map<String, FirebaseProject> projects = appdata.getprojects();
					if (projects != null)
						for (Map.Entry<String,FirebaseProject> entry : projects.entrySet()) {
							newprojects.add(projects.get(entry.getKey()));
							
						}
				}
				projectTokens = appdata.gettokens();
			}
		});
		connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
		System.out.println("Connected ref " + connectedRef.toString());
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
		
		DatabaseReference ref = FirebaseDatabase.getInstance().getReference("server");

		DatabaseReference usersRef = ref.child("users");

		Map<String, User> users = new HashMap<>();
		users.put("alanisawesome", new User("June 23, 1912", "Alan Turing"));
		users.put("gracehop", new User("December 9, 1906", "Grace Hopper"));

		usersRef.setValueAsync(users);
	}

//	public boolean addPatient(FirebasePatient object) {
//		DatabaseReference globalRef = dbRef.child(PATIENTS_COLL).child(object.getUid());
//		globalRef.setValueAsync(object);
//		return true;
//	}
	
	public void deletePatient(FirebasePatient object) {
	//	System.out.println(object.getScreenName());
		System.out.println(object.getUid());
		DatabaseReference patientRef = dbRef.child(PATIENTS_COLL).child(object.getUid());
		patientRef.removeValueAsync();
	}

	public void sendPatientFeedback(FirebasePatient patient, String feedback){
		 final DatabaseReference firebaseFeedback =  dbRef.child(PATIENTS_COLL).child(patient.getName()).child("feedback").child(Long.toString(System.currentTimeMillis()));
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

    public boolean updatePatient(FirebasePatient patient) {
    	DatabaseReference patientsRef = dbRef.child(PATIENTS_COLL);
    //	System.out.println("patients name is " + patient.getScreenName());
    	patientsRef.child(patient.getUid()).setValueAsync(patient);
    	return true;
    }
    
    public boolean deleteProject(FirebaseProject obj) {
		dbRef.child(PROJECTS_COLL).child(obj.getname()).removeValueAsync();
    	return true;
    }
	public boolean saveProject(String oldname, FirebaseProject object) {
		if (oldname == null){
			try {
				kpg = KeyPairGenerator.getInstance("RSA");
				kpg.initialize(2048);
		        kp = kpg.genKeyPair();
		        publicKey = kp.getPublic();
		      
		        privateKey = kp.getPrivate();
		        object.setpubKey(Base64.encodeBase64String(publicKey.getEncoded()));
		        dbRef.child(TOKENS).child(object.getname()).setValueAsync(Base64.encodeBase64String(privateKey.getEncoded()));
		        
			} catch (NoSuchAlgorithmException e) {
				System.exit(1);
			}
	        

			//object.setresearcherno(uid);

		}

		object.setlastUpdated(System.currentTimeMillis());
		dbRef.child(PROJECTS_COLL).child(object.getname()).setValueAsync(object);
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
		DatabaseReference privateProjectRef = dbRef.child(PROJECTS_COLL).child(project.getname());
		project.setactive(true);
		privateProjectRef.setValueAsync(project);
	}

}
