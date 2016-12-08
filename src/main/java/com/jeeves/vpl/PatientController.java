package com.jeeves.vpl;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jeeves.vpl.firebase.FirebasePatient;
import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.firebase.FirebaseSurvey;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

public class PatientController extends Pane{
	

	@FXML private TableView<FirebasePatient> tblPatients;
	@FXML private Group grpInfo;
	@FXML private TextField txtFname;
	@FXML private TextField txtLname;
	@FXML private TextField txtAddress1;
	@FXML private TextField txtAddress2;
	@FXML private TextField txtAddress3;
	@FXML private TextField txtAddress4;
	@FXML private TextField txtPhone;
	@FXML private TextField txtEmail;
	
	@FXML private Label lblCompleted;
	@FXML private Label lblMissed;
	private Map<String,FirebaseSurvey> incompleteSurveys;
	private Map<String,FirebaseSurvey> completedSurveys;
	
	private FirebasePatient selectedPatient;
	private FirebaseDB firebase;
	
	
	public PatientController(MainController controller, FirebaseDB firebase){
		this.firebase = firebase; 
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/PatientPane.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
			loadPatients();
			} catch (Exception e) {
				e.printStackTrace();
			}
	//	getStylesheets().add(ViewElement.class.getResource("ButtonsDemo.css").toExternalForm());
	}
	public void loadPatients(){
		firebase.getpatients().addListener(new ListChangeListener<FirebasePatient>(){
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FirebasePatient> c) {
				loadPatientTable();
				tblPatients.setItems(firebase.getpatients());
			}
		});
		loadPatientTable();
		tblPatients.setItems(firebase.getpatients());
	}
	
	public void sort(int array[]){
		
	}
	@FXML
	public void updateInfo(Event e){
		selectedPatient.setFirstName(txtFname.getText());
		selectedPatient.setLastName(txtLname.getText());
		selectedPatient.setEmail(txtEmail.getText());
		selectedPatient.setPhone(txtPhone.getText());
		String address = txtAddress1.getText() + ";" + txtAddress2.getText() + ";" + txtAddress3.getText() + ";" + txtAddress4.getText();
		selectedPatient.setAddress(address);
		firebase.addPatient(selectedPatient);
	}
	
//	@FXML
//	public void addPatient(Event e){
//		Stage stage = new Stage(StageStyle.UNDECORATED);
//		NewPatientPane root = new NewPatientPane(stage);
//		Scene scene = new Scene(root);
//		stage.setScene(scene);
//		stage.setTitle("Add Patient");
//		stage.initModality(Modality.APPLICATION_MODAL);
//		stage.showAndWait();
//	}
	
	@FXML private ListView lstMessages;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadPatientTable(){
		TableColumn firstNameCol = new TableColumn("First Name");
		firstNameCol.setCellValueFactory(new PropertyValueFactory<FirebasePatient, String>("firstName"));

		TableColumn lastNameCol = new TableColumn("Last Name");
		lastNameCol.setCellValueFactory(new PropertyValueFactory<FirebasePatient, String>("lastName"));

		TableColumn emailCol = new TableColumn("Email");
		emailCol.setCellValueFactory(new PropertyValueFactory<FirebasePatient, String>("email"));

		TableColumn phoneCol = new TableColumn("Phone");
		phoneCol.setCellValueFactory(new PropertyValueFactory<FirebasePatient, String>("phone"));

		
		//This is in the 'runLater' because...
		//When we update the patients database, this automatically updates the table, but the patient database updating is NOT AN FX APPLICATION THREAD so it gets upset
		Platform.runLater(new Runnable(){
			public void run(){
				tblPatients.getColumns().clear();
				tblPatients.getColumns().addAll(firstNameCol, lastNameCol, emailCol, phoneCol);
				tblPatients.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FirebasePatient>() {
					@Override
					public void changed(ObservableValue<? extends FirebasePatient> observable, FirebasePatient oldValue, FirebasePatient newValue) {
						//Check whether item is selected and set value of selected item to Label
						if(tblPatients.getSelectionModel().getSelectedItem() != null) 
						{    
							TableViewSelectionModel<FirebasePatient> selectionModel = tblPatients.getSelectionModel();
							selectedPatient = selectionModel.getSelectedItem();
							lstMessages.getItems().clear();
							Map<String,Object> feedback = selectedPatient.getfeedback();
							Date date = new Date();
							if(feedback != null){
							Iterator<Entry<String,Object>> feeds = feedback.entrySet().iterator();
							ObservableList<String> items =FXCollections.observableArrayList ();
							while(feeds.hasNext()){
								Entry<String,Object> feed = feeds.next();
								date.setTime(Long.parseLong(feed.getKey()));
								String message = date.toGMTString() + ":    " + feed.getValue();
								items.add(message);
							}
								lstMessages.setItems(items);
							}
							incompleteSurveys = selectedPatient.getincomplete();
							completedSurveys = selectedPatient.getcomplete();
							if(incompleteSurveys == null)lblMissed.setText("0");
							
							else 
							{
								int incomplete = 0;
								Iterator<FirebaseSurvey> incompleteIter = incompleteSurveys.values().iterator();
								while(incompleteIter.hasNext()){
									FirebaseSurvey surveygroup = incompleteIter.next();
									incomplete ++;
								}
								lblMissed.setText(Integer.toString(incomplete));}
								
							if(completedSurveys == null)lblCompleted.setText("0");
							else lblCompleted.setText(Integer.toString(completedSurveys.size()));
							
							txtFname.setText(selectedPatient.getFirstName());
						//	txtFname.setOnKeyReleased(event->selectedPatient.setFirstName(txtFname.getText()));

							txtLname.setText(selectedPatient.getLastName());
						//	txtLname.setOnKeyReleased(event->selectedPatient.setLastName(txtLname.getText()));

							txtEmail.setText(selectedPatient.getEmail());
						//	txtEmail.setOnKeyReleased(event->selectedPatient.setEmail(txtEmail.getText()));

							txtPhone.setText(selectedPatient.getPhone());
						//	txtPhone.setOnKeyReleased(event->selectedPatient.setPhone(txtPhone.getText()));

//							EventHandler<KeyEvent> addyhandler = new EventHandler<KeyEvent>(){
//								@Override
//								public void handle(KeyEvent event) {
//									String address = txtAddress1.getText() + ";" + txtAddress2.getText() + ";" + txtAddress3.getText() + ";" + txtAddress4.getText();
//									selectedPatient.setAddress(address);
//								}
//							};
						//	txtAddress1.setOnKeyReleased(addyhandler);
						//	txtAddress2.setOnKeyReleased(addyhandler);
						//	txtAddress3.setOnKeyReleased(addyhandler);
						//	txtAddress4.setOnKeyReleased(addyhandler);

							String address = selectedPatient.getAddress();
							String[] lines = address.split(";");
							if(lines.length>0)txtAddress1.setText(lines[0]);
							if(lines.length>1)txtAddress2.setText(lines[1]);
							if(lines.length>2)txtAddress3.setText(lines[2]);
							if(lines.length>3)txtAddress4.setText(lines[3]);
						//	txtFname.setOnKeyReleased(event->selectedPatient.setFirstName(txtFname.getText()));

							grpInfo.setDisable(false);
						}
					}
				});
			}
		});
		
	}
	
	@FXML
	public void downloadResults(Event e){
		FileWriter writer;
		Date date = new Date();
		try {
			writer = new FileWriter("results.csv");
	    writer.append("Date/Time");
	    if(completedSurveys == null)return;
	    FirebaseSurvey survey = completedSurveys.values().iterator().next();
	    List<FirebaseQuestion> questions = survey.getquestions();
	    questions.forEach(question->{try {
			writer.append(',' + "\"" + question.questionText + "\"");
		} catch (Exception e1) {
		}});
	    writer.append('\n');

	    //Is this horrendously convoluted? Perhaps. Hopefully it won't slow things down
	    Collection<FirebaseSurvey> surveys = completedSurveys.values();
	    ArrayList<FirebaseSurvey> surveylist = new ArrayList<FirebaseSurvey>(surveys);
	    surveylist.sort(new Comparator<FirebaseSurvey>( ){

			@Override
			public int compare(FirebaseSurvey o1, FirebaseSurvey o2) {
				return (int)(o1.gettimeFinished() - o2.gettimeFinished());
			}} );
	    
		for(FirebaseSurvey nextsurvey : surveylist){
			date.setTime(nextsurvey.gettimeFinished());
			writer.append(date.toGMTString());
			List<Map<String, String>> answers = nextsurvey.getanswers();			
			for(Map<String,String> answer : answers){
				if(answer != null) //Really weird how some of the answer lists go 0,1,2,3,5. No 4? Find this out
				writer.append("," + "\"" + answer.get("answer") + "\"");
			}
			writer.append('\n');
			
		}
	    //generate whatever data you want
			
	    writer.flush();
	    writer.close();
	    Desktop.getDesktop().open(new File("results.csv"));
	    
		}
	    catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
