package com.jeeves.vpl;

import static com.jeeves.vpl.Constants.*;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import com.jeeves.vpl.firebase.FirebaseVariable;

/**
 * This is the controller class for a Custom variable, which appears when we
 * want to save a new variable to the database
 *
 * @author Daniel
 *
 */
public class VariablePane extends Pane { // NO_UCD (use default)

	@FXML
	private ComboBox<String> cboVarType;
	private Main gui;
	@FXML
	private Label lblError;
	private Stage stage;
	@FXML
	private TextField txtName;
	private FirebaseVariable variable;

	public VariablePane(Stage stage) {
		this.gui = Main.getContext();
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/PopupNewVariable.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
			this.stage = stage;
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init() {
		variable = new FirebaseVariable();
		cboVarType.getItems().addAll(VAR_BOOLEAN, VAR_NUMERIC, VAR_CLOCK, VAR_DATE, VAR_LOCATION);
		cboVarType.setValue(VAR_BOOLEAN);

	}

	@FXML
	private void handleCloseClick(Event e) {
		stage.hide();
	}

	@FXML
	private void handleSaveClick(Event e) {
		if (!verify())
			return;
		gui.addVariable(variable);
		gui.loadVariables();
		stage.hide();
	}

	/**
	 * Check all fields are filled in correctly, works for subclasses of this
	 * popup too
	 */
	private boolean verify() {
		String name = txtName.getText();
		if (name.length() == 0) {
			lblError.setText("Please enter a name in the name text box");
			return false;
		}
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(name);
		if (m.find()) {
			lblError.setText("Please enter only alphanumeric characters");
			return false;
		}
		if (cboVarType.getValue() == null) {
			lblError.setText("Please give a type to this variable");
			return false;
		}

		variable.setVartype(cboVarType.getValue());
		variable.setname(name);
		variable.setisCustom(true);
		lblError.setText("");

		cboVarType.setValue("");
		txtName.setText("");

		return true;
	}
}
