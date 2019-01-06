package com.jeeves.vpl;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class PatientPane extends Pane {
	final Logger logger = LoggerFactory.getLogger(PatientPane.class);
	private static final String INIT_TIME = "initTime";
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

	@FXML private ListView<String> lstSurveys;
	@FXML private Label lblSent;
	@FXML private Label lblComplete;
	@FXML private Label lblMissed;
	@FXML private Label lblCompliance;
	@FXML private Label lblTimeTriggers;
	@FXML private Label lblSensorTriggers;
	@FXML private Label lblButtonTriggers;
	@FXML private Label lblInitTime;
	@FXML private Label lblCompletionTime;

	@FXML private TextArea txtPatientMessage;
	@FXML private Button btnSendMessage;

	@FXML private TextArea txtAllMessage;
	@FXML private Button btnSendAll;

	@FXML private RadioButton rdioSelPatient;
	@FXML private RadioButton rdioAllPatient;
	private ToggleGroup patientGroup;
	@FXML private RadioButton rdioSelSurvey;
	@FXML private RadioButton rdioAllSurvey;
	private ToggleGroup surveyGroup;
	private PrivateKey privateKey;

	@FXML private ChoiceBox<String> cboChartType;
	@FXML private ChoiceBox<String> cboXAxis;
	@FXML private ChoiceBox<String> cboYAxis;

	public PrivateKey getPrivate(String keystr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Base64.decodeBase64(keystr);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
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

		return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
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
		new ReadOnlyObjectWrapper(((CellDataFeatures<FirebasePatient, String>)p).getValue().getScreenName())

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
						setText(item.getScreenName()));
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

			cboChartType.getItems().addAll("Line","Scatter","Bar","Pie");

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
	public List<FirebaseSurveyEntry> createSurveyList() {
		ArrayList<FirebaseSurveyEntry> surveylist = new ArrayList<>();
		Map<String,Map<String,FirebaseSurveyEntry>> surveydata = Constants.getOpenProject().getSurveyEntries();
		ArrayList<String> allSurveyIds = new ArrayList<>(surveydata.keySet());
		System.out.println("ALL NAMES: " + allSurveyIds.toString());
		Map<String,FirebaseSurvey> surveyIdMap = new HashMap<>();
		for(FirebaseSurvey survey :FirebaseDB.getInstance().getOpenProject().getsurveys()) {
			surveyIdMap.put(survey.gettitle(), survey);
		}
		String surveyId = "";
		if(rdioSelSurvey.isSelected()) {
			String surveyname = lstSurveys.getSelectionModel().getSelectedItem();
			surveyId = surveyIdMap.get(surveyname).getsurveyId();
		}
		for(String id : allSurveyIds) {
			//If we've selected a specific survey, only want the results for that one
			if(!surveyId.equals("") && !surveyId.equals(id))continue;
			Map<String,FirebaseSurveyEntry> data = surveydata.get(id);

			if (data == null || data.isEmpty()) {
				return surveylist;
			}
			Iterator<FirebaseSurveyEntry> iter = data.values().iterator();
			while(iter.hasNext()) {
				FirebaseSurveyEntry newentry = iter.next();
				newentry.setname(id);
				surveylist.add(newentry);
			}
		}
		return surveylist;
	}

	public Sheet doAThing(Workbook wb,Map<String,Sheet> sheets, FirebaseSurveyEntry nextsurvey,CellStyle style) {

		Sheet s = wb.createSheet();
		sheets.put(nextsurvey.getname(), s);
		wb.setSheetName(wb.getSheetIndex(s), nextsurvey.getname());
		Row r = s.createRow(0);
		Cell c = r.createCell(0);
		c.setCellStyle(style);
		c.setCellValue("Completed");
		c = r.createCell(1);
		c.setCellStyle(style);
		c.setCellValue("Patient ID");
		ObservableList<FirebaseSurvey> surveystuff = Constants.getOpenProject().getObservableSurveys();
		//When we download data for all surveys, want to get question text up there
		for(FirebaseSurvey survey : surveystuff) {
			if(survey.gettitle().equals(nextsurvey.getname())) {
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

		List<FirebaseSurveyEntry> surveylist = createSurveyList();
		surveylist.sort((o1, o2) ->
		(int) (o1.getcomplete() - o2.getcomplete())
				);

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

		for (FirebaseSurveyEntry nextsurvey : surveylist) {

			date.setTime(nextsurvey.getcomplete());
			// Do we have a sheet for this particular survey?
			s = sheets.get(nextsurvey.getname());
			if (s == null) {
				s = doAThing(wb,sheets,nextsurvey,style);
			}


			r = s.createRow(s.getLastRowNum() + 1);
			c = r.createCell(0);
			c.setCellStyle(cellStyle);

			c.setCellValue(date);
			c = r.createCell(1);
			c.setCellStyle(cellStyle);
			c.setCellValue(nextsurvey.getuid());
			String decoded = "";
			String encodedanswers = nextsurvey.getencodedAnswers();
			if(nextsurvey.getencodedKey() != null) {
				String encodedkey = nextsurvey.getencodedKey();
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
			fileChooser.setInitialFileName(selectedPatient.getName()+ ".xls");
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel spreadsheet(*.xls)", "*.xls"));

			fileChooser.setTitle("Save Patient Data");

			File file = fileChooser.showSaveDialog(getScene().getWindow());

			if (file != null) {
				if (!file.getName().contains(".")) {
					file = new File(file.getAbsolutePath() + ".xls");
				}
				writeFile(wb, file);
			}
		} catch (Exception ex) {
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
		surveydata.forEach(key->lstSurveys.getItems().add(key.gettitle()));
		lstSurveys.getSelectionModel().selectedItemProperty().addListener(surveyListener);
		FirebaseProject proj = FirebaseDB.getInstance().getOpenProject();
		if (proj == null) {
			return;
		}
		lstSurveys.getSelectionModel().clearAndSelect(0);

	}
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

		list.forEach(patient->{
			if(patient.getCurrentStudy().equals(name))
				try {
					privateKey = getPrivate(FirebaseDB.getInstance().getProjectToken());

					String personalInfo = decryptText(patient.getuserinfo(),privateKey);
					decryptInfo(patient,personalInfo);
					lstPatients.getItems().add(patient);
				} catch (Exception e) {
					logger.error(e.getMessage(),e.fillInStackTrace());
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


	private void decryptInfo(FirebasePatient patient, String personalInfo){
		String[] infoBits = personalInfo.split(";");
		String name = infoBits[0];
		String email = infoBits[1];
		patient.setScreenName(name);
		patient.setEmail(email);

	}

	Map<String,FirebaseSurvey> allIncomplete;
	Map<String,FirebaseSurvey> allComplete;

	private void loadPatientTable(String name) {
		System.out.println("Loading pateinet table");
		Platform.runLater(()->{
			allowedPatients.clear();
			
			FirebaseDB.getInstance().getpatients().forEach(patient -> {
				System.out.println("FOUND A PATIENT");
				if (patient.getCurrentStudy() != null && patient.getCurrentStudy().equals(name)) {
					try {
						System.out.println("CORRECT");
						allowedPatients.add(patient);
						privateKey = getPrivate(FirebaseDB.getInstance().getProjectToken());
						String personalInfo = decryptText(patient.getuserinfo(),privateKey);
						decryptInfo(patient,personalInfo);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e.getMessage(),e.fillInStackTrace());
					}
				}
			}); 

			lstPatients.setItems(allowedPatients); // This is hacky but I'll get
			updatePatient();
		}
				);
		lstPatients.getSelectionModel().selectedItemProperty().addListener(patientListener);
	}

	private void updateSurvey(){
		String surveyname = lstSurveys.getSelectionModel().getSelectedItem();
		Map<String,Map<String,FirebaseSurveyEntry>> surveydata = FirebaseDB.getInstance().getOpenProject().getSurveyEntries();
		Map<String,FirebaseSurveyEntry> data = surveydata.get(surveyname);
		if(data == null){
			lblSent.setText("0");
			lblComplete.setText("");
			lblMissed.setText("");
			lblCompliance.setText("");
			lblInitTime.setText("");
			lblCompletionTime.setText("");
			return;
		}
		Map<String,FirebaseSurveyEntry> completedMap = surveydata.get("completed");
		Map<String,FirebaseSurveyEntry> missedMap = surveydata.get("missed");
		if(completedMap == null)completedMap = new HashMap<>();
		if(missedMap == null)missedMap = new HashMap<>();

		int sentsize = completedMap.size() + missedMap.size();
		int completedsize = completedMap.size();
		int missedsize = missedMap.size();
		lblSent.setText(""+sentsize);
		int initialised = 0; 
		int avgInitTime = 0;
		int avgCompleteTime = 0;

		Iterator iter = completedMap.values().iterator();
		while(iter.hasNext()){
			Map<String,Object> entrydata = (Map<String,Object>)iter.next();
			avgCompleteTime += (long)entrydata.get("complete");
			if(entrydata.containsKey(INIT_TIME) && (long)entrydata.get(INIT_TIME)>0){
				avgInitTime += (long)entrydata.get(INIT_TIME);
				initialised++;
			}
		}

		lblComplete.setText(completedsize+"");
		lblMissed.setText(missedsize+"");
		lblCompliance.setText((100*completedsize/sentsize)+"%");
		if((100*completedsize/sentsize) < 60)
			lblCompliance.setStyle("-fx-text-fill:#a6392e");
		else if((100*completedsize/sentsize) > 90)
			lblCompliance.setStyle("-fx-text-fill:#2fa845");
		if(initialised != 0)
			lblInitTime.setText(avgInitTime/(1000*initialised)+" seconds");
		else
			lblInitTime.setText("N/A");
		if(completedsize != 0)
			lblCompletionTime.setText(avgCompleteTime/(1000*completedsize)+" seconds");
		else
			lblCompletionTime.setText("N/A");
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
			} else {
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
			DateFormat df = new SimpleDateFormat("dd MMM HH:mm");
			df.setTimeZone(TimeZone.getTimeZone("GMT"));

			Iterator<Entry<String, Object>> feeds = orderedFeedback.entrySet().iterator();
			ObservableList<String> items = FXCollections.observableArrayList();
			while (feeds.hasNext()) {
				Entry<String, Object> feed = feeds.next();
				date.setTime(Long.parseLong(feed.getKey()));
				String message = df.format(date) + ":    " + feed.getValue();
				items.add(message);
			}
			lstMessages.setItems(items);

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
							setStyle("-fx-background-color: white");
						}
						setGraphic(null);

					}
				}
			};

			lstMessages.setCellFactory(cellFactory);


			incompleteSurveys = selectedPatient.getincomplete();
			completedSurveys = selectedPatient.getcomplete();
			int incomplete  = incompleteSurveys == null ? 0 : incompleteSurveys.values().size();
			lblMissed.setText(Integer.toString(incomplete));
			int complete = completedSurveys == null ? 0 : completedSurveys.values().size();
			lblComplete.setText(Integer.toString(complete));

			txtName.setText(selectedPatient.getScreenName());
			txtEmail.setText(selectedPatient.getEmail());
			txtPhone.setText(selectedPatient.getPhoneNo());

		});
	}



	@FXML
	public void sendMessage(Event e){
		String messageText = txtPatientMessage.getText();
		FirebaseDB.getInstance().sendPatientFeedback(lstPatients.getSelectionModel().getSelectedItem(), messageText);
		txtPatientMessage.clear();
	}
}
