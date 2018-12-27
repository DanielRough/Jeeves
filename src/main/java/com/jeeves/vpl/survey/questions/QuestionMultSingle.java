package com.jeeves.vpl.survey.questions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.jeeves.vpl.firebase.FirebaseQuestion;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import static com.jeeves.vpl.Constants.*;

public class QuestionMultSingle extends QuestionView {

	@FXML
	private Button btnAddOptS;
	@FXML
	private VBox paneChoiceOptsS;
	@FXML
	private Pane paneMultChoiceS;
	@FXML
	private ScrollPane paneOptionsS;

	public QuestionMultSingle(String label)  throws Exception {
		this(new FirebaseQuestion(label));
	}

	public QuestionMultSingle(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public void addEventHandlers() {
		btnAddOptS.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				handleAddOpt(paneChoiceOptsS, "");// Add a blank option
			}
		});
	}
	@Override
	public String getImagePath() {
		return "/img/icons/imgsingle.png";
	}

	@Override
	public String getQuestionType() {
		return MULT_SINGLE;
	}


	public void handleAddOpt(VBox choices, String s) { // NO_UCD (unused code)

		HBox optionBox = new HBox();
		optionBox.setSpacing(2);
		TextField choice = new TextField();
		choice.setText(s);
		Button remove = new Button();
		String[] opts = new String[0];

		remove.setText("X");
		remove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if(choices.getChildren().size() == 1){ //THIS IS OUR LAST OPTION DON'T REMOVE IT
					e.consume();
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Options required");
					alert.setHeaderText(null);
					alert.setContentText("Multi-choice questions must have at least one option!");
					alert.showAndWait();
					return;
				}
				if(!getInstance().getChildQuestions().isEmpty()){
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Dependent child question");
					alert.setHeaderText(null);
					alert.setContentText("Child questions may depend on these answers");
					alert.showAndWait();
					return;
				}
				choices.getChildren().remove(optionBox);
				Map<String, Object> qOptions = new HashMap<String, Object>();
				int optcount = 1;
				for (Node opt : choices.getChildren()) {
					HBox optbox = (HBox) opt;
					TextField opttext = (TextField) optbox.getChildren().get(0);
					qOptions.put("option" + Integer.toString(optcount++), opttext.getText());
					model.getparams().put("options",qOptions);
					categoryOpts.put(getAssignedVar(), qOptions.values().toArray(opts));

					
				}
			}
		});

		optionBox.getChildren().addAll(choice, remove);
		choices.getChildren().add(optionBox);
		choice.textProperty().addListener(handler->{

			Map<String, Object> qOptions = new HashMap<String, Object>();
			int optcount = 1;
			for (Node opt : choices.getChildren()) {
				HBox optbox = (HBox) opt;
				TextField opttext = (TextField) optbox.getChildren().get(0);
				qOptions.put("option" + Integer.toString(optcount++), opttext.getText());
				model.getparams().put("options",qOptions);
			}

			categoryOpts.put(getAssignedVar(), qOptions.values().toArray(opts));

		});

		Map<String, Object> qOptions = new HashMap<String, Object>();
		int optcount = 1;

		for (Node opt : choices.getChildren()) {
			HBox optbox = (HBox) opt;
			TextField opttext = (TextField) optbox.getChildren().get(0);
			qOptions.put("option" + Integer.toString(optcount++), opttext.getText());
			model.getparams().put("options",qOptions);

		}
	}

	
	@Override
	public void loadOptions() {
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/OptionsMultiChoiceSingle.fxml"));
		try {
			optionsPane = (Pane) surveyLoader.load();
			addEventHandlers();

		} catch (IOException e) {
		}
	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		paneChoiceOptsS.getChildren().clear();

		if (opts.isEmpty()){
			handleAddOpt(paneChoiceOptsS,"A");
			handleAddOpt(paneChoiceOptsS,"B");
			return;
		}

		Iterator<String> iter = opts.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			handleAddOpt(paneChoiceOptsS, opts.get(key).toString());

		}

	}


}
