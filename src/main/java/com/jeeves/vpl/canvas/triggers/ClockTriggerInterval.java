package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.INTERVAL_TRIGGER_TIME;

import com.jeeves.vpl.firebase.FirebaseTrigger;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
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
	private static final String FIXEDRANDOM = "fixedRandom";
	private static final String FIXED = "fixed";

	@FXML
	private ComboBox<String> cboFixedRandom;
	private String intervalTime = "0";
	@FXML
	private Pane pane;
	@FXML
	private Pane paneEndDate;
	@FXML
	private Pane paneIntervalFrom;
	@FXML
	private Pane paneIntervalTo;
	@FXML
	private Pane paneStartDate;
	@FXML
	private TextField txtFieldInterval;

	public ClockTriggerInterval(String name) {
		this(new FirebaseTrigger(name));
	}

	public ClockTriggerInterval(FirebaseTrigger data) {
		super(data);
		paneStartDate.getChildren().add(dateReceiverFrom);
		paneEndDate.getChildren().add(dateReceiverTo);
		paneIntervalFrom.getChildren().add(timeReceiverFrom);
		paneIntervalTo.getChildren().add(timeReceiverTo);

	}

	@Override
	public void addListeners() {
		super.addListeners();
		txtFieldInterval.addEventHandler(KeyEvent.KEY_TYPED,arg0->{
				try {
					Long.parseLong(arg0.getCharacter());
				} catch (NumberFormatException e) {
					arg0.consume();
				}
		});
		txtFieldInterval.textProperty().addListener((o,v0,v1)-> {
				if (txtFieldInterval.getText().equals(""))
					return;
				long intervalTriggerTime = Long.parseLong(txtFieldInterval.getText());// *
				params.put(INTERVAL_TRIGGER_TIME, intervalTriggerTime);
		});

		cboFixedRandom.valueProperty()
		.addListener((ChangeListener<String>) (arg0, arg1, arg2) -> params.put(FIXEDRANDOM, arg2));
	}

	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		String duration;
		cboFixedRandom.getItems().addAll(FIXED,"random");
		cboFixedRandom.setValue(FIXED);
		if (!params.isEmpty()) {
			if (params.containsKey(FIXEDRANDOM))
				duration = params.get(FIXEDRANDOM).toString();
			else
				duration = FIXED;
			if (params.containsKey(INTERVAL_TRIGGER_TIME))
				intervalTime = params.get(INTERVAL_TRIGGER_TIME).toString();


			if(intervalTime != null)
				txtFieldInterval.setText(intervalTime);
			cboFixedRandom.setValue(duration);

		}

	}


}
