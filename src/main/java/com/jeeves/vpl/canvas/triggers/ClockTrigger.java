package com.jeeves.vpl.canvas.triggers;

import java.time.LocalDate;

import com.jeeves.vpl.Main;
import com.jeeves.vpl.canvas.receivers.NewDatePane;
import com.jeeves.vpl.firebase.FirebaseTrigger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Toggle;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



public abstract class ClockTrigger extends Trigger{ // NO_UCD (use default)
	protected static final String[] DURATIONS = {"minutes","hours","days","weeks"};
	protected static final String LIMIT_BEFORE_HOUR = "limitBeforeHour";
	protected static final String LIMIT_AFTER_HOUR = "limitAfterHour";
	protected static final String NOTIFICATION_MIN_INTERVAL = "notificationMinInterval";
	protected static final String INTERVAL_TRIGGER_TIME = "intervalTriggerTime";
	protected static final String DATE_FROM = "dateFrom";
	protected static final String DATE_TO = "dateTo";
	protected Stage dateStage;
//	protected long dateFrom;
//	protected long dateTo;
	protected Pane datePane;
	protected NewDatePane newDatePane;
	public ClockTrigger(FirebaseTrigger data) {
		super(data);
	}

	public void setDateFrom(long dateFrom){
		params.put(DATE_FROM, dateFrom);
	//	this.dateFrom = dateFrom;
		
	}
	public void setDateTo(long dateTo){
		params.put(DATE_TO, dateTo);
	//	this.dateTo = dateTo;
	}
	
	public void addListeners(){
		super.addListeners();
		dateStage = gui.getDateStage(); //Ugly, will change also

		newDatePane = gui.getDatePane(); //Ugly, will change
//		newDatePane.getPckFrom().valueProperty().addListener(new ChangeListener<LocalDate>(){
//
//			@Override
//			public void changed(ObservableValue<? extends LocalDate> arg0,
//					LocalDate arg1, LocalDate arg2) {
//				setDateFrom(arg2.toEpochDay());
//			}
//			
//		});
//		newDatePane.getPckTo().valueProperty().addListener(new ChangeListener<LocalDate>(){
//
//			@Override
//			public void changed(ObservableValue<? extends LocalDate> arg0,
//					LocalDate arg1, LocalDate arg2) {
//				setDateTo(arg2.toEpochDay());
//			}
//			
//		});
//		newDatePane.getToggleGroup().selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
//
//			@Override
//			public void changed(ObservableValue<? extends Toggle> arg0,
//					Toggle arg1, Toggle arg2) {
//				if(arg2.equals(newDatePane.btnEveryday)){
//					setDateFrom(0);
//					setDateTo(0);
//				}
//				else{
//					
//				}
//			}
//			
//		});

	}
}
