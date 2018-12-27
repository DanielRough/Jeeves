package com.jeeves.vpl.firebase;

import static com.jeeves.vpl.Constants.DB_URL;
import static com.jeeves.vpl.Constants.PATIENTS_COLL;
import static com.jeeves.vpl.Constants.PRIVATE_COLL;
import static com.jeeves.vpl.Constants.PROJECTS_COLL;
import static com.jeeves.vpl.Constants.PUBLIC_COLL;
import static com.jeeves.vpl.Constants.SERVICE_JSON;
import static com.jeeves.vpl.Constants.CLOUD_JSON;
import static com.jeeves.vpl.Constants.generateProjectID;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.session.Session;
//import org.apache.shiro.subject.Subject;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.StorageClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.tasks.Task;
import com.jeeves.vpl.Main;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
@SuppressWarnings("deprecation")

public class FirebaseDB {

	private static DatabaseReference dbRef;// = myFirebaseRef.child(DBNAME);
	private static DatabaseReference privateRef;
	private static DatabaseReference patientsRef;
	private static DatabaseReference publicRef;
	private static String currentUserId;
	public static String currentUserEmail;
	public static String currentUserPass;
	DatabaseReference connectedRef;
	public static String projectKey;
	private ObservableList<FirebasePatient> newpatients = FXCollections.observableArrayList();
	private ObservableList<FirebaseProject> newprojects = FXCollections.observableArrayList();
	private ObservableList<FirebaseProject> publicprojects = FXCollections.observableArrayList();
	private static FirebaseDB instance;
	private static FirebaseProject openProject;
	private String uid;
	public static FirebaseDB getInstance(){
		return instance;
	}
	public static FirebaseProject getOpenProject(){
		return openProject;
	}
	public static void setOpenProject(FirebaseProject project){
		openProject = project;

		//Not particularly nice that I have to put the reference for survey data in here
		if(privateRef == null || project.getname() == null)return;
		privateRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				
				Platform.runLater(new Runnable() {
					public void run() {
						Map<String,Object> value = (Map<String,Object>)dataSnapshot.getValue();
						//It's a bit annoying, but this is the only bit of the project that gets updated from the Android side!
						projectKey = value.get(project.getname() + "token").toString();
					}
				});
						
				
			}
		
			@Override
			public void onCancelled(DatabaseError arg0) {
			}
		});
		DatabaseReference globalRef = privateRef.child(PROJECTS_COLL).child(project.getname());
		DatabaseReference surveyDataRef = globalRef.child("surveydata");

		surveyDataRef.addValueEventListener(new ValueEventListener(){
			@Override
			public void onCancelled(DatabaseError arg0) {
			}
			Map<String,Map<String,FirebaseSurveyEntry>> surveymap = new HashMap<String,Map<String,FirebaseSurveyEntry>>();
			Map<String,FirebaseSurveyEntry> entrymap = new HashMap<String,FirebaseSurveyEntry>();
			
			@Override
			public void onDataChange(DataSnapshot arg0) {
				for(DataSnapshot listofentries : arg0.getChildren()) {
					entrymap = new HashMap<String,FirebaseSurveyEntry>();
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
		Task<UserRecord> task = FirebaseAuth.getInstance().getUserByEmail(currentUserEmail)
				.addOnSuccessListener(userRecord -> {
					
					//Set the static thing here
					currentUserId = userRecord.getUid();
					FirebaseApp.getInstance().delete();
					firebaseLogin();
				})
				.addOnFailureListener(e -> {
					System.err.println("Error fetching user data: " + e.getMessage());
					connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
					connectedRef.addValueEventListener(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot snapshot) {
							boolean connected = snapshot.getValue(Boolean.class);
							if (connected) {
								connectedRef.removeEventListener(this);
								getUserCredentials();
							}
						}
						@Override
						public void onCancelled(DatabaseError error) {
							System.err.println("Listener was cancelled");
						}
					});
				});
	}

	//Signs us into the database with restricted access based on the generated userid
	public void firebaseLogin(){
		Map<String, Object> auth = new HashMap<String, Object>();
		uid = currentUserId;
		auth.put("uid", currentUserId);
		try {
			InputStream resource = FirebaseDB.class.getResourceAsStream(SERVICE_JSON);
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredential(FirebaseCredentials.fromCertificate(resource))
					.setDatabaseUrl(DB_URL)
					.setDatabaseAuthVariableOverride(auth)
				    .setStorageBucket("jeeves-27914.appspot.com")
					.build();
			FirebaseApp.initializeApp(options);
			resource.close();
			 resource = FirebaseDB.class.getResourceAsStream(SERVICE_JSON);
			
			addListeners(); //listen on the database AS OUR CURRENT USER
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

	}
	public FirebaseDB() {
		instance = this;
		try {
			InputStream stream = FirebaseDB.class.getResourceAsStream(SERVICE_JSON);
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredential(FirebaseCredentials.fromCertificate(stream))
					.setDatabaseUrl(DB_URL)
					.build();
			FirebaseApp.initializeApp(options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	public void putUserCredentials(String email, String password) {
		try {
			UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmailAsync(email).get();
			String uid = userRecord.getUid();
			dbRef = FirebaseDatabase.getInstance().getReference();
			privateRef =  dbRef.child(PRIVATE_COLL).child(uid);
			privateRef.child("token").setValue(password);
		} catch (InterruptedException | ExecutionException e1) {
			e1.printStackTrace();
		}
	}

	public String getProjectToken() {
		return projectKey;
	}
	public void addListeners() {
		dbRef = FirebaseDatabase.getInstance().getReference();
		privateRef =  dbRef.child(PRIVATE_COLL).child(currentUserId);
		patientsRef = privateRef.child("patients");
		publicRef = dbRef.child(PUBLIC_COLL);
		
		//We now have listeners for individual patients rather than just clearing and adding them all every time an
		//update happens. This should make the patients pane function more cleanly
		patientsRef.addChildEventListener(new ChildEventListener() {
		    @Override
		    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
		        FirebasePatient newPost = dataSnapshot.getValue(FirebasePatient.class);
		        newpatients.add(newPost);
		    }

		    @Override
		    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
		    	FirebasePatient changedPatient = dataSnapshot.getValue(FirebasePatient.class);
		    	newpatients.remove(changedPatient);
		    	newpatients.add(changedPatient);
		    }

		    @Override
		    public void onChildRemoved(DataSnapshot dataSnapshot) {
		    }
		    @Override
		    public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

		    @Override
		    public void onCancelled(DatabaseError databaseError) {}
		});
		privateRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onCancelled(DatabaseError arg0) {
				System.out.println("ERROR: " + arg0.getDetails());
			}
			@Override
			public void onDataChange(DataSnapshot arg0) {
				FirebasePrivate appdata = arg0.getValue(FirebasePrivate.class);
				newprojects.clear();
				if (appdata != null) {
					Map<String, FirebaseProject> projects = appdata.getprojects();
					if (projects != null)
						for (String key : projects.keySet()) {
							newprojects.add(projects.get(key));
						}
				}
			}
		});
		publicRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onCancelled(DatabaseError arg0) {
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

				if (connected) {
					Main.getContext().updateConnectedStatus(true);
				} else {
					Main.getContext().updateConnectedStatus(false);
				}
			}

			@Override
			public void onCancelled(DatabaseError error) {
				System.err.println("Listener was cancelled");
			}
		});
	}

	public boolean addPatient(FirebasePatient object) {
		DatabaseReference globalRef = privateRef.child(PATIENTS_COLL).child(object.getUid());
		globalRef.setValue(object);
		return true;
	}

	public void sendPatientFeedback(FirebasePatient patient, String feedback){
        final DatabaseReference firebaseFeedback =  privateRef.child(PATIENTS_COLL).child(patient.getName()).child("feedback").child(Long.toString(System.currentTimeMillis()));
        firebaseFeedback.setValue("You: " + feedback);
	}

	KeyPairGenerator kpg;
    KeyPair kp;
    PublicKey publicKey;
    PrivateKey privateKey;
    byte [] encryptedBytes,decryptedBytes;
    Cipher cipher,cipher1;
    String encrypted,decrypted;

	public boolean saveProject(String oldname, FirebaseProject object) {
		if (oldname == null){
			try {
				kpg = KeyPairGenerator.getInstance("RSA");
				kpg.initialize(1024);
		        kp = kpg.genKeyPair();
		        publicKey = kp.getPublic();
		 
		        privateKey = kp.getPrivate();
		        object.setpubKey(Base64.encodeBase64String(publicKey.getEncoded()));

		        privateRef.child(object.getname()+"token").setValue(Base64.encodeBase64String(privateKey.getEncoded()));
		  
		        
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
	        

			String projid = generateProjectID();
			object.setid(projid);
			object.setresearcherno(uid);

		}

		object.setlastUpdated(System.currentTimeMillis());
		privateRef.child(PROJECTS_COLL).child(object.getname()).setValue(object);
		
		if(object.getactive()){
			publicRef.child(PROJECTS_COLL).child(object.getname()).setValue(object);		}
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
		privateProjectRef.setValue(project);
		DatabaseReference globalRef = publicRef.child(PROJECTS_COLL).child(project.getname());
		globalRef.setValue(project);
	}

}
