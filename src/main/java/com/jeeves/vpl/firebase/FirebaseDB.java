package com.jeeves.vpl.firebase;

import static com.jeeves.vpl.Constants.*;
import static com.jeeves.vpl.Constants.DB_URL;
import static com.jeeves.vpl.Constants.PATIENTS_COLL;
import static com.jeeves.vpl.Constants.PROJECTS_COLL;
import static com.jeeves.vpl.Constants.SERVICE_JSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.auth.UserRecord;
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

public class FirebaseDB {

	private DatabaseReference dbRef;// = myFirebaseRef.child(DBNAME);
	private DatabaseReference firebaseRef;
	private ObservableList<FirebasePatient> newpatients = FXCollections.observableArrayList();
	private ObservableList<FirebaseProject> newprojects = FXCollections.observableArrayList();
	private Main gui;
	private Session currentsesh;
	public void getUserCredentials(String email){
		Subject currentUser = SecurityUtils.getSubject();
		Task<UserRecord> task = FirebaseAuth.getInstance().getUserByEmail(email)
			    .addOnSuccessListener(userRecord -> {
			      // See the UserRecord reference doc for the contents of userRecord.
			    	System.out.println("Successfully fetched user data: " + userRecord.getEmail());
			    	currentsesh = currentUser.getSession();
			    	currentsesh.setAttribute( "uid", userRecord.getUid());
					FirebaseApp.getInstance().delete();
					
					firebaseLogin();
			    })
			    .addOnFailureListener(e -> {
			      System.err.println("Error fetching user data: " + e.getMessage());
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
			System.out.println("Our User id is " + auth.get("uid"));
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
		firebaseRef = FirebaseDatabase.getInstance().getReference();
		dbRef =  firebaseRef.child(DBNAME + "/" + currentsesh.getAttribute("uid"));
		System.out.println("adding dem listeners");
		DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
		connectedRef.addValueEventListener(new ValueEventListener() {
		  @Override
		  public void onDataChange(DataSnapshot snapshot) {
		    boolean connected = snapshot.getValue(Boolean.class);
		    if (connected) {
		      gui.updateConnecetedStatus(true);
		    } else {
		      gui.updateConnecetedStatus(false);
		    }
		  }

		  @Override
		  public void onCancelled(DatabaseError error) {
		    System.err.println("Listener was cancelled");
		  }
		});
		dbRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onCancelled(DatabaseError arg0) {
				System.out.println("ERROR ERROR " + arg0.getDetails());
			}

			@Override
			public void onDataChange(DataSnapshot arg0) {
				FirebaseMain appdata = arg0.getValue(FirebaseMain.class);
				newprojects.clear();
				newpatients.clear();
				if (appdata != null) {
					Map<String, FirebaseProject> projects = appdata.getprojects();
					Map<String, FirebasePatient> patients = appdata.getpatients();
					if (projects != null)
						for (String key : projects.keySet())
							newprojects.add(projects.get(key));
					if (patients != null)
						newpatients.addAll(patients.values());
				}
			}
		});
	}

	public boolean addPatient(FirebasePatient object) {
		DatabaseReference globalRef = firebaseRef.child(DBNAME).child(currentsesh.getAttribute("uid").toString()).child(PATIENTS_COLL).child(object.getUid());
		globalRef.setValue(object);
		return true;
	}

	public void loadProject(String name){
		DatabaseReference globalRef = firebaseRef.child(DBNAME).child(currentsesh.getAttribute("uid").toString()).child(PROJECTS_COLL).child(name);
		globalRef.addListenerForSingleValueEvent(new ValueEventListener() {
		   @Override
		   public void onDataChange(DataSnapshot dataSnapshot) {
			   @SuppressWarnings("unchecked")
			   FirebaseProject proj = dataSnapshot.getValue(FirebaseProject.class);
			   Platform.runLater(new Runnable(){
				   public void run(){
					   gui.setCurrentProject(proj);

				   }
			   });
		   }

		@Override
		public void onCancelled(DatabaseError arg0) {
			// TODO Auto-generated method stub
			
		}

	
		});
	}

	public boolean addProject(String oldname, FirebaseProject object) {

		DatabaseReference globalRef = null;
		if (oldname == null || oldname.equals("")){
			globalRef = firebaseRef.child(DBNAME).child(currentsesh.getAttribute("uid").toString()).child(PROJECTS_COLL).child(object.getname());
			//This project needs an ID
			String projid = generateProjectID();
			object.setid(projid);
		}
		else {
			globalRef = firebaseRef.child(DBNAME).child(currentsesh.getAttribute("uid").toString()).child(PROJECTS_COLL).child(oldname); // Update
																						// an
																						// old
																						// one
			globalRef.removeValue();
			globalRef = firebaseRef.child(DBNAME).child(currentsesh.getAttribute("uid").toString()).child(PROJECTS_COLL).child(object.getname());
		}
		globalRef.setValue(object);
		return true;
	}

	public ObservableList<FirebasePatient> getpatients() {
		return newpatients;
	}

	// public void setUsername(String username){
	// this.username = username;
	// }
	
	public ObservableList<FirebaseProject> getprojects() {
		return newprojects;
	}

}
