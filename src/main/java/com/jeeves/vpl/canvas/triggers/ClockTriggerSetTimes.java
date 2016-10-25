package com.jeeves.vpl.canvas.triggers;

import java.time.LocalDate;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
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
	//@FXML protected ImageView imgCalendar;
	@FXML protected Pane paneDate;
	private int timeCount;
	
	public Node[] getWidgets(){
		return new Node[]{btnRemoveTime, btnAddTime};
	}
	
	public ClockTriggerSetTimes() {
		this(new FirebaseTrigger());
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
			
			Stage stage = new Stage(StageStyle.UNDECORATED);
			NewDatePane root = new NewDatePane(stage,paneDate,dateFrom,dateTo);
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Edit dates");
			stage.initModality(Modality.APPLICATION_MODAL);
			Point2D point = getInstance().localToScreen(event.getX(),event.getY());
			stage.setX(point.getX());
			stage.setY(point.getY());
			root.getPckFrom().valueProperty().addListener(new ChangeListener<LocalDate>(){

				@Override
				public void changed(ObservableValue<? extends LocalDate> arg0,
						LocalDate arg1, LocalDate arg2) {
					setDateFrom(arg2.toEpochDay());
				}
				
			});
			root.getPckTo().valueProperty().addListener(new ChangeListener<LocalDate>(){

				@Override
				public void changed(ObservableValue<? extends LocalDate> arg0,
						LocalDate arg1, LocalDate arg2) {
					setDateTo(arg2.toEpochDay());
				}
				
			});
			root.getToggleGroup().selectedToggleProperty().addListener(new ChangeListener<Toggle>(){

				@Override
				public void changed(ObservableValue<? extends Toggle> arg0,
						Toggle arg1, Toggle arg2) {
					if(arg2.equals(root.btnEveryday)){
						setDateFrom(0);
						setDateTo(0);
					}
					else{
						
					}
				}
				
			});
			stage.showAndWait();	
			
		});
			
	}
	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		Map<String,Object> params = model.getparams();
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
			newTimeReceiver.getTextField().textProperty().addListener(listen->{model.getparams().put(key, newTimeReceiver.getText());System.out.println("indeed");});//System.out.println("indeed");});

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
		model.getparams().put(key,setTimeReceiver.getText());
		setTimeReceiver.getTextField().textProperty().addListener(listen->{model.getparams().put(key, setTimeReceiver.getText());System.out.println("indeed");});//System.out.println("indeed");});
	}
	@FXML
	public void handleRemoveTime() {
		ObservableList<Node> times = paneTimes.getChildren();
		if(times.isEmpty())return;
		TimeReceiver lastReceiver = (TimeReceiver)times.get(times.size()-1);
		paneTimes.getChildren().remove(lastReceiver);
		timeCount--;
		model.getparams().remove("time"+(times.size())); //Removes the last time from parameters
	}
}
