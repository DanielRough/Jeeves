package com.jeeves.vpl.survey.questions;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.canvas.triggers.Trigger;
import com.jeeves.vpl.firebase.FirebaseSurvey;
import com.jeeves.vpl.firebase.FirebaseTrigger;

/**
 * This class represents a clock trigger that can be dragged around on the
 * canvas and have its fields edited
 *
 * @author Daniel
 */
public class SurveyOrganiser extends Trigger { // NO_UCD (use default)
	

	public SurveyOrganiser(String name) {
		this(new FirebaseTrigger(name));
	}

	public SurveyOrganiser(FirebaseTrigger data) {
		super(data);
		}

	@Override
	public void addListeners() {
		super.addListeners();
	}


	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		
	}
}