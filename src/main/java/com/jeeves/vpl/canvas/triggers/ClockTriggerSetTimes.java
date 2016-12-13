package com.jeeves.vpl.canvas.triggers;

import java.time.LocalDate;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
public class ClockTriggerSetTimes extends ClockTrigger { // NO_UCD (use default)
	@FXML protected Button btnAddTime;
	@FXML protected Button btnRemoveTime;
	@FXML protected VBox paneTimes;
	@FXML protected Pane paneDate;
	private int timeCount;
	
	public Node[] getWidgets(){
		return new Node[]{btnRemoveTime, btnAddTime};
	}
	
	public ClockTriggerSetTimes() {
		this(new FirebaseTrigger());
	}

	public void fxmlInit(){
		super.fxmlInit();
		super.datePane = paneDate;
	}
	public ClockTriggerSetTimes(FirebaseTrigger data) {
		super(data);
		name.setValue("SET TIMES TRIGGER");
		description = "Execute actions at a specific list of times";
		if(paneDate.getChildren().isEmpty())
			paneDate.getChildren().add(new CalendarEveryday());
		addListeners();
	}
	public void addListeners(){
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
			dateStage.setY(initY);dateStage.setX(point.getX());
			dateStage.setY(point.getY());
		
			dateStage.showAndWait();	
			
		});
			
	}
	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
	//	Map<String,Object> params = model.getparams();
		paneDate.getChildren().add(new CalendarEveryday());
		if(params.containsKey(DATE_FROM)){
			dateFrom = ((Long)params.get(DATE_FROM)).intValue();dateTo = ((Long)params.get(DATE_TO)).intValue();
			if(dateFrom != 0 && dateTo != 0){
			paneDate.getChildren().clear();
			CalendarFromTo calendarpane = new CalendarFromTo();
			paneDate.getChildren().add(calendarpane);
			calendarpane.setCalDates(LocalDate.ofEpochDay(dateFrom),LocalDate.ofEpochDay(dateTo));
			}
			
		}
		
		for(int i = 0; i < params.values().size();i++){
			if(!params.containsKey("time"+i))break;
		String time = params.get("time"+i).toString();
		String key = "time"+i;
		TimeReceiver newTimeReceiver = new TimeReceiver(Expression.VAR_CLOCK);
			newTimeReceiver.setText(time);
			timeCount++;
			paneTimes.getChildren().add(newTimeReceiver);
			newTimeReceiver.getTextField().textProperty().addListener(listen->{params.put(key, newTimeReceiver.getText());System.out.println("indeed");});//System.out.println("indeed");});

		}
	}

	@Override
	public String getViewPath() {
		return String.format("/ClockTriggerSetTime.fxml", this.getClass().getSimpleName());
	}

	@FXML
	public void handleAddTime() {
		TimeReceiver setTimeReceiver = new TimeReceiver(Expression.VAR_CLOCK);
		paneTimes.getChildren().add(setTimeReceiver);
		String key = "time" + timeCount++;
		params.put(key,setTimeReceiver.getText());
		setTimeReceiver.getTextField().textProperty().addListener(listen->{params.put(key, setTimeReceiver.getText());System.out.println("indeed");});//System.out.println("indeed");});
	}
	@FXML
	public void handleRemoveTime() {
		ObservableList<Node> times = paneTimes.getChildren();
		if(times.isEmpty())return;
		TimeReceiver lastReceiver = (TimeReceiver)times.get(times.size()-1);
		paneTimes.getChildren().remove(lastReceiver);
		timeCount--;
		params.remove("time"+(times.size())); //Removes the last time from parameters
	}
}
