package com.jeeves.vpl;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseVariable;

/**
 * This is the controller class for a Custom variable, which appears when we want
 * to save a new variable to the database
 *
 * @author Daniel
 *
 */
public class VariablePane extends Pane{ // NO_UCD (use default)

	@FXML private ComboBox<String> cboVarType;
	@FXML private Label lblError;
	@FXML private TextField txtName;
	private FirebaseVariable variable;
	private Stage stage;
	private ExpressionReceiver time;
	private TextField text;
	private RadioButton trueButton;
	private RadioButton falseButton;
	private Main gui;
	/**
	 * Check all fields are filled in correctly, works for subclasses of this popup too
	 */
	private boolean verify(){
		String name = txtName.getText();
		if (name.length() == 0) {
			lblError.setText("Please enter a name in the name text box");
			return false;
		}
		//Make sure the custom name is only alphanumeric characters
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(name);
		if (m.find()) {
			lblError.setText("Please enter only alphanumeric characters");
			return false;
		}
		if(cboVarType.getValue() == null){
			lblError.setText("Please give a type to this variable");
			return false;
		}


		variable.setVartype(cboVarType.getValue());
		variable.setname(name);
		variable.setisCustom(true);
		lblError.setText("");
switch(cboVarType.getValue()){
		
		case Expression.VAR_BOOLEAN: variable.setValue(trueButton.isSelected() ? "true" : "false");break;
		case Expression.VAR_CLOCK: variable.setValue(time.getText());break;
		case Expression.VAR_NUMERIC: variable.setValue(text.getText());break;
		case Expression.VAR_LOCATION: variable.setValue(text.getText());break;
		}
		cboVarType.setValue("");
		txtName.setText("");
		
		return true;
	}

	@FXML
	public void handleSaveClick(Event e) { // NO_UCD (unused code)
		if(!verify())return;
		gui.addVariable(variable);
		gui.loadVariables();
		stage.hide();
	}
	@FXML
	public void handleCloseClick(Event e) { // NO_UCD (unused code)
		stage.hide();
	}
	public void init(){
		variable = new FirebaseVariable();
		cboVarType.getItems().addAll(Expression.VAR_BOOLEAN,Expression.VAR_NUMERIC,Expression.VAR_CLOCK,Expression.VAR_LOCATION);
		cboVarType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String newVal) {
				text = new TextField();
				text.setPrefHeight(25);
				
				time = new ExpressionReceiver(Expression.VAR_CLOCK);
				time.getTextField().setFont(new Font("Calibri",16));
				trueButton = new RadioButton();
				trueButton.setText("true");
				falseButton = new RadioButton();
				falseButton.setText("false");
				ToggleGroup group = new ToggleGroup(); // for when we're updating a
				trueButton.setToggleGroup(group);
				falseButton.setToggleGroup(group);
				trueButton.setSelected(true);
			}
			
		});
		cboVarType.setValue(Expression.VAR_BOOLEAN);

	}
	public boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	public VariablePane(Stage stage) {
		Main gui = Main.getContext();
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/newvarpopup.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);	
			this.stage = stage;
			init();
			} catch (Exception e) {
				e.printStackTrace();
			}
	//	getStylesheets().add(ViewElement.class.getResource("ButtonsDemo.css").toExternalForm());
	}
}
