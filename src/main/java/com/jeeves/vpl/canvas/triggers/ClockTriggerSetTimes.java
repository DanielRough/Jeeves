package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.VAR_CLOCK;
import static com.jeeves.vpl.Constants.VAR_DATE;
import static com.jeeves.vpl.Constants.getSaltString;

import java.util.ArrayList;
import java.util.List;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.DateReceiver;
import com.jeeves.vpl.canvas.receivers.TimeReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseTrigger;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * This class represents a clock trigger that can be dragged around on the
 * canvas and have its fields edited
 *
 * @author Daniel
 */
public class ClockTriggerSetTimes extends ClockTrigger { // NO_UCD (use default)
	public static final String DESC = "Schedule actions to take place at specific times";
	public static final String NAME = "Set Times Trigger";
	@FXML
	private Pane pane;
	@FXML
	private Pane paneEndDate;
	// @FXML protected Pane paneDate;
	@FXML
	private Pane paneStartDate;
	private int timeCount;
	@FXML
	protected Button btnAddTime;
	@FXML
	protected Button btnRemoveTime;
	protected long dateFrom;
	protected long dateTo;
	@FXML
	protected VBox paneTimes;
	protected List<FirebaseExpression> times;
	//
	public ClockTriggerSetTimes() {
		this(new FirebaseTrigger());
	}

	public ClockTriggerSetTimes(FirebaseTrigger data) {
		super(data);

	}

	@Override
	public void addListeners() {
		super.addListeners();
		
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		description = DESC;
		dateReceiverTo = new DateReceiver(VAR_DATE);
		dateReceiverFrom = new DateReceiver(VAR_DATE);
		paneStartDate.getChildren().add(dateReceiverFrom);
		paneEndDate.getChildren().add(dateReceiverTo);
	}

	@Override
	public String getViewPath() {
		return String.format("/TriggerClockSetTime.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { dateReceiverFrom, dateReceiverTo, btnRemoveTime, btnAddTime };
	}

	@FXML
	public void handleAddTime() {
		TimeReceiver setTimeReceiver = new TimeReceiver(VAR_CLOCK);
		pane.setPrefHeight(USE_COMPUTED_SIZE);
		paneTimes.getChildren().add(setTimeReceiver);
		int myindex = paneTimes.getChildren().size()-1;
		if(model.gettimes() == null)
			model.settimes(new ArrayList<FirebaseExpression>());
		model.gettimes().add(new FirebaseExpression());
		model.settriggerId(getSaltString());
		setTimeReceiver.getTextField().textProperty().addListener(listen -> {
			model.gettimes().get(myindex).setvalue(setTimeReceiver.getText());
			model.gettimes().get(myindex).setIsValue(true);
			model.settriggerId(getSaltString()); // Again, update, must

		});
		setTimeReceiver.getChildElements().addListener(
				(ListChangeListener<ViewElement>) listener -> {
					model.gettimes().set(myindex,setTimeReceiver.getChildModel());
					model.settriggerId(getSaltString());
				});// timeReceiverFrom.getChildElements().get(0).getModel())));
		
	}

	@FXML
	public void handleRemoveTime() {
		ObservableList<Node> times = paneTimes.getChildren();
		if (times.isEmpty())
			return;
		TimeReceiver lastReceiver = (TimeReceiver) times.get(times.size() - 1);
		paneTimes.getChildren().remove(lastReceiver);
		model.gettimes().remove(model.gettimes().size()-1);//remove the last one
		model.settriggerId(getSaltString()); // Again, update, must

	}
	int timeindex = 0;

	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		if(model.gettimes() == null)
			model.settimes(new ArrayList<FirebaseExpression>());
		this.times = (List<FirebaseExpression>) model.gettimes();
		timeindex = 0;
		if(times != null)
		for(FirebaseExpression time :times){
			TimeReceiver newTimeReceiver = new TimeReceiver(VAR_CLOCK);
			paneTimes.getChildren().add(newTimeReceiver);
			if(time.getisValue()){
				newTimeReceiver.setText(time.getvalue());
			}
			else{
				UserVariable timevar = UserVariable.create(time);
				newTimeReceiver.addChild(timevar, 0, 0);
			}
			
			newTimeReceiver.getTextField().textProperty().addListener(listen -> {
				times.get(timeindex).setvalue(newTimeReceiver.getText());
				times.get(timeindex).setIsValue(true);
				model.settriggerId(getSaltString()); // Again, update, must

			});
			newTimeReceiver.getChildElements().addListener(
					(ListChangeListener<ViewElement>) listener -> {
						times.set(timeindex,newTimeReceiver.getChildModel());
						model.settriggerId(getSaltString());
					});// timeReceiverFrom.getChildElements().get(0).getModel())));
			timeindex++;
		}
		
	}

	@Override
	public void setDateFrom(long dateFrom) {
		super.setDateFrom(dateFrom);
		this.dateFrom = dateFrom;
	}

	@Override
	public void setDateTo(long dateTo) {
		super.setDateTo(dateTo);
		this.dateTo = dateTo;
	}

}
