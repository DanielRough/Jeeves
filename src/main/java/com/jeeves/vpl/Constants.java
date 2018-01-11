package com.jeeves.vpl;

import java.util.ArrayList;
import java.util.Random;

import com.jeeves.vpl.firebase.FirebaseProject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

public class Constants {
	
	public static FirebaseProject openProject;
	
	public static void setOpenProject(FirebaseProject proj){
		openProject = proj;
	}
	//When we first load up the triggers from file, we don't want to update the trigger IDs
	public static boolean SHOULD_UPDATE_TRIGGERS = true;
	public static enum ElementType {
		ACTION, CTRL_ACTION, EXPRESSION, QUESTION, TRIGGER, UIELEMENT, VARIABLE;
	}
	
	//Question types (these are simple ints rather than enums for simplicity in uploading to Firebase)
	public static final int OPEN_ENDED = 1;
	public static final int MULT_SINGLE = 2;
	public static final int MULT_MANY = 3; 
	public static final int SCALE = 4;
	public static final int DATE = 5; 
	public static final int GEO = 6; 
	public static final int BOOLEAN = 7;
	public static final int NUMERIC = 8;
	public static final int TIME = 9; 
	public static final int WIFI = 10;
	public static final int BLUETOOTH = 11;
	public static final int IMAGEPRESENT = 12;
	public static final int TEXTPRESENT = 13;
	//Variable types (these are Strings because they also refer to class names in Styles.css)
	public static final String VAR_ANY = "Any";
	public static final String VAR_BOOLEAN = "Boolean";
	public static final String VAR_CLOCK = "Time";
	public static final String VAR_DATE = "Date";
	public static final String VAR_LOCATION = "Location";
	public static final String VAR_WIFI = "WiFi";
	public static final String VAR_CATEGORY = "Category";
	public static final String VAR_BLUETOOTH = "Bluetooth";
	public static final String VAR_NONE = "None";
	public static final String VAR_NUMERIC = "Numeric";
	public static class Sensor {

		private String image;
		private String name;
		private String[] values;
		private boolean isPull;

		public Sensor(String name, String image, String[] values, boolean isPull) {
			this.name = name;
			this.image = image;
			this.values = values;
			this.isPull = isPull;
		}
		

		
		public boolean isPull(){
			return isPull;
		}
		public String getimage() {
			return image;
		}
		public String getname() {
			return name;
		}
		public String[] getvalues() {
			return values;
		}
	}
	// Sensor constants
		private static Sensor accelSensor = new Sensor("Activity", "/img/icons/accelerometer.png",
				new String[] {"Walking", "Running", "Still", "Driving"},true);
		//LOCATIONS ARE TREATED IN A SEPARATE TRIGGER NOW
		//public static Sensor locSensor = new Sensor(VAR_LOCATION, "/img/icons/location.png", new String[] {},true);
		public static Sensor bluetoothSensor = new Sensor(VAR_BLUETOOTH, "/img/icons/bluetooth.png", new String[] {},true);
		public static Sensor wifiSensor = new Sensor(VAR_WIFI, "/img/icons/wifi.png", new String[] {},true);
		public static Sensor microphoneSensor = new Sensor("Microphone", "/img/icons/microphone.png", new String[] {"Noisy","Quiet"},true);
		private static Sensor smsSensor = new Sensor("SMS", "/img/icons/sms.png",
				new String[] { "Message Sent", "Message Received" },false);
		
		public static final Sensor[] sensors = { accelSensor, /*locSensor, */smsSensor,wifiSensor,bluetoothSensor,microphoneSensor };
		public static ObservableMap<String,String[]> categoryOpts = FXCollections.observableHashMap();

	//Trigger, action, other such names for dynamic loading
	public static String[] actionNames = { "com.jeeves.vpl.canvas.actions.PromptAction",
			"com.jeeves.vpl.canvas.actions.SendTextAction", 
			//"com.jeeves.vpl.canvas.actions.SpeakerAction",
			"com.jeeves.vpl.canvas.actions.SurveyAction",
			/*"com.jeeves.vpl.canvas.actions.AskForDataAction",*/ "com.jeeves.vpl.canvas.actions.CaptureDataAction",
			"com.jeeves.vpl.canvas.actions.WaitingAction", "com.jeeves.vpl.canvas.ifsloops.IfControl"};
	public static String[] exprNames = { "com.jeeves.vpl.canvas.expressions.AndExpression",
			"com.jeeves.vpl.canvas.expressions.OrExpression", "com.jeeves.vpl.canvas.expressions.NotExpression",
			"com.jeeves.vpl.canvas.expressions.EqualsExpression", "com.jeeves.vpl.canvas.expressions.GreaterExpression","com.jeeves.vpl.canvas.expressions.LessExpression","com.jeeves.vpl.canvas.expressions.LocationExpression", "com.jeeves.vpl.canvas.expressions.SensorExpression","com.jeeves.vpl.canvas.expressions.DateBeforeAfter",
			"com.jeeves.vpl.canvas.expressions.TimeExpression","com.jeeves.vpl.canvas.expressions.DateExpression" };
	public static final String[] questionNames = { "com.jeeves.vpl.survey.questions.QuestionDate","com.jeeves.vpl.survey.questions.QuestionTime",
			"com.jeeves.vpl.survey.questions.QuestionLikert", "com.jeeves.vpl.survey.questions.QuestionLocation",
			//"com.jeeves.vpl.survey.questions.QuestionBluetooth",
			//"com.jeeves.vpl.survey.questions.QuestionWifi",
			"com.jeeves.vpl.survey.questions.QuestionMultMany", "com.jeeves.vpl.survey.questions.QuestionMultSingle",
			"com.jeeves.vpl.survey.questions.QuestionNumber", "com.jeeves.vpl.survey.questions.QuestionText",
			"com.jeeves.vpl.survey.questions.QuestionTrueFalse","com.jeeves.vpl.survey.questions.QuestionImagePresent","com.jeeves.vpl.survey.questions.PresentText"
	};
	public static final String[] triggerNames = { "com.jeeves.vpl.canvas.triggers.BeginTrigger",
			"com.jeeves.vpl.canvas.triggers.ButtonTrigger", "com.jeeves.vpl.canvas.triggers.ClockTriggerInterval",
			//"com.jeeves.vpl.canvas.triggers.ClockTriggerRandom", 
			"com.jeeves.vpl.canvas.triggers.ClockTriggerSetTimes","com.jeeves.vpl.canvas.triggers.LocationTrigger",
			"com.jeeves.vpl.canvas.triggers.SensorTrigger", "com.jeeves.vpl.canvas.triggers.SurveyTrigger" };
	public static final String[] uiElementNames = { "com.jeeves.vpl.canvas.uielements.UIButton",
			"com.jeeves.vpl.canvas.uielements.UILabel" };
	
	
	public static final String[] CHILD_COLOURS = new String[] { "lightcyan", "pink", "lemonchiffon", "palegreen",
			"lavender", "sandybrown", "white" };
	
	public static ArrayList<Integer> CONSTRAINT_NUMS = new ArrayList<Integer>();
	public static final String DATE_FROM = "dateFrom";

	public static final String DATE_TO = "dateTo";

	// Database constants
	public static final String DB_URL = "https://jeeves-27914.firebaseio.com/";
	public static final String SERVICE_JSON = "/Jeeves-9b9326e90601.json";
	public static final String CLOUD_JSON = "/Jeeves-290e97745883.json";
	public static final String PRIVATE_COLL = "private";
	public static final String PUBLIC_COLL = "public";
	public static final String PATIENTS_COLL = "patients";
	public static final String PROJECTS_COLL = "projects";
	
	// Clock trigger / time expression constants
	public static final String[] DURATIONS_SHORT = {"minutes","hours"};
	public static final String[] DURATIONS = { "minutes", "hours", "days", "weeks" };
	public static final String INTERVAL_TRIGGER_TIME = "intervalTriggerTime";
	public static final String LIMIT_AFTER_HOUR = "limitAfterHour";
	public static final String LIMIT_BEFORE_HOUR = "limitBeforeHour";
	public static final String INTERVAL_WINDOW = "intervalWindowLength";

	
	//A static method to make Info alerts
	public static void makeInfoAlert(String titleText, String headerText, String infoText){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(titleText);
		alert.setHeaderText(headerText);
		alert.setContentText(infoText);
		alert.showAndWait();
	}
	// A static event handler for ensuring we don't enter non-numeric characters
	// into a numeric text box
	public static EventHandler<KeyEvent> numberHandler = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent arg0) {
			if (arg0.getEventType().equals(KeyEvent.KEY_TYPED))
				try {
					Long.parseLong(arg0.getCharacter());
				} catch (NumberFormatException e) {
					arg0.consume();
					return;
				}
		}
	};

	// Finally, a static method to style the combo boxes
	public static void styleTextCombo(ComboBox<String> combo) {
		combo.getStyleClass().addAll("shadowy", "styled-select");
		combo.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> param) {
				final ListCell<String> cell = new ListCell<String>() {
					{
						super.getStyleClass().add("trigger");
					}

					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						setText(item);
						getStyleClass().add("mycell");
					}
				};
				return cell;
			}
		});
	}
	
	//Used for generating an ID in various elements
	public static String getSaltString() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 18) {
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;

	}
	
	//Generate a smaller project ID (easier to remember)
	public static String generateProjectID() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 5) {
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;

	}
}
