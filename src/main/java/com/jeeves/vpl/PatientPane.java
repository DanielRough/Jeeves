package com.jeeves.vpl;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebasePatient;
import com.jeeves.vpl.firebase.FirebaseProject;
import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.firebase.FirebaseSurvey;
import com.jeeves.vpl.firebase.FirebaseSurveyEntry;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class PatientPane extends Pane {
	final Logger logger = LoggerFactory.getLogger(PatientPane.class);
	private ObservableList<FirebasePatient> allowedPatients = FXCollections.observableArrayList();
	private Map<String, FirebaseSurvey> completedSurveys;

	private Map<String, FirebaseSurvey> incompleteSurveys;
	@FXML private ListView<String> lstMessages;
	@FXML private ListView<FirebasePatient> lstPatients;
	@FXML private TextField txtEmail;
	@FXML private TextField txtName;
	@FXML private TextField txtPhone;
	private ChangeListener<FirebasePatient> patientListener;
	private ChangeListener<String> surveyListener;
	private FirebasePatient selectedPatient;
	private FirebaseSurvey selectedSurvey;
	Map<String,FirebaseSurvey> surveyIdMap;
	@FXML private ListView<String> lstSurveys;
	@FXML private Label lblCompTimeThis;
	@FXML private Label lblComplete;
	@FXML private Label lblMissed;
	@FXML private Label lblCompTimeAll;

	@FXML private TableView tblSchedule;
	@FXML private TableColumn colWake;
	@FXML private TableColumn colSleep;
	@FXML private TableColumn colDay;
	@FXML private Button btnUpdateSchedule;
	@FXML private Label lblUpdated;
	@FXML private TextArea txtPatientMessage;
	@FXML private Button btnSendMessage;

	@FXML private TextArea txtAllMessage;
	@FXML private Button btnSendAll;
	
	//@FXML private Button btnChangeName;
	@FXML private Button btnDelete;

	@FXML private RadioButton rdioSelPatient;
	@FXML private RadioButton rdioAllPatient;
	private ToggleGroup patientGroup;
	@FXML private RadioButton rdioSelSurvey;
	@FXML private RadioButton rdioAllSurvey;
	private ToggleGroup surveyGroup;
	private PrivateKey privateKey;

	ValueEventListener patientChangeListener = new ValueEventListener() {

		@Override
		public void onDataChange(DataSnapshot snapshot) {
			System.out.println("We have ourselves a change");
			FirebasePatient p = snapshot.getValue(FirebasePatient.class);
			System.out.println("Feedback size is " + p.getfeedback().size());
			System.out.println("But apparently it think it's");
			for(FirebasePatient patient : lstPatients.getItems()) {
				//I'm SURE there must be a better way but I don't know yet so...
				if(patient.getName().equals(p.getName())) {
					patient.setfeedback(p.getfeedback());			
					patient.setcomplete(p.getcomplete());
					patient.setincomplete(p.getincomplete());
				}
			}
			updatePatient();
		//	System.out.println(lstPatients.getSelectionModel().getSelectedItem().getfeedback().size());
		//	lstPatients.getSelectionModel().select(p);
		//	loadPatientTable(FirebaseDB.getInstance().getOpenProject().getname());
		}

		@Override
		public void onCancelled(DatabaseError error) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public PrivateKey getPrivate(String keystr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Base64.decodeBase64(keystr);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	public PublicKey getPublic(String keystr) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
        byte[] keyBytes = Base64.decodeBase64(keystr);
        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            PublicKey key = kf.generatePublic(X509publicKey);
            return key;
	}
	
	private Cipher cipher;


	public String decryptSymmetric(String msg, String symmetrickey) {
		// Decode the encoded data with AES
		byte[] encodedBytes = Base64.decodeBase64(msg);
		try {
			Cipher c = Cipher.getInstance("AES");
			SecretKeySpec sks = new SecretKeySpec(Base64.decodeBase64(symmetrickey), "AES");

			c.init(Cipher.DECRYPT_MODE, sks);
			byte[] decodedBytes = c.doFinal(encodedBytes);
			return new String(decodedBytes, "UTF-8");
		} catch (Exception e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
		return msg;
	}
	public String decryptText(String msg, PrivateKey key)
			throws InvalidKeyException, UnsupportedEncodingException,
			IllegalBlockSizeException, BadPaddingException {
		this.cipher.init(Cipher.DECRYPT_MODE, key);
		System.out.println("message: " + msg);
		if(msg==null) {
			return "";
		}
		return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
	}
	
	public String encryptText(String msg, PublicKey key) throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.encodeBase64String((cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8))));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PatientPane() {
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		patientListener = (o,v0,v1)->
		updatePatient();
		surveyListener = (o,v0,v1)->
		updateSurvey();
		try {
			this.cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e1) {
			logger.error(e1.getMessage(),e1.fillInStackTrace());
		}

		URL location = this.getClass().getResource("/PatientPane.fxml");

		TableColumn nameCol = new TableColumn("Name");
		nameCol.setCellValueFactory(p->
		new ReadOnlyObjectWrapper(((CellDataFeatures<FirebasePatient, String>)p).getValue().getEmail())

				);
		fxmlLoader.setLocation(location);
		try {
			Node root = fxmlLoader.load();
			getChildren().add(root);
			lstPatients.setCellFactory(list->
			new ListCell<FirebasePatient>(){
				@Override
				public void updateItem(FirebasePatient item, boolean empty) {
					super.updateItem(item, empty);
					if(item != null) {
						Platform.runLater(()->
						setText(item.getEmail()));
					}
				}
			}

					);
			surveyGroup = new ToggleGroup();
			patientGroup = new ToggleGroup();
			rdioSelSurvey.setToggleGroup(surveyGroup);
			rdioAllSurvey.setToggleGroup(surveyGroup);
			rdioSelPatient.setToggleGroup(patientGroup);
			rdioAllPatient.setToggleGroup(patientGroup);

		} catch (Exception e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
	}

	@FXML
	public void downloadData(Event e){
		if(rdioSelPatient.isSelected()){
			this.downloadPatient(e);
		} else {
			try {
				this.downloadSurvey(e);
			} catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e1) {
				logger.error(e1.getMessage(),e1.fillInStackTrace());
			}
		}
	}

	public Sheet doAThing(Workbook wb,Map<String,Sheet> sheets, FirebaseSurvey nextsurvey,CellStyle style) {

		Sheet s = wb.createSheet();
		sheets.put(nextsurvey.getname(), s);
		wb.setSheetName(wb.getSheetIndex(s), nextsurvey.gettitle());
		Row r = s.createRow(0);
		Cell c = r.createCell(0);
		c.setCellStyle(style);
		c.setCellValue("Completed");
		c = r.createCell(1);
		c.setCellStyle(style);
		c.setCellValue("Participant ID");
		ObservableList<FirebaseSurvey> surveystuff = Constants.getOpenProject().getObservableSurveys();
		//When we download data for all surveys, want to get question text up there
		for(FirebaseSurvey survey : surveystuff) {
			if(survey.gettitle().equals(nextsurvey.gettitle())) {
				int qcount = 2;
				for(FirebaseQuestion q : survey.getquestions()){
					c = r.createCell(qcount);
					qcount++;
					c.setCellValue(q.getquestionText());
					c.setCellStyle(style);
				}
				break;
			}
		}
		return s;
	}
	@FXML
	public void downloadSurvey(Event e) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		Date date = new Date();
		FileChooser fileChooser = new FileChooser();

		fileChooser.setInitialFileName(lstSurveys.getSelectionModel().getSelectedItem()+ ".xls");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel spreadsheet(*.xls)", "*.xls"));

		fileChooser.setTitle("Save Survey Data");
		Row r = null;
		Cell c = null;

		File file = fileChooser.showSaveDialog(getScene().getWindow());
		if (!file.getName().contains(".")) {
			file = new File(file.getAbsolutePath() + ".xls");
		}
		HSSFWorkbook wb = new HSSFWorkbook();

		HashMap<String, Sheet> sheets = new HashMap<>();

		Sheet s = null;
		CreationHelper createHelper = wb.getCreationHelper();
		CellStyle cellStyle = wb.createCellStyle();
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setFontHeightInPoints((short)10);
		font.setBold(true);
		style.setFont(font);
		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
		cellStyle.setFont(font);

		String surveyId = "";
		if(rdioSelSurvey.isSelected()) {
			String surveyname = lstSurveys.getSelectionModel().getSelectedItem();
			System.out.println("survye name is " + surveyname);
			surveyId = surveyIdMap.get(surveyname).getsurveyId();
			System.out.println("And so the id is " + surveyId);
		}
		for (FirebasePatient p : allowedPatients) {
			Map<String,FirebaseSurvey> completed = p.getcomplete();
			if(completed == null || completed.values() == null)
				continue;
			Iterator<FirebaseSurvey> iter = completed.values().iterator();
			while(iter.hasNext()) {
				FirebaseSurvey surv = iter.next();
				if(!surveyId.equals("") && !surveyId.equals(surv.getsurveyId()))
					continue;
				date.setTime(surv.gettimeFinished());
				s = sheets.get(surv.gettitle());
				if (s == null) {
					s = doAThing(wb,sheets,surv,style);
					sheets.put(surv.gettitle(), s);
				}
				r = s.createRow(s.getLastRowNum() + 1);
				c = r.createCell(0);
				c.setCellStyle(cellStyle);

				c.setCellValue(date);
				c = r.createCell(1);
				c.setCellStyle(cellStyle);
				
				c.setCellValue(p.getEmail());
				String decoded = "";
				String encodedanswers = surv.getencodedAnswers();
				if(surv.getencodedKey() != null) {
					String encodedkey = surv.getencodedKey();
					String symmetrickey = decryptText(encodedkey, privateKey);

					decoded = decryptSymmetric(encodedanswers, symmetrickey);
				}
				else {
					decoded = decryptText(encodedanswers, privateKey);
				}
				String[] answers = decoded.split(";");
				int answercounter = 2;
				for (String answer : answers) {

					c = r.createCell(answercounter);
					answercounter++;

					c.setCellValue(answer);
				}
				s.setColumnWidth(0, 20*256);
				for (int i = 1; i < answercounter; i++){
					s.autoSizeColumn(i);
				}
			}
		}

		try(FileOutputStream fileOut = new FileOutputStream(file)){
			wb.write(fileOut);
			Desktop.getDesktop().open(file);
			wb.close();
		}

	} 

	@FXML
	public void downloadPatient(Event e) {

		try(Workbook wb = new HSSFWorkbook()) {
			FileChooser fileChooser = new FileChooser();
			if(selectedPatient == null && rdioSelPatient.isSelected()) {
				Constants.makeInfoAlert("Exception", "No participant selected", "Please select a participant from the list");
				return;
			}
			fileChooser.setInitialFileName("jeevesdata.xls");
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel spreadsheet(*.xls)", "*.xls"));

			fileChooser.setTitle("Save Participant Data");

			File file = fileChooser.showSaveDialog(getScene().getWindow());

			if (file != null) {
				if (!file.getName().contains(".")) {
					file = new File(file.getAbsolutePath() + ".xls");
				}
				//if(rdioSelPatient.isSelected())
					writeFile(wb, file);
				//else
					
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}

	}

	public void writeFile(Workbook wb,File file) throws IOException {
		Row r = null;
		Cell c = null;
		int answerlength = 0;

		if (completedSurveys == null || completedSurveys.isEmpty()) {
			return;
		}
		Font font = wb.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setFontHeightInPoints((short)10);
		font.setBold(true);
		CellStyle style = wb.createCellStyle();
		CellStyle cellStyle = wb.createCellStyle();
		CreationHelper createHelper = wb.getCreationHelper();

		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
		cellStyle.setFont(font);
		style.setFont(font);

		HashMap<String, Sheet> sheets = new HashMap<>();

		Collection<FirebaseSurvey> surveys = completedSurveys.values();
		ArrayList<FirebaseSurvey> surveylist = new ArrayList<>(surveys);
		surveylist.sort((o1,o2) ->
		(int) (o1.gettimeFinished() - o2.gettimeFinished())	
				);

		Sheet s = null;

		for (FirebaseSurvey nextsurvey : surveylist) {

			String surveyname = nextsurvey.gettitle();
			if(rdioSelSurvey.isSelected() && !surveyname.equals(lstSurveys.getSelectionModel().getSelectedItem()))
				continue; //Skip any surveys that don't have the correct name
			// Do we have a sheet for this particular survey?
			s = sheets.get(surveyname);
			if (s == null) {
				s = wb.createSheet();
				sheets.put(surveyname, s);
				wb.setSheetName(wb.getSheetIndex(s), surveyname);
				r = s.createRow(0);
				int count = 1;
				c = r.createCell(0);
				c.setCellStyle(style);
				c.setCellValue("Completed");

				for(FirebaseQuestion q : nextsurvey.getquestions()){
					answerlength++;
					c = r.createCell(count);
					count++;
					c.setCellValue(q.getquestionText());
					c.setCellStyle(style);
				}
			}
			writeAnswers(s,nextsurvey,cellStyle);
			s.setColumnWidth(0, 20*256);
			for (int i = 1; i < answerlength; i++){
				s.autoSizeColumn(i);
			}
		}
		try(FileOutputStream fileOut = new FileOutputStream(file)){
			wb.write(fileOut);
		}

		Desktop.getDesktop().open(file);
	}

	public void writeAnswers(Sheet s, FirebaseSurvey nextsurvey,CellStyle style) {
		Date date = new Date();
		date.setTime(nextsurvey.gettimeFinished());
		System.out.println("TIME is " + nextsurvey.gettimeFinished());

		Row r = s.createRow(s.getLastRowNum() + 1);
		Cell c = r.createCell(0);
		c.setCellStyle(style);
		c.setCellValue(date);

		String encodedanswers = nextsurvey.getencodedAnswers();
		String decoded = "";
		String encodedkey = nextsurvey.getencodedKey();
		String symmetrickey;
		try {
			symmetrickey = decryptText(encodedkey, privateKey);
			decoded = decryptSymmetric(encodedanswers, symmetrickey);

		} catch (InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException
				| BadPaddingException e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}

		String[] answers = decoded.split(";");
		int answercounter = 1;
		for (String answer : answers) {
			c = r.createCell(answercounter);
			answercounter++;
			c.setCellValue(answer);
		}
	}
	public void loadSurveys(){

		List<FirebaseSurvey> surveydata = Constants.getOpenProject().getObservableSurveys();
		surveyIdMap = new HashMap<>();

		surveydata.forEach(key->{surveyIdMap.put(key.gettitle(), key);lstSurveys.getItems().add(key.gettitle());System.out.println("AND ITS ID IS " + key.getsurveyId());});
		lstSurveys.getSelectionModel().selectedItemProperty().addListener(surveyListener);
		FirebaseProject proj = FirebaseDB.getInstance().getOpenProject();
		if (proj == null) {
			return;
		}
		lstSurveys.getSelectionModel().clearAndSelect(0);

	}
	int lastRemovedIndex = 0;
	public void loadPatients() {
		FirebaseProject proj = FirebaseDB.getInstance().getOpenProject();
		if (proj == null)
			return;
		FirebaseDB.getInstance().getpatients().addListener((javafx.collections.ListChangeListener.Change<? extends FirebasePatient> c)->{
			//Keep a reference to the patient we had selected
			FirebasePatient selected = lstPatients.getSelectionModel().getSelectedItem();
			c.next();
			if(c.wasAdded()){
				
				addToTable(c.getAddedSubList()); 
				lstPatients.getSelectionModel().select(selected);
				updatePatient();
			}
			else if (c.wasRemoved()){
				//For some unknown reason it fires twice, and the first time is -1
				lastRemovedIndex = Math.max(0,lstPatients.getItems().indexOf(c.getRemoved().get(0)));
				System.out.println("Last removed index was " + lastRemovedIndex);
				removeFromTable(c.getRemoved());
				lstPatients.getSelectionModel().select(selected);
				updatePatient();
			}
		}
				);

		loadPatientTable(FirebaseDB.getInstance().getOpenProject().getname());
		lstPatients.getSelectionModel().clearAndSelect(0);
		updatePatient();
	}


	/**
	 * Decrypts a patient's personal info and adds it to the table
	 * @param patient
	 */
	private void addToTable(List<? extends FirebasePatient> list){
		FirebaseProject proj = FirebaseDB.getInstance().getOpenProject();
		if (proj == null)
			return;
		String name = proj.getname();
		System.out.println("NAME IS " + name);
		list.forEach(patient->{
			if(patient.getCurrentStudy().equals(name))
				try {
					privateKey = getPrivate(FirebaseDB.getInstance().getProjectToken());

					System.out.println("EMAIL IS " + patient.getEmail());
					String email = decryptText(patient.getEmail(),privateKey);
					patient.setEmail(email);
					lstPatients.getItems().add(lastRemovedIndex,patient);
				} catch (Exception e) {
					logger.error(e.getMessage(),e.fillInStackTrace());
					lstPatients.getItems().add(lastRemovedIndex,patient);

				}

		});


	}
	/**
	 * If a patient is uncoupled from the study, this method removes them
	 * @param patient
	 */

	private void removeFromTable(List<? extends FirebasePatient> list){
		lstPatients.getItems().removeAll(list);

	}


//	private void decryptInfo(FirebasePatient patient, String personalInfo){
//		String[] infoBits = personalInfo.split(";");
//		if(infoBits.length < 2) {
//			return; //Bad things have happened. 
//		}
//		String name = infoBits[0];
//		String email = infoBits[1];
//		patient.setScreenName(name);
//		patient.setEmail(email);
//
//	}

	Map<String,FirebaseSurvey> allIncomplete;
	Map<String,FirebaseSurvey> allComplete;

	private void loadPatientTable(String name) {
		Platform.runLater(()->{
			allowedPatients.clear();
			
			FirebaseDB.getInstance().getpatients().forEach(patient -> {
				if (patient.getCurrentStudy() != null && patient.getCurrentStudy().equals(name)) {
					try {
						allowedPatients.add(patient);
						System.out.println("Private key is " + FirebaseDB.getInstance().getProjectToken());
						String patientName = patient.getName();
						FirebaseDB.getInstance().getPatientsRef().child(patientName).addValueEventListener(patientChangeListener);
						privateKey = getPrivate(FirebaseDB.getInstance().getProjectToken());
						System.out.println("user info: " + patient.getuserinfo());
						String email = decryptText(patient.getEmail(),privateKey);
						System.out.println("Email is " + email);
						patient.setEmail(email);
						//decryptInfo(patient,personalInfo);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e.getMessage(),e.fillInStackTrace());
					}
				}
			}); 
			
			//New code to test sorting of people
			Comparator<FirebasePatient> comparator = Comparator.comparingLong(FirebasePatient::getsignuptime); 
			comparator = comparator.reversed();
			FXCollections.sort(allowedPatients, comparator);
			
			lstPatients.setItems(allowedPatients); // This is hacky but I'll get
			
			updatePatient();
		}
				);
		lstPatients.getSelectionModel().selectedItemProperty().addListener(patientListener);
	}


	
	private void updateSurvey(){
		String surveyname = lstSurveys.getSelectionModel().getSelectedItem();
		String surveyId = surveyIdMap.get(surveyname).getsurveyId();
		selectedSurvey = surveyIdMap.get(surveyname);
		Map<String,Map<String,FirebaseSurveyEntry>> surveydata = FirebaseDB.getInstance().getOpenProject().getSurveyEntries();
		Map<String,FirebaseSurveyEntry> data = surveydata.get(surveyId);
		if(data == null){
		//	lblComplete.setText("");
		//	lblMissed.setText("");
		//	lblCompTimeThis.setText("");
		//	lblCompTimeAll.setText("");
			return;
		}

		int avgCompleteTime = 0;

		Iterator<FirebaseSurveyEntry> iter = data.values().iterator();
		while(iter.hasNext()){
			FirebaseSurveyEntry entrydata = iter.next();
			avgCompleteTime += (entrydata.getcomplete() + entrydata.getinitTime());
		}
		avgCompleteTime /= data.values().size();
		lblCompTimeAll.setText((avgCompleteTime/1000) + " seconds");
		updatePatient();
	}
	/**
	 * This method gets the selected patient in the patient table, and udpates the rest of the GUI with this
	 * patient's information, including decrypted personal information, study compliance, and feedback
	 */
	private void updatePatient() {

		Platform.runLater(()->{
			MultipleSelectionModel<FirebasePatient> selectionModel = lstPatients.getSelectionModel();
			if (selectionModel.getSelectedItem() != null) {
				selectedPatient = selectionModel.getSelectedItem();
			//	btnChangeName.setDisable(false);
				btnDelete.setDisable(false);
			} else {
				System.out.println("Bugger it's null");
				return;
			}
			lstMessages.getItems().clear();
			
			TreeMap<String,Object> orderedFeedback;
			Map<String, Object> feedback = selectedPatient.getfeedback();
			if(feedback != null) {
				orderedFeedback = new TreeMap(feedback);
			}
			else {
				orderedFeedback = new TreeMap();
			}
			Date date = new Date();
			DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
			df.setTimeZone(TimeZone.getDefault());

			Iterator<Entry<String, Object>> feeds = orderedFeedback.entrySet().iterator();
			ObservableList<String> items = FXCollections.observableArrayList();
			while (feeds.hasNext()) {
				Entry<String, Object> feed = feeds.next();
				date.setTime(Long.parseLong(feed.getKey()));
				String message = df.format(date) + ":    " + feed.getValue();
				items.add(message);
			}
			System.out.println("CLEARING");
			System.out.println("Items length is " + items.size());
			lstMessages.setItems(items);
			
			if(selectedPatient.getschedule() != null) {
				ObservableList<ScheduleItem> scheduleItems = FXCollections.observableArrayList();
				List<String> scheduleStrings = selectedPatient.getschedule();
				int count = 1;
				for(String day : scheduleStrings) {
					String[] wakeSleep = day.split(":");
					ScheduleItem s = new ScheduleItem(count,wakeSleep[0],wakeSleep[1]);
					scheduleItems.add(s);
					count++;
				}
				colDay.setCellValueFactory(new PropertyValueFactory<>("studyDay"));
				colWake.setCellValueFactory(new PropertyValueFactory<>("wakeTime"));
				colSleep.setCellValueFactory(new PropertyValueFactory<>("sleepTime"));
				tblSchedule.setDisable(false);
				btnUpdateSchedule.setDisable(false);
				tblSchedule.setItems(scheduleItems);
				
				Callback<TableColumn<ScheduleItem,String>, TableCell<ScheduleItem,String>> cellFactoryDate = (TableColumn<ScheduleItem,String> p) -> new DateTimeCell();
				colWake.setCellFactory(cellFactoryDate);
				colWake.setOnEditCommit(
				    new EventHandler<CellEditEvent<ScheduleItem,String>>() {
				        @Override
				        public void handle(CellEditEvent<ScheduleItem,String> t) {
				            try {
				                df.parse(t.getNewValue());
				            } catch (ParseException e) {

				            }				
				            ((ScheduleItem) t.getTableView().getItems()
				            		.get(t.getTablePosition().getRow()))
					                .setWakeTime(t.getNewValue());
				            
			            	getTableViewValues(tblSchedule);

				        }
				    }
				);
				colSleep.setCellFactory(cellFactoryDate);
				colSleep.setOnEditCommit(
				    new EventHandler<CellEditEvent<ScheduleItem,String>>() {
				        @Override
				        public void handle(CellEditEvent<ScheduleItem,String> t) {
				            try {
				                df.parse(t.getNewValue());
				            } catch (ParseException e) {
				            }
				            ((ScheduleItem) t.getTableView().getItems()
				            		.get(t.getTablePosition().getRow()))
				            		.setSleepTime(t.getNewValue());
			            	getTableViewValues(tblSchedule);

				        }
				    }
				);
			}
			else {
				tblSchedule.getItems().clear();
				tblSchedule.setDisable(true);
				btnUpdateSchedule.setDisable(true);
			}
			Callback<ListView<String>, ListCell<String>> cellFactory = param->

			new ListCell<String>() {
				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (!empty) {
						setText(item);
						if(item.contains("You: " )){
							setStyle("-fx-background-color: lightgrey");
						}
						else {
							setStyle("-fx-background-color: darkgrey");
						}
						setGraphic(null);

					}
				}
			};

			lstMessages.setCellFactory(cellFactory);


			incompleteSurveys = selectedPatient.getincomplete();
			completedSurveys = selectedPatient.getcomplete();
			int avgTime = 0;
			int numSurveys = 0;
			if(completedSurveys != null) {
			Iterator<FirebaseSurvey> completeIter = completedSurveys.values().iterator();

			while(completeIter.hasNext()) {
				FirebaseSurvey entry = completeIter.next();
				if(!entry.getsurveyId().equals(selectedSurvey.getsurveyId()))
					continue;
				long finishTime = entry.gettimeFinished();
				long startTime = entry.gettimeSent();
				avgTime += (finishTime-startTime);
				numSurveys++;
			}
			}
			if(numSurveys == 0)
				avgTime = 0;
			else
				avgTime /= numSurveys;
			int incomplete  = incompleteSurveys == null ? 0 : incompleteSurveys.values().size();
			lblMissed.setText(Integer.toString(incomplete));
			int complete = completedSurveys == null ? 0 : completedSurveys.values().size();
			lblComplete.setText(Integer.toString(complete));
			lblCompTimeThis.setText((avgTime/1000) + " seconds");
			
		});
		
	}
	
	private List<String> getTableViewValues(TableView tableView) {
	    ObservableList<TableColumn> columns = tableView.getColumns();
	    TableColumn wakeCol = columns.get(1);
	    TableColumn sleepCol = columns.get(2);
	    List<String> scheduleVals = new ArrayList<String>();
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
		df.setTimeZone(TimeZone.getDefault());
		df.setLenient(false);
	    for (Object row : tableView.getItems()) {
	    	 String wakeStr = wakeCol.getCellData(row).toString();
	    	 String sleepStr = sleepCol.getCellData(row).toString();
	    	 String scheduleStr = "";
	    	 try {
	    		 scheduleStr = df.parse(wakeStr).getTime() + ":" + df.parse(sleepStr).getTime();
	    		 scheduleVals.add(scheduleStr);
	    	 }
	    	 catch(ParseException e) {
	    		 btnUpdateSchedule.setDisable(true);
	    		 return scheduleVals;
	    	 }
	    }
	    btnUpdateSchedule.setDisable(false);
	    return scheduleVals;
	  }

	@FXML
	public void updateSchedule(Event e) {
		List<String> scheduleVals = getTableViewValues(tblSchedule);
		selectedPatient.setschedule(scheduleVals);
		FirebaseDB.getInstance().updatePatient(selectedPatient);
		Bounds lblBounds = lblUpdated.getBoundsInParent();
		Toast.makeText(Main.getContext().getStage(),localToScreen(lblBounds).getMinX(),localToScreen(lblBounds).getMinY(),lblUpdated.getWidth(), "Schedule updated!",18);
		
	}
	
//	@FXML
//	public void changeUsername(Event e) {
//		TextInputDialog dialog = new TextInputDialog(selectedPatient.getEmail());
//		dialog.setTitle("Change name");
//		dialog.setHeaderText("Please enter a new name for this user");
//		dialog.setContentText("New name:");
//
//		Optional<String> result = dialog.showAndWait();
//		// The Java 8 way to get the response value (with lambda expression).
//		result.ifPresent(name ->{
//			System.out.println("Your name: " + name);
//			try {
//				privateKey = getPrivate(FirebaseDB.getInstance().getProjectToken());
//				String personalInfo = decryptText(selectedPatient.getuserinfo(),privateKey);
//				String newInfo = personalInfo.replaceFirst("[^;]*", name);
//				System.out.println("persona linfo was " + personalInfo + " and is now " + newInfo);
//				String encryptedInfo = encryptText(newInfo,getPublic(FirebaseDB.getInstance().getOpenProject().getpubKey()));
//				selectedPatient.setuserinfo(encryptedInfo);
//			} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e1) {
//				e1.printStackTrace();
//			}
//
//			FirebaseDB.getInstance().updatePatient(selectedPatient);
//
//		});
//	}
	
	@FXML
	public void deleteUser(Event e) {
		Alert alert = new Alert(AlertType.CONFIRMATION, "Delete user " + selectedPatient.getEmail() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES) {
		    FirebaseDB.getInstance().deletePatient(selectedPatient);
		}
	}
	@FXML
	public void sendMessage(Event e){
		String messageText = txtPatientMessage.getText();
		FirebaseDB.getInstance().sendPatientFeedback(lstPatients.getSelectionModel().getSelectedItem(), messageText);
		txtPatientMessage.clear();
	}
	
	@FXML
	public void sendToAll(Event e){
		String messageText = txtAllMessage.getText();
		for(FirebasePatient patient : allowedPatients) {
			FirebaseDB.getInstance().sendPatientFeedback(patient, messageText);
		}
		txtAllMessage.clear();
	}
}
