package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.DATE_FROM;
import static com.jeeves.vpl.Constants.DATE_TO;
import static com.jeeves.vpl.Constants.DURATIONS_SHORT;
import static com.jeeves.vpl.Constants.LIMIT_AFTER_HOUR;
import static com.jeeves.vpl.Constants.LIMIT_BEFORE_HOUR;
import static com.jeeves.vpl.Constants.INTERVAL_WINDOW;
import static com.jeeves.vpl.Constants.styleTextCombo;

import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.firebase.FirebaseTrigger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

/**
 * This class represents a clock trigger that can be dragged around on the
 * canvas and have its fields edited
 *
 * @author Daniel
 */
public class ClockTriggerRandom extends ClockTrigger { // NO_UCD (use default)
	public static final String DESC = "Schedule actions to take place randomly within windows of time";
	public static final String NAME = "Signal Contingent";
	private String duration = "seconds";
	private String frequencyR = "0";
	@FXML
	private Pane paneEndDate;

	// @FXML protected ImageView imgCalendar;
	// @FXML protected Pane paneDate;
	@FXML
	private Pane paneStartDate;
	@FXML
	protected ComboBox<String> cboRandom;
	protected long dateFrom;
	protected long dateTo;
	@FXML
	protected Group grpRandom;
	@FXML
	protected Pane paneRandomFrom;
	@FXML
	protected Pane paneRandomTo;
	@FXML
	protected TextField txtFieldRandom;

	public ClockTriggerRandom() {
		this(new FirebaseTrigger());
	}

	public ClockTriggerRandom(FirebaseTrigger data) {
		super(data);

	}

	@Override
	@SuppressWarnings("rawtypes")
	public void addListeners() {
		super.addListeners();

		txtFieldRandom.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent arg0) {
				try {
					Long.parseLong(arg0.getCharacter());
				} catch (NumberFormatException e) {
					arg0.consume();
					return;
				}
			}
		});
		txtFieldRandom.textProperty().addListener(new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (txtFieldRandom.getText().equals(""))
					return;
		//		long intervalTriggerTime = Long.parseLong(txtFieldRandom.getText());// *
																					// 1000;
//				if (duration.equals("hours"))
//					intervalTriggerTime *= 60;
			//	params.put(INTERVAL_WINDOW, intervalTriggerTime);
				params.put(INTERVAL_WINDOW, txtFieldRandom.getText());
			}
		});
		cboRandom.valueProperty()
				.addListener((ChangeListener<String>) (arg0, arg1, arg2) -> params.put("granularity", arg2));
		
		if(!model.getparams().containsKey(INTERVAL_WINDOW))
			txtFieldRandom.setText("60");
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		description = DESC;

		paneStartDate.getChildren().add(dateReceiverFrom);
		paneEndDate.getChildren().add(dateReceiverTo);
		paneRandomFrom.getChildren().add(timeReceiverFrom);
		paneRandomTo.getChildren().add(timeReceiverTo);
		styleTextCombo(cboRandom);
		cboRandom.getItems().addAll(DURATIONS_SHORT); // Add seconds, minutes, days,
												// etc
		cboRandom.setValue(DURATIONS_SHORT[0]);
	}

	@Override
	public String getViewPath() {
		return String.format("/TriggerClockRandom.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { dateReceiverFrom, dateReceiverTo, paneRandomFrom, paneRandomTo, txtFieldRandom, cboRandom };
	}

	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		if (params.containsKey("granularity"))
			duration = params.get("granularity").toString();
		else
			duration = "minutes";
		cboRandom.setValue(duration);

		if (params.containsKey(INTERVAL_WINDOW)) {
			frequencyR = params.get(INTERVAL_WINDOW).toString();
			txtFieldRandom.setText(frequencyR);
		}
				addListeners();
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
