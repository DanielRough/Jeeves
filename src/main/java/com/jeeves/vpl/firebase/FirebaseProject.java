package com.jeeves.vpl.firebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.IgnoreExtraProperties;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.triggers.Trigger;
import com.jeeves.vpl.canvas.uielements.UIElement;
import com.jeeves.vpl.survey.Survey;

/**
 * Created by Daniel on 28/04/2016.
 */
@IgnoreExtraProperties
public class FirebaseProject implements Serializable{

    public String getdescription() {
        return description;
    }


    public String getname() {
        return name;
    }

    public void setname(String name){
    	this.name = name;
    }
    public String gettype() {
        return type;
    }
    public void add(ViewElement elem){
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

    
    public void remove(ViewElement elem){
		FirebaseElement model = elem.getModel();
		if (elem instanceof Trigger)
			gettriggers().remove((FirebaseTrigger) model);
		else if (elem instanceof Expression)
			getexpressions().remove((FirebaseExpression) model);
		else if (elem instanceof UIElement)
			getuidesign().remove((FirebaseUI) model);
		else if (elem instanceof UserVariable)
			getvariables().remove((FirebaseVariable) model);
		else if (elem instanceof Survey)
			getsurveys().remove((FirebaseSurvey) model);
    }
    public String getresearcherno() { return researcherno; }
    public void setresearcherno(String researcherno){ this.researcherno = researcherno;}
    private String description;
    String name;
    private List<FirebaseSurvey> surveys = new ArrayList<>();
    private List<FirebaseTrigger> triggers = new ArrayList<>();
    private List<FirebaseUI> uidesign = new ArrayList<>();
    String type;
    private List<FirebaseVariable> variables = new ArrayList<>();
    private List<FirebaseExpression> expressions = new ArrayList<>();

    String researcherno;
    long maxNotifications;

    public FirebaseProject() {
        // empty default constructor, necessary for Firebase to be able to deserialize blog posts
    }
    public List<FirebaseSurvey> getsurveys() {
        return surveys;
    }
    public List<FirebaseTrigger> gettriggers() {
        return triggers;
    }
    public List<FirebaseVariable> getvariables() { return variables; }
    public List<FirebaseUI> getuidesign(){ return uidesign; }
    public List<FirebaseExpression> getexpressions() { return expressions; }
    public long getmaxNotifications(){ return maxNotifications;}
}
