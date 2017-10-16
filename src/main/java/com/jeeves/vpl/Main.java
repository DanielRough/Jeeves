package com.jeeves.vpl;

import static com.jeeves.vpl.Constants.SHOULD_UPDATE_TRIGGERS;
import static com.jeeves.vpl.Constants.VAR_BLUETOOTH;
import static com.jeeves.vpl.Constants.VAR_BOOLEAN;
import static com.jeeves.vpl.Constants.VAR_CLOCK;
import static com.jeeves.vpl.Constants.VAR_DATE;
import static com.jeeves.vpl.Constants.VAR_LOCATION;
import static com.jeeves.vpl.Constants.VAR_NUMERIC;
import static com.jeeves.vpl.Constants.VAR_WIFI;
import static com.jeeves.vpl.Constants.actionNames;
import static com.jeeves.vpl.Constants.exprNames;
import static com.jeeves.vpl.Constants.makeInfoAlert;
import static com.jeeves.vpl.Constants.questionNames;
import static com.jeeves.vpl.Constants.triggerNames;
import static com.jeeves.vpl.Constants.uiElementNames;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

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
import com.jeeves.vpl.firebase.FirebaseSurveyEntry;
import com.jeeves.vpl.firebase.FirebaseTrigger;
import com.jeeves.vpl.firebase.FirebaseUI;
import com.jeeves.vpl.firebase.FirebaseVariable;
import com.jeeves.vpl.survey.Survey;
import com.jeeves.vpl.survey.SurveyPane;
import com.jeeves.vpl.survey.questions.QuestionView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
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

	private ViewCanvas canvas; // The canvas group that contains the project

	private FirebaseProject openProject; // The currently selected project
	
	private ObservableList<FirebaseSurvey> currentsurveys = FXCollections
			.observableList(new ArrayList<FirebaseSurvey>());
	private ObservableList<FirebaseVariable> currentvariables = FXCollections
			.observableList(new ArrayList<FirebaseVariable>());
	private ObservableList<FirebaseUI> currentelements = FXCollections
			.observableList(new ArrayList<FirebaseUI>());
	private ObservableMap<String,Map<String,FirebaseSurveyEntry>> currentsurveydata = FXCollections.observableHashMap();
	
	private DragPane dragPane;
	//private FirebaseDB firebase;
	private Map<Label, VBox> labelPaneMap;
	private AnchorPane myPane; // The main pane
	private PatientPane patientController;
	private SurveyPane surveyController;
	private Stage primaryStage;
	private ElementReceiver receiver; // Where UI elements go
	
	private NameComparator nameComparator;
	private AgeComparator ageComparator;
	private TypeComparator typeComparator;
	@FXML private VBox vboxSurveyVars;

	@FXML private Label lblActions;
	@FXML private Label lblConditions;
	@FXML private Label lblTriggers;
	@FXML private VBox paneActions;
	@FXML private VBox paneConditions;
	@FXML private VBox paneTriggers;
	@FXML private VBox vboxUIElements;

	@FXML private Pane paneAndroid;
	@FXML private Pane panePatients;
	@FXML private ImageView imgTrash;
	
	@FXML private Button btnAddVar;
	@FXML private TextField txtAttrName;
	@FXML private ChoiceBox<String> cboAttrType;


	@FXML private Pane paneFrame;
	@FXML private Pane paneIntervention;
	@FXML private VBox paneQuestions;
	@FXML private SplitPane splitPane;
	@FXML private HBox surveyBox;
	@FXML private Tab tabFramework;
	@FXML private Tab tabPatients;
	@FXML private TabPane tabPane;
	@FXML private Label lblWelcome;
	@FXML private Label lblConnection;
	@FXML private Label lblOpenProject;
	private EventHandler<MouseEvent> viewElementHandler;

	ArrayList<ViewElement> elements;

	public static Main getContext() {
		return currentGUI;
	}
	public static void main(String[] args) {
		launch(args);
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		new Main(primaryStage);
	}
	public Main() {
	}
	
	private Main(Stage primaryStage) {
		currentGUI = this;
		this.primaryStage = primaryStage;
		//connectedStatus = new SimpleStringProperty();

		Platform.setImplicitExit(false);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Main.fxml"));
		fxmlLoader.setController(this);
		try {
			myPane = (AnchorPane) fxmlLoader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Scene scene = new Scene(myPane);
		dragPane = new DragPane(myPane.getWidth(), myPane.getHeight());
		myPane.getChildren().add(dragPane);
		
		primaryStage.setTitle("Jeeves");
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(event -> System.exit(0));
		primaryStage.setScene(scene);
		splitPane.lookupAll(".split-pane-divider").stream().forEach(div -> div.setMouseTransparent(true));

		openProject = new FirebaseProject();
		lblOpenProject.setText("New project");
		FirebaseDB.setOpenProject(openProject);
		new FirebaseDB(this);
		nameComparator = new NameComparator();
		ageComparator = new AgeComparator();
		typeComparator = new TypeComparator();
		//currentproject = new FirebaseProject();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				resetPanes();
				loadCanvasElements();
				loadVariables();

			}
		});

		
	}
	
	/*
	 * GETTER AND SETTER METHODS
	 */
	public AnchorPane getMainPane() {
		return myPane;
	}

	public ObservableList<FirebaseSurvey> getSurveys() {
		return currentsurveys;
	}

	public ObservableList<FirebaseUI> getUIElements() {
		return currentelements;
	}

	public ObservableMap<String,Map<String,FirebaseSurveyEntry>> getSurveyEntries(){
		return currentsurveydata;
	}
	
	public ObservableList<FirebaseVariable> getVariables() {
		return currentvariables;
	}
	
	/**
	 * It's helpful to know whether we have an Internet connection, it may be that we don't see changes 
	 * propagate to the database immediately so it'd be nice to know why
	 */
	public void updateConnectedStatus(boolean connected){
		if(connected){
			System.out.println("Should be setting this?");
			lblConnection.setText("Connected!");
			lblConnection.setStyle("-fx-text-fill: #2ba42f");
		}
		else{
			lblConnection.setText("Disconnected, unable to sync changes");
			lblConnection.setStyle("-fx-text-fill: #bf2e2e");		
		}
	}
	/**
	 * When we reload elements from our saved files, this method ensures that each element knows its
	 * respective parent pane. This is done elsewhere when we physically drag and drop the element, but 
	 * needs to be manually done here
	 * @param draggable
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setElementParent(ViewElement draggable) {
		if (draggable.getType() == ElementType.UIELEMENT || draggable.getType() == ElementType.QUESTION)
			draggable.setParentPane(dragPane);
		else
			draggable.setParentPane(canvas);
	}
	
	private void refreshPatientPane(){
		panePatients.getChildren().clear();
		patientController = new PatientPane(this);
		panePatients.getChildren().add(patientController);
	}
	private void refreshCanvas(){
		if (canvas != null && canvas.mouseHandler != null)
			paneIntervention.removeEventHandler(MouseEvent.ANY, canvas.mouseHandler);
		paneIntervention.getChildren().remove(canvas);
		canvas = new ViewCanvas();
		paneIntervention.getChildren().add(canvas);
		canvas.addEventHandlers();
	}
	private void refreshInterfaceDesigner(){
		paneAndroid.getChildren().clear();
		receiver = new ElementReceiver(215, 307);
		paneAndroid.getChildren().add(receiver);
	}
	private void refreshSurveyCreator(){
		if (surveyBox.getChildren().size() > 1)
			surveyBox.getChildren().remove(1);
		surveyController = new SurveyPane();
		
		surveyBox.getChildren().add(surveyController); // reset dat shit
		surveyController.registerSurveyListener(new ListChangeListener<FirebaseSurvey>(){

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FirebaseSurvey> c) {
				c.next();
				if(c.wasAdded()){
					c.getAddedSubList().forEach(survey->{currentsurveys.add(survey);openProject.getsurveys().add(survey);});
					
				}
			}
			
		});
	}
	private void addListeners(){
		receiver.getChildElements().addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> arg0) {
				arg0.next();
				if (arg0.wasAdded()) {
					ViewElement added = (ViewElement) arg0.getAddedSubList().get(0);
					int index = receiver.getChildElements().indexOf(added);
					FirebaseUI uiModel = (FirebaseUI) added.getModel();
					
					if(uiModel.gettext()!=null){
						openProject.getuidesign().add(index, uiModel);
						currentelements.add(index, uiModel);
					}
					else
					//We wait until we've set the text before we actually add it
					uiModel.getMyTextProperty().addListener(listener ->{
						openProject.getuidesign().add(index, uiModel);
						currentelements.add(index, uiModel);
					});
				} else {
					List<ViewElement> removed = (List<ViewElement>) arg0.getRemoved();
					removed.forEach(elem -> {
						openProject.getuidesign().remove(elem.getModel());
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
						openProject.add((ViewElement) addedlist.get(0));
					} else if (arg0.wasRemoved()) {
						List removedlist = arg0.getRemoved();
						openProject.remove((ViewElement) removedlist.get(0));

					}
				}
			}
		};
		canvas.addChildrenListener(canvasListener);
	}
	private void resetPanes() {
		currentsurveys.clear();
		currentvariables.clear();
		currentelements.clear();
		currentsurveydata.clear();
		
		refreshCanvas();
		refreshInterfaceDesigner();
		refreshSurveyCreator();
		refreshPatientPane();
		
		addProjectElements();
		addListeners();
		

		currentelements.addAll(openProject.getuidesign());
		currentsurveys.addAll(openProject.getsurveys());
		if(openProject.getsurveydata() != null)
		currentsurveydata.putAll(openProject.getsurveydata());
		patientController.loadPatients(); // Reset so we have the			// patients for THIS project
		patientController.loadSurveys();
		tabPane.getSelectionModel().select(tabFramework);

	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void addProjectElements() {
		ArrayList<ViewElement> views = new ArrayList<ViewElement>();
		for (FirebaseTrigger trig : openProject.gettriggers()) {
			Trigger triggerview = Trigger.create(trig);
			views.add(triggerview);
		}
		for (FirebaseExpression expr : openProject.getexpressions()) {
			Expression exprview = Expression.create(expr);
			views.add(exprview);
		}
		for (FirebaseVariable var : openProject.getvariables()) {
			UserVariable varview = new UserVariable(var);
			views.add(varview);
		}
		int index = 0;
		for (FirebaseUI var : openProject.getuidesign()) {

			UIElement element = UIElement.create(var);
			var.getMyTextProperty().addListener(change -> {
				receiver.getChildElements().remove(element);
				receiver.getChildElements().add(element);
			});
			element.previouslyAdded = true; 
			receiver.addChildAtIndex(element, index++);
			setElementParent(element);
			element.addEventHandler(MouseEvent.ANY, element.mainHandler);
		}
		for (FirebaseSurvey survey : openProject.getsurveys()) {
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
	public void showLoginRegister(){
		Stage stage = new Stage(StageStyle.UNDECORATED);
		LoginRegisterPane root = new LoginRegisterPane(this, stage);
		stage.setScene(new Scene(root));
		Pane pane = new Pane();
		pane.getStyleClass().add("shadowpane");
		pane.setPrefWidth(myPane.getPrefWidth());
		pane.setPrefHeight(myPane.getPrefHeight());
		myPane.getChildren().add(pane);
		
		stage.initStyle(StageStyle.TRANSPARENT);
		
		stage.initOwner(splitPane.getScene().getWindow());

		stage.showAndWait();
		myPane.getChildren().remove(pane);
		Subject currentuser = SecurityUtils.getSubject();
		lblWelcome.setText("Welcome, " + currentuser.getPrincipal());
		
		FirebaseDB.getInstance().getUserCredentials(currentuser.getPrincipal().toString());
		patientController = new PatientPane(this);
		panePatients.getChildren().add(patientController);	
	}

	public void addVariable(FirebaseVariable var) {
		openProject.getvariables().add(var);
	}




	public void loadVariables() {
		currentvariables.clear();
		vboxSurveyVars.getChildren().clear();

		String[] globalVarNames = new String[] { "Missed Surveys", "Completed Surveys", "Last Survey Score",
				"Survey Score Difference" };
		ArrayList<FirebaseVariable> globalVars = new ArrayList<FirebaseVariable>();
//		for (String name : globalVarNames) {
//			FirebaseVariable var = new FirebaseVariable();
//			var.setname(name);
//			var.setVartype(VAR_NUMERIC);
//			openProject.getvariables().add(var);
//			globalVars.add(var);
//		}
		openProject.getvariables().forEach(variable -> {
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
			openProject.getvariables().remove(var); // gotta take them out
														// again or they get
														// duplicated every time
		}
		//TODO: These should really be read from the Constants class
		cboAttrType.getItems().clear();
		cboAttrType.getItems().addAll("True/False","Date","Time","Location","WiFi","Bluetooth","Number");
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
	
	public void registerSurveyDataListener(MapChangeListener<String,Object> listener){
		currentsurveydata.addListener(listener);
	}

	@FXML
	public void saveAsStudyMenu(Event e) {
		Stage stage = new Stage(StageStyle.UNDECORATED);
		SaveAsPane root = new SaveAsPane(stage);
		stage.setScene(new Scene(root));
		stage.setTitle("Add property");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(splitPane.getScene().getWindow());
		stage.showAndWait();

	}


	private VBox createBoxyBox(ViewElement elem) {
		Label newlable = new Label(elem.getName());

		HBox box = new HBox();
		box.setSpacing(15);
		VBox boxybox = new VBox();
		newlable.prefWidthProperty().bind(elem.widthProperty());
		box.setFillHeight(true);
		box.getChildren().add(newlable);
		boxybox.getChildren().addAll(box, elem);
		boxybox.setFillWidth(true);
		newlable.setFont(Font.font("Calibri", FontWeight.NORMAL, 16));
		
		return boxybox;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadCanvasElements() {
		Divider d1 = splitPane.getDividers().get(1);
		d1.positionProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				if (arg2.doubleValue() < 0.73)
					d1.setPosition(0.73);
				else
					imgTrash.setLayoutX(arg2.doubleValue() * myPane.getWidth() - 80);
			}

		});
		d1.setPosition(0.73);
		viewElementHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				ViewElement clicked = ((ViewElement) arg0.getSource());
				if (arg0.isSecondaryButtonDown())
					return;
				if (arg0.getEventType() == MouseEvent.MOUSE_PRESSED)
					myPane.getChildren().add(clicked.getDraggable());
				if (arg0.getEventType() == MouseEvent.MOUSE_RELEASED) {
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
				if(element instanceof QuestionView)
					element.setPadding(new Insets(0,0,10,0));
				else	
					element.setPadding(new Insets(0, 0, 20, 0));
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
					divider.setPosition(0.25);
				else if (arg2 != null && arg2.equals(tabPatients))
					divider.setPosition(0.6);
				else
					divider.setPosition(0.75);
			}
		});

		labelPaneMap = new HashMap<Label, VBox>();
		labelPaneMap.put(lblActions, paneActions);
		labelPaneMap.put(lblConditions, paneConditions);
		labelPaneMap.put(lblTriggers, paneTriggers);
		lblTriggers.setUserData("selected");
		activeMenu = lblTriggers;
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
		showLoginRegister();

	}

	public void setCurrentProject(FirebaseProject project){
		SHOULD_UPDATE_TRIGGERS = false;
		
		openProject = project;
		lblOpenProject.setText(project.getname());
		FirebaseDB.setOpenProject(openProject);
		primaryStage.setTitle("Jeeves");
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
		SHOULD_UPDATE_TRIGGERS = true;
	}

	/**Method called from SaveAsPane until I can be bothered with a better way of doing things**/
	public void setNameLabel(String name){
		lblOpenProject.setText(name);
	}
	@FXML
	private void removeHighlight(Event e) {
		Label label = (Label) e.getSource();
		if (label.getUserData() == null || !label.getUserData().equals("selected"))
			label.setTextFill(Color.BLACK);
	}

	@FXML
	public void addGlow(Event e){
		ImageView image = (ImageView)e.getSource();
		image.getStyleClass().add("drop_shadow");
		VBox parent = (VBox)image.getParent();
		Label txtDescr = (Label)parent.getChildren().get(1);
		txtDescr.setVisible(true);
	}
	@FXML
	public void removeGlow(Event e){
		ImageView image = (ImageView)e.getSource();
		image.getStyleClass().remove("drop_shadow");
		VBox parent = (VBox)image.getParent();
		Label txtDescr = (Label)parent.getChildren().get(1);
		txtDescr.setVisible(false);
	}
	
	/** User press 'New' button **/
	@FXML
	public void newStudy(Event e) {
		setCurrentProject(new FirebaseProject());
		// isNewProject = true;
		lblOpenProject.setText("New project");

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
	
	/** User press 'Open' button **/
	@FXML
	public void openStudy(Event e){
		Stage stage = new Stage(StageStyle.UNDECORATED);
		ProjectsPane root = new ProjectsPane(this,stage);
		stage.setScene(new Scene(root));
		stage.setTitle("Available Projects");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(splitPane.getScene().getWindow());
		stage.showAndWait();
	}
	
	@FXML
	private void addHighlight(Event e) {
		Label label = (Label) e.getSource();
		label.setTextFill(Color.ORANGE);
	}
	
	@FXML
	public void saveStudy(Event e){
		//Do some validation on the survye names here
				ArrayList<String> currentnames = new ArrayList<String>();
				for(FirebaseSurvey survey : currentsurveys){
					if(survey.gettitle().equals("New survey")){
						makeInfoAlert("Jeeves","No name given to survey","All surveys must have a title (not 'New survey!')");

						return;
					}
					else if(currentnames.contains(survey.gettitle())){
						makeInfoAlert("Jeeves","Duplicate survey names","All surveys must have unique names");
						return;
					}
					currentnames.add(survey.gettitle());
				};
				if (openProject.getname() == null) {
					saveAsStudyMenu(e);
					return;
				} else {
					FirebaseDB.getInstance().saveProject(openProject.getname(), this.openProject);
				}

	}
	
	@FXML
	public void openSettings(Event e){
		Stage stage = new Stage(StageStyle.UNDECORATED);
		SettingsPane root = new SettingsPane(this,stage);
		stage.setScene(new Scene(root));
		stage.setTitle("Settings");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(splitPane.getScene().getWindow());
		stage.showAndWait();
	}
	
	@FXML
	public void publish(Event e){
		if(openProject.getactive()){
			makeInfoAlert("Jeeves", "Already published","Your study is already published!");
			    return;
		}
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm Publish");
		alert.setHeaderText("Publish study " + openProject.getname() + "?");
		alert.setContentText("This will allow other researchers to view the study spec, and allow you to assign patients to the study");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    FirebaseDB.getInstance().publishStudy(openProject);
		    makeInfoAlert("Study published","Successfully published!","Your study is now published! Configuration settings, including the study ID that you distribute to patients, are available in the Settings menu");
		} 
	}
	
	@FXML
	public void exit(Event e){
		System.exit(0);
	}

	@FXML
	private void showMenu(Event e) {
		Label label = (Label) e.getSource();
		showMenu(label);
	}

	Label lastActiveMenu;
	Label activeMenu;
	public void highlightMenu(ElementType type,boolean shouldHighlight){
		
		if(shouldHighlight==false){
			showMenu(lastActiveMenu);
		}
		else
			lastActiveMenu = activeMenu;
		
		switch(type){
		case ACTION:
			if(shouldHighlight){
			showMenu(lblActions);
			paneActions.getChildren().forEach(action->action.getStyleClass().add("light_drop_shadow"));
			}
			else
				paneActions.getChildren().forEach(action->action.getStyleClass().remove("light_drop_shadow"));

			break;
		case TRIGGER:
			if(shouldHighlight){
			showMenu(lblTriggers);
			paneTriggers.getChildren().forEach(action->action.getStyleClass().add("drop_shadow"));
			}
			else
				paneTriggers.getChildren().forEach(action->action.getStyleClass().remove("drop_shadow"));

			break;
		case EXPRESSION:
			if(shouldHighlight){
			showMenu(lblConditions);
			paneConditions.getChildren().forEach(action->action.getStyleClass().add("drop_shadow"));
			}
			else
				paneConditions.getChildren().forEach(action->action.getStyleClass().remove("drop_shadow"));

			break;
		default:
			break;
		}
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
		activeMenu = label;
	}

	boolean isOverTrash(double mouseX, double mouseY) {
		return imgTrash.getBoundsInParent().contains(mouseX, mouseY);
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
		case "WiFi":var.setVartype(VAR_WIFI);break;
		case "Bluetooth":var.setVartype(VAR_BLUETOOTH);break;

		}
		var.setisCustom(true);
		var.settimeCreated(System.currentTimeMillis());
		addVariable(var);
		loadVariables();
		
	}
	class NameComparator implements Comparator<FirebaseVariable> {
		int reverse =1;
		public void doReverse(){reverse = -reverse;}
	    @Override
	    public int compare(FirebaseVariable a, FirebaseVariable b) {
	    	return reverse * a.getname().compareToIgnoreCase(b.getname());
	    }
	}
	class TypeComparator implements Comparator<FirebaseVariable> {
		int reverse =1;
		public void doReverse(){reverse = -reverse;}
		@Override
	    public int compare(FirebaseVariable a, FirebaseVariable b) {
	        return reverse * a.getvartype().compareToIgnoreCase(b.getvartype());
	    }
	}
	class AgeComparator implements Comparator<FirebaseVariable> {
	    int reverse =1;
		public void doReverse(){reverse = -reverse;}
	    @Override
	    public int compare(FirebaseVariable a, FirebaseVariable b) {
	        return a.gettimeCreated() >= b.gettimeCreated() ? reverse : -reverse;
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
		nameComparator.doReverse();
		currentvariables.sort(nameComparator);
		reAddVariables();
	
	}
	
	@FXML
	public void sortByTime(Event e){
		ageComparator.doReverse();
		currentvariables.sort(ageComparator);
		reAddVariables();
	}
	
	@FXML
	public void sortByType(Event e){
		typeComparator.doReverse();
		currentvariables.sort(typeComparator);
		reAddVariables();
	}
}