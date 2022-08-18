package com.jeeves.vpl.canvas.actions;

import java.util.Map;
import java.util.Optional;

import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseDB;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ScheduleAction extends Action { // NO_UCD (unused code)
	private static final String PASSWORD = "password";
	@FXML
	private CheckBox chkPassword;

	public ScheduleAction(String name) {
		this(new FirebaseAction(name));
	}

	public ScheduleAction(FirebaseAction data) {
		super(data);
	}

	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);

		Map<String, Object> params = model.getparams();
		if (params.isEmpty())
			return;
		if (params.containsKey(PASSWORD)) {
			chkPassword.setSelected(true);
			chkPassword.setText(params.get(PASSWORD).toString());
		}
	}

	@Override
	public void addListeners() {
		super.addListeners();
		chkPassword.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				System.out.println("New value is " + newValue);
				if(newValue) {
					TextInputDialog dialog = new TextInputDialog("Password");
					dialog.setTitle("Password");
					dialog.setHeaderText("Enter the password the user should input to access the scheduling functionality");
					dialog.setContentText("Password:");

					Optional<String> result = dialog.showAndWait();
					// The Java 8 way to get the response value (with lambda expression).
					result.ifPresent(password ->{
						System.out.println("Password: " + password);
						chkPassword.setText(password);
						params.put(PASSWORD, password);

					});
				}
				else {
					chkPassword.setText("Add password");
					params.remove(PASSWORD);
				}
			}
		});
	}
}