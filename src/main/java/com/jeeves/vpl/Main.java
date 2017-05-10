package com.jeeves.vpl;

import static com.jeeves.vpl.Constants.VAR_NUMERIC;
import static com.jeeves.vpl.Constants.actionNames;
import static com.jeeves.vpl.Constants.exprNames;
import static com.jeeves.vpl.Constants.questionNames;
import static com.jeeves.vpl.Constants.triggerNames;
import static com.jeeves.vpl.Constants.uiElements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
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
import com.jeeves.vpl.survey.SurveyPane;
/**
 * The controller class for our main GUI. A lot of boggy setup code in here but
 * it gets the job done. This is like a combination of the View/Controller,
 * where the FirebaseDB class acts as our model!
 * 
 * @author Daniel
 *
 */
public class Main extends Application {

	@FXML private Menu mnuStudies;
	@FXML private TabPane tabPane;
	@FXML private Label lblTriggers;
	@FXML private Label lblActions;
	@FXML private Label lblConditions;

	//@FXML private Label lblUIElements;
	@FXML private MenuBar mnuBar;
	@FXML private Menu mnuFile;
	@FXML private VBox paneTriggers;
	@FXML private VBox paneActions;
	@FXML private VBox paneConditions;
	@FXML private VBox vboxUIElements;
	@FXML private VBox vboxSurveyVars;
	@FXML private VBox paneVariables;
	@FXML private VBox paneQuestions;
	
	@FXML private ImageView imgPhone;
	@FXML private Button btnAddVar;
	@FXML private Pane paneIntervention;
	@FXML private Pane paneFrame;
	@FXML private Pane paneAndroid;

	@FXML private Tab tabFramework;
	@FXML private Tab tabSurvey;
	@FXML private Tab tabUsers;
	@FXML private SplitPane splitPane;
	@FXML private Pane paneIcons;
	@FXML private VBox vboxConfig;
	@FXML private ImageView imgTrash;
	@FXML private ContextMenu mnuContext;
	@FXML private VBox vboxFrame;
	@FXML private ScrollPane paneMain;
	@FXML private HBox surveyBox;

	//private boolean isNewProject = true;
	private Map<Label, VBox> labelPaneMap;
	private FirebaseDB firebase;
	private Stage primaryStage;

	private EventHandler<MouseEvent> viewElementHandler;
	private FirebaseProject currentproject; // The currently selected project
	private ViewCanvas canvas; // The canvas group that contains the project
	private ElementReceiver receiver; //Where UI elements go
	private AnchorPane myPane; // The main pane
	private PatientPane patientController;
	private DragPane dragPane;
	//private Stage dateStage;
	//private NewDatePane root;
	//	private ListProperty<ObservableValue<String>> listProperty = new SimpleListProperty<ObservableValue<String>>();
	//	private ObservableList<ObservableValue<String>> surveynames = FXCollections.observableList(new ArrayList<ObservableValue<String>>());

	private ObservableList<FirebaseSurvey> currentsurveys = FXCollections.observableList(new ArrayList<FirebaseSurvey>()); 
	private ObservableList<FirebaseVariable> currentvariables = FXCollections.observableList(new ArrayList<FirebaseVariable>());
	private ObservableList<FirebaseUI> currentelements = FXCollections.observableList(new ArrayList<FirebaseUI>());


	private static Main currentGUI;
	public static Main getContext(){
		return currentGUI;
	}
	//	public Stage getDateStage(){
	//		return dateStage;
	//	}
	//	public NewDatePane getDatePane(){
	//		return root;
	//	}
	public ObservableList<FirebaseSurvey> getSurveys(){return currentsurveys;}
	public void registerSurveyListener(ListChangeListener<FirebaseSurvey> listener){
		currentsurveys.addListener(listener);
	}
	public ObservableList<FirebaseVariable> getVariables(){return currentvariables;}

	public void registerVarListener(ListChangeListener<FirebaseVariable> listener){
		currentvariables.addListener(listener);
	}
	public ObservableList<FirebaseUI> getUIElements(){return currentelements;}
	public void registerElementListener(ListChangeListener<FirebaseUI> listener){
		currentelements.addListener(listener);
	}

	public ViewCanvas getViewCanvas(){
		return canvas;
	}
	public void hideMenu(){
		mnuFile.hide();
	}
	//	public void setNewProject(boolean isNew){
	//		this.isNewProject = isNew;
	//	}
	public AnchorPane getMainPane(){
		return myPane;
	}

	boolean isOverTrash(double mouseX, double mouseY){
		return imgTrash.getBoundsInParent().contains(mouseX,mouseY);
	}

	public void addVariable(FirebaseVariable var) {
		currentproject.getvariables().add(var);
	}

	private Main(Stage primaryStage) {
		currentGUI = this; 
		this.primaryStage = primaryStage;
		firebase = new FirebaseDB();
		firebase.addListeners();
		firebase.getprojects().addListener(new ListChangeListener<FirebaseProject>() {
			@Override
			public void onChanged(
					javafx.collections.ListChangeListener.Change<? extends FirebaseProject> c) {
				loadProjectsIntoMenu();

			}
		});
		//	addListeners();
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
		dragPane = new DragPane(myPane.getWidth(),myPane.getHeight());
		myPane.getChildren().add(dragPane);
		primaryStage.setOnCloseRequest(event -> System.exit(0));
		primaryStage.setScene(scene);
		splitPane.lookupAll(".split-pane-divider").stream().forEach(div -> div.setMouseTransparent(true));

		//	listProperty.set(surveynames);
		//surveyBox.getChildren().add(surveyController);
		//tabSurvey.setContent(surveyController);
		patientController = new PatientPane(this,firebase);
		tabUsers.setContent(patientController);
		currentproject = new FirebaseProject();
		//	isNewProject = true;

		Platform.runLater(new Runnable(){
			public void run(){
				resetPanes();
				loadCanvasElements();
				loadVariables();

			}
		});


	}

	private void loadProjectsIntoMenu() {
		mnuStudies.getItems().clear();
		firebase.getprojects().forEach(project -> {
			MenuItem item = new MenuItem(project.getname());
			mnuStudies.getItems().add(item);
			item.setOnAction(action -> {
				//			isNewProject = false;
				currentproject = project; // Sets this project as the current one
				primaryStage.setTitle("Jeeves - " + project.getname());
				patientController.loadPatients(); //Reset so we have the patients for THIS project
				resetPanes();			});
		});
	}

	private VBox createBoxyBox(ViewElement elem){
		Label newlable = new Label(elem.getName());

		HBox box = new HBox();
		box.setSpacing(3);
	//	ImageView infoIcon = createInfoIcon();
		VBox boxybox = new VBox();
	//	boxybox.setSpacing(3);
		//boxybox.setOnMouseEntered(event->{infoIcons.forEach(info->info.setOpacity(0));infoIcon.setOpacity(100);});
		//boxybox.setOnMouseExited(event->{Point2D point = new Point2D(event.getSceneX(),event.getSceneY());
	//										if(!boxybox.localToScene(boxybox.getBoundsInLocal()).contains(point)){infoIcon.setOpacity(0);}});
		//boxybox.setPrefWidth(elem.getWidth());
		newlable.prefWidthProperty().bind(elem.widthProperty());
		box.setFillHeight(true);
		box.getChildren().addAll(newlable);
		boxybox.getChildren().addAll(box,elem);
		boxybox.setFillWidth(true);
	//	newlable.setStyle("-fx-background-color: blue");
		//box.setPadding(new Insets(0,0,0,-15));
		Tooltip t = new Tooltip(elem.description);
	//	hackTooltipStartTiming(t); //A wonderful wonderful method someone else made
		//Tooltip.install(
	//		    infoIcon,
	//		    t
	//		);
		newlable.setFont(Font.font("Calibri", FontWeight.NORMAL, 16));

		return boxybox;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setElementParent(ViewElement draggable){
		if(draggable.getType() == ElementType.UIELEMENT || draggable.getType() == ElementType.QUESTION)
			draggable.parentPane = dragPane;
		else
			draggable.parentPane = canvas;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadCanvasElements() {
		Divider d1 = splitPane.getDividers().get(1);
		
		d1.positionProperty().addListener(new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {
				if(arg2.doubleValue() < 0.7)
					d1.setPosition(0.7);
				else
					imgTrash.setLayoutX(arg2.doubleValue()*myPane.getWidth()-37);
			}

		});
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
					ViewElement<FirebaseVariable> draggable = null;
					//annoying exception for user variables
					if(clicked.getType() == ElementType.VARIABLE)
						draggable = (UserVariable) new UserVariable(((FirebaseVariable)clicked.getModel()));
					else
						draggable = ViewElement.create(clicked.getClass().getName());
					setElementParent(draggable);
					clicked.setDraggable(draggable); // DJRNEW

				}
			}

		};
		try {
			ArrayList<ViewElement> elements = new ArrayList<ViewElement>();
			for(String trigName : triggerNames){
				ViewElement trigger = ViewElement.create(trigName);
				elements.add(trigger);
				VBox boxybox = createBoxyBox(trigger);	
				paneTriggers.getChildren().add(boxybox);
			}
			for(String actName : actionNames){
				ViewElement<FirebaseVariable> action = ViewElement.create(actName);
				elements.add(action);
				VBox boxybox = createBoxyBox(action);	
				paneActions.getChildren().add(boxybox);
			}
			for(String exprName : exprNames){
				ViewElement<FirebaseVariable> expr = ViewElement.create(exprName);
				elements.add(expr);
				VBox boxybox = createBoxyBox(expr);	
				paneConditions.getChildren().add(boxybox);
			}
			for(String uiElem : uiElements){
				ViewElement<FirebaseVariable> uielement = ViewElement.create(uiElem);
				elements.add(uielement);
				vboxUIElements.getChildren().add(uielement);
			}
			for (String qName : questionNames) {
				ViewElement<FirebaseQuestion> question = ViewElement.create(qName);
				elements.add(question);
				paneQuestions.getChildren().add(question);
			}
			for(ViewElement element : elements){
				element.setPadding(new Insets(0, 0, 10, 0));
				System.out.println("Element name is " + element.getClass().getName());
				ViewElement<FirebaseVariable> draggable = ViewElement.create(element.getClass().getName());
				element.setDraggable(draggable); // DJRNEW
				setElementParent(draggable);
				element.setReadOnly();
				element.setPickOnBounds(false);
				element.setHandler(viewElementHandler);
			}
			loadVariables();
		} catch (Exception e){
			e.printStackTrace();
		}


		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> arg0,
					Tab arg1, Tab arg2) {
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
	//	labelPaneMap.put(lblUIElements, paneUI);		
		lblTriggers.setUserData("selected");
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

	private void resetPanes(){
		currentsurveys.clear();
		currentvariables.clear();
		currentelements.clear();


		if( canvas != null &&canvas.mouseHandler != null)
			paneIntervention.removeEventHandler(MouseEvent.ANY, canvas.mouseHandler);
		paneIntervention.getChildren().remove(canvas);
		canvas = new ViewCanvas();
		paneIntervention.getChildren().add(canvas);
		canvas.addEventHandlers();
		ListChangeListener<Node> canvasListener = new ListChangeListener<Node>() {
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
		canvas.addChildrenListener(canvasListener);
		paneAndroid.getChildren().clear();
	//	QuestionReceiver myreceiver = new QuestionReceiver(paneAndroid.getPrefWidth(),paneAndroid.getPrefHeight());
		receiver = new ElementReceiver(paneAndroid.getPrefWidth(), paneAndroid.getPrefHeight());
		receiver.getChildElements().addListener(new ListChangeListener<Node>() {
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

		});
		paneAndroid.getChildren().add(receiver);

		//	receiver.setProject(currentproject);
	//	paneAndroid.getChildren().add(receiver);
		setProject();



		currentelements.addAll(currentproject.getuidesign());
		currentsurveys.addAll(currentproject.getsurveys());


		//	currentsurveys.addListener(surveyListener);
		surveyBox.getChildren().add(new SurveyPane(currentsurveys));
	//	tabSurvey.setContent(new SurveyPane(currentsurveys));


		tabPane.getSelectionModel().select(tabFramework);

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
			Point2D pos = view.getPosition();
			canvas.addChild(view, pos.getX(), pos.getY());
			view.addEventHandler(MouseEvent.ANY, view.mainHandler);
		});

	}

	public void loadVariables() {
		currentvariables.clear();
		vboxSurveyVars.getChildren().clear();
		//Load our default variables
		currentproject.getvariables().clear();
		String[] globalVarNames = new String[]{"Missed Surveys","Completed Surveys","Last Survey Score", "Survey Score Difference"};
		for(String name : globalVarNames){
			FirebaseVariable var = new FirebaseVariable();
			var.setname(name);
			var.setVartype(VAR_NUMERIC);
			currentproject.getvariables().add(var);

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
				ViewElement<FirebaseVariable> draggable = new UserVariable(variable);
				//ViewElement<FirebaseVariable> draggable = new UserVariable(variable);
				global.setDraggable(draggable); // DJRNEW
				global.setReadOnly();
				setElementParent(draggable);
				global.setHandler(viewElementHandler);
				vboxSurveyVars.getChildren().add(global);
			}

		});

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
	public void saveStudyMenu(Event e) {
		//		if (isNewProject) {
		saveAsStudyMenu(e);
		return;
		//		} else {
		//			firebase.addProject("",this.currentproject);
		//		}

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
	@Override
	public void start(Stage primaryStage) throws Exception {
		new Main(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}

	public Main() {}
}