package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.DATE_FROM;
import static com.jeeves.vpl.Constants.DATE_TO;
import static com.jeeves.vpl.Constants.LIMIT_AFTER_HOUR;
import static com.jeeves.vpl.Constants.LIMIT_BEFORE_HOUR;
import static com.jeeves.vpl.Constants.VAR_CLOCK;
import static com.jeeves.vpl.Constants.VAR_DATE;

import java.util.List;
import java.util.Map;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.DateReceiver;
import com.jeeves.vpl.canvas.receivers.TimeReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseTrigger;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public abstract class ClockTrigger extends Trigger { // NO_UCD (use default)

	protected Pane datePane;
	protected DateReceiver dateReceiverFrom;
	protected DateReceiver dateReceiverTo;
	protected Stage dateStage;
	protected TimeReceiver timeReceiverFrom;

	protected TimeReceiver timeReceiverTo;

	public ClockTrigger(FirebaseTrigger data) {
		super(data);
	}
	FirebaseVariable wakeVar = null, sleepVar = null,startVar = null,endVar = null;

	@Override
	public void addListeners() {
		super.addListeners();

		this.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(!Constants.getOpenProject().gethasSchedule()) {
					return;
				}
				
				ContextMenu contextMenu = new ContextMenu();
				 
		        MenuItem item1 = new MenuItem("Set to waking schedule");
		        item1.setOnAction(new EventHandler<ActionEvent>() {		 
		            @Override
		            public void handle(ActionEvent event) {
		            	setScheduled();
		            }
		        });
		        MenuItem unschedule = new MenuItem("Remove waking schedule");
		        unschedule.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						unSchedule();
					}
		        });
		        // Add MenuItem to ContextMenu
		        if(getModel().getisScheduled())
		        	contextMenu.getItems().add(unschedule);
		        else
		        	contextMenu.getItems().add(item1);
		        contextMenu.show(getInstance(), event.getScreenX(), event.getScreenY());
			}			
		});
		timeReceiverFrom.getChildElements().addListener(
				(ListChangeListener<ViewElement>) listener ->{
					listener.next();
					if(listener.wasAdded()){
						model.getvariables().add(timeReceiverFrom.getChildModel().getname());
					}
					else{
						ViewElement<?> removed = listener.getRemoved().get(0);
						FirebaseExpression removedModel = (FirebaseExpression)removed.getModel();
						model.getvariables().remove(removedModel.getname());
					}
					model.settimeFrom(timeReceiverFrom.getChildModel());
					params.put(LIMIT_BEFORE_HOUR,0); //Just so things get updated
					
				});

		timeReceiverTo.getChildElements().addListener(
				(ListChangeListener<ViewElement>) listener -> {
					listener.next();
					if(listener.wasAdded()){
						model.getvariables().add(timeReceiverTo.getChildModel().getname());
					}
					else{
						
						ViewElement<?> removed = listener.getRemoved().get(0);
						FirebaseExpression removedModel = (FirebaseExpression)removed.getModel();
						model.getvariables().remove(removedModel.getname());
					}
					model.settimeTo(timeReceiverTo.getChildModel());
					params.put(LIMIT_AFTER_HOUR,0);
				});
		
		timeReceiverFrom.getTextField().textProperty().addListener(listen -> 
			params.put(LIMIT_BEFORE_HOUR, Long.parseLong(timeReceiverFrom.getText()))
		);
		
		timeReceiverTo.getTextField().textProperty().addListener(listen -> 
			params.put(LIMIT_AFTER_HOUR, Long.parseLong(timeReceiverTo.getText()))
		);
		
		dateReceiverFrom.getChildElements().addListener((ListChangeListener<ViewElement>) listener -> {
			listener.next();
			if(listener.wasAdded()){
				//ViewElement<?> added = listener.getAddedSubList().get(0);
				//added.setPrefWidth(dateReceiverFrom.getWidth());
				//((UserVariable)added).setup();
				model.getvariables().add(dateReceiverFrom.getChildModel().getname());
			}
			else{
				ViewElement<?> removed = listener.getRemoved().get(0);
				FirebaseExpression removedModel = (FirebaseExpression)removed.getModel();
				model.getvariables().remove(removedModel.getname());
			}
			model.setdateFrom(dateReceiverFrom.getChildModel());
			params.put(DATE_FROM, System.currentTimeMillis()); //just so it gets updated
		});
		
		dateReceiverTo.getChildElements().addListener(
				
				(ListChangeListener<ViewElement>) listener -> {
					listener.next();
					if(listener.wasAdded()){
						model.getvariables().add(dateReceiverTo.getChildModel().getname());
					}
					else{
						ViewElement<?> removed = listener.getRemoved().get(0);
						FirebaseExpression removedModel = (FirebaseExpression)removed.getModel();
						model.getvariables().remove(removedModel.getname());
					}
					model.setdateTo(dateReceiverTo.getChildModel());
					params.put(DATE_TO, System.currentTimeMillis());
				});
		
		dateReceiverFrom.getTextField().textProperty().addListener(listen -> 
			params.put(DATE_FROM, Long.parseLong(dateReceiverFrom.getText()))
		);
		
		dateReceiverTo.getTextField().textProperty().addListener(listen -> 
			params.put(DATE_TO, Long.parseLong(dateReceiverTo.getText()))
		);


	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		timeReceiverFrom = new TimeReceiver(VAR_CLOCK);
		timeReceiverTo = new TimeReceiver(VAR_CLOCK);
		dateReceiverFrom = new DateReceiver(VAR_DATE);
		dateReceiverTo = new DateReceiver(VAR_DATE);
	}

	@Override
	public void setData(FirebaseTrigger data) {
		super.setData(data);
		if(model.getisScheduled()) {
			setScheduled();
			return;
		}
		if (model.gettimeFrom() != null) {
			timeReceiverFrom.addChild(UserVariable.create(model.gettimeFrom()), 0, 0);
		} else if (params.containsKey(LIMIT_BEFORE_HOUR)) {
			String beforeTime = params.get(LIMIT_BEFORE_HOUR).toString();
			timeReceiverFrom.setText(beforeTime);
		}
		if (model.gettimeTo() != null) {
			timeReceiverTo.addChild(UserVariable.create(model.gettimeTo()), 0, 0);
		} else if (params.containsKey(LIMIT_AFTER_HOUR)) {
			String afterTime = params.get(LIMIT_AFTER_HOUR).toString();
			timeReceiverTo.setText(afterTime);
		}
		if (model.getdateFrom() != null) {
			dateReceiverFrom.addChild(UserVariable.create(model.getdateFrom()), 0, 0);
		} else if (params.containsKey(DATE_FROM)) {
			String beforeDate = params.get(DATE_FROM).toString();
			dateReceiverFrom.setText(beforeDate);
		}
		if (model.getdateTo() != null) {
			dateReceiverTo.addChild(UserVariable.create(model.getdateTo()), 0, 0);
		} else if (params.containsKey(DATE_TO)) {
			String afterDate = params.get(DATE_TO).toString();
			dateReceiverTo.setText(afterDate);
		}
	}

	public void setDateFrom(long dateFrom) {
		params.put(DATE_FROM, dateFrom);

	}

	public void setDateTo(long dateTo) {
		params.put(DATE_TO, dateTo);
	}

	@Override
	public void setParentPane(DragPane parent) {
		super.setParentPane(parent);
		if (timeReceiverFrom.getChildExpression() != null)
			timeReceiverFrom.getChildExpression().setParentPane(parent);
		if (timeReceiverTo.getChildExpression() != null)
			timeReceiverTo.getChildExpression().setParentPane(parent);
		if (dateReceiverFrom.getChildExpression() != null)
			dateReceiverFrom.getChildExpression().setParentPane(parent);
		if (dateReceiverTo.getChildExpression() != null)
			dateReceiverTo.getChildExpression().setParentPane(parent);

	}
	public void unSchedule() {
		getModel().setisScheduled(false);
    	timeReceiverFrom.removeChild(timeReceiverFrom.getChildExpression());
    	timeReceiverFrom.setEditable(true);
    	timeReceiverTo.removeChild(timeReceiverTo.getChildExpression());
    	timeReceiverTo.setEditable(true);
    	dateReceiverFrom.removeChild(dateReceiverFrom.getChildExpression());    	
    	dateReceiverFrom.setEditable(true);    	
    	dateReceiverTo.removeChild(dateReceiverTo.getChildExpression());    	
    	dateReceiverTo.setEditable(true);
    	
    	getMyReceiver().getBrackets().getStyleClass().remove("schedule");
    	getMyReceiver().getBrackets().getStyleClass().add("trigger");
    	Pane topPane = (Pane)getChildren().get(0);
    	topPane.getStyleClass().remove("schedule");
    	topPane.getStyleClass().add("trigger");
    	topPane.getChildren().forEach(child->{
    		child.getStyleClass().remove("schedule");
    		child.getStyleClass().add("trigger");
    	});
	}
	public void setScheduled() {
		getModel().setisScheduled(true);
    	Map<String,Object> scheduleAttrs = Constants.getOpenProject().getscheduleAttrs();
    	String wakeTime = scheduleAttrs.get(Constants.WAKE_TIME).toString();
    	String sleepTime = scheduleAttrs.get(Constants.SLEEP_TIME).toString();
    	String startDate = scheduleAttrs.get(Constants.START_DATE).toString();
    	String endDate = scheduleAttrs.get(Constants.END_DATE).toString();
    	List<FirebaseVariable> vars = Constants.getOpenProject().getvariables();
    	vars.forEach(var ->{
    		if(var.getname().equals(wakeTime))
    			wakeVar = var;
    		else if(var.getname().equals(sleepTime))
    			sleepVar = var;
    		else if(var.getname().equals(startDate))
    			startVar = var;
    		else if(var.getname().equals(endDate))
    			endVar = var;
    	});
    	if(timeReceiverFrom.getChildExpression() != null)
    		timeReceiverFrom.removeChild(timeReceiverFrom.getChildExpression());
    	timeReceiverFrom.addChild(UserVariable.create(wakeVar), 0, 0);
    	timeReceiverFrom.setEditable(false);
    	
    	if(timeReceiverTo.getChildExpression() != null)
    		timeReceiverTo.removeChild(timeReceiverTo.getChildExpression());
    	timeReceiverTo.addChild(UserVariable.create(sleepVar), 0, 0);
    	timeReceiverTo.setEditable(false);

    	if(dateReceiverFrom.getChildExpression() != null)
    		dateReceiverFrom.removeChild(dateReceiverFrom.getChildExpression());
    	dateReceiverFrom.addChild(UserVariable.create(startVar), 0, 0);
    	dateReceiverFrom.setEditable(false);

    	if(dateReceiverTo.getChildExpression() != null)
    		dateReceiverTo.removeChild(dateReceiverTo.getChildExpression());
    	dateReceiverTo.addChild(UserVariable.create(endVar), 0, 0);
    	dateReceiverTo.setEditable(false);
    	
    	getMyReceiver().getBrackets().getStyleClass().remove("trigger");
    	getMyReceiver().getBrackets().getStyleClass().add("schedule");
    	Pane topPane = (Pane)getChildren().get(0);
    	topPane.getStyleClass().remove("trigger");
    	topPane.getStyleClass().add("schedule");
    	topPane.getChildren().forEach(child->{
    		child.getStyleClass().remove("trigger");
    		child.getStyleClass().add("schedule");
    	});
	}
}
