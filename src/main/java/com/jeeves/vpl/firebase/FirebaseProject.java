package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.controlsfx.control.PropertySheet.Item;

import com.google.firebase.database.IgnoreExtraProperties;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.triggers.Trigger;
import com.jeeves.vpl.canvas.uielements.UIElement;
import com.jeeves.vpl.survey.Survey;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Created by Daniel on 28/04/2016.
 */

@SuppressWarnings({ "serial", "rawtypes" })
@IgnoreExtraProperties
public class FirebaseProject implements Serializable {

	private String description;
	private List<FirebaseExpression> expressions = new ArrayList<>();
	private long maxNotifications;
	private String name;
	private String researcherno;
	private List<FirebaseSurvey> surveys = new ArrayList<>();
	private List<FirebaseTrigger> triggers = new ArrayList<>();
	private Map<String,Map<String,FirebaseSurveyEntry>> surveydata = new HashMap<String,Map<String,FirebaseSurveyEntry>>();
	private String type;
	private List<FirebaseUI> uidesign = new ArrayList<>();
	private List<FirebaseVariable> variables = new ArrayList<>();
	private List<String> sensors = new ArrayList<>();
	private String id;
	private boolean active;
	private boolean isPublic;
	private long lastUpdated;
	private String pubKey;
	
	private ObservableMap<String,Map<String,FirebaseSurveyEntry>> surveydataobservable = FXCollections.observableHashMap();

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
	public void setid(String id){
		this.id = id;
	}
	public String getid(){
		return id;
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


	public void add(ViewElement elem) {
		FirebaseElement model = elem.getModel();
		if (elem instanceof Trigger)
			gettriggers().add((FirebaseTrigger) model);
		else if (elem instanceof Expression)
			getexpressions().add((FirebaseExpression) model);
		else if (elem instanceof UIElement)
			getuidesign().add((FirebaseUI) model);
		else if (elem instanceof UserVariable)
			getvariables().add((FirebaseVariable) model);
		else if (elem instanceof Survey)
			getsurveys().add((FirebaseSurvey) model);
	}

	public String getdescription() {
		return description;
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

	public Map<String,Map<String,FirebaseSurveyEntry>> getsurveydata(){
		return surveydata;	
	}
	
	public ObservableMap<String,Map<String,FirebaseSurveyEntry>> getObservableSurveyData(){
		return surveydataobservable;
	}
	public void registerSurveyDataListener(){	}
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

	public String gettype() {
		return type;
	}

	public List<FirebaseUI> getuidesign() {
		return uidesign;
	}

	public List<FirebaseVariable> getvariables() {
		return variables;
	}

	public void remove(ViewElement elem) {
		FirebaseElement model = elem.getModel();
		if (elem instanceof Trigger)
			gettriggers().remove(model);
		else if (elem instanceof Expression)
			getexpressions().remove(model);
		else if (elem instanceof UIElement)
			getuidesign().remove(model);
		else if (elem instanceof UserVariable)
			getvariables().remove(model);
		else if (elem instanceof Survey)
			getsurveys().remove(model);
	}

	public void setname(String name) {
		this.name = name;
	}

	public void setresearcherno(String researcherno) {
		this.researcherno = researcherno;
	}
	
}
