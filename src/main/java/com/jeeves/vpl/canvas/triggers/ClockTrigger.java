package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.DATE_FROM;
import static com.jeeves.vpl.Constants.DATE_TO;
import static com.jeeves.vpl.Constants.LIMIT_AFTER_HOUR;
import static com.jeeves.vpl.Constants.LIMIT_BEFORE_HOUR;
import static com.jeeves.vpl.Constants.VAR_CLOCK;
import static com.jeeves.vpl.Constants.VAR_DATE;

import java.net.URL;
import java.util.ArrayList;

import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.DateReceiver;
import com.jeeves.vpl.canvas.receivers.TimeReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseTrigger;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public abstract class ClockTrigger extends Trigger { // NO_UCD (use default)

	public class CalendarEveryday extends Pane {

		@FXML
		private ImageView imgCalendar;

		public CalendarEveryday() {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setController(this);
			URL location = this.getClass().getResource("/calevery.fxml");
			fxmlLoader.setLocation(location);
			try {
				Node root = (Node) fxmlLoader.load();
				getChildren().add(root);
				imgCalendar.setImage(new Image("/img/icons/calenda.png"));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected Pane datePane;
	protected DateReceiver dateReceiverFrom;
	protected DateReceiver dateReceiverTo;
	protected Stage dateStage;
	protected TimeReceiver timeReceiverFrom;

	protected TimeReceiver timeReceiverTo;

	// protected NewDatePane newDatePane;
	public ClockTrigger(FirebaseTrigger data) {
		super(data);
	}

	@Override
	public void addListeners() {
		super.addListeners();
		if(model.getvariables() == null)
			model.setvariables(new ArrayList<String>());
		
		timeReceiverFrom.getChildElements().addListener(
				(ListChangeListener<ViewElement>) listener ->{
					listener.next();
					if(listener.wasAdded()){
						model.getvariables().add(timeReceiverFrom.getChildModel().getname());
					}
					else{
						ViewElement removed = listener.getRemoved().get(0);
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
						
						ViewElement removed = listener.getRemoved().get(0);
						FirebaseExpression removedModel = (FirebaseExpression)removed.getModel();
						model.getvariables().remove(removedModel.getname());
					}
					model.settimeTo(timeReceiverTo.getChildModel());
					params.put(LIMIT_AFTER_HOUR,0);
				});
		
		timeReceiverFrom.getTextField().textProperty().addListener(listen -> {
			params.put(LIMIT_BEFORE_HOUR, Long.parseLong(timeReceiverFrom.getText()));
		});
		
		timeReceiverTo.getTextField().textProperty().addListener(listen -> {
			params.put(LIMIT_AFTER_HOUR, Long.parseLong(timeReceiverTo.getText()));
		});
		
		dateReceiverFrom.getChildElements().addListener((ListChangeListener<ViewElement>) listener -> {
			listener.next();
			if(listener.wasAdded()){
				model.getvariables().add(dateReceiverFrom.getChildModel().getname());
			}
			else{
				ViewElement removed = listener.getRemoved().get(0);
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
						ViewElement removed = listener.getRemoved().get(0);
						FirebaseExpression removedModel = (FirebaseExpression)removed.getModel();
						model.getvariables().remove(removedModel.getname());
					}
					model.setdateTo(dateReceiverTo.getChildModel());
					params.put(DATE_TO, System.currentTimeMillis());
				});
		
		dateReceiverFrom.getTextField().textProperty().addListener(listen -> {
			params.put(DATE_FROM, Long.parseLong(dateReceiverFrom.getText()));
		});
		
		dateReceiverTo.getTextField().textProperty().addListener(listen -> {
			params.put(DATE_TO, Long.parseLong(dateReceiverTo.getText()));
		});


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
	public void setParentPane(ParentPane parent) {
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
}
