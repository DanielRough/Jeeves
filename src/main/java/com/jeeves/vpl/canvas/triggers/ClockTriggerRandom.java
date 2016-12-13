package com.jeeves.vpl.canvas.triggers;

import java.time.LocalDate;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.jeeves.vpl.CalendarEveryday;
import com.jeeves.vpl.CalendarFromTo;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.receivers.NewDatePane;
import com.jeeves.vpl.canvas.receivers.TimeReceiver;
import com.jeeves.vpl.firebase.FirebaseTrigger;

/**
 * This class represents a clock trigger that can be dragged around on the
 * canvas and have its fields edited
 *
 * @author Daniel
 */
public class ClockTriggerRandom extends ClockTrigger { // NO_UCD (use default)

	private String frequencyR = "0";
	private String duration = "seconds";
	@FXML protected Group grpRandom;
	private TimeReceiver timeReceiverFrom;
	private TimeReceiver timeReceiverTo;
	@FXML protected Pane paneRandomFrom;
	@FXML protected Pane paneRandomTo;
	@FXML protected TextField txtFieldRandom;
	@FXML protected ComboBox<String> cboRandom;
//	@FXML protected ImageView imgCalendar;
	@FXML protected Pane paneDate;
	
	public Node[] getWidgets(){
		return new Node[]{paneRandomFrom,paneRandomTo,txtFieldRandom,cboRandom};
	}


	public ClockTriggerRandom() {
		this(new FirebaseTrigger());
	}
	public ClockTriggerRandom(FirebaseTrigger data) {
		super(data);
		name.setValue("RANDOM TRIGGER");
		description = "Execute actions at a random point within time 'windows', between two times";
		if(paneDate.getChildren().isEmpty())
			paneDate.getChildren().add(new CalendarEveryday());
		addListeners();
	}
	
	public void fxmlInit(){
		super.fxmlInit();
		timeReceiverFrom = new TimeReceiver(Expression.VAR_CLOCK);
		timeReceiverTo = new TimeReceiver(Expression.VAR_CLOCK);
		super.datePane = paneDate;

		paneRandomFrom.getChildren().add(timeReceiverFrom);
		paneRandomTo.getChildren().add(timeReceiverTo);
		styleTextCombo(cboRandom);
		cboRandom.getItems().addAll(DURATIONS); //Add seconds, minutes, days, etc
		cboRandom.setValue(DURATIONS[0]);
	}
	public void addListeners() {
		super.addListeners();	
		paneDate.setOnMouseClicked(event->{
			
			newDatePane.setParams(dateStage, paneDate, dateFrom, dateTo);

			Point2D point = getInstance().localToScreen(event.getX(),event.getY());
			Rectangle2D bounds = Screen.getPrimary().getBounds();
			double initX = point.getX();
			double initY = point.getY();
			if(point.getX() > (bounds.getWidth()-200)){
				initX = bounds.getWidth()-200;
			}
			if(point.getY() > (bounds.getHeight() - 300)){
				initY = bounds.getHeight() - 300;
			}
			dateStage.setX(initX);
			dateStage.setY(initY);

			dateStage.showAndWait();
			
		});
		txtFieldRandom.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent arg0) {
				try{
					Long isValid = Long.parseLong(arg0.getCharacter());
				}
				catch(NumberFormatException e){
					arg0.consume();
					return;
				}	
			}
		});
		txtFieldRandom.addEventHandler(KeyEvent.KEY_RELEASED,new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent arg0) {
				if(txtFieldRandom.getText().equals(""))return;
				long intervalTriggerTime = Long.parseLong(txtFieldRandom.getText());// * 1000;
				if(duration.equals("hours"))
					intervalTriggerTime *=60;
				params.put(NOTIFICATION_MIN_INTERVAL, intervalTriggerTime);
				params.put("frequency", txtFieldRandom.getText());				
			}
		});
		cboRandom.valueProperty().addListener(
				(ChangeListener<String>) (arg0, arg1, arg2) -> params.put("granularity", arg2));
		timeReceiverFrom.getChildElements().addListener((ListChangeListener)(listener->params.put(LIMIT_BEFORE_HOUR, timeReceiverFrom.getChildElements().get(0).getModel())));
		timeReceiverTo.getChildElements().addListener((ListChangeListener)listener->params.put(LIMIT_AFTER_HOUR, timeReceiverTo.getChildElements().get(0).getModel()));
		timeReceiverFrom.getTextField().textProperty().addListener(listen->{params.put(LIMIT_BEFORE_HOUR, timeReceiverFrom.getText());});//;(KeyEvent.KEY_TYPED,event->	{params.put(LIMIT_BEFORE_HOUR, timeReceiverFrom.getText());System.out.println("yes");});
		timeReceiverTo.getTextField().textProperty().addListener(listen->{params.put(LIMIT_AFTER_HOUR, timeReceiverTo.getText());});//System.out.println("indeed");});
	}

	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		if(params.containsKey("granularity"))
		duration = params.get("granularity").toString();
		else
			duration = "minutes";
		cboRandom.setValue(duration);
	
		if(params.containsKey("frequency")){
		frequencyR = params.get("frequency").toString();
		txtFieldRandom.setText(frequencyR);
		}
		paneDate.getChildren().add(new CalendarEveryday());
		if(params.containsKey(DATE_FROM)){
			dateFrom =  ((Long)params.get(DATE_FROM)).intValue() ;dateTo = ((Long)params.get(DATE_TO)).intValue();
			if(dateFrom != 0 && dateTo != 0){
			paneDate.getChildren().clear();
			CalendarFromTo calendarpane = new CalendarFromTo();
			paneDate.getChildren().add(calendarpane);
			calendarpane.setCalDates(LocalDate.ofEpochDay(dateFrom),LocalDate.ofEpochDay(dateTo));
			}
			
		}
	if(params.containsKey(LIMIT_BEFORE_HOUR)){
		String fromdata = params.get(LIMIT_BEFORE_HOUR).toString();
		timeReceiverFrom.setText(fromdata);

	}
	if(params.containsKey(LIMIT_AFTER_HOUR)){
		String todata = params.get(LIMIT_AFTER_HOUR).toString();
			timeReceiverTo.setText(todata);
	}
		addListeners();
	}



	@Override
	public String getViewPath() {
		return String.format("/ClockTriggerRandom.fxml", this.getClass().getSimpleName());
	}

}
