package com.jeeves.vpl.canvas.triggers;

import com.jeeves.vpl.firebase.FirebaseTrigger;



public abstract class ClockTrigger extends Trigger{ // NO_UCD (use default)
	protected static final String[] DURATIONS = {"minutes","hours","days","weeks"};
	protected static final String LIMIT_BEFORE_HOUR = "limitBeforeHour";
	protected static final String LIMIT_AFTER_HOUR = "limitAfterHour";
	protected static final String NOTIFICATION_MIN_INTERVAL = "notificationMinInterval";
	protected static final String INTERVAL_TRIGGER_TIME = "intervalTriggerTime";
	protected static final String DATE_FROM = "dateFrom";
	protected static final String DATE_TO = "dateTo";
	protected long dateFrom;
	protected long dateTo;
	public ClockTrigger(FirebaseTrigger data) {
		super(data);
	}

	public void setDateFrom(long dateFrom){
		model.getparams().put(DATE_FROM, dateFrom);
		this.dateFrom = dateFrom;
		
	}
	public void setDateTo(long dateTo){
		model.getparams().put(DATE_TO, dateTo);
		this.dateTo = dateTo;
	}
}
