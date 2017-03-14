package com.jeeves.vpl;


import java.util.Map;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jeeves.vpl.firebase.FirebaseMain;
import com.jeeves.vpl.firebase.FirebasePatient;
import com.jeeves.vpl.firebase.FirebaseProject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FirebaseDB {

	public FirebaseDB(){
		System.out.println("AM i null " + (FirebaseDB.class.getResourceAsStream("/Jeeves-9b9326e90601.json") == null));;
			FirebaseOptions options = new FirebaseOptions.Builder()
			.setDatabaseUrl("https://jeeves-27914.firebaseio.com/")
			.setServiceAccount(FirebaseDB.class.getResourceAsStream("/Jeeves-9b9326e90601.json"))//new FileInputStream("/Users/Daniel/Documents/workspace/NewJeeves/Jeeves-9b9326e90601.json"))
			.build();
			
			FirebaseApp.initializeApp(options);


	
	}

	

	//private String username;
	
	private String DBNAME = "JeevesData";
	private String PATIENTS_COLL = "patients";
	private String PROJECTS_COLL = "projects";
	private DatabaseReference bigRef;// = myFirebaseRef.child(DBNAME);
	private DatabaseReference myFirebaseRef;
	private ObservableList<FirebaseProject> newprojects = FXCollections.observableArrayList();
	private ObservableList<FirebasePatient> newpatients = FXCollections.observableArrayList();

	
//	public void setUsername(String username){
//		this.username = username;
//	}
	public ObservableList<FirebaseProject> getprojects(){
		return newprojects;
	}
	
	public ObservableList<FirebasePatient> getpatients(){
		return newpatients;
	}

	public void addListeners(){
		myFirebaseRef = FirebaseDatabase
			    .getInstance()
			    .getReference();
		 bigRef = myFirebaseRef.child(DBNAME);
		bigRef.addValueEventListener(new ValueEventListener(){
			@Override
			public void onDataChange(DataSnapshot arg0) {
				@SuppressWarnings("unchecked")
				FirebaseMain appdata = arg0.getValue(FirebaseMain.class);
				newprojects.clear();
				newpatients.clear();
				if(appdata != null){
					Map<String,FirebaseProject>  projects = appdata.getprojects();
					Map<String,FirebasePatient> patients = appdata.getpatients();
					if(projects != null)
						for(String key : projects.keySet())
						//	if(key.startsWith(username))
								newprojects.add(projects.get(key));
					if(patients != null)
						newpatients.addAll(patients.values());
				
//					systemvars.addAll(vars.values());
				}			
			}
			@Override
			public void onCancelled(DatabaseError arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	public boolean addProject(String oldname, FirebaseProject object) {
		
		DatabaseReference globalRef = null;
		if(oldname == null || oldname.equals(""))
			globalRef = myFirebaseRef.child(DBNAME).child(PROJECTS_COLL).child(object.getname());
		else{
			globalRef = myFirebaseRef.child(DBNAME).child(PROJECTS_COLL).child(oldname); //Update an old one
			globalRef.removeValue();
			globalRef = myFirebaseRef.child(DBNAME).child(PROJECTS_COLL).child(object.getname());
		}
		globalRef.setValue(object);
		return true;
	}
	public boolean addPatient(FirebasePatient object) {
		DatabaseReference globalRef = myFirebaseRef.child(DBNAME).child(PATIENTS_COLL).child(object.getUid());
		globalRef.setValue(object);
		return true;
	}
	
}
