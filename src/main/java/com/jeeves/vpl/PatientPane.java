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
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.prefs.Preferences;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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

import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebasePatient;
import com.jeeves.vpl.firebase.FirebaseProject;
import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.firebase.FirebaseSurvey;
import com.jeeves.vpl.firebase.FirebaseSurveyEntry;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
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

	private ObservableList<FirebasePatient> allowedPatients = FXCollections.observableArrayList();
	private Map<String, FirebaseSurvey> completedSurveys;

	private Map<String,FirebaseSurveyEntry> selectedSurveyData;
	private Main gui;
	private Map<String, FirebaseSurvey> incompleteSurveys;
//	@FXML private Label lblPatientCompleted;
//	@FXML private Label lblPatientMissed;
	@FXML private ListView<String> lstMessages;
	@FXML private ListView<FirebasePatient> lstPatients;
//	@FXML private TableView<FirebasePatient> tblPatients;
	@FXML private TextField txtEmail;
	@FXML private TextField txtName;
	@FXML private TextField txtPhone;
	private ChangeListener<FirebasePatient> patientListener;
	private ChangeListener<String> surveyListener;
	private int selectedIndex = 0;
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

	public PrivateKey getPrivate(String keystr) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(keystr);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	public Cipher cipher;

	public String decryptText(String msg, PrivateKey key)
			throws InvalidKeyException, UnsupportedEncodingException,
			IllegalBlockSizeException, BadPaddingException {
		this.cipher.init(Cipher.DECRYPT_MODE, key);
		System.out.println("Message weas " + msg);
		return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PatientPane(Main gui) {
		this.gui = gui;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		patientListener = new ChangeListener<FirebasePatient>() {
			@Override
			public void changed(ObservableValue<? extends FirebasePatient> observable, FirebasePatient oldValue,
					FirebasePatient newValue) {
				updatePatient();
			}
		};
		surveyListener = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				updateSurvey();
			}
		};
		try {
			this.cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		URL location = this.getClass().getResource("/PatientPane.fxml");
		
		TableColumn nameCol = new TableColumn("Name");
		nameCol.setCellValueFactory(
				new Callback<CellDataFeatures<FirebasePatient, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<FirebasePatient, String> p) {
						return new ReadOnlyObjectWrapper(p.getValue().getScreenName());
					}
				});
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
			 lstPatients.setCellFactory(new Callback<ListView<FirebasePatient>, 
			            ListCell<FirebasePatient>>() {
			                @Override 
			                public ListCell<FirebasePatient> call(ListView<FirebasePatient> list) {
			                    return new ListCell<FirebasePatient>(){
			                        @Override
			                        public void updateItem(FirebasePatient item, boolean empty) {
			                            super.updateItem(item, empty);
			                            if(item != null)
			                            setText(item.getScreenName());
			                        }
			                    };
			                }
			            }
			        );
		//	tblPatients.getColumns().clear();
		//	tblPatients.getColumns().addAll(nameCol);
		//	tblPatients.setPlaceholder(new Label("No patients currently assigned to this study"));
			 surveyGroup = new ToggleGroup();
			 patientGroup = new ToggleGroup();
			 rdioSelSurvey.setToggleGroup(surveyGroup);
			 rdioAllSurvey.setToggleGroup(surveyGroup);
			 rdioSelPatient.setToggleGroup(patientGroup);
			 rdioAllPatient.setToggleGroup(patientGroup);
			 
			 cboChartType.getItems().addAll("Line","Scatter","Bar","Pie");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void downloadData(Event e){
		if(rdioSelPatient.isSelected()){
			this.downloadPatient(e);
		}
		else// if(rdioSelSurvey.isSelected()) {
			this.downloadSurvey(e);
		//}
	}
	@FXML
	public void downloadSurvey(Event e){
		int answerlength = 0;

		boolean newsheet = false;
		Date date = new Date();
		FileChooser fileChooser = new FileChooser();
		String surveyname = "";

		fileChooser.setInitialFileName(lstSurveys.getSelectionModel().getSelectedItem()+ ".xls");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel spreadsheet(*.xls)", "*.xls"));

		fileChooser.setTitle("Save Survey Data");
		Workbook wb = new HSSFWorkbook();
		Row r = null;
		Cell c = null;

		File file = fileChooser.showSaveDialog(getScene().getWindow());

		if (file != null) {
			if (!file.getName().contains(".")) {
				file = new File(file.getAbsolutePath() + ".xls");
			}
			try {
			
				FileOutputStream fileOut = new FileOutputStream(file);
				HashMap<String, Sheet> sheets = new HashMap<String, Sheet>();
				Map<String,Map<String,FirebaseSurveyEntry>> surveydata = gui.getSurveyEntries();
				ArrayList<String> allSurveyNames = new ArrayList<String>(surveydata.keySet());
				ArrayList<FirebaseSurveyEntry> surveylist = new ArrayList<FirebaseSurveyEntry>();

				if(rdioSelSurvey.isSelected())
					surveyname = lstSurveys.getSelectionModel().getSelectedItem();
				for(String name : allSurveyNames) {
					//If we've selected a specific survey, only want the results for that one
					if(!surveyname.equals("") && !surveyname.equals(name))continue;
				Map<String,FirebaseSurveyEntry> data = (Map<String,FirebaseSurveyEntry>)surveydata.get(name);

				if (data == null || data.isEmpty()) {
					wb.close();
					fileOut.close();

					return;
				}
				Iterator<FirebaseSurveyEntry> iter = data.values().iterator();
				while(iter.hasNext()) {
					FirebaseSurveyEntry newentry = (FirebaseSurveyEntry)iter.next();
					newentry.setname(name);
					surveylist.add(newentry);
				}
				}
				surveylist.sort(new Comparator<FirebaseSurveyEntry>() {

					@Override
					public int compare(FirebaseSurveyEntry o1, FirebaseSurveyEntry o2) {
						return (int) (o1.getcomplete() - o2.getcomplete());
					}
				});

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
						newsheet = true;
						s = wb.createSheet();
						sheets.put(nextsurvey.getname(), s);
						wb.setSheetName(wb.getSheetIndex(s), nextsurvey.getname());
						r = s.createRow(0);
						c = r.createCell(0);
						c.setCellStyle(style);
						c.setCellValue("Completed");
						c = r.createCell(1);
						c.setCellStyle(style);
						c.setCellValue("Patient ID");
					}
					r = s.createRow(s.getLastRowNum() + 1);
					c = r.createCell(0);
					c.setCellStyle(cellStyle);

					c.setCellValue(date);
					c = r.createCell(1);
					c.setCellStyle(cellStyle);
					c.setCellValue(nextsurvey.getuid());

					String encodedanswers = nextsurvey.getencodedAnswers();
					String decoded = decryptText(encodedanswers, privateKey);
					String[] answers = decoded.split(";");
					int answercounter = 2;
					for (String answer : answers) {

						c = r.createCell(answercounter);
						answercounter++;

						c.setCellValue(answer);
					}
					if(newsheet){
						s.setColumnWidth(0, 20*256);
						for (int i = 1; i < answerlength; i++){
							s.autoSizeColumn(i);
						}
						newsheet = false;
					}
				}
				wb.write(fileOut);
				fileOut.close();
				Desktop.getDesktop().open(file);
				wb.close();

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	} 

	@FXML
	public void downloadPatient(Event e) {
		int answerlength = 0;
		boolean newsheet = false;
		Date date = new Date();
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialFileName(selectedPatient.getName()+ ".xls");
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel spreadsheet(*.xls)", "*.xls"));

			fileChooser.setTitle("Save Patient Data");
			Workbook wb = new HSSFWorkbook();
			Row r = null;
			Cell c = null;

			File file = fileChooser.showSaveDialog(getScene().getWindow());

			if (file != null) {
				if (!file.getName().contains(".")) {
					file = new File(file.getAbsolutePath() + ".xls");
				}
				try {
					if (completedSurveys == null || completedSurveys.isEmpty()) {
						wb.close();
						return;
					}
					FileOutputStream fileOut = new FileOutputStream(file);
					HashMap<String, Sheet> sheets = new HashMap<String, Sheet>();
					//		HashMap<String, String> surveyids = new HashMap<String,String>();
					String lastSurveyId = "";
					// Is this horrendously convoluted? Perhaps. Hopefully it
					// won't slow things down
					Collection<FirebaseSurvey> surveys = completedSurveys.values();
					ArrayList<FirebaseSurvey> surveylist = new ArrayList<FirebaseSurvey>(surveys);
					surveylist.sort(new Comparator<FirebaseSurvey>() {

						@Override
						public int compare(FirebaseSurvey o1, FirebaseSurvey o2) {
							return (int) (o1.gettimeFinished() - o2.gettimeFinished());
						}
					});

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
					for (FirebaseSurvey nextsurvey : surveylist) {

						date.setTime(nextsurvey.gettimeFinished());
						String surveyname = nextsurvey.gettitle();
						if(rdioSelSurvey.isSelected() && !surveyname.equals(lstSurveys.getSelectionModel().getSelectedItem()))
							continue; //Skip any surveys that don't have the correct name
							// Do we have a sheet for this particular survey?
						s = sheets.get(surveyname);
						if (s == null) {
							newsheet = true;
							s = wb.createSheet();
							sheets.put(surveyname, s);
							lastSurveyId =  nextsurvey.getsurveyId();
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
						//The Survey ID has changed, that means our questions have changed!
						//Need to make a new line detailing the questions
						int newlength = 0;
						
//						if(!nextsurvey.getsurveyId().equals(lastSurveyId)){
//							lastSurveyId = nextsurvey.getsurveyId();
//							r = s.createRow(s.getLastRowNum() + 1);
//							int count =1;
//							c = r.createCell(0);
//							c.setCellStyle(style);
//
//							c.setCellValue("Completed");
//							for(FirebaseQuestion q : nextsurvey.getquestions()){
//								newlength++;
//								c = r.createCell(count);
//								count++;
//								c.setCellValue(q.getquestionText());
//								c.setCellStyle(style);
//
//							}
//							if(newlength > answerlength)
//								answerlength = newlength;
//						}

						r = s.createRow(s.getLastRowNum() + 1);
						c = r.createCell(0);
						c.setCellStyle(cellStyle);

						c.setCellValue(date);
			
						//	s.autoSizeColumn(0);

						String encodedanswers = nextsurvey.getencodedAnswers();
						String decoded = decryptText(encodedanswers, privateKey);
						String[] answers = decoded.split(";");
//						List<String> answers = nextsurvey.getanswers();
						int answercounter = 1;
						for (String answer : answers) {

							c = r.createCell(answercounter);
							answercounter++;

							c.setCellValue(answer);
						}
						if(newsheet){
							s.setColumnWidth(0, 20*256);
							for (int i = 1; i < answerlength; i++){
								s.autoSizeColumn(i);
							}
							newsheet = false;
						}
					}
					//	s.autoSizeColumn(0);

					wb.write(fileOut);
					fileOut.close();
					Desktop.getDesktop().open(file);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				wb.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	public void loadSurveys(){

		List<FirebaseSurvey> surveydata = gui.getSurveys();
		surveydata.forEach(key->lstSurveys.getItems().add(key.gettitle()));
		lstSurveys.getSelectionModel().selectedItemProperty().addListener(surveyListener);
		FirebaseProject proj = FirebaseDB.getOpenProject();
		if (proj == null)
			return;
		proj.getObservableSurveyData().addListener(new MapChangeListener<String,Object>(){

			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change) {
				updateSurvey();
			}
			
		});
		 lstSurveys.getSelectionModel().clearAndSelect(0);

	}
	public void loadPatients() {
		FirebaseProject proj = FirebaseDB.getOpenProject();
		if (proj == null)
			return;
		FirebaseDB.getInstance().getpatients().addListener(new ListChangeListener<FirebasePatient>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FirebasePatient> c) {
				//Keep a reference to the patient we had selected
				FirebasePatient selected = lstPatients.getSelectionModel().getSelectedItem();
				c.next();
				if(c.wasAdded()){
					System.out.println("added length is " + c.getAddedSize());
						addToTable(c.getAddedSubList()); 
						lstPatients.getSelectionModel().select(selected);
						updatePatient();
				}
				else if (c.wasRemoved()){
					System.out.println("removed length is " + c.getRemovedSize());
					removeFromTable(c.getRemoved());
					lstPatients.getSelectionModel().select(selected);
					updatePatient();
				}
			}
		});
		loadPatientTable(FirebaseDB.getOpenProject().getname());
		 lstPatients.getSelectionModel().clearAndSelect(0);
		updatePatient();
	}

	
	/**
	 * Decrypts a patient's personal info and adds it to the table
	 * @param patient
	 */
	private void addToTable(List<? extends FirebasePatient> list){
		Preferences prefs = Preferences.userRoot().node("key");
		String privateKeyStr = prefs.get("privateKey" + FirebaseDB.getOpenProject().getname(), "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ1u6xx6NS0XV07/nWmpx+G45NzduB4Bc3AMbOTbgrOhsTM+3NwxZXiSDHiJgNOZF3gGP92Z1yp1wRbB+w75dwVeswdbaEkVwjn3AJPjo2VBzMz7dYHqFY+ZyvW0ML50jGBF8tEvewJg2QarAmNtP9bAWMhpl5Fv8i5AcRgqwvoZAgMBAAECgYBfzp+ADhMMZNb6OW7HXc5JXKbSjo+8mu9wce9W+ws4XB8la40m51y0GlVCiZN/sfvpTAxTxIp/yXd/bP9nJoO6KLx4YSYDeAnou6TEPnt5nNxcTcgxGJ3nUKLeI39PcjycyiWCrzx3c31YJJJbMbLwVKTRiQ5GUMc5Dghuv7nu8QJBANmDr1tyu+XK4CBmSLt98VdsI8HCamLJsPY44d67vTkztNGqTVaArhPuUid5mk7MZ1iIqcrVLU3o+QJ2UMhPzb0CQQC5SdvlMLwty1N1/ZLQfkm8UQ5R9z2mnRL0dXbKK4Kcju8T28aQ4ZZA7yzKEs26LrCd1Rl0L1C8LeyoLnh75V2NAkBtT09Fzr/8uFqwDZcJmj4559+EVRavtJpY8rcX/xMV9xUstMAO87YH0CG7MtJIPVLGXE+v3jfZSnYxNZJdSDWlAkEAtCjvqfLwFirsVP6hAR66PWQm42XeSSHTa2THgx45WlbUed+pO/hMq4ijaTxNUunRCzZIEKNtAfw5bvH4bqd/hQJAQVOoR0toId7iqCuxKl/MxtXuwrSrV5wZPsX0X2nlhuojG2B4He5PFtv+F3fLugFDeV8+DogqSXTqky0gTvKtXQ==");
		FirebaseProject proj = FirebaseDB.getOpenProject();
		if (proj == null)
			return;
		String name = proj.getname();
		//String personalInfo = "";
		
		list.forEach(patient->{
			if(patient.getCurrentStudy().equals(name))
				try {
					privateKey = getPrivate(privateKeyStr);

					String personalInfo = decryptText(patient.getuserinfo(),privateKey);
					decryptInfo(patient,personalInfo);
					lstPatients.getItems().add(patient);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		String phone = infoBits[2];
		patient.setScreenName(name);
		patient.setEmail(email);
		patient.setPhoneNo(phone);
	}

	Map<String,FirebaseSurvey> allIncomplete;
	Map<String,FirebaseSurvey> allComplete;
	
	private void loadPatientTable(String name) {
		Preferences prefs = Preferences.userRoot().node("key");
		String privateKeyStr = prefs.get("privateKey" + FirebaseDB.getOpenProject().getname(), "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ1u6xx6NS0XV07/nWmpx+G45NzduB4Bc3AMbOTbgrOhsTM+3NwxZXiSDHiJgNOZF3gGP92Z1yp1wRbB+w75dwVeswdbaEkVwjn3AJPjo2VBzMz7dYHqFY+ZyvW0ML50jGBF8tEvewJg2QarAmNtP9bAWMhpl5Fv8i5AcRgqwvoZAgMBAAECgYBfzp+ADhMMZNb6OW7HXc5JXKbSjo+8mu9wce9W+ws4XB8la40m51y0GlVCiZN/sfvpTAxTxIp/yXd/bP9nJoO6KLx4YSYDeAnou6TEPnt5nNxcTcgxGJ3nUKLeI39PcjycyiWCrzx3c31YJJJbMbLwVKTRiQ5GUMc5Dghuv7nu8QJBANmDr1tyu+XK4CBmSLt98VdsI8HCamLJsPY44d67vTkztNGqTVaArhPuUid5mk7MZ1iIqcrVLU3o+QJ2UMhPzb0CQQC5SdvlMLwty1N1/ZLQfkm8UQ5R9z2mnRL0dXbKK4Kcju8T28aQ4ZZA7yzKEs26LrCd1Rl0L1C8LeyoLnh75V2NAkBtT09Fzr/8uFqwDZcJmj4559+EVRavtJpY8rcX/xMV9xUstMAO87YH0CG7MtJIPVLGXE+v3jfZSnYxNZJdSDWlAkEAtCjvqfLwFirsVP6hAR66PWQm42XeSSHTa2THgx45WlbUed+pO/hMq4ijaTxNUunRCzZIEKNtAfw5bvH4bqd/hQJAQVOoR0toId7iqCuxKl/MxtXuwrSrV5wZPsX0X2nlhuojG2B4He5PFtv+F3fLugFDeV8+DogqSXTqky0gTvKtXQ==");

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				allowedPatients.clear();
				FirebaseDB.getInstance().getpatients().forEach(patient -> {
					System.out.println("THERE ARE CURRENTLY " + FirebaseDB.getInstance().getpatients().size() + " Patients");
					if (patient.getCurrentStudy() != null && patient.getCurrentStudy().equals(name))
					try {
						allowedPatients.add(patient);
						privateKey = getPrivate(privateKeyStr);
						System.out.println("NAME IS " + name);
						String personalInfo = decryptText(patient.getuserinfo(),privateKey);
						decryptInfo(patient,personalInfo);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}); 

				lstPatients.setItems(allowedPatients); // This is hacky but I'll get
				updatePatient();
			}
		});
		lstPatients.getSelectionModel().selectedItemProperty().addListener(patientListener);
	}

	private void updateSurvey(){
//		String surveyname = lstSurveys.getSelectionModel().getSelectedItem();
//		Map<String,Map<String,FirebaseSurveyEntry>> surveydata = FirebaseDB.getOpenProject().getObservableSurveyData();
//		//Oh dear
//		Map<String,FirebaseSurveyEntry> data = (Map<String,FirebaseSurveyEntry>)surveydata.get(surveyname);
//		this.selectedSurveyData = data;
//		if(data == null){
//			lblSent.setText("0");
//			lblComplete.setText("");
//			lblMissed.setText("");
//			lblCompliance.setText("");
//			lblInitTime.setText("");
//			lblCompletionTime.setText("");
//			return;
//		}
//		Map<String,Object> completedMap = data.get("completed");
//		Map<String,Object> missedMap = data.get("missed");
//		if(completedMap == null)completedMap = new HashMap<String,Object>();
//		if(missedMap == null)missedMap = new HashMap<String,Object>();
//		
//		int sentsize = completedMap.size() + missedMap.size();
//		int completedsize = completedMap.size();
//		int missedsize = missedMap.size();
//		lblSent.setText(""+sentsize);
//		int initialised = 0; int avgInitTime = 0;
//		int avgCompleteTime = 0;
//		
//		Iterator iter = completedMap.values().iterator();
//		while(iter.hasNext()){
//			Map<String,Object> entrydata = (Map<String,Object>)iter.next();
//			avgCompleteTime += (long)entrydata.get("complete");
//			if(entrydata.containsKey("initTime") && (long)entrydata.get("initTime")>0){
//				avgInitTime += (long)entrydata.get("initTime");
//				initialised++;
//			}
//		}
//
//		lblComplete.setText(completedsize+"");
//		lblMissed.setText(missedsize+"");
//		lblCompliance.setText((100*completedsize/sentsize)+"%");
//		if((100*completedsize/sentsize) < 60)
//			lblCompliance.setStyle("-fx-text-fill:#a6392e");
//		else if((100*completedsize/sentsize) > 90)
//			lblCompliance.setStyle("-fx-text-fill:#2fa845");
//		if(initialised != 0)
//		lblInitTime.setText(avgInitTime/(1000*initialised)+" seconds");
//		else
//			lblInitTime.setText("N/A");
//		if(completedsize != 0)
//			lblCompletionTime.setText(avgCompleteTime/(1000*completedsize)+" seconds");
//		else
//			lblCompletionTime.setText("N/A");
	}
	/**
	 * This method gets the selected patient in the patient table, and udpates the rest of the GUI with this
	 * patient's information, including decrypted personal information, study compliance, and feedback
	 */
	private void updatePatient() {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				MultipleSelectionModel<FirebasePatient> selectionModel = lstPatients.getSelectionModel();
				if (selectionModel.getSelectedItem() != null) {
					selectedPatient = selectionModel.getSelectedItem();
					selectedIndex = selectionModel.getSelectedIndex();
				} else {
					lstPatients.getSelectionModel().selectedItemProperty().removeListener(patientListener);
					lstPatients.getSelectionModel().clearAndSelect(selectedIndex);
					selectedPatient = selectionModel.getSelectedItem();
					lstPatients.getSelectionModel().selectedItemProperty().addListener(patientListener);
				}
				lstMessages.getItems().clear();
				if (selectedPatient == null)
					return;

				Map<String, Object> feedback = selectedPatient.getfeedback();
				Date date = new Date();
				DateFormat df = new SimpleDateFormat("dd MMM kk:mm");
				df.setTimeZone(TimeZone.getTimeZone("GMT"));

				if (feedback != null) {
					Iterator<Entry<String, Object>> feeds = feedback.entrySet().iterator();
					ObservableList<String> items = FXCollections.observableArrayList();
					while (feeds.hasNext()) {
						Entry<String, Object> feed = feeds.next();
						date.setTime(Long.parseLong(feed.getKey()));
						String message = df.format(date) + ":    " + feed.getValue();
						items.add(message);
					}
					lstMessages.setItems(items);

					Callback<ListView<String>, ListCell<String>> cellFactory = new Callback<ListView<String>, ListCell<String>>() {
						@Override
						public ListCell<String> call(ListView<String> param) {

							return new ListCell<String>() {
								@Override
								public void updateItem(String item, boolean empty) {
									super.updateItem(item, empty);
									if (!empty) {
										setText(item);
										if(item.contains("You: " )){
										//getStyleClass().clear();
										setStyle("-fx-background-color: lightgrey");
										}
										setGraphic(null);
										
									}
								}
							};
						}
					};
					lstMessages.setCellFactory(cellFactory);
					
				}

				incompleteSurveys = selectedPatient.getincomplete();
				completedSurveys = selectedPatient.getcomplete();
				if (incompleteSurveys == null || incompleteSurveys.isEmpty())
					lblMissed.setText("0");

				else {
					int incomplete = 0;
					Iterator<FirebaseSurvey> incompleteIter = incompleteSurveys.values().iterator();
					while (incompleteIter.hasNext()) {
						incomplete++;
						incompleteIter.next(); //You bloody idiot
					}
					lblMissed.setText(Integer.toString(incomplete));
				}

				if (completedSurveys == null || completedSurveys.isEmpty())
					lblComplete.setText("0");
				else
					lblComplete.setText(Integer.toString(completedSurveys.size()));

				txtName.setText(selectedPatient.getScreenName());
				txtEmail.setText(selectedPatient.getEmail());
				txtPhone.setText(selectedPatient.getPhoneNo());
			}
		});
	}



	@FXML
	public void sendMessage(Event e){
		String messageText = txtPatientMessage.getText();
		FirebaseDB.getInstance().sendPatientFeedback(lstPatients.getSelectionModel().getSelectedItem(), messageText);
		txtPatientMessage.clear();
	}
}
