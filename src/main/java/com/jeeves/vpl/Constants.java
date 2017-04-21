package com.jeeves.vpl;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class Constants {
	public static final String VAR_ANY = "Any";
	public static final String VAR_BOOLEAN = "Boolean";
	public static final String VAR_CLOCK = "Time";
	public static final String VAR_DATE = "Date";
	public static final String VAR_NUMERIC = "Numeric";
	public static final String VAR_LOCATION = "Location";
	public static final String VAR_NONE = "None";
	
	public enum ElementType {
		EXPRESSION, ACTION, CTRL_ACTION, TRIGGER, VARIABLE, UIELEMENT, QUESTION;
	}

	
	//Database constants
	public static final String DBNAME = "JeevesData";
	public static final String PATIENTS_COLL = "patients";
	public static final String PROJECTS_COLL = "projects";
	public static final String DB_URL = "https://jeeves-27914.firebaseio.com/";
	public static final String SERVICE_JSON = "/Jeeves-9b9326e90601.json";
	
	//Class names
	public static String[] exprNames = {
		"com.jeeves.vpl.canvas.expressions.AndExpression",
		"com.jeeves.vpl.canvas.expressions.OrExpression",
		"com.jeeves.vpl.canvas.expressions.NotExpression",
		"com.jeeves.vpl.canvas.expressions.EqualsExpression",
		"com.jeeves.vpl.canvas.expressions.GreaterExpression",
		"com.jeeves.vpl.canvas.expressions.LessExpression",
		"com.jeeves.vpl.canvas.expressions.SensorExpression",
		"com.jeeves.vpl.canvas.expressions.TimeExpression",
		"com.jeeves.vpl.canvas.ifsloops.IfControl"
};
	public static String[] actionNames = {
		"com.jeeves.vpl.canvas.actions.PromptAction",
		"com.jeeves.vpl.canvas.actions.SendTextAction",
		"com.jeeves.vpl.canvas.actions.SpeakerAction",
		"com.jeeves.vpl.canvas.actions.SurveyAction",
		"com.jeeves.vpl.canvas.actions.UpdateAction",
		"com.jeeves.vpl.canvas.actions.WaitingAction",
};
	public static final String[] triggerNames = {
		"com.jeeves.vpl.canvas.triggers.BeginTrigger",
		"com.jeeves.vpl.canvas.triggers.ButtonTrigger",
		"com.jeeves.vpl.canvas.triggers.ClockTriggerInterval",
		"com.jeeves.vpl.canvas.triggers.ClockTriggerRandom",
		"com.jeeves.vpl.canvas.triggers.ClockTriggerSetTimes",
		"com.jeeves.vpl.canvas.triggers.SensorTrigger",
		"com.jeeves.vpl.canvas.triggers.SurveyTrigger"
};
	public static final String[] questionNames = {
		"com.jeeves.vpl.survey.questions.QuestionDateTime",
		"com.jeeves.vpl.survey.questions.QuestionLikert",
		"com.jeeves.vpl.survey.questions.QuestionLocation",
		"com.jeeves.vpl.survey.questions.QuestionMultMany",
		"com.jeeves.vpl.survey.questions.QuestionMultSingle",
		"com.jeeves.vpl.survey.questions.QuestionNumber",
		"com.jeeves.vpl.survey.questions.QuestionText",
		"com.jeeves.vpl.survey.questions.QuestionTrueFalse"
		
};
	public static String[] uiElements = {
		"com.jeeves.vpl.canvas.uielements.UIButton",
		"com.jeeves.vpl.canvas.uielements.UILabel"
};
	
	//Sensor constants
	private static Sensor accelSensor = new Sensor("Accelerometer","/img/icons/accelerometer.png",new String[]{"Stopped","Started","Moving","Stationary"});
	private static Sensor locSensor = new Sensor("Location","/img/icons/location.jpg",new String[]{});
	private static Sensor smsSensor = new Sensor("SMS","/img/icons/sms.jpg",new String[]{"Message Sent","Message Received"});
	
	public static class Sensor{
		
		public Sensor(String name, String image, String[] values){
			this.name = name;
			this.image = image;
			this.values = values;
		}
		public String getimage() {
			return image;
		}
		public String getname(){
			return name;
		}
		public String[] getvalues() {
			return values;
		}
		private String name;
		private String image;
		private String[] values;
	}
	public static final Sensor[] sensors = {accelSensor,locSensor,smsSensor};

	//Clock trigger constants
	public static final String[] DURATIONS = {"minutes","hours","days","weeks"};
	public static final String LIMIT_BEFORE_HOUR = "limitBeforeHour";
	public static final String LIMIT_AFTER_HOUR = "limitAfterHour";
	public static final String NOTIFICATION_MIN_INTERVAL = "notificationMinInterval";
	public static final String INTERVAL_TRIGGER_TIME = "intervalTriggerTime";
	public static final String DATE_FROM = "dateFrom";
	public static final String DATE_TO = "dateTo";
	
	//Question type constants;
	public static final int NUMBER_OF_TYPES = 8;
	public static final int MULT_MANY = 3;
	public static final int MULT_SINGLE = 2;
	public static final int OPEN_ENDED = 1;
	public static final int SCALE = 4;
	public static final int DATETIME = 5;
	public static final int GEO = 6;
	public static final int BOOLEAN = 7;
	public static final int NUMERIC = 8;
	
	//Finally, a static method to style the combo boxes
	public static void styleTextCombo(ComboBox<String> combo){
		combo.getStyleClass().addAll("shadowy","styled-select");
		combo.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override public ListCell<String> call(ListView<String> param) {
                final ListCell<String> cell = new ListCell<String>() {
                    {
                       super.getStyleClass().add("trigger");
                    }    
                    @Override public void updateItem(String item, 
                        boolean empty) {
                    		super.updateItem(item, empty);
                    		setText(item);
                            getStyleClass().add("mycell");
                        }
            };
            return cell;
        }
		});
	}
}
