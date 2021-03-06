package com.jeeves.vpl.survey.questions;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.firebase.FirebaseQuestion;

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

public class QuestionMultMany extends QuestionView {
	private static final String OPTION = "option";
	private static final String OPTIONS = "options";
	@FXML
	private Button btnAddOptM;

	@FXML
	private VBox paneChoiceOptsM;

	@FXML
	private Pane paneMultChoiceM;

	@FXML
	private ScrollPane paneOptionsM;

	public QuestionMultMany(String label) {
		this(new FirebaseQuestion(label));
	}

	public QuestionMultMany(FirebaseQuestion data) {
		super(data);
	}
	@Override
	public void addEventHandlers() {
		btnAddOptM.setOnAction(e -> 
				handleAddOpt(paneChoiceOptsM, ""));
	}
	@Override
	public String getImagePath() {
		return "/img/icons/imgmany.png";
	}


	@Override
	public String getQuestionType() {
		return MULT_MANY;
	}

	/**
	 * Add an option to a multiple choice question
	 * 
	 * @param s
	 *            The option text
	 */
	public void handleAddOpt(VBox choices, String s) { // NO_UCD (unused code)
		HBox optionBox = new HBox();
		optionBox.setSpacing(2);
		TextField choice = new TextField();
		choice.setText(s);
		Button remove = new Button();
		remove.setText("X");
		remove.setOnAction(e -> {
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
				Map<String, Object> qOptions = new HashMap<>();
				int optcount = 1;
				for (Node opt : choices.getChildren()) {
					HBox optbox = (HBox) opt;
					TextField opttext = (TextField) optbox.getChildren().get(0);
					qOptions.put(OPTION + Integer.toString(optcount++), opttext.getText());
					model.getparams().put(OPTIONS,qOptions);
				}
			
		});

		optionBox.getChildren().addAll(choice, remove);
		choices.getChildren().add(optionBox);
		choice.textProperty().addListener(handler->{
			Map<String, Object> qOptions = new HashMap<>();
			int optcount = 1;
			for (Node opt : choices.getChildren()) {
				HBox optbox = (HBox) opt;
				TextField opttext = (TextField) optbox.getChildren().get(0);
				qOptions.put(OPTION + Integer.toString(optcount++), opttext.getText());
				model.getparams().put(OPTIONS,qOptions);
			}
		});
		Map<String, Object> qOptions = new HashMap<>();
		int optcount = 1;

		for (Node opt : choices.getChildren()) {
			HBox optbox = (HBox) opt;
			TextField opttext = (TextField) optbox.getChildren().get(0);
			qOptions.put(OPTION + Integer.toString(optcount++), opttext.getText());
			model.getparams().put(OPTIONS,qOptions);
		}
	}


	@Override
	public void loadOptions() {
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/OptionsMultiChoiceMany.fxml"));
		try {
			optionsPane = surveyLoader.load();
			addEventHandlers();
		} catch (IOException e) {
			System.exit(1);
		}
	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		paneChoiceOptsM.getChildren().clear();
		if (opts.isEmpty()){
			handleAddOpt(paneChoiceOptsM,"A");
			handleAddOpt(paneChoiceOptsM,"B");

			return;
		}
		TreeMap<String, Object> sortedmap = new TreeMap<String, Object>(new Comparator<String>() {
		    @Override
		    public int compare(String o1, String o2) {
		    	System.out.println("o1 is " + o1);
		      return Integer.parseInt(o1.substring(6)) - Integer.parseInt(o2.substring(6)); //Gets rid of the 'option' bit that
		      																				//I've inexplicably added to each option
		    }
		  });
	sortedmap.putAll(opts);
		for (Object opt : sortedmap.values()) {
			handleAddOpt(paneChoiceOptsM, opt.toString());
		}
	}
	@Override
	public String getAnswerType() {
		return Constants.VAR_CATEGORY;
	}
}
