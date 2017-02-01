package com.jeeves.vpl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
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
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
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
import javafx.util.Duration;

import com.jeeves.vpl.canvas.actions.Action;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.ElementReceiver;
import com.jeeves.vpl.canvas.receivers.NewDatePane;
import com.jeeves.vpl.canvas.triggers.Trigger;
import com.jeeves.vpl.canvas.uielements.UIElement;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseProject;
import com.jeeves.vpl.firebase.FirebaseSurvey;
import com.jeeves.vpl.firebase.FirebaseTrigger;
import com.jeeves.vpl.firebase.FirebaseUI;
import com.jeeves.vpl.firebase.FirebaseVariable;
import com.jeeves.vpl.survey.SurveyController;

/**
 * The controller class for our main GUI. A lot of boggy setup code in here but
 * it gets the job done. This is like a combination of the View/Controller,
 * where the FirebaseDB class acts as our model!
 * 
 * @author Daniel
 *
 */
public class MainController extends Application {

	@FXML private Menu mnuStudies;
	//@FXML
	//public Tab tabCanvas; // I don't like making this public but meh

	@FXML private TabPane tabPane;
	@FXML private Label lblTriggers;
	@FXML private Label lblActions;
	@FXML private Label lblConditions;
	public boolean isNewProject = true;

	@FXML private Label lblUIElements;
	@FXML private MenuBar mnuBar;
	@FXML public Menu mnuFile;
	@FXML private VBox paneTriggers;
	@FXML private VBox paneActions;
	@FXML private VBox paneConditions;
	@FXML private VBox paneUI;
	@FXML private VBox vboxSurveyVars;
	@FXML private VBox paneVariables;

	@FXML private ImageView imgPhone;
	@FXML private Button btnAddVar;
	private Map<Label, VBox> labelPaneMap;
	@FXML private Pane paneIntervention;
	@FXML private Pane paneFrame;
	//@FXML private TabPane tabPaneDesign;
	@FXML private Pane paneAndroid;

	@FXML private Tab tabFramework;
	@FXML private Tab tabSurvey;
	@FXML private Tab tabUsers;
//	@FXML private Tab tabDesign;
	@FXML private SplitPane splitPane;

	@FXML private Pane paneIcons;

	@FXML private VBox vboxConfig;
//	@FXML private VBox vboxDesign;

	private FirebaseDB firebase;
	private Stage primaryStage;
	private ListChangeListener<Node> canvasListener;
	private ListChangeListener<Node> receiverListener;
	private ListChangeListener<FirebaseSurvey> surveyListener;
	private EventHandler<MouseEvent> viewElementHandler;
	private ChangeListener<Tab> tabListener;
	private ChangeListener<Tab> canvasOrDesignTabListener;
	private FirebaseProject currentproject; // The currently selected project
	private ViewCanvas canvas; // The canvas group that contains the project
	private ElementReceiver receiver; //Where UI elements go
	private AnchorPane myPane; // The main pane
	private PatientController patientController;
	private SurveyController surveyController;
	public Stage dateStage;
	public NewDatePane root;
	public ViewCanvas getViewCanvas(){
		return canvas;
	}
	public AnchorPane getMainPane(){
		return myPane;
	}
	private ListProperty<ObservableValue<String>> listProperty = new SimpleListProperty<ObservableValue<String>>();
	private ObservableList<ObservableValue<String>> surveynames = FXCollections.observableList(new ArrayList<ObservableValue<String>>());


	public ObservableList<FirebaseSurvey> currentsurveys = FXCollections
			.observableList(new ArrayList<FirebaseSurvey>()); 
	public ObservableList<FirebaseVariable> currentvariables = FXCollections
			.observableList(new ArrayList<FirebaseVariable>());
	public ObservableList<FirebaseUI> currentelements = FXCollections
			.observableList(new ArrayList<FirebaseUI>());

	public static MainController currentGUI;
	@FXML private ImageView imgTrash;

	boolean isOverTrash(double mouseX, double mouseY){
		return imgTrash.getBoundsInParent().contains(mouseX,mouseY);
	}

	public void addVariable(FirebaseVariable var) {
		currentproject.getvariables().add(var);
	}


	@Override
	public void start(Stage primaryStage) throws Exception {
		new MainController(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}

	public MainController() {
	}

	private MainController(Stage primaryStage) {
		currentGUI = this; 
		this.primaryStage = primaryStage;
		firebase = new FirebaseDB();
		firebase.addListeners();
		addListeners();
		primaryStage.setTitle("Jeeves - New Project");
		Platform.setImplicitExit(false);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/maingui.fxml"));
		fxmlLoader.setController(this);
		try {
			myPane = (AnchorPane) fxmlLoader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		fxmlLoader.setController(this);
		Scene scene = new Scene(myPane);

		primaryStage.setOnCloseRequest(event -> System.exit(0));
		primaryStage.setScene(scene);
		//	scene.getStylesheets().add(this.getClass().getResource("ButtonsDemo.css").toExternalForm());
		splitPane.lookupAll(".split-pane-divider").stream().forEach(div -> div.setMouseTransparent(true));

		listProperty.set(surveynames);
		surveyController = new SurveyController(currentsurveys);
		tabSurvey.setContent(surveyController);
		patientController = new PatientController(this,firebase);
		tabUsers.setContent(patientController);
		imgPhone.setImage(new Image(this.getClass().getResourceAsStream("/img/icons/phone.png")));

		imgTrash.setImage(new Image(this.getClass().getResourceAsStream("/img/icons/recycle.png")));
		Platform.runLater(new Runnable(){
			public void run(){
				loadCanvasElements();
			}
		});
		loadNewApp(); 


	}
	public static void hackTooltipStartTiming(Tooltip tooltip) {
	    try {
	        Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
	        fieldBehavior.setAccessible(true);
	        Object objBehavior = fieldBehavior.get(tooltip);

	        Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
	        fieldTimer.setAccessible(true);
	        Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

	        objTimer.getKeyFrames().clear();
	        objTimer.getKeyFrames().add(new KeyFrame(new Duration(250)));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
@FXML private ScrollPane paneMain;

	private void loadProjectsIntoMenu() {
		mnuStudies.getItems().clear();
		firebase.getprojects().forEach(project -> {
			MenuItem item = new MenuItem(project.getname());
			mnuStudies.getItems().add(item);
			item.setOnAction(action -> {
				loadProject(project);
			});
		});
	}

	public void printHeight(){
		System.out.println("TRIGGER PANE HEIGHT IS " + paneTriggers.getHeight());
	}
	ArrayList<ImageView> infoIcons = new ArrayList<ImageView>();

	public ImageView createInfoIcon(){
		ImageView infoIcon = new ImageView();
		infoIcons.add(infoIcon);
		infoIcon.setImage(new Image("/img/icons/information-icon.png"));
		infoIcon.setFitHeight(14);
		infoIcon.setFitWidth(14);
		infoIcon.setOpacity(0);
		infoIcon.setOnMouseEntered(event->infoIcon.setCursor(Cursor.HAND));
		return infoIcon;
	}
	private VBox createBoxyBox(ViewElement elem){
		Label newlable = new Label(elem.name.get());

		HBox box = new HBox();
		box.setSpacing(7);
		ImageView infoIcon = createInfoIcon();
		VBox boxybox = new VBox();
		boxybox.setSpacing(5);
		boxybox.setOnMouseEntered(event->{infoIcons.forEach(info->info.setOpacity(0));infoIcon.setOpacity(100);});
		boxybox.setOnMouseExited(event->{Point2D point = new Point2D(event.getSceneX(),event.getSceneY());
											if(!boxybox.localToScene(boxybox.getBoundsInLocal()).contains(point)){infoIcon.setOpacity(0);}});
		boxybox.prefWidthProperty().bind(paneFrame.widthProperty());		
		box.setFillHeight(true);
		box.getChildren().addAll(infoIcon,newlable);
		boxybox.getChildren().addAll(box,elem);
		box.setPadding(new Insets(0,0,0,-15));
		Tooltip t = new Tooltip(elem.description);
		hackTooltipStartTiming(t); //A wonderful wonderful method someone else made
		Tooltip.install(
			    infoIcon,
			    t
			);
		newlable.setFont(Font.font("Calibri", FontWeight.NORMAL, 16));

		return boxybox;
	}
	@FXML private ContextMenu mnuContext;
	@FXML private VBox vboxFrame;
	@SuppressWarnings("unchecked")
	private void loadCanvasElements() {
		dateStage = new Stage(StageStyle.UNDECORATED);
		root = new NewDatePane();
		Scene scene = new Scene(root);
		dateStage.setScene(scene);
		dateStage.setTitle("Edit dates");
		dateStage.initModality(Modality.APPLICATION_MODAL);
		System.out.println("HERE");
		Divider d1 = splitPane.getDividers().get(1);
		d1.positionProperty().addListener(new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {
				System.out.println("double value is " + arg2.doubleValue());
				imgTrash.setLayoutX(arg2.doubleValue()*myPane.getWidth()-37);
			}
			
		});
	//	imgTrash.layoutXProperty().bind(splitPane.getDividers().get(1).positionProperty()); //Trash can gets dragged around with the divider
		try {
				ArrayList<ViewElement> elements = new ArrayList<ViewElement>();
				for(String trigName : Trigger.triggerNames){
					ViewElement trigger = ViewElement.create(trigName);
					elements.add(trigger);
					VBox boxybox = createBoxyBox(trigger);		
					paneTriggers.getChildren().add(boxybox);
				}
				for(String actName : Action.actionNames){
					ViewElement action = ViewElement.create(actName);
					elements.add(action);
					VBox boxybox = createBoxyBox(action);		
					paneActions.getChildren().add(boxybox);
				}
				for(String exprName : Expression.exprNames){
					ViewElement expr = ViewElement.create(exprName);
					elements.add(expr);
					VBox boxybox = createBoxyBox(expr);		
					paneConditions.getChildren().add(boxybox);
				}
				for(String uiElem : UIElement.uiElements){
					ViewElement uielement = ViewElement.create(uiElem);
					elements.add(uielement);
					VBox boxybox = createBoxyBox(uielement);		
					paneUI.getChildren().add(boxybox);
				}
				for(ViewElement element : elements){
					element.setPadding(new Insets(10, 0, 10, 0));
					ViewElement draggable = ViewElement.create(element.getClass().getName());
					element.setDraggable(draggable); // DJRNEW
					element.setReadOnly();
					element.setHandler(viewElementHandler);
				}

		
		} catch (Exception e){
			e.printStackTrace();
		}

	
		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.selectedItemProperty().addListener(tabListener);

	//	SingleSelectionModel<Tab> canvasOrDesign = tabPaneDesign.getSelectionModel();
	//	canvasOrDesign.selectedItemProperty().addListener(canvasOrDesignTabListener);

		labelPaneMap = new HashMap<Label, VBox>();
		labelPaneMap.put(lblActions, paneActions);
		labelPaneMap.put(lblConditions, paneConditions);
		labelPaneMap.put(lblTriggers, paneTriggers);
		paneTriggers.heightProperty().addListener(new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				System.out.println("Changed from " + oldValue + " to " + newValue);
			}
			
		});
		labelPaneMap.put(lblUIElements, paneUI);
		lblTriggers.setUserData("selected");
		lblTriggers.setTextFill(Color.ORANGE); // Initial selection of triggers
		DropShadow ds = new DropShadow( 20, Color.AQUA );

		imgTrash.addEventHandler(MouseDragEvent.ANY, new EventHandler<MouseDragEvent>(){

			@Override
			public void handle(MouseDragEvent arg0) {
				if(arg0.getEventType().equals(MouseDragEvent.MOUSE_DRAG_ENTERED)){
					imgTrash.setEffect(ds);
				}
				else if(arg0.getEventType().equals(MouseDragEvent.MOUSE_DRAG_EXITED)){
					imgTrash.setEffect(null);
				}
			}

		});
		primaryStage.show();

	}

	public FirebaseProject getCurrentProject(){
		return this.currentproject;
	}
	private void loadProject(FirebaseProject proj) {
		isNewProject = false;

		currentproject = proj; // Sets this project as the current one
	//	paneIntervention = new Pane();
		//tabCanvas.setText(proj.getname() + " Configuration");
		//tabDesign.setText("UI Design");
	//	System.out.println("11 Height here is " + paneTriggers.getHeight());
		primaryStage.setTitle("Jeeves - " + proj.getname());
		//tabCanvas.setContent(paneIntervention);
	//	System.out.println("Height here is " + paneTriggers.getHeight());
		patientController.loadPatients(); //Reset so we have the patients for THIS project
		resetPanes();
	//	System.out.println("22 Height here is " + paneTriggers.getHeight());

	}

	private void loadNewApp() {
		isNewProject = true;

		currentproject = new FirebaseProject();
		//tabCanvas.setText("New ESM Study");
		//paneIntervention = new Pane();
		//Add the global variables
		String[] globalVarNames = new String[]{"Missed Surveys","Completed Surveys","Last Survey Score", "Survey Score Difference"};
		for(String name : globalVarNames){
			FirebaseVariable var = new FirebaseVariable();
			var.setname(name);
			var.setVartype(Expression.VAR_NUMERIC);
			currentproject.getvariables().add(var);
		}
		loadVariables();
		//tabCanvas.setContent(paneIntervention);

		resetPanes();
		//Every study should have these on it
		canvas.addChild(ViewElement.create(BeginTrigger.class.getName()), 5400, 5250);



	}
	private void resetPanes(){
		currentsurveys.removeListener(surveyListener); //Hopefully this will stop all surveys being removed when project is loaded wtice in a row
		currentsurveys.clear();
		currentvariables.clear();
		currentelements.clear();
		if( canvas != null &&canvas.mouseHandler != null)
		paneIntervention.removeEventHandler(MouseEvent.ANY, canvas.mouseHandler);
		paneIntervention.getChildren().remove(canvas);
		canvas = new ViewCanvas();
		paneIntervention.getChildren().add(canvas);
	//	paneIntervention.setStyle("-fx-background-color: blue");
		canvas.addEventHandlers();
		paneAndroid.getChildren().clear();

		receiver = new ElementReceiver(paneAndroid.getPrefWidth(), paneAndroid.getPrefHeight());

		setProject();

		canvas.addChildrenListener(canvasListener); // DJRNEW
		receiver.getChildElements().addListener(receiverListener);

		receiver.setProject(currentproject);
		paneAndroid.getChildren().add(receiver);

		currentelements.addAll(currentproject.getuidesign());

		currentsurveys.addAll(currentproject.getsurveys());
		//	currentsurveys.forEach(survey->surveynames.add(survey.name)); //add list of survey names

		currentsurveys.addListener(surveyListener);
		tabSurvey.setContent(new SurveyController(currentsurveys));

		loadVariables();
		tabPane.getSelectionModel().select(tabFramework);
		//mnuBar.setOnMousePressed(handler->{mnuBar.requestFocus();});
		System.out.println("canvas parent is " + canvas.getParent());

	}


	public void loadVariables() {
		currentvariables.clear();
		vboxSurveyVars.getChildren().clear();
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
				ViewElement draggable = (UserVariable) UserVariable.create(variable);
				global.setDraggable(draggable); // DJRNEW
				global.setReadOnly();
				global.setHandler(viewElementHandler);
				vboxSurveyVars.getChildren().add(global);
			}

		});

	}

	void setProject() {

		ArrayList<ViewElement> views = new ArrayList<ViewElement>();
		for (FirebaseTrigger trig : currentproject.gettriggers()) {
			Trigger triggerview = Trigger.create(trig);		
			views.add(triggerview);
			canvas.addChild(triggerview, trig.getxPos(), trig.getyPos());
		}
		for (FirebaseExpression expr : currentproject.getexpressions()) {
			Expression exprview = Expression.create(expr);
			views.add(exprview);
			Point2D pos = exprview.getPosition();
			canvas.addChild(exprview, pos.getX(), pos.getY());
		}
		for (FirebaseVariable var : currentproject.getvariables()) {
			UserVariable varview = (UserVariable) UserVariable.create(var);
			views.add(varview);
			Point2D pos = varview.getPosition();
			canvas.addChild(varview, pos.getX(), pos.getY());
		}
		for (FirebaseUI var : currentproject.getuidesign()) {
			UIElement element = UIElement.create(var);
			var.getMyTextProperty().addListener(change->{
				receiver.getChildElements().remove(element);
				receiver.getChildElements().add(element);
			});
			views.add(element);
			element.previouslyAdded = true; //This is a bit hacky but stops the boxes popping up when we load a new project
			receiver.addChild(element, 0, 0);
		}
		views.forEach(view -> {
			view.addEventHandler(MouseEvent.ANY, view.mainHandler);
		});

	}

	private void addListeners(){

		tabListener = new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> arg0,
					Tab arg1, Tab arg2) {
				Divider divider = splitPane.getDividers().get(0);
				if (arg2 != null && arg2.equals(tabFramework))
					divider.setPosition(0.3);
				else
					divider.setPosition(0.7);
			}
		};
//		canvasOrDesignTabListener = new ChangeListener<Tab>() {
//			@Override
//			public void changed(ObservableValue<? extends Tab> arg0,
//					Tab arg1, Tab arg2) {
//				if (arg2.equals(tabDesign)) {
//					vboxConfig.setVisible(false);
//					paneIcons.setPrefHeight(vboxDesign.getHeight());
//					vboxDesign.setVisible(true);
//					showMenu(lblUIElements);
//				}
//				if (arg2.equals(tabCanvas)) {
//					vboxConfig.setVisible(true);
//					paneIcons.setPrefHeight(vboxConfig.getHeight());
//					vboxDesign.setVisible(false);
//					showMenu(lblTriggers);
//
//				}
//			}
//		};


		firebase.getprojects().addListener(new ListChangeListener<FirebaseProject>() {
			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends FirebaseProject> c) {
				loadProjectsIntoMenu();

			}
		});

		canvasListener = new ListChangeListener<Node>() {
			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends Node> arg0) {
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

		viewElementHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				ViewElement clicked = ((ViewElement)arg0.getSource());
				if(arg0.isSecondaryButtonDown())return;
				if(arg0.getEventType() == MouseEvent.MOUSE_PRESSED)
					myPane.getChildren().add(clicked.getDraggable());
				if (arg0.getEventType() == MouseEvent.MOUSE_RELEASED) {
					//if(canvas.getIsMouseOver() == false){
						myPane.getChildren().remove(clicked.getDraggable());
				//	}
					ViewElement draggable = null;
					//annoying exception for user variables
					if(clicked instanceof UserVariable){
						draggable = (UserVariable) UserVariable.create(((FirebaseExpression)clicked.getModel()));
					}
					else
						draggable = ViewElement
						.create(clicked.getClass().getName());
					clicked.setDraggable(draggable); // DJRNEW
				}
			}

		};

		surveyListener = new ListChangeListener<FirebaseSurvey>(){

			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends FirebaseSurvey> arg0) {
				currentproject.getsurveys().clear();
				currentsurveys.forEach(survey->currentproject.getsurveys().add(survey));
			}

		};

		//Listen on adding UI elements
		receiverListener = new ListChangeListener<Node>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> arg0) {
				currentelements.clear();
				currentproject.getuidesign().clear();
				receiver.getChildElements().forEach(child->{currentproject.getuidesign().add((FirebaseUI)child.getModel());
				currentelements.add((FirebaseUI)child.getModel());
				((FirebaseUI)child.getModel()).getMyTextProperty().addListener(change->{
					receiver.getChildElements().remove(child);
					receiver.getChildElements().add(child);
				});
				});
			}

		};
	}


	//	--------------------------------------- UI Triggered Methods ----------------------------------------------

	@FXML
	private void addVariable(Event e) {
		Stage stage = new Stage(StageStyle.UNDECORATED);
		VariablePane root = new VariablePane(stage);
		stage.setScene(new Scene(root));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(btnAddVar.getScene().getWindow());
		stage.showAndWait();

	}

	@FXML
	public void newStudyMenu(Event e) {
		loadNewApp();
	}

	@FXML
	public void saveStudyMenu(Event e) {
		if (isNewProject) {
			saveAsStudyMenu(e);
			return;
		} else {
			firebase.addProject("",this.currentproject);
		}

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
	}

	@FXML
	public void openSettings(Event e) {
		Stage stage = new Stage(StageStyle.UNDECORATED);
		SettingsPane root = new SettingsPane(stage, currentproject);
		stage.setScene(new Scene(root));
		stage.setTitle("Adjust settings");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(splitPane.getScene().getWindow());
		stage.showAndWait();
	}

	@FXML
	public void quitStudyMenu(Event e) {
		System.exit(0);
	}

	@FXML
	private void addHighlight(Event e) {
		Label label = (Label) e.getSource();
		label.setTextFill(Color.ORANGE);
	}

	@FXML
	private void removeHighlight(Event e) {
		Label label = (Label) e.getSource();
		if (label.getUserData() == null
				|| !label.getUserData().equals("selected"))
			label.setTextFill(Color.BLACK);
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
//		if(label.equals(lblUIElements)){
//			ObservableList<Node> items = splitPane.getItems();
//			Node item2 = items.get(1);
//			Node item3 = items.get(2);
//			items.set(1, item3);
//			items.set(2, item2);
//		}
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

		if (pane.equals(paneConditions)) {
			paneVariables.setVisible(true);
			paneVariables.toFront();
			divider.setPosition((30 + paneVariables.getBoundsInParent()
			.getMaxX()) / splitPane.getWidth());
		} else {
			paneVariables.setVisible(false);
			paneVariables.toBack();
		}
	}

}