package com.jeeves.vpl;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.canvas.actions.UpdateAction;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseElement;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * The main superclass that governs all the draggable elements in the visual
 * language
 * 
 * @author Daniel
 *
 */
@SuppressWarnings("rawtypes")

public abstract class ViewElement<T extends FirebaseElement> extends Pane {
	// Create a new ViewElement from just the class name
	public static ViewElement create(String name, String classname) {
		try {
			return (ViewElement) Class.forName(classname).getConstructor(String.class).newInstance(name);
		} catch (Exception e) {
			e.printStackTrace();// better than null
		}
		return null;
	}
	protected T model;
	protected ElementType type;
	protected DragPane parentPane;
	protected int oldIndex;
	protected Main gui;

	protected double mouseX;
	protected double mouseY;
	protected double x;
	protected double y;
	protected Point2D position = new Point2D(0, 0);

	private ViewElement draggable; // Self-referencing class, hm...
	protected EventHandler<MouseEvent> draggedHandler;
	protected EventHandler<MouseEvent> mainHandler;
	protected EventHandler<MouseEvent> releasedHandler;
	protected EventHandler<MouseEvent> sidebarElemHandler;

	//I 
	public ViewElement(T data, Class<T> typeParameterClass) {
		this.gui = Main.getContext();
		fxmlInit();
		addListeners();
		setData(data);
		initEventHandlers();
		setPickOnBounds(false);
	}

	// Nice new convenience methods
	public void addAllHandlers() {
		addEventHandler(MouseEvent.ANY, mainHandler);
		addEventHandler(MouseEvent.DRAG_DETECTED, draggedHandler);
		addEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
	}

	public abstract void fxmlInit();
	public abstract ViewElement<T> getInstance();

	public ViewElement getDraggable() {
		return draggable;
	}


	public FirebaseElement getModel() {
		return model;
	}

	public String getName() {
		return model.getname();
	}

	// The element's position is an X,Y coordinate on the Canvas
	public Point2D getPosition() {
		return position;
	}

	public ElementType getType() {
		return type;
	}

	public void initEventHandlers() {
		draggedHandler = event -> {
			if (event.isSecondaryButtonDown()) {
				event.consume();
				return;
			}
			event.consume();
			startFullDrag();
			setMouseTransparent(true);
		};
		releasedHandler = event -> {
			if (event.getButton().equals(MouseButton.SECONDARY))
				return;
			if (gui.isOverTrash(event.getSceneX(), event.getSceneY())) {
				if (parentPane != null)
					parentPane.removeChild(getInstance());
			} else {
				setPosition((new Point2D(getLayoutX(), getLayoutY())));
				setCursor(Cursor.HAND);
				setManaged(true);
				setMouseTransparent(false);
			}

			setMouseTransparent(false);
		};

		// This is the handler for when the element is one of the sidebar
		// elements
		sidebarElemHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isSecondaryButtonDown()) {
					event.consume();
					return;
				}
				setOnDragDetected(event1 -> {
					if (event1.isSecondaryButtonDown())
						return;
					draggable.startFullDrag();
				});
				if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
					draggable.setMouseTransparent(true);
					draggable.setLayoutX(event.getSceneX());
					draggable.setLayoutY(event.getSceneY());
					draggable.getStyleClass().add("drop_shadow");

					setEffect(null);
				} else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
					draggable.setLayoutX(event.getSceneX());
					draggable.setLayoutY(event.getSceneY());
				} else if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
					setCursor(Cursor.HAND);
					getStyleClass().add("drop_shadow");
				} else if (event.getEventType().equals(MouseEvent.MOUSE_EXITED)) {
					getStyleClass().remove("drop_shadow");
				} else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {

					draggable.getStyleClass().remove("drop_shadow");
					draggable.addAllHandlers();
				}
			}
		};

		mainHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isSecondaryButtonDown()) {
					return;
				}
				// An event for when we press the mouse
				else if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
					requestFocus();
					parentPane.addChild(getInstance(), event.getSceneX(), event.getSceneY());
					event.consume();
					setManaged(false);
					toFront();
					Point2D parentPoint = getParent().sceneToLocal(event.getSceneX(), event.getSceneY());
					x = getLayoutX();
					y = getLayoutY();
					mouseX = parentPoint.getX();
					mouseY = parentPoint.getY();
				}
				// An event for dragging the element about
				else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
					if (event.isSecondaryButtonDown()) {
						return;
					}
					setMouseTransparent(true);
					Point2D parentPoint = getParent().sceneToLocal(event.getSceneX(), event.getSceneY());
					double offsetX = parentPoint.getX() - mouseX;
					double offsetY = parentPoint.getY() - mouseY;
					x += offsetX;
					y += offsetY;
					setLayoutX(x);
					setLayoutY(y);
					mouseX = parentPoint.getX();
					mouseY = parentPoint.getY();

					event.consume();
				} else if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
					setCursor(Cursor.HAND);
				}
			}
		};
		addEventHandler(MouseEvent.DRAG_DETECTED, draggedHandler);
		addEventFilter(MouseEvent.MOUSE_RELEASED, releasedHandler);
	}

	public void removeAllHandlers() {
		removeEventHandler(MouseEvent.ANY, mainHandler);
		removeEventHandler(MouseEvent.DRAG_DETECTED, draggedHandler);
		removeEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
	}


	public void setDraggable(ViewElement draggable) {
		this.draggable = draggable;
	}

	// DJRNEW
	public void setHandler(EventHandler<MouseEvent> handler) {
		this.addEventHandler(MouseEvent.ANY, handler);
	}

	public void setParentPane(DragPane parent) {
		this.parentPane = parent;
		addAllHandlers();
	}

	// This sets a new position of the element
	public void setPosition(Point2D pos) {
		setLayoutX(pos.getX());
		setLayoutY(pos.getY());
		position = pos;
	}

	public void setReadOnly() {
		removeAllHandlers();
		addEventHandler(MouseEvent.ANY, sidebarElemHandler);
		Pane child = (Pane)getChildren().get(0);
		for(int i = 0; i < child.getChildren().size(); i++) {
			child.getChildren().get(i).setDisable(true);
			child.getChildren().get(i).setMouseTransparent(true);
		}
	}

	public void setOldIndex(int index) {
		this.oldIndex = index;
	}
	public int getOldIndex(){
		return oldIndex;
	}
	protected void addListeners() {
		layoutXProperty().addListener(listener -> model.setxPos((long) getLayoutX()));
		layoutYProperty().addListener(listener -> {
			model.setyPos((long) getLayoutY());
		});

	}

	//I worry that this will overwrite the model stuff that's already in there.
	protected void setData(T model) {
		
		this.model = model;
		if(this instanceof UpdateAction) {
			System.out.println("WOO " + ((FirebaseAction)model).getvars());
			System.out.println("Position: " + model.getxPos() + "," + model.getyPos());
		}
		Point2D position = new Point2D(model.getxPos(), model.getyPos());
		setPosition(position);
	}

}
