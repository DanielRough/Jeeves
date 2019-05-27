package com.jeeves.vpl;

import static com.jeeves.vpl.Constants.USER_BOOLEAN;
import static com.jeeves.vpl.Constants.USER_NUMERIC;
import static com.jeeves.vpl.Constants.VAR_BOOLEAN;
import static com.jeeves.vpl.Constants.VAR_CATEGORY;
import static com.jeeves.vpl.Constants.VAR_CLOCK;
import static com.jeeves.vpl.Constants.VAR_DATE;
import static com.jeeves.vpl.Constants.VAR_NUMERIC;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseProject;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AttributesPane extends Pane{
	@FXML private VBox vboxSurveyVars;
	@FXML private CheckBox chkRandom;
	@FXML private Button btnAddVar;
	@FXML private Button btnDeleteVar;
	@FXML private Button btnInspectVar;
	@FXML private Label lblSelected;
	@FXML private TextField txtAttrName;
	@FXML private ChoiceBox<String> cboAttrType;
	private UserVariable selectedVar;
	private ViewCanvas canvas;
	EventHandler<MouseEvent> viewElementHandler;
	
	private class EditDeletePane extends Pane{
		final Logger logger = LoggerFactory.getLogger(RandomDatePane.class);
		@FXML private ImageView imgEdit;
		@FXML private ImageView imgDel;
		
		public EditDeletePane() {
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/EditDeletePane.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = fxmlLoader.load();
			getChildren().add(root);
		} catch (Exception e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}
		addEffects();
		}
		public void registerEditListener(EventHandler<MouseEvent> handler) {
			imgEdit.setOnMousePressed(handler);
		}
		public void registerDeleteListener(EventHandler<MouseEvent> handler) {
			imgDel.setOnMousePressed(handler);
		}
		public void addEffects() {
			imgEdit.setOnMouseEntered(handler->{setCursor(Cursor.HAND);imgEdit.getStyleClass().add("drop_shadow");});
			imgDel.setOnMouseEntered(handler->{setCursor(Cursor.HAND);imgDel.getStyleClass().add("drop_shadow");});			
			imgEdit.setOnMouseExited(handler->{setCursor(Cursor.DEFAULT);imgEdit.getStyleClass().remove("drop_shadow");});
			imgDel.setOnMouseExited(handler->{setCursor(Cursor.DEFAULT);imgDel.getStyleClass().remove("drop_shadow");});
		}
	}
	EditDeletePane editDeletePane;
	public AttributesPane() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AttributesPane.fxml"));
		fxmlLoader.setController(this);
		try {
			Pane myPane = fxmlLoader.load();
			getChildren().add(myPane);
		} catch (IOException e) {
			e.printStackTrace();
		}
		cboAttrType.getItems().clear();
		cboAttrType.getItems().addAll("True/False","Number","Category","Date","Time","Location");
		editDeletePane = new EditDeletePane();
		getChildren().add(editDeletePane);
		editDeletePane.setVisible(false);
		addEventHandler(MouseEvent.MOUSE_EXITED, handler->{editDeletePane.setVisible(false);});


	}
	public void addVariable(FirebaseVariable var) {
		Constants.getOpenProject().getvariables().add(var);
	}
	public void reset(ViewCanvas canvas) {
		this.canvas = canvas;
	}
	public void setEventHandler(EventHandler<MouseEvent> viewElementHandler) {
		this.viewElementHandler = viewElementHandler;
	}
	public void loadVariables() {
		vboxSurveyVars.getChildren().clear();
		FirebaseProject openProject = Constants.getOpenProject();
		openProject.getvariables().forEach(variable -> {
			if(variable != null) {
				UserVariable global = new UserVariable(variable);
					ViewElement<FirebaseExpression> draggable = new UserVariable(variable);
					global.setDraggable(draggable); // DJRNEW
					global.setReadOnly();
					draggable.setParentPane(canvas);
					global.setHandler(viewElementHandler);
					vboxSurveyVars.getChildren().add(global);
					global.addEventHandler(MouseEvent.MOUSE_ENTERED, (x)->{
						selectedVar = global;
						//Thou shalt not edit the schedule attributes
						if(openProject.getscheduleAttrs().values().contains(selectedVar.getName())){
							editDeletePane.setVisible(false);
							return;
						}
						Point2D point = new Point2D(global.getLayoutX()+global.getWidth(),global.getLayoutY()-30);
						Point2D worsepoint = global.parentToLocal(point);
						Point2D holyfuckpoint = global.localToScene(worsepoint);
						Point2D betterpoint = sceneToLocal(holyfuckpoint);
						editDeletePane.setLayoutX(betterpoint.getX());
						editDeletePane.setLayoutY(betterpoint.getY());
						EventHandler<MouseEvent> editHandler = new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								inspectAttribute();
								editDeletePane.setVisible(false);
							}
						};
						EventHandler<MouseEvent> delHandler = new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								deleteAttribute();
								editDeletePane.setVisible(false);
							}
						};					
						editDeletePane.registerEditListener(editHandler);
						editDeletePane.registerDeleteListener(delHandler);
						editDeletePane.setVisible(true);
					});
			}
		});

	}

	public void deleteAttribute() {
		FirebaseProject openProject = Constants.getOpenProject();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Jeeves");
		alert.setHeaderText("Delete Attribute");
		alert.setContentText("Do you really want to delete attribute " + selectedVar.getName() + "?");
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get().equals(ButtonType.OK)) {
			openProject.getvariables().remove(selectedVar.getModel());
			selectedVar = null;
			loadVariables();
		}
	}
	@FXML
	public void addAttribute(Event e){
		FirebaseVariable var = new FirebaseVariable();
		String attrName = txtAttrName.getText();
		if(attrName.isEmpty()){
			return;
		}
		for(FirebaseVariable v : Constants.getOpenProject().getvariables()) {
			if(attrName.equals(v.getname())) {
				Constants.makeInfoAlert("Duplicate", "Duplicate Attribute Name", "Attribute with name "  + attrName + " already exists");
				return;
			}
		}
		var.setname(attrName);
		String attrType = cboAttrType.getValue();
		if(attrType == null || attrType.isEmpty()){
			return;
		}
		switch(attrType){
		case USER_BOOLEAN:var.setVartype(VAR_BOOLEAN);break;
		case USER_NUMERIC:var.setVartype(VAR_NUMERIC);break;
		default :var.setVartype(attrType);break;
		}
		var.setisCustom(true);
		var.settimeCreated(System.currentTimeMillis());
		if(chkRandom.isSelected()) {
			var.setisRandom(true);
			Stage stage = new Stage(StageStyle.UNDECORATED);
			stage.setTitle("Random attribute");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(getScene().getWindow());
			switch(attrType) {
			case "Number":
				RandomNumberPane nRoot = new RandomNumberPane(stage,var,true);
				stage.setScene(new Scene(nRoot));
				stage.showAndWait();
				break;
			case "Category":
				RandomCategoryPane cRoot = new RandomCategoryPane(stage,var,true);
				stage.setScene(new Scene(cRoot));
				stage.showAndWait();
				break;
			case "Date": 
				RandomDatePane dRoot = new RandomDatePane(stage,var,true);
				stage.setScene(new Scene(dRoot));
				stage.showAndWait();
				break;
			case "Time": 
				RandomTimePane tRoot = new RandomTimePane(stage,var,true);
				stage.setScene(new Scene(tRoot));
				stage.showAndWait();
				break;
			default:addVariable(var); break;
			}
		}
		else {
			addVariable(var);
		}
		loadVariables();

	}
	//Attributes stuff

		
		public void inspectAttribute() {
			String attrType = selectedVar.getVarType();
			Stage stage = new Stage(StageStyle.UNDECORATED);
			stage.setTitle(selectedVar.getName() + " details");
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(getScene().getWindow());
			switch(attrType) {
			case VAR_NUMERIC:
				RandomNumberPane nRoot = new RandomNumberPane(stage,(FirebaseVariable)selectedVar.getModel(),false);
				stage.setScene(new Scene(nRoot));
				stage.showAndWait();
				break;
			case VAR_CATEGORY:
				RandomCategoryPane cRoot = new RandomCategoryPane(stage,(FirebaseVariable)selectedVar.getModel(),false);
				stage.setScene(new Scene(cRoot));
				stage.showAndWait();
				break;
			case VAR_DATE: 
				RandomDatePane dRoot = new RandomDatePane(stage,(FirebaseVariable)selectedVar.getModel(),false);
				stage.setScene(new Scene(dRoot));
				stage.showAndWait();
				break;
			case VAR_CLOCK: 
				RandomTimePane tRoot = new RandomTimePane(stage,(FirebaseVariable)selectedVar.getModel(),false);
				stage.setScene(new Scene(tRoot));
				stage.showAndWait();
				break;
			default:break;
			}
		}
		
}
