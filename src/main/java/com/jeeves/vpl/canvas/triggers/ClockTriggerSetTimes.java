package com.jeeves.vpl.canvas.triggers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import com.jeeves.vpl.canvas.receivers.DateReceiver;
import com.jeeves.vpl.canvas.receivers.TimeReceiver;
import com.jeeves.vpl.firebase.FirebaseTrigger;

import static com.jeeves.vpl.Constants.*;

/**
 * This class represents a clock trigger that can be dragged around on the
 * canvas and have its fields edited
 *
 * @author Daniel
 */
public class ClockTriggerSetTimes extends ClockTrigger { // NO_UCD (use default)
	public static final String DESC = "Schedule actions to take place at specific times";
	public static final String NAME = "Specific Times";
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
		String key = "time" + timeCount++;
		params.put(key, setTimeReceiver.getText());
		setTimeReceiver.getTextField().textProperty().addListener(listen -> {
			params.put(key, setTimeReceiver.getText());
		});
	}

	@FXML
	public void handleRemoveTime() {
		ObservableList<Node> times = paneTimes.getChildren();
		if (times.isEmpty())
			return;
		TimeReceiver lastReceiver = (TimeReceiver) times.get(times.size() - 1);
		paneTimes.getChildren().remove(lastReceiver);
		timeCount--;
		params.remove("time" + (times.size())); 
	}

	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
	
		for (int i = 0; i < params.values().size(); i++) {
			if (!params.containsKey("time" + i))
				break;
			String time = params.get("time" + i).toString();
			String key = "time" + i;
			TimeReceiver newTimeReceiver = new TimeReceiver(VAR_CLOCK);
			newTimeReceiver.setText(time);
			timeCount++;
			paneTimes.getChildren().add(newTimeReceiver);
			newTimeReceiver.getTextField().textProperty().addListener(listen -> {
				params.put(key, newTimeReceiver.getText());
			});

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
