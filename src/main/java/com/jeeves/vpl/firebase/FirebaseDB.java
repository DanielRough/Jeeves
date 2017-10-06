package com.jeeves.vpl.firebase;

import static com.jeeves.vpl.Constants.DB_URL;
import static com.jeeves.vpl.Constants.PATIENTS_COLL;
import static com.jeeves.vpl.Constants.PRIVATE_COLL;
import static com.jeeves.vpl.Constants.PROJECTS_COLL;
import static com.jeeves.vpl.Constants.PUBLIC_COLL;
import static com.jeeves.vpl.Constants.SERVICE_JSON;
import static com.jeeves.vpl.Constants.generateProjectID;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.tasks.Task;
import com.jeeves.vpl.Main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class FirebaseDB {

	private static DatabaseReference dbRef;// = myFirebaseRef.child(DBNAME);
	private static DatabaseReference privateRef;
	private static DatabaseReference patientsRef;
	private static DatabaseReference surveyDataRef;
	private static DatabaseReference publicRef;
	DatabaseReference connectedRef;
	private ObservableList<FirebasePatient> newpatients = FXCollections.observableArrayList();
	private ObservableList<FirebaseProject> newprojects = FXCollections.observableArrayList();
	private ObservableList<FirebaseProject> publicprojects = FXCollections.observableArrayList();
	private Main gui;
	private Session currentsesh;
	private static FirebaseDB instance;
	private static FirebaseProject openProject;
	private String uid;
	private boolean loaded = false;
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
		
		DatabaseReference globalRef = privateRef.child(PROJECTS_COLL).child(project.getname());
		DatabaseReference surveyDataRef = globalRef.child("surveydata");
		System.out.println("I did definitely make a reference right?");
		surveyDataRef.addValueEventListener(new ValueEventListener(){
			@Override
			public void onCancelled(DatabaseError arg0) {
				System.out.println("ERROR ERROR " + arg0.getDetails());
			}
			@Override
			public void onDataChange(DataSnapshot arg0) {
				Map<String,Map<String,FirebaseSurveyEntry>> surveymap = (Map<String,Map<String,FirebaseSurveyEntry>>)arg0.getValue();
				openProject.setsurveydata(surveymap);
			}
		});
	
	}
	public void getUserCredentials(String email){
		Subject currentUser = SecurityUtils.getSubject();
		Task<UserRecord> task = FirebaseAuth.getInstance().getUserByEmail(email)
				.addOnSuccessListener(userRecord -> {
					// See the UserRecord reference doc for the contents of userRecord.
					currentsesh = currentUser.getSession();
					currentsesh.setAttribute( "uid", userRecord.getUid());
					FirebaseApp.getInstance().delete();
					loaded = true;
					firebaseLogin();
				})
				.addOnFailureListener(e -> {
					System.err.println("Error fetching user data: " + e.getMessage());
					//HOPEFULLY this means that if we fail to load for a start, this'll reload things properly
					connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
					connectedRef.addValueEventListener(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot snapshot) {
							boolean connected = snapshot.getValue(Boolean.class);
							if (connected) {
								connectedRef.removeEventListener(this);
								getUserCredentials(email);
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
		Subject currentUser = SecurityUtils.getSubject();
		FileInputStream serviceAccount;
		//The role of our current user represents their unique ID. 
		//This makes sense because then each role corresponds to certain read/write privileges in Firebase
		// Initialize the app with a custom auth variable, limiting the server's access
		Map<String, Object> auth = new HashMap<String, Object>();
		//	Session session = currentUser.getSession();
		uid = currentsesh.getAttribute("uid").toString();
		auth.put("uid", currentsesh.getAttribute("uid"));
		try {
			URL resource = FirebaseDB.class.getResource(SERVICE_JSON);
			File file = new File(resource.toURI());
			serviceAccount = new FileInputStream(file);
			//			serviceAccount = new FileInputStream(SERVICE_JSON);
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
					.setDatabaseUrl(DB_URL)
					.setDatabaseAuthVariableOverride(auth)
					.build();
			FirebaseApp.initializeApp(options);
			addListeners(); //listen on the database AS OUR CURRENT USER
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public FirebaseDB(Main gui) {
		this.gui = gui;
		instance = this;
		try {
			URL resource = FirebaseDB.class.getResource(SERVICE_JSON);
			File file = new File(resource.toURI());
			FileInputStream serviceAccount = new FileInputStream(file);
			//			serviceAccount = new FileInputStream(SERVICE_JSON);
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
					.setDatabaseUrl(DB_URL)
					//  .setDatabaseAuthVariableOverride(auth)
					.build();
			FirebaseApp.initializeApp(options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//	getUserCredentials(email);

	}


	public void addListeners() {
		dbRef = FirebaseDatabase.getInstance().getReference();
		privateRef =  dbRef.child(PRIVATE_COLL).child(currentsesh.getAttribute("uid").toString());
		patientsRef = privateRef.child("patients");
		publicRef = dbRef.child(PUBLIC_COLL);
		
		//We now have listeners for individual patients rather than just clearing and adding them all every time an
		//update happens. This should make the patients pane function more cleanly
		patientsRef.addChildEventListener(new ChildEventListener() {
		    @Override
		    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
		        FirebasePatient newPost = dataSnapshot.getValue(FirebasePatient.class);
		        System.out.println("WE ADDED A CHILD");
		        newpatients.add(newPost);
		    }

		    @Override
		    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
		    	System.out.println("WE CHANGED A CHILD");
		    	FirebasePatient changedPatient = dataSnapshot.getValue(FirebasePatient.class);
		    	System.out.println("name of patient is " + changedPatient.getName());
		    	newpatients.remove(changedPatient);
		    	newpatients.add(changedPatient);
		    }

		    @Override
		    public void onChildRemoved(DataSnapshot dataSnapshot) {
		        FirebasePatient newPost = dataSnapshot.getValue(FirebasePatient.class);
		        System.out.println("WE REMOVED A CHILD");
		    	System.out.println("name of patient is " + newPost.getName());

		        System.out.println("removal result is " +newpatients.remove(newPost) );
		        ;
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
			//	System.out.println("WE CHANNNNNGEDs");
				FirebasePrivate appdata = arg0.getValue(FirebasePrivate.class);
				newprojects.clear();
			//	newpatients.clear();
				if (appdata != null) {
					Map<String, FirebaseProject> projects = appdata.getprojects();
			//		Map<String, FirebasePatient> patients = appdata.getpatients();
					if (projects != null)
						for (String key : projects.keySet())
							newprojects.add(projects.get(key));
//					if (patients != null)
//						newpatients.addAll(patients.values());
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
				//	newpatients.clear();
				if (projects != null) {
					//for (String key : publicprojs.keySet())
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
//	public void loadProject(String name){
//
//		DatabaseReference globalRef = privateRef.child(PROJECTS_COLL).child(name);
//		globalRef.addListenerForSingleValueEvent(new ValueEventListener() {
//			@Override
//			public void onDataChange(DataSnapshot dataSnapshot) {
//				@SuppressWarnings("unchecked")
//				FirebaseProject proj = dataSnapshot.getValue(FirebaseProject.class);
//				openProject = proj;
//				//It's a bit annoying, but this is the only bit of the project that gets updated from the Android side!
//				
//				Platform.runLater(new Runnable(){
//					public void run(){
//						gui.setCurrentProject(proj);
//					}
//				});
//				
//			}
//
//			@Override
//			public void onCancelled(DatabaseError arg0) {
//				// TODO Auto-generated method stub
//
//			}
//
//
//		});
//	}
	KeyPairGenerator kpg;
    KeyPair kp;
    PublicKey publicKey;
    PrivateKey privateKey;
    byte [] encryptedBytes,decryptedBytes;
    Cipher cipher,cipher1;
    String encrypted,decrypted;

	public boolean saveProject(String oldname, FirebaseProject object) {

		//Gonna try some basic encryption stuff
		
//		DatabaseReference globalRef = null;
//		DatabaseReference publicRef = null;
		//just reloading something we've already made. Need to update it in the public bit too (if it's currently active)
		if (oldname == null/* || oldname.equals("")*/){
			try {
				kpg = KeyPairGenerator.getInstance("RSA");
				kpg.initialize(1024);
		        kp = kpg.genKeyPair();
		        publicKey = kp.getPublic();
		 
		        privateKey = kp.getPrivate();
		        Preferences prefs = Preferences.userRoot().node("key");
		        object.setpubKey(Base64.encodeBase64String(publicKey.getEncoded()));

		        //SPECIFIC PRIVATE KEY TO THIS PROJECT YOU FUCKING IDIOT
		        prefs.put("privateKey"+object.getname(),Base64.encodeBase64String(privateKey.getEncoded()));
		        System.out.println("Just set the private key to " + prefs.get("privateKey"+object.getname(), ""));
		        
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
		//	publicRef = dbRef.child(PUBLIC_COLL).child(PROJECTS_COLL).child(object.getname());
		//	globalRef = privateRef.child(PROJECTS_COLL).child(object.getname());
			//This project needs an ID
			String projid = generateProjectID();
			object.setid(projid);
			object.setresearcherno(uid);
//			
//			if(object.getactive()){
//				publicRef.child(PROJECTS_COLL).child(object.getname()).setValue(object);
//			}
		}
		else {
		//	publicRef = dbRef.child(PUBLIC_COLL).child(PROJECTS_COLL)child(oldname);
		//	publicRef.child(PROJECTS_COLL).child(oldname).removeValue();
	//		privateRef.child(PROJECTS_COLL).child(oldname).removeValue();
			//			publicRef = dbRef.child(PUBLIC_COLL).child(object.getname());
	//		globalRef = privateRef.child(PROJECTS_COLL).child(oldname); // Update
//			globalRef.removeValue();
//			publicRef.removeValue();
//			globalRef = privateRef.child(PROJECTS_COLL).child(object.getname());
//			publicRef = publicRef.child(PUBLIC_COLL).child(object.getname());
		}
		object.setlastUpdated(System.currentTimeMillis());
		privateRef.child(PROJECTS_COLL).child(object.getname()).setValue(object);
		
	//	System.out.println("My uid is " + currentsesh.getAttribute("uid"));
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

	public ObservableList<FirebaseProject> getpublicprojects(){
		return publicprojects;
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
