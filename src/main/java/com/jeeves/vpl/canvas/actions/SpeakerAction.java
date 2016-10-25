package com.jeeves.vpl.canvas.actions;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import com.jeeves.vpl.firebase.FirebaseAction;

public class SpeakerAction extends Action { // NO_UCD (unused code)
	private String volumeOn;
	@FXML
	private ComboBox<String> cboVolumeOn;

	public SpeakerAction() {
		this(new FirebaseAction());
	}
	public SpeakerAction(FirebaseAction data) {
		super(data);
		this.name.setValue("SPEAKER ACTION");
		this.description = "Turn phone volume on or off";
		cboVolumeOn.getItems().addAll("On", "Off");
		addListeners();

	}
	public Node[] getWidgets() {
		return new Node[] { cboVolumeOn };
	}

	public void setData(FirebaseAction model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();
		if(params.isEmpty())return;
		volumeOn = params.get("volume").toString();
		cboVolumeOn.setValue(volumeOn);
	}

	@Override
	public String getViewPath() {
		return String.format("/actionSpeaker.fxml", this.getClass()
				.getSimpleName());
	}

	@Override
	protected void addListeners() {
		super.addListeners();
		cboVolumeOn.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				model.getparams().put("volume", arg2);
			}

		});

	}
}