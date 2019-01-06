package com.jeeves.vpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseProject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;

public class Constants {

	public static FirebaseProject getOpenProject() {
		return FirebaseDB.getInstance().getOpenProject();
	}

	public static final int OBJID_LENGTH = 18;
	public static final int PROJID_LENGTH = 5;
	
	//When we first load up the triggers from file, we don't want to update the trigger IDs
	private static boolean shouldUpdateTriggers = true;
	
	public static void setUpdateTriggers(boolean update) {
		shouldUpdateTriggers = update;
	}
	public static boolean shouldUpdateTriggers() {
		return shouldUpdateTriggers;
	}
	public enum ElementType {
		ACTION, CTRL_ACTION, EXPRESSION, QUESTION, TRIGGER, UIELEMENT, VARIABLE;
	}


	public static final String TITLE = "Jeeves";
	public static final String GLOW_CLASS = "drop_shadow";
	public static final String SELECTED = "selected";
	public static final String SENSOR = "selectedSensor";
	
	public static final String REG_ERROR = "Registration Error";
	//Question types (these are simple ints rather than enums for simplicity in uploading to Firebase)
	public static final String OPEN_ENDED = "OPEN_ENDED";
	public static final String MULT_SINGLE = "MULT_SINGLE";
	public static final String MULT_MANY = "MULT_MANY"; 
	public static final String SCALE = "SCALE";
	public static final String DATE = "DATE"; 
	public static final String GEO = "GEO"; 
	public static final String BOOLEAN = "BOOLEAN";
	public static final String NUMERIC = "NUMERIC";
	public static final String TIME = "TIME"; 
	public static final String IMAGEPRESENT = "IMAGEPRESENT";
	public static final String TEXTPRESENT = "TEXTPRESENT";
	public static final String HEART = "HEART";
	public static final String AUDIO = "AUDIO";
	
	public static final String USER_BOOLEAN = "True/False";
	public static final String USER_NUMERIC = "Number";
	
	//Variable types (these are Strings because they also refer to class names in Styles.css)
	public static final String VAR_ANY = "Any";
	public static final String VAR_BOOLEAN = "Boolean";
	public static final String VAR_CLOCK = "Time";
	public static final String VAR_DATE = "Date";
	public static final String VAR_LOCATION = "Location";
	public static final String VAR_CATEGORY = "Category";
	public static final String VAR_NONE = "None";
	public static final String VAR_NUMERIC = "Numeric";
	public static final String VAR_RANDOM = "Random";

	//Some other String constants
	public static final String NEW_PROJ = "New Project";
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
	private static Sensor accelSensor = new Sensor("Activity", "/img/icons/accelerometer.png", new String[] {"Walking", "Running", "Still", "Driving"},true);
	private static Sensor microphoneSensor = new Sensor("Microphone", "/img/icons/microphone.png", new String[] {"Noisy","Quiet"},true);
	private static Sensor smsSensor = new Sensor("SMS", "/img/icons/sms.png", new String[] { "Message Sent", "Message Received" },false);

	public static final Sensor[] sensors = { accelSensor,smsSensor,microphoneSensor };
	private static ObservableMap<String,String[]> categoryOpts = FXCollections.observableHashMap();

	public static ObservableMap<String,String[]> getCategoryOpts(){
		return categoryOpts;
	}
	public static void addCategoryOpt(String name,String[] values) {
		categoryOpts.put(name, values);
	}
	public static final String[][] questionNames = {
			{DATE,"/img/icons/imgdate.png","Select a Date","QuestionDate"},
			{TIME,"/img/icons/imgtime.png","Select a Time","QuestionTime"},
			{SCALE,"/img/icons/imgscale.png","Select from a Likert Scale","QuestionLikert"},
			{GEO,"/img/icons/imggeo.png","Choose a location on a map","QuestionLocation"},
			{MULT_MANY,"/img/icons/imgmany.png","Select multiple options from a list","QuestionMultMany"},
			{MULT_SINGLE,"/img/icons/imgsingle.png","Select one option from a list","QuestionMultSingle"},
			{NUMERIC,"/img/icons/imgnumeric.png","Select a number","QuestionNumber"},
			{OPEN_ENDED,"/img/icons/imgfreetext.png","Enter free text","QuestionText"},
			{BOOLEAN,"/img/icons/imgbool.png","Choose true or false","QuestionTrueFalse"},
			{HEART,"/img/icons/heart.png","Capture user's heart rate","QuestionHeart"},
			{AUDIO,"/img/icons/audio.png","Play audio file to user","QuestionAudio"},
			{IMAGEPRESENT,"/img/icons/camera.png","Present image to user","QuestionImagePresent"},
			{TEXTPRESENT,"/img/icons/textpresent.png","Present text to user","PresentText"}
	};
	public static final Map<String,String> trigNames = Stream.of(new String[][] {
		{"Begin Trigger","BeginTrigger"},
		{"Button Trigger","ButtonTrigger"},
		{"Repeated Time Trigger","ClockTriggerInterval"},
		{"Set Times Trigger","ClockTriggerSetTimes"},
		{"Location Trigger","LocationTrigger"},
		{"Sensor Trigger","SensorTrigger"},
		{"Survey Trigger","SurveyTrigger"}, 
		}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
	public static final Map<String,String> actNames = Stream.of(new String[][] {
		{"Prompt User","PromptAction"},
		{"Send SMS","SendTextAction"}, 
		{"Update User Attribute","UpdateAction"},
		{"Send Survey","SurveyAction"}, 
		{"Sense Data","CaptureDataAction"},
		{"Snooze App","WaitingAction"}, 
		{"If Condition","IfControl"}, 
		{"While Condition","WhileControl"},
		}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
	public static final Map<String,String> exprNames = Stream.of(new String[][] {
		{"Both True","AndExpression"},
		{"Either True","OrExpression"},
		{"Not True","NotExpression"},
		{"Equality","EqualsExpression"},
		{"Greater Than","GreaterExpression"},
		{"Less Than","LessExpression"},
		{VAR_LOCATION,"LocationExpression"},
		{VAR_CATEGORY,"CategoryExpression"},
		{"Date Before/After","DateBeforeAfter"},
		{"Survey Result","SurveyExpression"},
		{"Time Bounds","TimeExpression"},
		{"Date Bounds","DateExpression"}
	}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
	
	public static final Map<String,String> elemNames = Stream.of(new String[][] {
		{"button","UIButton"},
		{"label","UILabel"}, 
		}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
	

	public static final String[] CHILD_COLOURS = new String[] { "lightcyan", "pink", "lemonchiffon", "palegreen",
			"lavender", "sandybrown", "white" };

	private static List<Integer> constraintNums = new ArrayList<>();

	public static List<Integer> getConstraintNums(){
		return constraintNums;
	}
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
	public static final String DATE_FROM = "dateFrom";
	public static final String DATE_TO = "dateTo";

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
	public static EventHandler<KeyEvent> numberHandler = arg0 ->{
			if (arg0.getEventType().equals(KeyEvent.KEY_TYPED))
				try {
					Long.parseLong(arg0.getCharacter());
				} catch (NumberFormatException e) {
					arg0.consume();
				}
	};


	public static String getSalt(int length) {
		String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < length) {
			int index = (int) (rnd.nextFloat() * saltChars.length());
			salt.append(saltChars.charAt(index));
		}
		return salt.toString();
	}
	//Used for generating an ID in various elements
	public static String getSaltString() {
		return getSalt(OBJID_LENGTH);
	}

	//Generate a smaller project ID (easier to remember)
	public static String generateProjectID() {
		return getSalt(PROJID_LENGTH);
	}
}
