package com.jeeves.vpl.canvas.actions;

import static com.jeeves.vpl.Constants.numberHandler;

import java.util.Map;

import com.jeeves.vpl.TextUtils;
import com.jeeves.vpl.firebase.FirebaseAction;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

/**
 * Action to remain in the execution of a trigger temporarily
 * 
 * @author Daniel
 *
 */
public class WaitingAction extends Action { // NO_UCD (unused code)
	private static final String GRAN = "granularity";
	@FXML
	private ComboBox<String> cboWaitGranularity;
	@FXML
	private HBox hbox;
	@FXML
	private TextField txtWaitTime;
	ChangeListener<String> selectionListener;

	public WaitingAction(String name) {
		this(new FirebaseAction(name));
	}

	public WaitingAction(FirebaseAction data) {
		super(data);
		cboWaitGranularity.getItems().addAll("seconds", "minutes", "hours");
	}

	@Override
	public void addListeners() {
		super.addListeners();
		ChangeListener<String> textChanged = (arg0,arg1,arg2)->{

				txtWaitTime.setPrefWidth(
						TextUtils.computeTextWidth(txtWaitTime.getFont(), txtWaitTime.getText(), 0.0D) + 10);
				autosize();
				params.put("time", txtWaitTime.getText());

		};
		txtWaitTime.addEventFilter(KeyEvent.KEY_TYPED, numberHandler);

		txtWaitTime.textProperty().addListener(textChanged);
		selectionListener = (arg0,arg1,arg2)->{
				if (arg2 != null) // aaaaaaaargh

					params.put(GRAN, arg2);

		};
		cboWaitGranularity.valueProperty().addListener(selectionListener);
	}


	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();
		if (params.isEmpty())
			return;

		if (params.containsKey(GRAN))
			cboWaitGranularity.setValue(params.get(GRAN).toString());
		if (params.containsKey("time"))
			txtWaitTime.setText(params.get("time").toString());
	}
	


}
