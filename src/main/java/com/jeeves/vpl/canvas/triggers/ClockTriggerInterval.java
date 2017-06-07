package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.DATE_FROM;
import static com.jeeves.vpl.Constants.DURATIONS;
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
	public static final String NAME = "Interval Contingent";
	@FXML
	private ComboBox<String> cboInterval;
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
		// paneDate.setOnMouseClicked(event->{
		// newDatePane.setParams(dateStage, this, dateFrom, dateTo);
		// Point2D point =
		// getInstance().localToScreen(event.getX(),event.getY());
		// Rectangle2D bounds = Screen.getPrimary().getBounds();
		// double initX = point.getX();
		// double initY = point.getY();
		// if(point.getX() > (bounds.getWidth()-200)){
		// initX = bounds.getWidth()-200;
		// }
		// if(point.getY() > (bounds.getHeight() - 300)){
		// initY = bounds.getHeight() - 300;
		// }
		// dateStage.setX(initX);
		// dateStage.setY(initY);
		//
		// dateStage.showAndWait();
		//
		// });
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
																						// 1000;
//				if (duration.equals("hours"))
//					intervalTriggerTime *= 60;
				params.put(INTERVAL_TRIGGER_TIME, intervalTriggerTime);
			//	params.put("intervalTime", txtFieldInterval.getText());				
			}
		});

		cboInterval.valueProperty()
				.addListener((ChangeListener<String>) (arg0, arg1, arg2) -> params.put("granularity", arg2));
		if(!model.getparams().containsKey(INTERVAL_TRIGGER_TIME))
			txtFieldInterval.setText("60");
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
		// super.datePane = paneDate;
		styleTextCombo(cboInterval);
		cboInterval.getItems().addAll(DURATIONS);
		cboInterval.setValue(DURATIONS[0]);
	}

	@Override
	public String getViewPath() {
		return String.format("/TriggerClockInterval.fxml", this.getClass().getSimpleName());
	}

	@Override
	public Node[] getWidgets() {
		return new Node[] { dateReceiverFrom, dateReceiverTo, paneIntervalFrom, paneIntervalTo, txtFieldInterval,
				cboInterval };
	}

	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		// Map<String,Object> params = model.getparams();
		if (!params.isEmpty()) {
			if (params.containsKey("granularity"))
				duration = params.get("granularity").toString();
			else
				duration = "minutes";
			if (params.containsKey(INTERVAL_TRIGGER_TIME))
				intervalTime = params.get(INTERVAL_TRIGGER_TIME).toString();
			// paneDate.getChildren().add(new CalendarEveryday());

			if (params.containsKey(DATE_FROM)) {
				// String dateFrom = ((String)params.get(DATE_FROM));String
				// dateTo = ((String)params.get(DATE_TO));
				// if(dateFrom != 0 && dateTo != 0){
				// paneDate.getChildren().clear();
				// CalendarFromTo calendarpane = new CalendarFromTo();
				// paneDate.getChildren().add(calendarpane);
				// calendarpane.setCalDates(LocalDate.ofEpochDay(dateFrom),LocalDate.ofEpochDay(dateTo));
				// }
			}
			txtFieldInterval.setText(intervalTime);
			cboInterval.setValue(duration);

			// addListeners();
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
