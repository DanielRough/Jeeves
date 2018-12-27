package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.INTERVAL_TRIGGER_TIME;

import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.firebase.FirebaseTrigger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;

/**
 * This class represents a clock trigger that can be dragged around on the
 * canvas and have its fields edited
 *
 * @author Daniel
 */
public class ClockTriggerInterval extends ClockTrigger { // NO_UCD (use default)
	@FXML
	private ComboBox<String> cboFixedRandom;
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
	@FXML
	private Pane paneStartDate;
	@FXML
	private TextField txtFieldInterval;

	public ClockTriggerInterval(String name) {
		this(new FirebaseTrigger(name));
	}

	public ClockTriggerInterval(FirebaseTrigger data) {
		super(data);
	}

	{
		paneStartDate.getChildren().add(dateReceiverFrom);
		paneEndDate.getChildren().add(dateReceiverTo);
		paneIntervalFrom.getChildren().add(timeReceiverFrom);
		paneIntervalTo.getChildren().add(timeReceiverTo);
		cboFixedRandom.getItems().addAll("fixed","random");
		cboFixedRandom.setValue("fixed");
	}
	@Override
	@SuppressWarnings("rawtypes")
	public void addListeners() {
		super.addListeners();
		cboFixedRandom.setCellFactory(
	            new Callback<ListView<String>, ListCell<String>>() {
	                @Override public ListCell<String> call(ListView<String> param) {
	                    final ListCell<String> cell = new ListCell<String>() {
	                        {
	                            super.setPrefWidth(100);
	                        }    
	                        @Override public void updateItem(String item, 
	                            boolean empty) {
	                                super.updateItem(item, empty);
	                                if (item != null) {
	                                    setText(item);    
	                
	                                        setTextFill(Color.RED);
	                                        setFont(new Font("Calibri",18));
	                                }
	                                else {
	                                    setText(null);
	                                }
	                            }
	                };
	                return cell;
	            }
	        });

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
	}

	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		if (!params.isEmpty()) {
			if (params.containsKey("fixedRandom"))
				duration = params.get("fixedRandom").toString();
			else
				duration = "fixed";
			if (params.containsKey(INTERVAL_TRIGGER_TIME))
				intervalTime = params.get(INTERVAL_TRIGGER_TIME).toString();

			if(intervalTime != null)
				txtFieldInterval.setText(intervalTime);
			cboFixedRandom.setValue(duration);

		}

	}

	@Override
	public void setDateFrom(long dateFrom) {
		super.setDateFrom(dateFrom);
	}

	@Override
	public void setDateTo(long dateTo) {
		super.setDateTo(dateTo);
	}

	@Override
	public void setParentPane(DragPane parent) {
		super.setParentPane(parent);

	}

}
