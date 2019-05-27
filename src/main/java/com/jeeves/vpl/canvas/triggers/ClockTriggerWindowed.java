package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.INTERVAL_TRIGGER_TIME;
import static com.jeeves.vpl.Constants.INTERVAL_TRIGGER_WINDOW;

import com.jeeves.vpl.firebase.FirebaseTrigger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

/**
 * This class represents a clock trigger that can be dragged around on the
 * canvas and have its fields edited
 *
 * @author Daniel
 */
public class ClockTriggerWindowed extends ClockTrigger { // NO_UCD (use default)


	private String intervalTime = "0";
	private String windowTime = "0";
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
	@FXML
	private TextField txtFieldWindow;
	
	public ClockTriggerWindowed(String name) {
		this(new FirebaseTrigger(name));
	}

	public ClockTriggerWindowed(FirebaseTrigger data) {
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
		txtFieldWindow.addEventHandler(KeyEvent.KEY_TYPED,arg0->{
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
		txtFieldWindow.textProperty().addListener((o,v0,v1)-> {
			if (txtFieldWindow.getText().equals(""))
				return;
			long intervalTriggerTime = Long.parseLong(txtFieldWindow.getText());// *
			params.put(INTERVAL_TRIGGER_WINDOW, intervalTriggerTime);
		});
		paneStartDate.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				System.out.println("date things are " + paneStartDate.getWidth() + " long");
				
			}
			
		});
		paneIntervalFrom.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				System.out.println("And time things are " + paneIntervalFrom.getWidth() + " long");
				
			}
			
		});
	}

	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		if (!params.isEmpty()) {
			if (params.containsKey(INTERVAL_TRIGGER_TIME))
				intervalTime = params.get(INTERVAL_TRIGGER_TIME).toString();
			if(intervalTime != null)
				txtFieldInterval.setText(intervalTime);
			if (params.containsKey(INTERVAL_TRIGGER_WINDOW))
				windowTime = params.get(INTERVAL_TRIGGER_WINDOW).toString();
			if(windowTime != null)
				txtFieldWindow.setText(windowTime);
		}

	}


}
