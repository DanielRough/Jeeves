package com.jeeves.vpl;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeeves.vpl.canvas.receivers.ElementReceiver;
import com.jeeves.vpl.canvas.uielements.UIElement;
import com.jeeves.vpl.firebase.FirebaseProject;
import com.jeeves.vpl.firebase.FirebaseUI;

import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class AndroidPane extends Pane{
	@FXML private Pane paneAndroid;
	@FXML private VBox vboxUIElements;
	ElementReceiver receiver; 
	DragPane dragPane;
	EditDeletePane editDeletePane;

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
	public AndroidPane() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AndroidPane.fxml"));
		fxmlLoader.setController(this);
		try {
			Pane myPane = fxmlLoader.load();
			getChildren().add(myPane);
		} catch (IOException e) {
			e.printStackTrace();
		}
		DragPane dragPane = new DragPane(getWidth(),getHeight());
		getChildren().add(dragPane);
		editDeletePane = new EditDeletePane();
		getChildren().add(editDeletePane);
		editDeletePane.setVisible(false);
		addEventHandler(MouseEvent.MOUSE_EXITED, handler->{editDeletePane.setVisible(false);});
	}
	public VBox getContainer() {
		return vboxUIElements;
	}

	public void reset() {
		paneAndroid.getChildren().clear();
		receiver = new ElementReceiver(215, 307);
		paneAndroid.getChildren().add(receiver);
		receiver.getChildElements().addListener(new ListChangeListener<ViewElement>() {
			@Override
			public void onChanged(Change<? extends ViewElement> c) {		
				c.next();
				if(c.getAddedSize() ==0) {
					return;
				}
				ViewElement elem = c.getAddedSubList().get(0);
				elem.addEventHandler(MouseEvent.MOUSE_PRESSED, handler->{editDeletePane.setVisible(false);});
				elem.addEventHandler(MouseEvent.MOUSE_ENTERED, handler->{
					
					Point2D point = new Point2D(elem.getLayoutX()+elem.getWidth(),elem.getLayoutY()-30);
					
					System.out.println("Layout x is " + elem.getLayoutX() + " and y is " + elem.getLayoutY());
					Point2D worsepoint = elem.parentToLocal(point);
					Point2D holyfuckpoint = elem.localToScene(worsepoint);
					Point2D betterpoint = sceneToLocal(holyfuckpoint);
					editDeletePane.setLayoutX(betterpoint.getX());
					editDeletePane.setLayoutY(betterpoint.getY());
					EventHandler<MouseEvent> editHandler = new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							((UIElement)elem).update(receiver.getChildElements());
							editDeletePane.setVisible(false);
						}
					};
					EventHandler<MouseEvent> delHandler = new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {							
							receiver.removeChild(elem);
							editDeletePane.setVisible(false);
						}
					};					
					editDeletePane.registerEditListener(editHandler);
					editDeletePane.registerDeleteListener(delHandler);
					editDeletePane.setVisible(true);
				});
			}
		});

		FirebaseProject openProject = Constants.getOpenProject();


		int index = 0;

		for (FirebaseUI var : openProject.getuidesign()) {

			UIElement element = UIElement.create(var);
			var.getMyTextProperty().addListener(change -> {
				receiver.getChildElements().remove(element);
				receiver.getChildElements().add(element);
			});

			element.setPreviouslyAdded(true);  
			receiver.addChildAtIndex(element, index++);
			element.setParentPane(dragPane);
			element.addEventHandler(MouseEvent.ANY, element.mainHandler);
		}
		receiver.addChildListeners();

	}
	
}
