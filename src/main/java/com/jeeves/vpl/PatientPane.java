package com.jeeves.vpl;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
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

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class PatientPane extends Pane {

	private ObservableList<FirebasePatient> allowedPatients = FXCollections.observableArrayList();
	private Map<String, FirebaseSurvey> completedSurveys;
	private FirebaseDB firebase;
	@FXML
	private Group grpInfo;
	private Main gui;
	private Map<String, FirebaseSurvey> incompleteSurveys;
	@FXML
	private Label lblCompleted;
	@FXML
	private Label lblMissed;
	private ChangeListener<FirebasePatient> listener;
	@FXML
	private ListView<String> lstMessages;

	private int selectedIndex = 0;
	private FirebasePatient selectedPatient;
	@FXML
	private TableView<FirebasePatient> tblPatients;
	@FXML
	private TextField txtAddress1;
	@FXML
	private TextField txtAddress2;
	@FXML
	private TextField txtAddress3;
	@FXML
	private TextField txtAddress4;
	@FXML
	private TextField txtEmail;
	@FXML
	private TextField txtFname;
	@FXML
	private TextField txtLname;

	@FXML
	private TextField txtPhone;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PatientPane(Main gui, FirebaseDB firebase) {
		this.firebase = firebase;
		this.gui = gui;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		listener = new ChangeListener<FirebasePatient>() {
			@Override
			public void changed(ObservableValue<? extends FirebasePatient> observable, FirebasePatient oldValue,
					FirebasePatient newValue) {
				update();
			}
		};
		URL location = this.getClass().getResource("/PatientPane.fxml");
		TableColumn firstNameCol = new TableColumn("First Name");
		firstNameCol.setCellValueFactory(
				new Callback<CellDataFeatures<FirebasePatient, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<FirebasePatient, String> p) {
						return new ReadOnlyObjectWrapper(p.getValue().getName());
					}
				});
		TableColumn emailCol = new TableColumn("Email");
		emailCol.setCellValueFactory(
				new Callback<CellDataFeatures<FirebasePatient, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<FirebasePatient, String> p) {
						return new ReadOnlyObjectWrapper(p.getValue().getEmail());
					}
				});
		TableColumn phoneCol = new TableColumn("Phone");
		phoneCol.setCellValueFactory(
				new Callback<CellDataFeatures<FirebasePatient, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<FirebasePatient, String> p) {
						// p.getValue() returns the Person instance for a
						// particular
						// TableView row
						return new ReadOnlyObjectWrapper(p.getValue().getPhoneNo());
					}
				});

		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
			tblPatients.getColumns().clear();
			tblPatients.getColumns().addAll(firstNameCol, emailCol, phoneCol);
			tblPatients.setPlaceholder(new Label("No patients currently assigned to this study"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void downloadResults(Event e) {
		int answerlength = 0;
		boolean newsheet = false;
		Date date = new Date();
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialFileName("results.xls");
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel spreadsheet(*.xls)", "*.xls"));

			fileChooser.setTitle("Save Image");
			Workbook wb = new HSSFWorkbook();
			Row r = null;
			Cell c = null;

			File file = fileChooser.showSaveDialog(getScene().getWindow());

			if (file != null) {
				if (!file.getName().contains(".")) {
					file = new File(file.getAbsolutePath() + ".xls");
				}
				try {
					if (completedSurveys.isEmpty()) {
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
						if(!nextsurvey.getsurveyId().equals(lastSurveyId)){
							lastSurveyId = nextsurvey.getsurveyId();
							r = s.createRow(s.getLastRowNum() + 1);
							int count =1;
							c = r.createCell(0);
							c.setCellStyle(style);

							c.setCellValue("Completed");
							for(FirebaseQuestion q : nextsurvey.getquestions()){
								newlength++;
								c = r.createCell(count);
								count++;
								c.setCellValue(q.getquestionText());
								c.setCellStyle(style);

							}
							if(newlength > answerlength)
								answerlength = newlength;
						}
						
						r = s.createRow(s.getLastRowNum() + 1);
						c = r.createCell(0);
						c.setCellStyle(cellStyle);
						
						c.setCellValue(date);
					//	s.autoSizeColumn(0);

						List<String> answers = nextsurvey.getanswers();
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

	// @FXML
	// public void addPatient(Event e){
	// Stage stage = new Stage(StageStyle.UNDECORATED);
	// NewPatientPane root = new NewPatientPane(stage);
	// Scene scene = new Scene(root);
	// stage.setScene(scene);
	// stage.setTitle("Add Patient");
	// stage.initModality(Modality.APPLICATION_MODAL);
	// stage.showAndWait();
	// }

	public void loadPatients() {
		FirebaseProject proj = gui.getCurrentProject();
		if (proj == null)
			return;
		String name = proj.getname();
		firebase.getpatients().addListener(new ListChangeListener<FirebasePatient>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FirebasePatient> c) {
				loadPatientTable(name);

			}
		});
		loadPatientTable(name);
								// back to it k?
		update();
	}

	private void loadPatientTable(String name) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				//loadPatientTable();
				allowedPatients.clear();
				firebase.getpatients().forEach(patient -> {
					if (patient.getCurrentStudy() != null && patient.getCurrentStudy().equals(name))
						allowedPatients.add(patient);

				});
				System.out.println("DOES THIS EVEN FUCKING HAPPEN FUCK FUCK FUCK");
				System.out.println("We have " + allowedPatients.size() + " allowed patientes");
			//	tblPatients.getItems().clear();
				tblPatients.setItems(allowedPatients); // This is hacky but I'll get

				update();

			}
		});
		tblPatients.getSelectionModel().selectedItemProperty().addListener(listener);
	}

	private void update() {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				TableViewSelectionModel<FirebasePatient> selectionModel = tblPatients.getSelectionModel();
				if (selectionModel.getSelectedItem() != null) {
					selectedPatient = selectionModel.getSelectedItem();
					selectedIndex = selectionModel.getSelectedIndex();
				} else {
					tblPatients.getSelectionModel().selectedItemProperty().removeListener(listener);
					tblPatients.getSelectionModel().clearAndSelect(selectedIndex);
					selectedPatient = selectionModel.getSelectedItem();
					tblPatients.getSelectionModel().selectedItemProperty().addListener(listener);
				}
				lstMessages.getItems().clear();
				if (selectedPatient == null)
					return;

				Map<String, Object> feedback = selectedPatient.getfeedback();
				Date date = new Date();
				DateFormat df = new SimpleDateFormat("dd MMM yyyy kk:mm:ss z");
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
					lblCompleted.setText("0");
				else
					lblCompleted.setText(Integer.toString(completedSurveys.size()));

				txtFname.setText(selectedPatient.getName());
				txtEmail.setText(selectedPatient.getEmail());
				txtPhone.setText(selectedPatient.getPhoneNo());

				String address = selectedPatient.getAddress();
				if (address != null) {
					String[] lines = address.split(";");
					if (lines.length > 0)
						txtAddress1.setText(lines[0]);
					if (lines.length > 1)
						txtAddress2.setText(lines[1]);
					if (lines.length > 2)
						txtAddress3.setText(lines[2]);
					if (lines.length > 3)
						txtAddress4.setText(lines[3]);
				}

				grpInfo.setDisable(false);
			}
		});
	}

	@FXML
	private void updateInfo(Event e) {
		selectedPatient.setName(txtFname.getText());
		selectedPatient.setEmail(txtEmail.getText());
		selectedPatient.setPhoneNo(txtPhone.getText());
		String address = txtAddress1.getText() + ";" + txtAddress2.getText() + ";" + txtAddress3.getText() + ";"
				+ txtAddress4.getText();
		selectedPatient.setaddress(address);
		firebase.addPatient(selectedPatient);
	}
}
