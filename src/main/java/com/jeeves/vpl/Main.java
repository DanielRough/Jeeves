package com.jeeves.vpl;

import static com.jeeves.vpl.Constants.GLOW_CLASS;
import static com.jeeves.vpl.Constants.NEW_PROJ;
import static com.jeeves.vpl.Constants.SELECTED;
import static com.jeeves.vpl.Constants.TITLE;
import static com.jeeves.vpl.Constants.actNames;
import static com.jeeves.vpl.Constants.elemNames;
import static com.jeeves.vpl.Constants.exprNames;
import static com.jeeves.vpl.Constants.questionNames;
import static com.jeeves.vpl.Constants.setUpdateTriggers;
import static com.jeeves.vpl.Constants.trigNames;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.poi.util.SystemOutLogger;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.cloud.StorageClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.canvas.actions.ScheduleAction;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.triggers.Trigger;
import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseProject;
import com.jeeves.vpl.firebase.FirebaseSurvey;
import com.jeeves.vpl.firebase.FirebaseTrigger;
import com.jeeves.vpl.survey.Survey;
import com.jeeves.vpl.survey.SurveyPane;
import com.jeeves.vpl.survey.questions.QuestionView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
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

	private DragPane dragPane;
	private Map<Label, VBox> labelPaneMap;
	private AnchorPane myPane; // The main pane
	private PatientPane patientController;
	private Stage primaryStage;
	@FXML private Label lblActions;
	@FXML private Label lblConditions;
	@FXML private Label lblTriggers;
	@FXML private VBox paneActions;
	@FXML private VBox paneConditions;
	@FXML private VBox paneTriggers;
	@FXML private Pane panePatients;
	@FXML private ImageView imgTrash;
	@FXML private Pane paneFrame;
	@FXML private Pane paneIntervention;
	@FXML private VBox paneQuestions;
	@FXML private SplitPane splitPane;
	@FXML private SplitPane paneSplit; //Cryptic
	@FXML private HBox surveyBox;
	@FXML private Tab tabFramework;
	@FXML private Tab tabPatients;
	@FXML private TabPane tabPane;
	@FXML private Label lblConnection;
	@FXML private Label lblOpenProject;
	@FXML private ChoiceBox<String> cboDebug;
	private EventHandler<MouseEvent> viewElementHandler;
	private AndroidPane paneAndroid;
	private AttributesPane paneAttributes;
	ArrayList<ViewElement<?>> elements;

	public static Main getContext() {
		return currentGUI;
	}
//	public static void main(String[] args) {
//		launch(args);
//	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		new Main(primaryStage);
	}
	public Main() {
	}

	public Main(String[] args) {
		launch(args);
	}
	public Stage getStage() {
		return primaryStage;
	}
	private Main(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		currentGUI = this;
		Platform.setImplicitExit(false);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Main.fxml"));
		fxmlLoader.setController(this);
		myPane = fxmlLoader.load();
		Scene scene = new Scene(myPane);
		dragPane = new DragPane(myPane.getWidth(), myPane.getHeight());
		myPane.getChildren().add(dragPane);


		paneAndroid = new AndroidPane();
		paneSplit.getItems().add(0,paneAndroid);
		paneAttributes = new AttributesPane();
		paneSplit.getItems().add(1,paneAttributes);
		
		primaryStage.setTitle(TITLE);
		primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(event -> System.exit(0));
		primaryStage.setScene(scene);
		splitPane.lookupAll(".split-pane-divider").stream().forEach(div -> div.setMouseTransparent(true));

		openProject = new FirebaseProject();
		lblOpenProject.setText(NEW_PROJ);
		openProject.setisDebug(true);
		FirebaseDB.getInstance().setOpenProject(openProject);

		cboDebug.getItems().add("debug version");
		cboDebug.getItems().add("release version");
		cboDebug.getSelectionModel().select("debug version");
		Platform.runLater(() ->{
				resetPanes();
				loadCanvasElements();
			});	
	}

	public AnchorPane getMainPane() {
		return myPane;
	}

	/**
	 * It's helpful to know whether we have an Internet connection, it may be that we don't see changes 
	 * propagate to the database immediately so it'd be nice to know why
	 */
	public void updateConnectedStatus(boolean connected){
		if(connected){
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
	@SuppressWarnings("rawtypes")
	private void setElementParent(ViewElement draggable) {
		if (draggable.getType() == ElementType.UIELEMENT || draggable.getType() == ElementType.QUESTION)
			draggable.setParentPane(dragPane);
		else
			draggable.setParentPane(canvas);
	}

	private void resetPanes() {
		SurveyPane surveyController;
		panePatients.getChildren().clear();
		patientController = new PatientPane();
		panePatients.getChildren().add(patientController);

		if (canvas != null && canvas.mouseHandler != null)
			paneIntervention.removeEventHandler(MouseEvent.ANY, canvas.mouseHandler);
		paneIntervention.getChildren().remove(canvas);
		canvas = new ViewCanvas();
		paneIntervention.getChildren().add(canvas);

		paneAndroid.reset(dragPane);
		paneAttributes.reset(canvas);
		if (surveyBox.getChildren().size() > 1)
			surveyBox.getChildren().remove(1);
		surveyController = new SurveyPane();
		surveyBox.getChildren().add(surveyController); // reset dat shit

		//Add the project elements
		ArrayList<ViewElement<?>> views = new ArrayList<>();

		for (FirebaseTrigger trig : openProject.gettriggers()) {
			Trigger triggerview = Trigger.create(trig);
			views.add(triggerview);
		}
		for (FirebaseExpression expr : openProject.getexpressions()) {
			Expression exprview = Expression.create(expr);
			views.add(exprview);
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
		canvas.addEventHandlers();
		patientController.loadPatients(); // Reset so we have the patients for THIS project
		patientController.loadSurveys();
		tabPane.getSelectionModel().select(tabFramework);

	}

	public void showLoginRegister(){
		Stage stage = new Stage(StageStyle.UNDECORATED);
		LoginRegisterPane root;
		try {
			root = new LoginRegisterPane(stage);
			stage.setScene(new Scene(root));
			if(root.shouldLoad()) {
				Pane pane = new Pane();
				pane.getStyleClass().add("shadowpane");
				pane.setPrefWidth(myPane.getPrefWidth());
				pane.setPrefHeight(myPane.getPrefHeight());
				myPane.getChildren().add(pane);
	
				stage.initStyle(StageStyle.TRANSPARENT);
				stage.initOwner(splitPane.getScene().getWindow());
				stage.showAndWait();
				myPane.getChildren().remove(pane);
			}
		} catch (IOException e) {			
			System.exit(1);
		}
		patientController = new PatientPane();
		panePatients.getChildren().add(patientController);

	}

	@FXML
	public void saveAsStudyMenu(Event e) {
		Stage stage = new Stage(StageStyle.UNDECORATED);
		SaveAsPane root = new SaveAsPane(stage);
		stage.setScene(new Scene(root));
		stage.setTitle("Save Study As...");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(splitPane.getScene().getWindow());
		stage.showAndWait();

	}

	private VBox createSidebarView(ViewElement<?> elem) {
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

	private void loadType(Map<String,String> names,String suffix,VBox container) {
		Iterator<String> iter = names.keySet().iterator();
		String prefix = "com.jeeves.vpl.";
		while(iter.hasNext()) {
			String actName = iter.next();
			String actClass = names.get(actName);
			ViewElement<?> action = ViewElement.create(actName,prefix+suffix+ actClass);
			elements.add(action);
			VBox boxybox = createSidebarView(action);
			container.getChildren().add(boxybox);
		}
	}
	
	private void addCanvasListeners() {
		Divider d1 = splitPane.getDividers().get(1);
		d1.positionProperty().addListener((arg0,arg1,arg2)->{
				if (arg2.doubleValue() < 0.73) {
					d1.setPosition(0.73);
					return;
				}
				imgTrash.setLayoutX(arg2.doubleValue() * myPane.getWidth() - 80);
			});
		DropShadow ds = new DropShadow(20, Color.AQUA);

		imgTrash.addEventHandler(MouseDragEvent.ANY, arg0->{
				if (arg0.getEventType().equals(MouseDragEvent.MOUSE_DRAG_ENTERED)) {
					imgTrash.setEffect(ds);
				} else if (arg0.getEventType().equals(MouseDragEvent.MOUSE_DRAG_EXITED)) {
					imgTrash.setEffect(null);
				}
			});
		d1.setPosition(0.73);
		viewElementHandler = arg0->{
				ViewElement<?> clicked = ((ViewElement<?>) arg0.getSource());
				if (arg0.isSecondaryButtonDown())
					return;
				else if (arg0.getEventType() == MouseEvent.MOUSE_PRESSED)
					myPane.getChildren().add(clicked.getDraggable());
				else if (arg0.getEventType() == MouseEvent.MOUSE_RELEASED) {
					myPane.getChildren().remove(clicked.getDraggable());
					//Not ideal but will have to do for now
					//If we add a Schedule Action and haven't added one before, create schedule attributes
					if(clicked instanceof ScheduleAction && !Constants.getOpenProject().gethasSchedule()) {
						Stage stage = new Stage(StageStyle.UNDECORATED);
						ScheduleAttributesPane cRoot = new ScheduleAttributesPane(stage,paneAttributes);
						stage.setScene(new Scene(cRoot));
						stage.showAndWait();
					}
					ViewElement<?> draggable = null;
					// annoying exception for user variables
					if (clicked.getType() == ElementType.VARIABLE)
						draggable = new UserVariable(((FirebaseExpression) clicked.getModel()));
					else
						draggable = ViewElement.create(clicked.getName(),clicked.getClass().getName());
					setElementParent(draggable);
					clicked.setDraggable(draggable); // DJRNEW

				}
			};
	}
	//Position the dividers and trash can
	private void loadCanvasElements() {
		addCanvasListeners();
		
		
			elements = new ArrayList<>();
			String prefix = "com.jeeves.vpl.";
			loadType(trigNames,"canvas.triggers.",paneTriggers);
			loadType(actNames,"canvas.actions.",paneActions);
			loadType(exprNames,"canvas.expressions.",paneConditions);
			loadType(elemNames,"canvas.uielements.",paneAndroid.getContainer());
			for (String[] qName : questionNames) {
				ViewElement<?> question = ViewElement.create(qName[0],prefix+"survey.questions." + qName[3]);
				elements.add(question);
				((QuestionView)question).setQuestionType(qName[0]);
				((QuestionView)question).setImage(qName[1]);
				paneQuestions.getChildren().add(question);
			}
			for (ViewElement<?> element : elements) {
				if(element instanceof QuestionView)
					element.setPadding(new Insets(0,0,10,0));
				else	
					element.setPadding(new Insets(0, 0, 20, 0));
				ViewElement<?> draggable = ViewElement.create(element.getName(),element.getClass().getName());
				element.setDraggable(draggable); // DJRNEW
				setElementParent(draggable);
				element.setReadOnly();
				element.setPickOnBounds(false);
				element.setHandler(viewElementHandler);

			}
			paneAttributes.setEventHandler(viewElementHandler);
		//	paneAttributes.loadVariables();
		

		//Resize the tabs depending on what's focused
		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.selectedItemProperty().addListener((arg0,arg1,arg2)->{
				Divider divider = splitPane.getDividers().get(0);
				if (arg2 != null && arg2.equals(tabFramework)) {
					divider.setPosition(0.25);
					imgTrash.setVisible(true);
				}
				else if (arg2 != null && arg2.equals(tabPatients)) {
					divider.setPosition(0.6);
					imgTrash.setVisible(false);
				}
				else {
					divider.setPosition(0.75);
					imgTrash.setVisible(false);
				}
			});

		labelPaneMap = new HashMap<>();
		labelPaneMap.put(lblActions, paneActions);
		labelPaneMap.put(lblConditions, paneConditions);
		labelPaneMap.put(lblTriggers, paneTriggers);
		lblTriggers.setUserData(SELECTED);
		activeMenu = lblTriggers;


		primaryStage.show();
		showLoginRegister();

	}

	public void setCurrentProject(FirebaseProject project){
		setUpdateTriggers(false);

		openProject = project;
		lblOpenProject.setText(project.getname());
		FirebaseDB.getInstance().setOpenProject(openProject);
		primaryStage.setTitle(TITLE);
		resetPanes();
		
		if(openProject.getisDebug())
			cboDebug.getSelectionModel().select("debug version");
		else
			cboDebug.getSelectionModel().select("release version");
		
		cboDebug.getSelectionModel().selectedItemProperty()
	    .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
	    	if(newValue.equals("debug version"))
	    		openProject.setisDebug(true);
	    	else
	    		openProject.setisDebug(false);
	    });

		
		for (ViewElement<?> element : elements) {
			ViewElement<?> draggable = ViewElement.create(element.getName(),element.getClass().getName());
			element.setDraggable(draggable); // DJRNEW
			setElementParent(draggable);
			element.setReadOnly();
			element.setPickOnBounds(false);
			element.setHandler(viewElementHandler);
		}
		paneAttributes.loadVariables();
		setUpdateTriggers(true);
	}

	/**Method called from SaveAsPane until I can be bothered with a better way of doing things**/
	public void setNameLabel(String name){
		lblOpenProject.setText(name);
	}
	@FXML
	private void removeHighlight(Event e) {
		Label label = (Label) e.getSource();
		if (label.getUserData() == null || !label.getUserData().equals(SELECTED))
			label.setTextFill(Color.BLACK);
	}

	@FXML
	public void addGlow(Event e){
		ImageView image = (ImageView)e.getSource();
		image.getStyleClass().add(GLOW_CLASS);
		VBox parent = (VBox)image.getParent();
		Label txtDescr = (Label)parent.getChildren().get(1);
		txtDescr.setVisible(true);
	}
	@FXML
	public void removeGlow(Event e){
		ImageView image = (ImageView)e.getSource();
		image.getStyleClass().remove(GLOW_CLASS);
		VBox parent = (VBox)image.getParent();
		Label txtDescr = (Label)parent.getChildren().get(1);
		txtDescr.setVisible(false);
	}

	/** User press 'New' button **/
	@FXML
	public void newStudy(Event e) {
		setCurrentProject(new FirebaseProject());
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
	public void cloneStudy(Event e) {
		if(openProject.getname() == null) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("No saved study");
			alert.setHeaderText(null);
			alert.setContentText("Please save your study before attempting to clone it");
			alert.showAndWait();
			return;
		}
		TextInputDialog dialog = new TextInputDialog("Copy of " + openProject.getname());
		dialog.setTitle("Clone name");
		dialog.setHeaderText("Please enter a name for the cloned study");
		dialog.setContentText("Clone name:");

		Optional<String> result = dialog.showAndWait();
		// The Java 8 way to get the response value (with lambda expression).
		result.ifPresent(name ->{
			System.out.println("Your name: " + name);
			openProject.setname(name);
			FirebaseDB.getInstance().saveProject(null, this.openProject);

		});

	}
	Storage storage;
	Bucket bucket;
	private void getStorage() {
		try {
			InputStream resource = new FileInputStream(Constants.FILEPATH);
			storage = StorageOptions.newBuilder().setProjectId("firebaseId")
					.setCredentials(ServiceAccountCredentials.fromStream(resource))
					.build()
					.getService();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	@FXML
	public void generateStudyUrl(Event e) {
		if(storage == null) {
			getStorage();
		}

		Bucket bucket = StorageClient.getInstance().bucket();
		File file = new File(Constants.ANDROIDPATH); //Path to JSON config file
		JsonParser parser = new JsonParser();
		JsonElement fileStuff;
		try {
			JsonObject studyInfo = new JsonObject();
			studyInfo.addProperty("title", openProject.getname());
			studyInfo.addProperty("description", "");
			System.out.println("AND HERE IT IS will be " + openProject.getresearcherno());

			studyInfo.addProperty("researcher", openProject.getresearcherno());
			fileStuff = parser.parse(new JsonReader(new FileReader(file)));
			fileStuff.getAsJsonObject().add("studyinfo", studyInfo);
			FileWriter writer = new FileWriter(Constants.ANDROIDPATH + "_updated");
			writer.write(fileStuff.toString());
			writer.close();
		} catch (JsonIOException | JsonSyntaxException | IOException e2) {
			e2.printStackTrace();
		}
		file = new File(Constants.ANDROIDPATH + "_updated"); //Doing this again just in case
		if(file.exists()) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Existing study URL");
			alert.setHeaderText(null);
			alert.setContentText("A URL for this study already exists. Would you like to make a new one?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() != ButtonType.OK){
				return; //User has said no
			}
		}
		if (file != null) {
			try {
				BlobId blobId = BlobId.of(bucket.getName(), file.getName());
				BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/json").build();
			    Blob blob = storage.create(blobInfo, Files.readAllBytes(Paths.get(file.getName())));
				//Blob blob = storage.create(blobInfo,new FileInputStream(file));
				URL myUrl = blob.signUrl(14, TimeUnit.DAYS);
				System.out.println("media link is " + blob.getMediaLink());
				//System.out.println(myUrl);
				TextInputDialog dialog = new TextInputDialog(myUrl.toString());
				//TextInputDialog dialog = new TextInputDialog(blob.getMediaLink());
				dialog.setTitle("Study URL");
				dialog.setHeaderText("Ask your participants to open this URL on their device after installing the Jeeves app");
				//dialog.getEditor().setPrefWidth(250);
			//	dialog.setWidth(340);
			//	dialog.setContentText("Ask your participants to open this URL on their device after installing the Jeeves app:");
				dialog.getEditor().setEditable(false);
				dialog.show();
			//	Constants.makeInfoAlert("Jeeves", "New study URL", "Your study URL is " + myUrl);
			} catch (IOException e1) {
				e1.printStackTrace();
			} 

		}	
	}
	@FXML
	public void saveStudy(Event e){
		String toastMsg = "Project saved";
		Point2D canvasPos = paneIntervention.localToScreen(new Point2D(0,0));
		double canvasLength = paneIntervention.getWidth();
		if (openProject.getname() == null) {
			saveAsStudyMenu(e);
		} else {
			FirebaseDB.getInstance().saveProject(openProject.getname(), this.openProject);
		}
		Toast.makeText(primaryStage,canvasPos.getX(),canvasPos.getY(), canvasLength, toastMsg,24);
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


	/**
	 * Potential unimplemented feature - highlighting menu in which to find a relevant element
	 */
	Label lastActiveMenu;
	Label activeMenu;
	public void highlightMenu(ElementType type,boolean shouldHighlight){

		if(!shouldHighlight){
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
				paneTriggers.getChildren().forEach(action->action.getStyleClass().add(GLOW_CLASS));
			}
			else
				paneTriggers.getChildren().forEach(action->action.getStyleClass().remove(GLOW_CLASS));

			break;
		case EXPRESSION:
			if(shouldHighlight){
				showMenu(lblConditions);
				paneConditions.getChildren().forEach(action->action.getStyleClass().add(GLOW_CLASS));
			}
			else
				paneConditions.getChildren().forEach(action->action.getStyleClass().remove(GLOW_CLASS));

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
		label.setUserData(SELECTED);
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
}