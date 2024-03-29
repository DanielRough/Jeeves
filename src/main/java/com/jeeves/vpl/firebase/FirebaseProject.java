package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.triggers.LocationTrigger;
import com.jeeves.vpl.canvas.triggers.Trigger;
import com.jeeves.vpl.canvas.uielements.UIElement;
import com.jeeves.vpl.survey.Survey;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * Created by Daniel on 28/04/2016.
 */

@SuppressWarnings({ "serial", "rawtypes" })
@IgnoreExtraProperties
public class FirebaseProject implements Serializable {

	private List<FirebaseExpression> expressions = new ArrayList<>();
	private long maxNotifications;
	private String name;
	private String description;
	private String researcherno;
	private List<FirebaseSurvey> surveys = new ArrayList<>();
	private List<FirebaseTrigger> triggers = new ArrayList<>();
	private Map<String,Map<String,FirebaseSurveyEntry>> surveydata = new HashMap<>();
	private List<FirebaseUI> uidesign = new ArrayList<>();
	private List<FirebaseVariable> variables = new ArrayList<>();
	private List<String> sensors = new ArrayList<>();
	private boolean active;
	private boolean isPublic;
	private long lastUpdated;
	private String pubKey;
	private boolean hasSchedule;
	private Map<String,Object> scheduleAttrs;
	private boolean isDebug;
	
	public void setisDebug(boolean debug) {
		this.isDebug= debug;
	}
	
	public boolean getisDebug() {
		return isDebug;
	}
	
	public void sethasSchedule(boolean schedule) {
		this.hasSchedule = schedule;
	}
	public boolean gethasSchedule() {
		return hasSchedule;
	}
	public Map<String,Object> getscheduleAttrs(){
		return scheduleAttrs;
	}
	public void setscheduleAttrs(Map<String,Object> scheduleAttrs) {
		this.scheduleAttrs = scheduleAttrs;
	}
	@Exclude
	public transient ObservableMap<String,Map<String,FirebaseSurveyEntry>> surveydataobservable = FXCollections.observableMap(surveydata);
	@Exclude
	public transient ObservableList<FirebaseSurvey> currentsurveys = FXCollections
	.observableList(surveys);
	@Exclude
	public transient ObservableList<FirebaseVariable> currentvariables = FXCollections
	.observableList(variables);
	@Exclude
	public transient ObservableList<FirebaseUI> currentelements = FXCollections
	.observableList(uidesign);
	@Exclude
	public void registerElementListener(ListChangeListener<FirebaseUI> listener) {
		currentelements.addListener(listener);
	}
	@Exclude
	public void registerSurveyListener(ListChangeListener<FirebaseSurvey> listener) {
		currentsurveys.addListener(listener);
	}
	@Exclude
	public void registerVarListener(ListChangeListener<FirebaseVariable> listener) {
		currentvariables.addListener(listener);
	}
	@Exclude
	public void unregisterVarListener(ListChangeListener<FirebaseVariable> listener) {
		currentvariables.removeListener(listener);
	}
	@Exclude
	public ObservableList<FirebaseSurvey> getObservableSurveys() {
		return currentsurveys;
	}
	@Exclude
	public ObservableList<FirebaseUI> getUIElements() {
		return currentelements;
	}
	@Exclude
	public ObservableMap<String,Map<String,FirebaseSurveyEntry>> getSurveyEntries(){
		return surveydataobservable;
	}
	@Exclude
	public ObservableList<FirebaseVariable> getObservableVariables() {
		return currentvariables;
	}
	
	public void setsensors(List<String> sensors){
		this.sensors = sensors;
	}
	public List<String> getsensors(){
		return sensors;
	}
	public void setpubKey(String pubKey){
		this.pubKey = pubKey;
	}
	public String getpubKey(){
		return pubKey;
	}
	public void setisPublic(boolean isPublic){
		this.isPublic = isPublic;
	}
	public boolean getisPublic(){
		return isPublic;
	}
	public void setactive(boolean active){
		this.active = active;
	}
	public boolean getactive(){
		return active;
	}
	public void setlastUpdated(long lastUpdated){
		this.lastUpdated = lastUpdated;
	}
	public long getlastUpdated(){
		return lastUpdated;
	}
	
	
	public FirebaseProject() {
		// empty default constructor, necessary for Firebase to be able to
		// deserialize blog posts
	}
	public void addStuff() {
		currentvariables = FXCollections
				.observableList(variables);
		currentsurveys = FXCollections
				.observableList(surveys);
		currentelements = FXCollections
				.observableList(uidesign);
		surveydataobservable = FXCollections
				.observableMap(surveydata);
	}

	public void add(ViewElement elem, int pos) {
		FirebaseElement model = elem.getModel();
		if (elem instanceof Trigger) {
			gettriggers().add((FirebaseTrigger) model);
			if(elem instanceof LocationTrigger) {
				sensors.add("Location");
			}
		}
		else if (elem instanceof Expression) {
			getexpressions().add((FirebaseExpression) model);
		}
		else if (elem instanceof UIElement) {
			currentelements.add(pos,(FirebaseUI)model);
		}
		else if (elem instanceof UserVariable) {
			currentvariables.add((FirebaseVariable)model);
		}
		else if (elem instanceof Survey) {
			currentsurveys.add((FirebaseSurvey)model);
		}
	}

	public List<FirebaseExpression> getexpressions() {
		return expressions;
	}

	public long getmaxNotifications() {
		return maxNotifications;
	}

	public String getname() {
		return name;
	}

	public String getresearcherno() {
		return researcherno;
	}
	
	public String getdescription() {
		return description;
	}

	public Map<String,Map<String,FirebaseSurveyEntry>> getsurveydata(){
		return surveydata;	
	}
	
	public void setsurveydata(Map<String,Map<String,FirebaseSurveyEntry>> surveydata){
		this.surveydata = surveydata;
		surveydataobservable.putAll(surveydata);
	}
	
	public List<FirebaseSurvey> getsurveys() {
		return surveys;
	}

	
	public List<FirebaseTrigger> gettriggers() {
		return triggers;
	}

	public List<FirebaseUI> getuidesign() {
		return uidesign;
	}

	public List<FirebaseVariable> getvariables() {
		return variables;
	}

	public void remove(ViewElement elem) {
		FirebaseElement model = elem.getModel();
		if (elem instanceof Trigger) {
			gettriggers().remove(model);
			if(elem instanceof LocationTrigger) {
				sensors.remove("Location");	
			}
		}
		else if (elem instanceof Expression)
			getexpressions().remove(model);
		else if (elem instanceof UIElement)
			getuidesign().remove(model);
		else if (elem instanceof UserVariable)
			getvariables().remove(model);
		else if (elem instanceof Survey)
			currentsurveys.remove(model);
	}

	public void setname(String name) {
		this.name = name;
	}

	public void setresearcherno(String researcherno) {
		this.researcherno = researcherno;
	}
	
}
