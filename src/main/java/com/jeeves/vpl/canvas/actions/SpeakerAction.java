//package com.jeeves.vpl.canvas.actions;
//
//import java.util.Map;
//
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.fxml.FXML;
//import javafx.scene.Node;
//import javafx.scene.control.ComboBox;
//
//import com.jeeves.vpl.firebase.FirebaseAction;
//
//public class SpeakerAction extends Action { // NO_UCD (unused code)
//	public static final String DESC = "Turn the patient's phone volume on or off";
//	public static final String NAME = "Adjust phone volume";
//	@FXML
//	private ComboBox<String> cboVolumeOn;
//	private String volumeOn;
//
//	public SpeakerAction() {
//		this(new FirebaseAction());
//	}
//
//	public SpeakerAction(FirebaseAction data) {
//		super(data);
//	}
//
//	@Override
//	public void addListeners() {
//		super.addListeners();
//		cboVolumeOn.getItems().addAll("On", "Off");
//
//		cboVolumeOn.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
//
//			@Override
//			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
//				params.put("volume", arg2);
//			}
//
//		});
//
//	}
//
//	@Override
//	public void fxmlInit() {
//		super.fxmlInit();
//		name = NAME;
//		description = DESC;
//	}
//
//	@Override
//	public String getViewPath() {
//		return String.format("/actionSpeaker.fxml", this.getClass().getSimpleName());
//	}
//
//	@Override
//	public Node[] getWidgets() {
//		return new Node[] { cboVolumeOn };
//	}
//
//	@Override
//	public void setData(FirebaseAction model) {
//		super.setData(model);
//		Map<String, Object> params = model.getparams();
//		if (params.isEmpty())
//			return;
//		volumeOn = params.get("volume").toString();
//		cboVolumeOn.setValue(volumeOn);
//	}
//
//}