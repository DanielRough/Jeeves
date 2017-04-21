package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.DateReceiver;
import com.jeeves.vpl.canvas.receivers.TimeReceiver;
import com.jeeves.vpl.firebase.FirebaseTrigger;
/**
 * This class represents a clock trigger that can be dragged around on the
 * canvas and have its fields edited
 *
 * @author Daniel
 */
public class ClockTriggerInterval extends ClockTrigger { // NO_UCD (use default)
	public static final String NAME = "Interval Contingent";
	public static final String DESC = "Schedule actions to take place at regular intervals";
	@FXML
	private ComboBox<String> cboInterval;
	private String intervalTime = "0";
	private String duration = "seconds";
	@FXML
	private Pane paneIntervalFrom;
	@FXML
	private Pane paneIntervalTo;
	@FXML
	private Pane pane;
	private long dateFrom;
	private long dateTo;
	private TimeReceiver timeReceiverFrom;
	private TimeReceiver timeReceiverTo;
	@FXML
	private TextField txtFieldInterval;

	//@FXML protected ImageView imgCalendar;
//	@FXML private Pane paneDate;
	@FXML private Pane paneStartDate;
	@FXML private Pane paneEndDate;
	private DateReceiver dateReceiverFrom;
	private DateReceiver dateReceiverTo;
	
	public Node[] getWidgets() {
		return new Node[] { paneIntervalFrom, paneIntervalTo, txtFieldInterval, cboInterval };
	}

//public ClockTriggerInterval() {
//	this(new FirebaseTrigger());
//}
public void setDateFrom(long dateFrom){
	super.setDateFrom(dateFrom);
	this.dateFrom = dateFrom;
}
public void setDateTo(long dateTo){
	super.setDateTo(dateTo);
	this.dateTo = dateTo;
}
	@SuppressWarnings("rawtypes")
	public void addListeners() {
		super.addListeners();
//		paneDate.setOnMouseClicked(event->{	
//			newDatePane.setParams(dateStage, this, dateFrom, dateTo);
//			Point2D point = getInstance().localToScreen(event.getX(),event.getY());
//			Rectangle2D bounds = Screen.getPrimary().getBounds();
//			double initX = point.getX();
//			double initY = point.getY();
//			if(point.getX() > (bounds.getWidth()-200)){
//				initX = bounds.getWidth()-200;
//			}
//			if(point.getY() > (bounds.getHeight() - 300)){
//				initY = bounds.getHeight() - 300;
//			}
//			dateStage.setX(initX);
//			dateStage.setY(initY);
//			
//			dateStage.showAndWait();		
//			
//		});
		txtFieldInterval.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent arg0) {
				try{
					Long.parseLong(arg0.getCharacter());
				}
				catch(NumberFormatException e){
					arg0.consume();
					return;
				}	
			}
		});
		txtFieldInterval.addEventHandler(KeyEvent.KEY_RELEASED,new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent arg0) {
				if(txtFieldInterval.getText().equals(""))
					return;
				long intervalTriggerTime = Long.parseLong(txtFieldInterval.getText());// * 1000;
				if(duration.equals("hours"))
					intervalTriggerTime *=60;
				params.put(INTERVAL_TRIGGER_TIME, intervalTriggerTime);
				params.put("intervalTime", txtFieldInterval.getText());				
			}
		});

		cboInterval.valueProperty().addListener((ChangeListener<String>) (arg0, arg1, arg2) -> params.put("granularity", arg2));
		timeReceiverFrom.getChildElements().addListener((ListChangeListener<ViewElement>)(listener->params.put(LIMIT_BEFORE_HOUR, timeReceiverFrom.getChildElements().get(0).getModel())));
		timeReceiverTo.getChildElements().addListener((ListChangeListener<ViewElement>)listener->params.put(LIMIT_AFTER_HOUR, timeReceiverTo.getChildElements().get(0).getModel()));
		timeReceiverFrom.getTextField().textProperty().addListener(listen->{params.put(LIMIT_BEFORE_HOUR, timeReceiverFrom.getText());});//;(KeyEvent.KEY_TYPED,event->	{params.put(LIMIT_BEFORE_HOUR, timeReceiverFrom.getText());System.out.println("yes");});
		timeReceiverTo.getTextField().textProperty().addListener(listen->{params.put(LIMIT_AFTER_HOUR, timeReceiverTo.getText());});//System.out.println("indeed");});

	}
//	public ClockTriggerInterval(FirebaseTrigger data) {
//		super(data);
//		if(paneDate.getChildren().isEmpty())
//			paneDate.getChildren().add(new CalendarEveryday());
//		
//		addListeners();
//	}
	
	public void fxmlInit(){
		super.fxmlInit();
		name = NAME;
		description = DESC;
		timeReceiverFrom = new TimeReceiver(VAR_CLOCK);
		timeReceiverTo = new TimeReceiver(VAR_CLOCK);
		dateReceiverFrom = new DateReceiver(VAR_DATE);
		dateReceiverTo = new DateReceiver(VAR_DATE);
		paneStartDate.getChildren().add(dateReceiverFrom);
		paneEndDate.getChildren().add(dateReceiverTo);
		paneIntervalFrom.getChildren().add(timeReceiverFrom);
		paneIntervalTo.getChildren().add(timeReceiverTo);
	//	super.datePane = paneDate;
		styleTextCombo(cboInterval);
		cboInterval.getItems().addAll(DURATIONS); 
		cboInterval.setValue(DURATIONS[0]);
	}
	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
	//	Map<String,Object> params = model.getparams();
		if(!params.isEmpty()){
			if(params.containsKey("granularity"))
				duration = params.get("granularity").toString();
			else
				duration = "minutes";
		if(params.containsKey("intervalTime"))
			intervalTime = params.get("intervalTime").toString();
		//paneDate.getChildren().add(new CalendarEveryday());

		if(params.containsKey(DATE_FROM)){
			dateFrom = ((Long)params.get(DATE_FROM)).intValue();dateTo = ((Long)params.get(DATE_TO)).intValue();
			if(dateFrom != 0 && dateTo != 0){
	//		paneDate.getChildren().clear();
		//	CalendarFromTo calendarpane = new CalendarFromTo();
	//		paneDate.getChildren().add(calendarpane);
		//	calendarpane.setCalDates(LocalDate.ofEpochDay(dateFrom),LocalDate.ofEpochDay(dateTo));
			}
		}
		txtFieldInterval.setText(intervalTime);
		cboInterval.setValue(duration);
		if(params.containsKey(LIMIT_BEFORE_HOUR)){
			String fromdata = params.get(LIMIT_BEFORE_HOUR).toString();
			timeReceiverFrom.setText(fromdata);

		}
		if(params.containsKey(LIMIT_AFTER_HOUR)){
			String todata = params.get(LIMIT_AFTER_HOUR).toString();
			timeReceiverTo.setText(todata);

		}
		}
	//	addListeners();
	}

	@Override
	public String getViewPath() {
		return String.format("/TriggerClockInterval.fxml", this.getClass().getSimpleName());
	}

}
