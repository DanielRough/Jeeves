package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.DURATIONS_SHORT;
import static com.jeeves.vpl.Constants.INTERVAL_TRIGGER_TIME;
import static com.jeeves.vpl.Constants.styleTextCombo;

import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.firebase.FirebaseTrigger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
public class ClockTriggerInterval extends ClockTrigger { // NO_UCD (use default)
	public static final String DESC = "Schedule actions to take place at regular intervals";
	public static final String NAME = "Repeated Time Trigger";
	@FXML
	private ComboBox<String> cboFixedRandom;
	//private ComboBox<String> cboInterval;
	private long dateFrom;
	private long dateTo;
	private String duration = "seconds";
	private String intervalTime = "0";
	@FXML
	private Pane pane;
	@FXML
	private Pane paneEndDate;
	@FXML
	private Pane paneIntervalFrom;

	@FXML
	private Pane paneIntervalTo;

	// @FXML protected ImageView imgCalendar;
	// @FXML private Pane paneDate;
	@FXML
	private Pane paneStartDate;
	@FXML
	private TextField txtFieldInterval;

	public ClockTriggerInterval() {
		this(new FirebaseTrigger());
	}

	public ClockTriggerInterval(FirebaseTrigger data) {
		super(data);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void addListeners() {
		super.addListeners();

		txtFieldInterval.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {

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
		txtFieldInterval.textProperty().addListener(new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (txtFieldInterval.getText().equals(""))
					return;
				long intervalTriggerTime = Long.parseLong(txtFieldInterval.getText());// *
				params.put(INTERVAL_TRIGGER_TIME, intervalTriggerTime);
			}
		});

		cboFixedRandom.valueProperty()
		.addListener((ChangeListener<String>) (arg0, arg1, arg2) -> params.put("fixedRandom", arg2));
//		cboInterval.valueProperty()
//				.addListener((ChangeListener<String>) (arg0, arg1, arg2) -> params.put("granularity", arg2));
		if(!model.getparams().containsKey(INTERVAL_TRIGGER_TIME))
			txtFieldInterval.setText("5");
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		name = NAME;
		description = DESC;

		paneStartDate.getChildren().add(dateReceiverFrom);
		paneEndDate.getChildren().add(dateReceiverTo);
		paneIntervalFrom.getChildren().add(timeReceiverFrom);
		paneIntervalTo.getChildren().add(timeReceiverTo);
//		styleTextCombo(cboInterval);
//		cboInterval.getItems().addAll(DURATIONS_SHORT);
//		cboInterval.setValue(DURATIONS_SHORT[0]);
		cboFixedRandom.getItems().addAll("fixed","random");
		cboFixedRandom.setValue("fixed");
	}

	@Override
	public String getViewPath() {
		return String.format("/TriggerClockInterval.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { dateReceiverFrom, dateReceiverTo, paneIntervalFrom, paneIntervalTo, txtFieldInterval,cboFixedRandom 
				};
	}

	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		// Map<String,Object> params = model.getparams();
		if (!params.isEmpty()) {
			if (params.containsKey("fixedrandom"))
				duration = params.get("fixedrandom").toString();
			else
				duration = "minutes";
			if (params.containsKey(INTERVAL_TRIGGER_TIME))
				intervalTime = params.get(INTERVAL_TRIGGER_TIME).toString();

			txtFieldInterval.setText(intervalTime);
			cboFixedRandom.setValue(duration);

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

	@Override
	public void setParentPane(ParentPane parent) {
		super.setParentPane(parent);

	}

}
