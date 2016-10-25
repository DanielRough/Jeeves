package com.jeeves.vpl.canvas.triggers;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleGroup;

import com.jeeves.vpl.MainController;
import com.jeeves.vpl.firebase.FirebaseTrigger;
import com.jeeves.vpl.firebase.FirebaseUI;


/**
 * This class represents a clock trigger that can be dragged around on the
 * canvas and have its fields edited
 *
 * @author Daniel
 */
public class ButtonTrigger extends Trigger {  // NO_UCD (unused code)
	
	@FXML private ComboBox<String> cboButton;
	String value;
	final ToggleGroup group = new ToggleGroup();
	public Node[] getWidgets(){
		return new Node[]{cboButton};
	}

	
	public void addListeners() {
		super.addListeners();
		cboButton.getItems().clear();

		ObservableList<FirebaseUI> uielements = MainController.currentGUI.currentelements; //UGH THIS IS HORRIBLE PLEASE FIX //myCanvas.getProject().getUIElements();
		uielements.forEach(survey->{cboButton.getItems().add(survey.gettext());		cboButton.getSelectionModel().selectFirst();});
		uielements.addListener(new ListChangeListener<FirebaseUI>(){

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FirebaseUI> c) {
				if(cboButton.getValue() != null)value = cboButton.getValue();
				cboButton.getItems().clear();
			//	cboButton.setValue("");
				for(FirebaseUI button : uielements){
				if(button.gettext() == null)continue;
				if(button.getname().equals("LABEL"))continue; //We don't count labels
				cboButton.getItems().add(button.gettext());
				
				System.out.println("A value is " + value);
				if(button.gettext().equals(value)){
					cboButton.setValue(value); //reset it if the original survey we had selected didn't change
					model.getparams().put("selectedButton", value);
				}
				}

			}
			
		});
		
		cboButton.valueProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				model.getparams().put("selectedButton", arg2);
			}
		});
	}

	public ButtonTrigger() {
		this(new FirebaseTrigger());
	}
	public ButtonTrigger(FirebaseTrigger data) {
		super(data);
		name.setValue("BUTTON TRIGGER");
		description = "Initiate actions when a user presses a UI button";
		if(cboButton.getItems()!= null && cboButton.getItems().size()>0)
			cboButton.getSelectionModel().clearAndSelect(0);
		addListeners();
	}
	
	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		Map<String,Object> params = model.getparams();

		if(cboButton.getItems()!= null && cboButton.getItems().size()>0)
			cboButton.getSelectionModel().clearAndSelect(0);

		if(params.get("selectedButton") == null)return;
		cboButton.setValue(params.get("selectedButton").toString());

	}
	@Override
	public String getViewPath() {
		return String.format("/ButtonTrigger.fxml", this.getClass().getSimpleName());
	}
}
