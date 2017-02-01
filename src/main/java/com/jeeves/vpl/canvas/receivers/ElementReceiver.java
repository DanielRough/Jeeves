package com.jeeves.vpl.canvas.receivers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.uielements.UIElement;
import com.jeeves.vpl.firebase.FirebaseProject;

public class ElementReceiver extends Pane implements IReceiver {
	private VBox elements;
	private int hoveredIndex = -1; // If we've dragged over a particular child

	private Rectangle captureRect;
	private ObservableList<ViewElement> childList = FXCollections
			.observableArrayList();
	private EventHandler<MouseDragEvent> mexited;
	private EventHandler<MouseDragEvent> mentered;
	private EventHandler<MouseDragEvent> mreleased;
	private EventHandler<MouseDragEvent> draggedhandler;
	private FirebaseProject project;


	public void setProject(FirebaseProject project) {
		this.project = project;
	}

	public ElementReceiver(double width, double height) {
		elements = new VBox();
		elements.setPadding(new Insets(15, 0, 0, 0)); 
		getChildren().add(elements);
		captureRect = new Rectangle();
		getChildren().add(captureRect);
		captureRect.setLayoutY(0);
		captureRect.setLayoutX(0);
		captureRect.toBack();
		captureRect.setHeight(height);
		captureRect.setWidth(width);
		elements.setPrefWidth(width);
		elements.setFillWidth(false);

		captureRect.setOpacity(0);
		elements.setAlignment(Pos.TOP_CENTER);
		elements.setSpacing(10);

		setPickOnBounds(false);
		elements.setPickOnBounds(false);

		mentered = event -> {
			event.consume();
			if(! (event.getGestureSource() instanceof ViewElement))
				return;
			if (!isValidElement((ViewElement) event.getGestureSource()))
				return;

		};
		mexited = event -> {
			event.consume();
			if(! (event.getGestureSource() instanceof ViewElement))
				return;
			if (!isValidElement((ViewElement) event.getGestureSource()))
				return;

		};
		mreleased = event -> {
			event.consume();
			if(!(event.getGestureSource() instanceof UIElement))
				return;
			UIElement elem = (UIElement) event.getGestureSource();
			
			addElement(elem, event.getSceneX(), event.getSceneY());

		};
		draggedhandler = event -> {
			if(!(event.getGestureSource() instanceof UIElement))
				return;
			((UIElement) (event.getGestureSource())).dragged = true;
			hoveredIndex = Math.min((int) ((event.getY() - 55) / 50),
					childList.size());
		};
		captureRect
				.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, mentered);
		captureRect.addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, mexited);
		captureRect.addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED,
				mreleased);
		captureRect.addEventHandler(MouseDragEvent.MOUSE_DRAG_OVER,
				draggedhandler);
	}

	private UIElement newElement;
	@Override
	public void addChild(ViewElement child, double mouseX, double mouseY) {
		UIElement newChild = (UIElement) child;
		newChild.setReceiver(this);

		Point2D point = elements.sceneToLocal(mouseX, mouseY);
		for (int i = 0; i < elements.getChildren().size(); i++) {

			ViewElement elem = ((ViewElement) elements.getChildren().get(i));
			double max = (elem.getLayoutY() + elem.getHeight());
			if (point.getY() < max) {
				break;
			}
			hoveredIndex = (i + 1);
		}
		if (hoveredIndex > -1) {
			childList.add(hoveredIndex, newChild);

		} else {
			childList.add(newChild);
		}

		newChild.setManaged(true); // So it sits in the appropriate place
		newChild.setMouseTransparent(false);

		EventHandler<MouseEvent> removeHandler = new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				if (e.isSecondaryButtonDown()) {
					return;
				}
				((UIElement) newChild).dragged = false;

				removeChild(newChild);
				newChild.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
			}
		};

		newChild.addEventHandler(MouseEvent.MOUSE_PRESSED, removeHandler);
		ChangeListener<Number> updateListener1 = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				elements.getChildren().clear();
				elements.getChildren().addAll(childList);
				elements.setAlignment(Pos.TOP_CENTER);
				Platform.runLater(new Runnable() {
					public void run() {
						newElement = null;
						for(ViewElement childy : childList){
							UIElement elem = (UIElement) childy;
							if (elem.previouslyAdded == false) {
								elem.previouslyAdded = true;
								newElement = elem;
								break;
							}
						}
						if(newElement != null)
							newElement.update(); // Ask to edit text if we haven't
					}
				});

			}
		};
		ChangeListener<Number> updateListener2 = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {

				elements.getChildren().clear();
				elements.getChildren().addAll(childList);
				elements.setAlignment(Pos.TOP_CENTER);

			}
		};
		elements.heightProperty().addListener(updateListener1);
		newChild.heightProperty().addListener(updateListener2);

		newChild.setOnMouseDragExited(event -> {
			hoveredIndex = -1;
			if (getElements().contains(event.getX() + 20, event.getY())
					&& event.isPrimaryButtonDown()) {
				mentered.handle(event);
			} else {
				mexited.handle(event);
			}
		});

		// This is getting a bit complicated, but we ONLY WANT TO FIRE MOUSE
		// RELEASED IF IT HASNT BEEN DRAGGED AT ALL
		newChild.setOnMouseReleased(event -> {
			event.consume();
			if (event.getButton().equals(MouseButton.SECONDARY))
				return;
			if (newChild.dragged == false) {
				event.consume();
				addElement((UIElement) event.getSource(), event.getSceneX(),
						event.getSceneY());
			}
			else
			newChild.dragged = false;
	//		project.getuidesign().add(child);
		});
		child.setOnMouseDragReleased(event -> {
			event.consume();
			captureRect.fireEvent(event);
			newChild.dragged = false;
		}); // Because the children are over it so it needs to be fired

		if (child.getHeight() > 0) {
			elements.getChildren().clear();
			elements.getChildren().addAll(childList);
		}
		

	}

	@Override
	public ObservableList<ViewElement> getChildElements() {
		return childList;

	}

	@Override
	public boolean isValidElement(ViewElement element) {
		if (element instanceof UIElement)
			return true;
		return false;
	}

	public void addElement(UIElement elem, double xpos, double ypos) {
		if (!isValidElement((UIElement) elem))
			return;
		addChild(elem, xpos, ypos);
		elem.toFront();
	}

	@Override
	public void removeChild(ViewElement child) {
		child.setOnMouseDragExited(null);
		child.setOnMouseDragReleased(null); 
		child.setOnMouseDragEntered(null);
		elements.getChildren().remove(child);
		elements.autosize();
		childList.remove(child);
		getChildren().remove(child);
	}

	public VBox getElements() {
		return elements;
	}
}
