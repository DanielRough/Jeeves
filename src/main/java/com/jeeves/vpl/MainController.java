package com.jeeves.vpl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jeeves.vpl.canvas.actions.Action;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.ifsloops.Control;
import com.jeeves.vpl.canvas.receivers.ElementReceiver;
import com.jeeves.vpl.canvas.triggers.Trigger;
import com.jeeves.vpl.canvas.uielements.UIElement;
import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseProject;
import com.jeeves.vpl.firebase.FirebaseSurvey;
import com.jeeves.vpl.firebase.FirebaseTrigger;
import com.jeeves.vpl.firebase.FirebaseUI;
import com.jeeves.vpl.firebase.FirebaseVariable;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Popup;
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
public class MainController extends Application {

	@FXML private Menu mnuStudies;
	@FXML
	public Tab tabCanvas; // I don't like making this public but meh

	@FXML private TabPane tabPane;
	@FXML private Label lblTriggers;
	@FXML private Label lblActions;
	@FXML private Label lblConditions;
	public boolean isNewProject = true;

	@FXML private Label lblUIElements;
	@FXML private MenuBar mnuBar;
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
	@FXML private TabPane tabPaneDesign;
	@FXML private Pane paneAndroid;

	@FXML private Tab tabFramework;
	@FXML private Tab tabSurvey;
	@FXML private Tab tabUsers;
	@FXML private Tab tabDesign;
	@FXML private SplitPane splitPane;

	@FXML private Pane paneIcons;

	@FXML private VBox vboxConfig;
	@FXML private VBox vboxDesign;

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

	public boolean isOverTrash(double mouseX, double mouseY){
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

	//------------------------CLASS-FINDING METHODS -----------------------//
	@SuppressWarnings("unchecked")
	private static Class<ViewElement>[] getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class<ViewElement>> classes = new ArrayList<Class<ViewElement>>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	private static List<Class<ViewElement>> findClasses(File directory, String packageName) throws ClassNotFoundException {

		List<Class<ViewElement>> classes = new ArrayList<Class<ViewElement>>();
		if (!directory.exists()) 
			return classes;
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file,packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				@SuppressWarnings("unchecked")
				Class<ViewElement> classToAdd = (Class<ViewElement>) Class.forName(packageName+ '.' + file.getName().substring(0,file.getName().length() - 6));
				if (!Modifier.isAbstract(classToAdd.getModifiers()) && classToAdd.getEnclosingClass() == null) 
					classes.add(classToAdd);
			}
		}
		return classes;
	}
	//------------------------CLASS-FINDING METHODS -----------------------//

	private MainController(Stage primaryStage) {
		currentGUI = this; 
		this.primaryStage = primaryStage;
		firebase = new FirebaseDB();
		Stage stage = new Stage(StageStyle.UNDECORATED);
		StudentIDGetter root = new StudentIDGetter(stage,firebase);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Welcome");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
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
		scene = new Scene(myPane);

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

		imgTrash.setImage(new Image("https://sachi.cs.st-andrews.ac.uk/wp-content/uploads/2011/02/recycle.png"));
		Platform.runLater(new Runnable(){
			public void run(){
				loadCanvasElements();
			}
		});
		loadNewApp(); 


	}


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


	@SuppressWarnings("unchecked")
	private void loadCanvasElements() {
		try {
			System.out.println("DOIGETCALLED");
			List<Class> classes = new ArrayList<>();
			new FastClasspathScanner("com.jeeves.vpl.canvas")
			.matchSubclassesOf(ViewElement.class, classes::add)
			.scan();
			if(classes.size() == 0){
				ArrayList<ViewElement> elements = new ArrayList<ViewElement>();
				for(String trigName : Trigger.triggerNames){
					ViewElement trigger = ViewElement.create(trigName);
					elements.add(trigger);
					Label newlable = new Label(trigger.name.get());
					newlable.setFont(Font.font("Calibri", FontWeight.BOLD, 14));
					paneTriggers.getChildren().addAll(newlable,trigger);
				}
				for(String actName : Action.actionNames){
					ViewElement action = ViewElement.create(actName);
					elements.add(action);
					Label newlable = new Label(action.name.get());
					newlable.setFont(Font.font("Calibri", FontWeight.BOLD, 14));
					paneActions.getChildren().addAll(newlable,action);
				}
				for(String exprName : Expression.exprNames){
					ViewElement expr = ViewElement.create(exprName);
					elements.add(expr);
					Label newlable = new Label(expr.name.get());
					newlable.setFont(Font.font("Calibri", FontWeight.BOLD, 14));
					paneConditions.getChildren().addAll(newlable,expr);
				}
				for(String uiElem : UIElement.uiElements){
					ViewElement uielement = ViewElement.create(uiElem);
					elements.add(uielement);
					Label newlable = new Label(uielement.name.get());
					newlable.setFont(Font.font("Calibri", FontWeight.BOLD, 14));
					paneUI.getChildren().addAll(newlable,uielement);
				}
				for(ViewElement element : elements){
					element.setPadding(new Insets(10, 0, 10, 0));
					ViewElement draggable = ViewElement.create(element.getClass().getName());
					element.setDraggable(draggable); // DJRNEW
					element.setReadOnly();
					System.out.println("Added a represented thing");
					element.setHandler(viewElementHandler);
				}
			}

			int counter = 0;
			for (Class<ViewElement> classname : classes) {
				counter++;
				System.out.println("Er, hello?");
				if (!ViewElement.class.isAssignableFrom(classname) || Modifier.isAbstract(classname.getModifiers()))
					continue;
				final ViewElement represented = ViewElement.create(classname.getName());
				Label nameLabel = new Label(represented.name.get());

				if (represented instanceof Trigger) {																							
					paneTriggers.getChildren().addAll(nameLabel, represented);
				} else if (represented instanceof Expression && !(represented instanceof UserVariable) || represented instanceof Control)
					paneConditions.getChildren().addAll(nameLabel, represented);
				else if (represented instanceof Action)
					paneActions.getChildren().addAll(nameLabel, represented);
				else if (represented instanceof UIElement)
					paneUI.getChildren().addAll(nameLabel, represented);
				represented.setPadding(new Insets(0, 0, 10, 0));
				ViewElement draggable = ViewElement.create(classname.getName());
				represented.setDraggable(draggable); // DJRNEW
				represented.setReadOnly();
				System.out.println("Added a represented thing");
				represented.setHandler(viewElementHandler);
				nameLabel.setFont(Font.font("Calibri", FontWeight.BOLD, 14));
			}
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(primaryStage);
			VBox dialogVbox = new VBox(20);
			dialogVbox.getChildren().add(new Label("This is a Dialog with " + counter ));
			Scene dialogScene = new Scene(dialogVbox, 300, 200);
			dialog.setScene(dialogScene);
			dialog.show();
		} catch (Exception e){
			e.printStackTrace();
		}

		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.selectedItemProperty().addListener(tabListener);

		SingleSelectionModel<Tab> canvasOrDesign = tabPaneDesign.getSelectionModel();
		canvasOrDesign.selectedItemProperty().addListener(canvasOrDesignTabListener);

		labelPaneMap = new HashMap<Label, VBox>();
		labelPaneMap.put(lblActions, paneActions);
		labelPaneMap.put(lblConditions, paneConditions);
		labelPaneMap.put(lblTriggers, paneTriggers);
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


	private void loadProject(FirebaseProject proj) {
		isNewProject = false;

		currentproject = proj; // Sets this project as the current one
		paneIntervention = new Pane();
		tabCanvas.setText(proj.getname() + " Configuration");
		tabDesign.setText("UI Design");
		primaryStage.setTitle("Jeeves - " + proj.getname());
		tabCanvas.setContent(paneIntervention);
		resetPanes();
	}

	private void loadNewApp() {
		isNewProject = true;

		currentproject = new FirebaseProject();
		tabCanvas.setText("New ESM Study");
		paneIntervention = new Pane();
		//Add the global variables
		String[] globalVarNames = new String[]{"Missed Surveys","Completed Surveys","Last Survey Score", "Survey Score Difference"};
		for(String name : globalVarNames){
			FirebaseVariable var = new FirebaseVariable();
			var.setname(name);
			var.setVartype(Expression.VAR_NUMERIC);
			currentproject.getvariables().add(var);
		}
		loadVariables();
		tabCanvas.setContent(paneIntervention);

		resetPanes();
		//Every study should have these on it
		canvas.addChild(ViewElement.create(BeginTrigger.class.getName()), 5400, 5250);



	}
	private void resetPanes(){
		currentsurveys.removeListener(surveyListener); //Hopefully this will stop all surveys being removed when project is loaded wtice in a row
		currentsurveys.clear();
		currentvariables.clear();
		currentelements.clear();
		paneIntervention.getChildren().remove(canvas);
		canvas = new ViewCanvas();
		paneIntervention.getChildren().add(canvas);

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
		canvasOrDesignTabListener = new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> arg0,
					Tab arg1, Tab arg2) {
				if (arg2.equals(tabDesign)) {
					vboxConfig.setVisible(false);
					paneIcons.setPrefHeight(vboxDesign.getHeight());
					vboxDesign.setVisible(true);
					showMenu(lblUIElements);
				}
				if (arg2.equals(tabCanvas)) {
					vboxConfig.setVisible(true);
					paneIcons.setPrefHeight(vboxConfig.getHeight());
					vboxDesign.setVisible(false);
					showMenu(lblTriggers);

				}
			}
		};

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
					if(canvas.getIsMouseOver() == false){
						myPane.getChildren().remove(clicked.getDraggable());
					}
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