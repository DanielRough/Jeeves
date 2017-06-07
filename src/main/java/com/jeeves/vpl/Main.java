package com.jeeves.vpl;

import static com.jeeves.vpl.Constants.SHOULD_UPDATE_TRIGGERS;
import static com.jeeves.vpl.Constants.VAR_BOOLEAN;
import static com.jeeves.vpl.Constants.VAR_CLOCK;
import static com.jeeves.vpl.Constants.VAR_DATE;
import static com.jeeves.vpl.Constants.VAR_LOCATION;
import static com.jeeves.vpl.Constants.VAR_NUMERIC;
import static com.jeeves.vpl.Constants.actionNames;
import static com.jeeves.vpl.Constants.exprNames;
import static com.jeeves.vpl.Constants.questionNames;
import static com.jeeves.vpl.Constants.triggerNames;
import static com.jeeves.vpl.Constants.uiElementNames;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.ElementReceiver;
import com.jeeves.vpl.canvas.triggers.Trigger;
import com.jeeves.vpl.canvas.uielements.UIElement;
import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseProject;
import com.jeeves.vpl.firebase.FirebaseQuestion;
import com.jeeves.vpl.firebase.FirebaseSurvey;
import com.jeeves.vpl.firebase.FirebaseTrigger;
import com.jeeves.vpl.firebase.FirebaseUI;
import com.jeeves.vpl.firebase.FirebaseVariable;
import com.jeeves.vpl.survey.Survey;
import com.jeeves.vpl.survey.SurveyPane;
import com.jeeves.vpl.survey.questions.QuestionView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The controller class for our main GUI. A lot of boggy setup code in here but
 * it gets the job done. This is like a combination of the View/Controller,
 * where the FirebaseDB class acts as our model!
 * 
 * @author Daniel
 *
 */
public class Main extends Application {

	private static Main currentGUI;
	public static Main getContext() {
		return currentGUI;
	}
	public static void main(String[] args) {
		launch(args);
	}
	@FXML
	private Button btnAddVar;
	private ViewCanvas canvas; // The canvas group that contains the project

	private ObservableList<FirebaseUI> currentelements = FXCollections.observableList(new ArrayList<FirebaseUI>());
	private FirebaseProject currentproject; // The currently selected project
	private ObservableList<FirebaseSurvey> currentsurveys = FXCollections
			.observableList(new ArrayList<FirebaseSurvey>());
	private ObservableList<FirebaseVariable> currentvariables = FXCollections
			.observableList(new ArrayList<FirebaseVariable>());
	private DragPane dragPane;
	private FirebaseDB firebase;
	@FXML
	private ImageView imgPhone;
	@FXML
	private ImageView imgTrash;
	private boolean isNewProject = true;

	private Map<Label, VBox> labelPaneMap;
	@FXML
	private Label lblActions;
	@FXML
	private Label lblConditions;
	@FXML
	private Label lblTriggers;
	// @FXML private Label lblUIElements;
	@FXML
	private MenuBar mnuBar;

	@FXML
	private ContextMenu mnuContext;
	@FXML
	private Menu mnuFile;
	@FXML
	private Menu mnuStudies;
	private AnchorPane myPane; // The main pane
	@FXML
	private VBox paneActions;
	@FXML
	private Pane paneAndroid;
	@FXML
	private VBox paneConditions;
	@FXML
	private Pane paneFrame;
	@FXML
	private Pane paneIcons;
	@FXML
	private Pane paneIntervention;
	@FXML
	private ScrollPane paneMain;

	@FXML
	private VBox paneQuestions;
	@FXML
	private VBox paneTriggers;
//	@FXML
//	private VBox paneVariables;
	private PatientPane patientController;

	private Stage primaryStage;
	private ElementReceiver receiver; // Where UI elements go
	@FXML
	private SplitPane splitPane;
	@FXML
	private HBox surveyBox;
	private SurveyPane surveyController;
	@FXML
	private Tab tabFramework;
	@FXML
	private TabPane tabPane;
	@FXML
	private Tab tabSurvey;
	@FXML
	private Tab tabUsers;

	@FXML
	private VBox vboxConfig;
	@FXML
	private VBox vboxFrame;
	@FXML
	private VBox vboxSurveyVars;

	@FXML
	private VBox vboxUIElements;

	@FXML
	private TitledPane paneLogic;
	
	@FXML 
	private TextField txtAttrName;
	@FXML
	private ChoiceBox<String> cboAttrType;
	
	private StringProperty connectedStatus;
	
	public void updateConnecetedStatus(boolean connected){
		if(connected)
			connectedStatus.set("Online - ready to make app changes!");
		else
			connectedStatus.set("Offline - changes will be made on reconnection");
	}
	private EventHandler<MouseEvent> viewElementHandler;

	ArrayList<ViewElement> elements;

	public Main() {
	}

	private Main(Stage primaryStage) {
		currentGUI = this;
		this.primaryStage = primaryStage;
		primaryStage.setResizable(false);
		connectedStatus = new SimpleStringProperty();
		firebase = new FirebaseDB(this);
		firebase.addListeners();
		firebase.getprojects().addListener(new ListChangeListener<FirebaseProject>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FirebaseProject> c) {
				loadProjectsIntoMenu();

			}
		});
		// addListeners();
		primaryStage.setTitle("Jeeves - New Project");
		Platform.setImplicitExit(false);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Main.fxml"));
		fxmlLoader.setController(this);
		try {
			myPane = (AnchorPane) fxmlLoader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		fxmlLoader.setController(this);
		Scene scene = new Scene(myPane);
		dragPane = new DragPane(myPane.getWidth(), myPane.getHeight());
		myPane.getChildren().add(dragPane);
		primaryStage.setOnCloseRequest(event -> System.exit(0));
		primaryStage.setScene(scene);
		splitPane.lookupAll(".split-pane-divider").stream().forEach(div -> div.setMouseTransparent(true));

		patientController = new PatientPane(this, firebase);
		tabUsers.setContent(patientController);
		currentproject = new FirebaseProject();
		
		paneLogic.setText(connectedStatus.get());
		connectedStatus.addListener(new ChangeListener<String>(){

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
				paneLogic.setText(newValue);
					}
				});
			}
			
		});

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				resetPanes();
				loadCanvasElements();
				loadVariables();

			}
		});

	}

	public void addVariable(FirebaseVariable var) {
		currentproject.getvariables().add(var);
	}

	public FirebaseProject getCurrentProject() {
		return this.currentproject;
	}

	public AnchorPane getMainPane() {
		return myPane;
	}

	public ObservableList<FirebaseSurvey> getSurveys() {
		return currentsurveys;
	}

	public ObservableList<FirebaseUI> getUIElements() {
		return currentelements;
	}

	public ObservableList<FirebaseVariable> getVariables() {
		return currentvariables;
	}

	public ViewCanvas getViewCanvas() {
		return canvas;
	}

	public void hideMenu() {
		mnuFile.hide();
	}

	public void loadVariables() {
		currentvariables.clear();
		vboxSurveyVars.getChildren().clear();

		String[] globalVarNames = new String[] { "Missed Surveys", "Completed Surveys", "Last Survey Score",
				"Survey Score Difference" };
		ArrayList<FirebaseVariable> globalVars = new ArrayList<FirebaseVariable>();
		for (String name : globalVarNames) {
			FirebaseVariable var = new FirebaseVariable();
			var.setname(name);
			var.setVartype(VAR_NUMERIC);
			currentproject.getvariables().add(var);
			globalVars.add(var);
		}
		currentproject.getvariables().forEach(variable -> {
			UserVariable global = new UserVariable(variable);
			String varname = variable.getname();
			boolean alreadyExists = false;

			for (int i = 0; i < currentvariables.size(); i++) {
				if (currentvariables.get(i).getname().equals(varname))
					alreadyExists = true;
			}
			if (alreadyExists == false) {
				currentvariables.add(variable);
				ViewElement<FirebaseExpression> draggable = new UserVariable(variable);
				global.setDraggable(draggable); // DJRNEW
				global.setReadOnly();
				setElementParent(draggable);
				global.setHandler(viewElementHandler);
				vboxSurveyVars.getChildren().add(global);
			}

		});
		for (FirebaseVariable var : globalVars) {
			currentproject.getvariables().remove(var); // gotta take them out
														// again or they get
														// duplicated every time
		}
		cboAttrType.getItems().clear();
		cboAttrType.getItems().addAll("True/False","Date","Time","Location","Number");
	}

	@FXML
	public void newStudy(Event e) {
		currentproject = new FirebaseProject();
		isNewProject = true;
		primaryStage.setTitle("Jeeves - New Project");
		// isNewProject = true;

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				resetPanes();
				for (ViewElement element : elements) {
					ViewElement<FirebaseVariable> draggable = ViewElement.create(element.getClass().getName());
					element.setDraggable(draggable); // DJRNEW
					setElementParent(draggable);
					element.setReadOnly();
					element.setPickOnBounds(false);
					element.setHandler(viewElementHandler);
				}
				loadVariables();
			}
		});
	}

	@FXML
	public void quitStudyMenu(Event e) {
		System.exit(0);
	}

	public void registerElementListener(ListChangeListener<FirebaseUI> listener) {
		currentelements.addListener(listener);
	}

	public void registerSurveyListener(ListChangeListener<FirebaseSurvey> listener) {
		currentsurveys.addListener(listener);
	}

	public void registerVarListener(ListChangeListener<FirebaseVariable> listener) {
		currentvariables.addListener(listener);
	}

	@FXML
	public void saveAsStudyMenu(Event e) {
		Stage stage = new Stage(StageStyle.UNDECORATED);
		SaveAsPane root = new SaveAsPane(this, stage, currentproject, firebase);
		stage.setScene(new Scene(root));
		stage.setTitle("Add property");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(splitPane.getScene().getWindow());
		stage.showAndWait();

		primaryStage.setTitle("Jeeves - " + currentproject.getname());

	}

	@FXML
	public void saveStudyMenu(Event e) {
		//Do some validation on the survye names here
		ArrayList<String> currentnames = new ArrayList<String>();
		for(FirebaseSurvey survey : currentsurveys){
			if(survey.gettitle().equals("New survey")){
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("No name given to survey");
				alert.setHeaderText(null);
				alert.setContentText("All surveys must have a title (not 'New survey!')");
				alert.showAndWait();
				return;
			}
			else if(currentnames.contains(survey.gettitle())){
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Duplicate survey names");
				alert.setHeaderText(null);
				alert.setContentText("All surveys must have unique names");
				alert.showAndWait();
				return;
			}
			currentnames.add(survey.gettitle());
		};
		if (isNewProject) {
			saveAsStudyMenu(e);
			return;
		} else {
			firebase.addProject("", this.currentproject);
		}

	}

	public void setNewProject(boolean isNew) {
		this.isNewProject = isNew;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		new Main(primaryStage);
	}

	// --------------------------------------- UI Triggered Methods
	// ----------------------------------------------

	@FXML
	private void addHighlight(Event e) {
		Label label = (Label) e.getSource();
		label.setTextFill(Color.ORANGE);
	}

	@FXML
	private void addVariable(Event e) {
		Stage stage = new Stage(StageStyle.UNDECORATED);
		VariablePane root = new VariablePane(stage);
		stage.setScene(new Scene(root));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(btnAddVar.getScene().getWindow());
		stage.showAndWait();

	}

	private VBox createBoxyBox(ViewElement elem) {
		Label newlable = new Label(elem.getName());

		HBox box = new HBox();
		box.setSpacing(3);
		// ImageView infoIcon = createInfoIcon();
		VBox boxybox = new VBox();
		// boxybox.setSpacing(3);
		// boxybox.setOnMouseEntered(event->{infoIcons.forEach(info->info.setOpacity(0));infoIcon.setOpacity(100);});
		// boxybox.setOnMouseExited(event->{Point2D point = new
		// Point2D(event.getSceneX(),event.getSceneY());
		// if(!boxybox.localToScene(boxybox.getBoundsInLocal()).contains(point)){infoIcon.setOpacity(0);}});
		// boxybox.setPrefWidth(elem.getWidth());
		newlable.prefWidthProperty().bind(elem.widthProperty());
		box.setFillHeight(true);
		box.getChildren().addAll(newlable);
		boxybox.getChildren().addAll(box, elem);
		boxybox.setFillWidth(true);
		// newlable.setStyle("-fx-background-color: blue");
		// box.setPadding(new Insets(0,0,0,-15));
		Tooltip t = new Tooltip(elem.description);
		// hackTooltipStartTiming(t); //A wonderful wonderful method someone
		// else made
		// Tooltip.install(
		// infoIcon,
		// t
		// );
		newlable.setFont(Font.font("Calibri", FontWeight.NORMAL, 16));

		return boxybox;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadCanvasElements() {
		Divider d1 = splitPane.getDividers().get(1);

		d1.positionProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				if (arg2.doubleValue() < 0.7)
					d1.setPosition(0.7);
				else
					imgTrash.setLayoutX(arg2.doubleValue() * myPane.getWidth() - 50);
			}

		});
		d1.setPosition(0.7);
		viewElementHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				ViewElement clicked = ((ViewElement) arg0.getSource());
				if (arg0.isSecondaryButtonDown())
					return;
				if (arg0.getEventType() == MouseEvent.MOUSE_PRESSED)
					myPane.getChildren().add(clicked.getDraggable());
				if (arg0.getEventType() == MouseEvent.MOUSE_RELEASED) {
					// if(canvas.getIsMouseOver() == false){
					myPane.getChildren().remove(clicked.getDraggable());
					// }
					ViewElement<FirebaseExpression> draggable = null;
					// annoying exception for user variables
					if (clicked.getType() == ElementType.VARIABLE)
						draggable = new UserVariable(((FirebaseExpression) clicked.getModel()));
					else
						draggable = ViewElement.create(clicked.getClass().getName());
					setElementParent(draggable);
					clicked.setDraggable(draggable); // DJRNEW

				}
			}

		};
		try {
			elements = new ArrayList<ViewElement>();
			for (String trigName : triggerNames) {
				ViewElement trigger = ViewElement.create(trigName);
				elements.add(trigger);
				VBox boxybox = createBoxyBox(trigger);
				paneTriggers.getChildren().add(boxybox);
			}
			for (String actName : actionNames) {
				ViewElement<FirebaseVariable> action = ViewElement.create(actName);
				elements.add(action);
				VBox boxybox = createBoxyBox(action);
				paneActions.getChildren().add(boxybox);
			}
			for (String exprName : exprNames) {
				ViewElement<FirebaseVariable> expr = ViewElement.create(exprName);
				elements.add(expr);
				VBox boxybox = createBoxyBox(expr);
				paneConditions.getChildren().add(boxybox);
			}
			for (String uiElem : uiElementNames) {
				ViewElement<FirebaseVariable> uielement = ViewElement.create(uiElem);
				elements.add(uielement);
				vboxUIElements.getChildren().add(uielement);
			}
			for (String qName : questionNames) {
				ViewElement<FirebaseQuestion> question = ViewElement.create(qName);
				elements.add(question);
				paneQuestions.getChildren().add(question);
			}
			for (ViewElement element : elements) {
				element.setPadding(new Insets(0, 0, 10, 0));
				ViewElement<FirebaseVariable> draggable = ViewElement.create(element.getClass().getName());
				element.setDraggable(draggable); // DJRNEW
				setElementParent(draggable);
				element.setReadOnly();
				element.setPickOnBounds(false);
				element.setHandler(viewElementHandler);

			}
			loadVariables();
		} catch (Exception e) {
			e.printStackTrace();
		}

		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> arg0, Tab arg1, Tab arg2) {
				Divider divider = splitPane.getDividers().get(0);
				if (arg2 != null && arg2.equals(tabFramework))
					divider.setPosition(0.3);
				else
					divider.setPosition(0.7);
			}
		});

		labelPaneMap = new HashMap<Label, VBox>();
		labelPaneMap.put(lblActions, paneActions);
		labelPaneMap.put(lblConditions, paneConditions);
		labelPaneMap.put(lblTriggers, paneTriggers);
		lblTriggers.setUserData("selected");
		DropShadow ds = new DropShadow(20, Color.AQUA);

		imgTrash.addEventHandler(MouseDragEvent.ANY, new EventHandler<MouseDragEvent>() {

			@Override
			public void handle(MouseDragEvent arg0) {
				if (arg0.getEventType().equals(MouseDragEvent.MOUSE_DRAG_ENTERED)) {
					imgTrash.setEffect(ds);
				} else if (arg0.getEventType().equals(MouseDragEvent.MOUSE_DRAG_EXITED)) {
					imgTrash.setEffect(null);
				}
			}

		});
		primaryStage.show();

	}

	public void setCurrentProject(FirebaseProject project){
		SHOULD_UPDATE_TRIGGERS = false;

		currentproject = project;
		primaryStage.setTitle("Jeeves - " + project.getname());
		patientController.loadPatients(); // Reset so we have the
											// patients for THIS project
		resetPanes();
		isNewProject = false;
		for (ViewElement element : elements) {
			ViewElement<FirebaseVariable> draggable = ViewElement.create(element.getClass().getName());
			element.setDraggable(draggable); // DJRNEW
			setElementParent(draggable);
			element.setReadOnly();
			element.setPickOnBounds(false);
			element.setHandler(viewElementHandler);
		}
		loadVariables();
		SHOULD_UPDATE_TRIGGERS = true;

	}
	private void loadProjectsIntoMenu() {
		mnuStudies.getItems().clear();
		firebase.getprojects().forEach(project -> {
			
			MenuItem item = new MenuItem(project.getname());
			mnuStudies.getItems().add(item);
			item.setOnAction(action -> {
				// isNewProject = false;
				//if(currentproject.getname() != null)

						firebase.loadProject(project.getname());

				});
				//currentproject = project; // Sets this project as the current

		});
	}

	@FXML
	private void removeHighlight(Event e) {
		Label label = (Label) e.getSource();
		if (label.getUserData() == null || !label.getUserData().equals("selected"))
			label.setTextFill(Color.BLACK);
	}

	private void resetPanes() {
		currentsurveys.clear();
		currentvariables.clear();
		currentelements.clear();

		if (canvas != null && canvas.mouseHandler != null)
			paneIntervention.removeEventHandler(MouseEvent.ANY, canvas.mouseHandler);
		paneIntervention.getChildren().remove(canvas);
		canvas = new ViewCanvas();
		paneIntervention.getChildren().add(canvas);
		canvas.addEventHandlers();

		paneAndroid.getChildren().clear();
		// QuestionReceiver myreceiver = new
		// QuestionReceiver(paneAndroid.getPrefWidth(),paneAndroid.getPrefHeight());
		// TODO: Get hardcoded variables out of here
		receiver = new ElementReceiver(215, 307);

		paneAndroid.getChildren().add(receiver);
		if (surveyBox.getChildren().size() > 1)
			surveyBox.getChildren().remove(1);
		surveyController = new SurveyPane();
		
		surveyBox.getChildren().add(surveyController); // reset dat shit
		surveyController.registerSurveyListener(new ListChangeListener<FirebaseSurvey>(){

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FirebaseSurvey> c) {
				c.next();
				if(c.wasAdded()){
					c.getAddedSubList().forEach(survey->{currentsurveys.add(survey);currentproject.getsurveys().add(survey);});
					
				}
			}
			
		});
		// receiver.setProject(currentproject);
		// paneAndroid.getChildren().add(receiver);
		setProject();
		receiver.getChildElements().addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> arg0) {
				arg0.next();
				if (arg0.wasAdded()) {
					ViewElement added = (ViewElement) arg0.getAddedSubList().get(0);
					int index = receiver.getChildElements().indexOf(added);
					FirebaseUI uiModel = (FirebaseUI) added.getModel();
					
					if(uiModel.gettext()!=null){
						currentproject.getuidesign().add(index, uiModel);
						currentelements.add(index, uiModel);
					}
					else
					//We wait until we've set the text before we actually add it
					uiModel.getMyTextProperty().addListener(listener ->{
						currentproject.getuidesign().add(index, uiModel);
						currentelements.add(index, uiModel);
					});
				} else {
					List<ViewElement> removed = (List<ViewElement>) arg0.getRemoved();
					removed.forEach(elem -> {
						currentproject.getuidesign().remove(elem.getModel());
						currentelements.remove(elem.getModel());
					});

				}

			}

		});
		ListChangeListener<Node> canvasListener = new ListChangeListener<Node>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> arg0) {
				while (arg0.next()) {
					if (arg0.wasAdded()) {
						List addedlist = arg0.getAddedSubList();
						currentproject.add((ViewElement) addedlist.get(0));
					} else if (arg0.wasRemoved()) {
						List removedlist = arg0.getRemoved();
						currentproject.remove((ViewElement) removedlist.get(0));

					}
				}
			}
		};
		canvas.addChildrenListener(canvasListener);

		currentelements.addAll(currentproject.getuidesign());
		currentsurveys.addAll(currentproject.getsurveys());

		tabPane.getSelectionModel().select(tabFramework);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setElementParent(ViewElement draggable) {
		if (draggable.getType() == ElementType.UIELEMENT || draggable.getType() == ElementType.QUESTION)
			draggable.setParentPane(dragPane);
		else
			draggable.setParentPane(canvas);
	}

	@FXML
	private void showMenu(Event e) {
		Label label = (Label) e.getSource();
		showMenu(label);

	}

	private void showMenu(Label label) {
		labelPaneMap.keySet().forEach(lbl -> {
			lbl.setUserData("unselected");
			lbl.setTextFill(Color.BLACK);
		});
		label.setUserData("selected");
		label.setTextFill(Color.ORANGE);
		labelPaneMap.entrySet().forEach(pane -> {
			pane.getValue().setVisible(false);
			paneFrame.getChildren().remove(pane.getValue());
		});
		VBox pane = labelPaneMap.get(label);

		if (!paneFrame.getChildren().contains(pane))
			paneFrame.getChildren().add(pane);
		pane.setVisible(true);
		Divider divider = splitPane.getDividers().get(0);
		double fractionwidth = paneFrame.getWidth() / splitPane.getWidth();
		divider.setPosition(fractionwidth);
//
//		if (pane.equals(paneConditions)) {
//			paneVariables.setVisible(true);
//			paneVariables.toFront();
//			divider.setPosition((30 + paneVariables.getBoundsInParent().getMaxX()) / splitPane.getWidth());
//		} else {
//			paneVariables.setVisible(false);
//			paneVariables.toBack();
//		}
	}

	boolean isOverTrash(double mouseX, double mouseY) {
		return imgTrash.getBoundsInParent().contains(mouseX, mouseY);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	void setProject() {
		ArrayList<ViewElement> views = new ArrayList<ViewElement>();
		for (FirebaseTrigger trig : currentproject.gettriggers()) {
			Trigger triggerview = Trigger.create(trig);
			views.add(triggerview);
		}
		for (FirebaseExpression expr : currentproject.getexpressions()) {
			Expression exprview = Expression.create(expr);
			views.add(exprview);
		}
		for (FirebaseVariable var : currentproject.getvariables()) {
			UserVariable varview = new UserVariable(var);
			views.add(varview);
		}
		int index = 0;
		for (FirebaseUI var : currentproject.getuidesign()) {

			UIElement element = UIElement.create(var);
			var.getMyTextProperty().addListener(change -> {
				receiver.getChildElements().remove(element);
				receiver.getChildElements().add(element);
			});
			element.previouslyAdded = true; // This is a bit hacky but stops the
											// boxes popping up when we load a
											// new project
			receiver.addChildAtIndex(element, index++);
			setElementParent(element);
			element.addEventHandler(MouseEvent.ANY, element.mainHandler);
		}
		for (FirebaseSurvey survey : currentproject.getsurveys()) {
			Survey surveyview = new Survey(survey);
			surveyController.addSurvey(surveyview);
			for (QuestionView q : surveyview.getQuestions()) {
				setElementParent(q); // This means we can drag questions around
			}
		}
		views.forEach(view -> {
			Point2D pos = view.getPosition();
			Point2D canvasPos = canvas.localToScene(pos);
			canvas.addChild(view, canvasPos.getX(), canvasPos.getY());
			setElementParent(view);
			view.addEventHandler(MouseEvent.ANY, view.mainHandler);
		});

	}
	
	//Attributes stuff
	
	@FXML
	public void addAttribute(Event e){
		FirebaseVariable var = new FirebaseVariable();
		String attrName = txtAttrName.getText();
		if(attrName.isEmpty()){
			return;
		}
		var.setname(attrName);
		String attrType = cboAttrType.getValue();
		if(attrType == null || attrType.isEmpty()){
			return;
		}
		switch(attrType){
		case "True/False":var.setVartype(VAR_BOOLEAN);break;
		case "Number":var.setVartype(VAR_NUMERIC);break;
		case "Date":var.setVartype(VAR_DATE); break;
		case "Time":var.setVartype(VAR_CLOCK);break;
		case "Location":var.setVartype(VAR_LOCATION);break;
		}
		System.out.println("VAR TYPE IS " + var.gettype());
		var.setisCustom(true);
		var.settimeCreated(System.currentTimeMillis());
		addVariable(var);
		loadVariables();
	//	UserVariable newvar = UserVariable.create(var);
		
	}
	class NameComparator implements Comparator<FirebaseVariable> {
	    @Override
	    public int compare(FirebaseVariable a, FirebaseVariable b) {
	    	return a.getname().compareToIgnoreCase(b.getname());
//	        return a.name.compareToIgnoreCase(b.name);
	    }
	}
	class TypeComparator implements Comparator<FirebaseVariable> {
	    @Override
	    public int compare(FirebaseVariable a, FirebaseVariable b) {
	        return a.getvartype().compareToIgnoreCase(b.getvartype());
	    }
	}
	class AgeComparator implements Comparator<FirebaseVariable> {
	    @Override
	    public int compare(FirebaseVariable a, FirebaseVariable b) {
	        return a.gettimeCreated() >= b.gettimeCreated() ? 1 : -1;
	    }
	}
	
	public void reAddVariables(){
		vboxSurveyVars.getChildren().clear();
		currentvariables.forEach(variable->{
			UserVariable global = new UserVariable(variable);

		ViewElement<FirebaseExpression> draggable = new UserVariable(variable);
		global.setDraggable(draggable); // DJRNEW
		global.setReadOnly();
		setElementParent(draggable);
		global.setHandler(viewElementHandler);
		vboxSurveyVars.getChildren().add(global);
		});
	}
	@FXML
	public void sortByName(Event e){
		currentvariables.sort(new NameComparator());
		reAddVariables();
	//	currentvariables.add(variable);
	
	}
	
	@FXML
	public void sortByTime(Event e){
		currentvariables.sort(new AgeComparator());
		reAddVariables();
	}
	
	@FXML
	public void sortByType(Event e){
		currentvariables.sort(new TypeComparator());
		reAddVariables();
	}
}