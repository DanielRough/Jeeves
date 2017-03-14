package com.jeeves.vpl;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import com.jeeves.vpl.canvas.receivers.ElementReceiver;
import com.jeeves.vpl.canvas.receivers.IReceiver;
import com.jeeves.vpl.firebase.FirebaseElement;

/**
 * The main superclass that governs all the draggable elements in the visual
 * language
 * @author Daniel
 *
 */
public abstract class ViewElement<T extends FirebaseElement> extends Pane{
	private ViewElement draggable; //Self-referencing class, hm...
	protected T model;
     public boolean isReadOnly = false;

     public void setReadOnly(){
    	 isReadOnly = true;
    	 removeEventHandler(MouseEvent.ANY,mainHandler);
    	 addEventHandler(MouseEvent.ANY,sidebarElemHandler);
			Node[] widgets = getWidgets();
			for(int i = 0; i < widgets.length;i++){
				widgets[i].setDisable(true);
				widgets[i].setMouseTransparent(true);		
			} 
     }

	public FirebaseElement getModel(){
		return model;
	}

	static ViewElement create(String type) {
		try {
			return (ViewElement) Class.forName(type).getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setDraggable(ViewElement draggable){
		this.draggable = draggable;
	}

	public ViewElement getDraggable(){
		return draggable;
	}
	protected Main gui;
	public EventHandler<MouseEvent> draggedHandler;
	public EventHandler<MouseEvent> mainHandler;
	private EventHandler<MouseEvent> sidebarElemHandler;
	public EventHandler<MouseEvent> releasedHandler;
	private ViewCanvas currentCanvas; 
	//Position of the element on the canvas
	protected Point2D position = new Point2D(0, 0);

	//This lets the element reference be acquired within event handler methods
	public abstract ViewElement<T> getInstance();

	//Getting the widgets on this element that can be interacted with
	public abstract Node[] getWidgets();
	
	public StringProperty name = new SimpleStringProperty();
	public String description;
	public String getName(){
		return name.get();
	}
	private IReceiver receiver;
	private double initialHeight;
	protected ActionHolder parent;

	//For whatever reason we want the element to stop responding in the normal way
	public void removeHander(){
		this.removeEventHandler(MouseEvent.ANY, mainHandler);
	}
	public String getDescription(){
		return description;
	}
	public void setName(String name){
		this.name.setValue(name);
	}
	public void setDescription(String description){
		this.description = description;
	}

	public double getInitHeight(){
		return initialHeight;
	}
	public void setInitHeight(double height){
		this.initialHeight = height;
	}
	
	public void setActionHolder(ActionHolder holder){
		this.parent = holder;
	}
	public ActionHolder getActionHolder(){
		return parent;
	}
	public void setReceiver(IReceiver receiver){
		this.receiver = receiver;
	}
	public IReceiver getReceiver(){
		return receiver;
	}

	//DJRNEW
	public void setHandler(EventHandler<MouseEvent> handler){
		this.addEventHandler(MouseEvent.ANY, handler);
	}
	
	public abstract void fxmlInit();
	
	public ViewElement(T data,Class<T> typeParameterClass) {
		this.gui = Main.getContext();
		this.model= data;
		fxmlInit();
		if(data.getname() != null) //Data is null if it's a new element{
			setData(data);
		addListeners();
	//	this.typeParameterClass = typeParameterClass;
		 try {
			 if(data.getname() == null)
			this.model = typeParameterClass.newInstance();
		} catch (Exception e){
			e.printStackTrace();
		}
		 DropShadow shadow = new DropShadow();
		 shadow.setWidth(25);
		 shadow.setHeight(25);
		 shadow.setRadius(15);
		 shadow.setSpread(0.8);
		 shadow.setColor(Color.LIGHTBLUE);
		setPickOnBounds(false);
		//Note that these handlers depend on whether the element is read-only or not
		draggedHandler = event -> {
			if (event.isSecondaryButtonDown()){
				event.consume();
				return;
			}
			event.consume();
			startFullDrag();
			setMouseTransparent(true);
		};
		releasedHandler = event -> setMouseTransparent(false);
		
		
		//This is the handler for when the element is one of the sidebar elements
		sidebarElemHandler = new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event){
				if(event.isSecondaryButtonDown()){event.consume();return;}
				setOnDragDetected(event1 ->{if(event1.isSecondaryButtonDown())return; draggable.startFullDrag();});
			if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
				draggable.setMouseTransparent(true);
				draggable.setLayoutX(event.getSceneX());
				draggable.setLayoutY(event.getSceneY());//Should hopefully add it to the main pane
				draggable.setEffect(shadow);
				draggable.currentCanvas = gui.getViewCanvas(); //I don't like this much
				setEffect(null);
			} else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
				draggable.setLayoutX(event.getSceneX());
				draggable.setLayoutY(event.getSceneY());
			} else if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
				setCursor(Cursor.HAND);
		     	setEffect(shadow);
		     	gui.hideMenu();
			} 
			else if (event.getEventType().equals(MouseEvent.MOUSE_EXITED)){
				setEffect(null);
			}
			else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
				
				draggable.setEffect(null);
			}
		}};

		mainHandler = new EventHandler<MouseEvent>() {
			private double x, y, mouseX, mouseY;
			@Override
			public void handle(MouseEvent event) {
				//If we right click
				if (event.isSecondaryButtonDown()){				
					return;
				}
				// An event for when we press the mouse
				else if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
					requestFocus();
				//	currentCanvas.setIsMouseOver(true);
					Point2D canvasPoint = currentCanvas.sceneToLocal(new Point2D(event.getSceneX(),event.getSceneY()));
					if(getReceiver() != null && (!(getReceiver() instanceof ElementReceiver))){ //This does not apply to Element Receivers
						getReceiver().removeChild(getInstance());

					currentCanvas.addChild(getInstance(), canvasPoint.getX(), canvasPoint.getY());
					
					}
					event.consume();
				//	contextMenu.hide();
					setManaged(false);
					toFront();
					Point2D parentPoint = getParent().sceneToLocal(event.getSceneX(),event.getSceneY());
					x = getLayoutX(); y = getLayoutY();
					mouseX = parentPoint.getX(); mouseY = parentPoint.getY();
				}
				// An event for dragging the element about
				else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
					if(event.isSecondaryButtonDown()){return;};
					setMouseTransparent(true);
					Point2D parentPoint = getParent().sceneToLocal(event.getSceneX(),event.getSceneY());
					double offsetX = parentPoint.getX() - mouseX; double offsetY = parentPoint.getY() - mouseY;
					x += offsetX; y += offsetY;
					setLayoutX(x); setLayoutY(y);
					mouseX = parentPoint.getX(); mouseY = parentPoint.getY();
					
					event.consume();
				} 
				//When the mouse is released
				else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
					if(event.getButton().equals(MouseButton.SECONDARY))return;
					if(gui.isOverTrash(event.getSceneX(), event.getSceneY())){
						currentCanvas.removeChild(getInstance());
						if(getInstance().getReceiver() != null){
							getInstance().getReceiver().removeChild(getInstance()); //Make sure it's totally gotten rid of
						}
					}
					else{
					setPosition((new Point2D(getLayoutX(), getLayoutY())));
					setCursor(Cursor.HAND);
					setManaged(true);
					setMouseTransparent(false);
					}
				} 
				else if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)){
					setCursor(Cursor.HAND);
					gui.hideMenu();
				}}
		};

		
		addEventHandler(MouseEvent.ANY,mainHandler);
		setOnDragDetected(draggedHandler);
		setOnMouseReleased(releasedHandler);


	}


	protected void setData(T model){
		this.setName(model.getname());
		this.setDescription(model.getdescription());
		Point2D position = new Point2D(model.getxPos(),model.getyPos());
		setPosition(position);
		
	}
	
	protected void addListeners(){
		layoutXProperty().addListener(listener->model.setxPos((long)getLayoutX()));
		layoutYProperty().addListener(listener->{model.setyPos((long)getLayoutY());});
		model.settype(getInstance().getClass().getName());
		model.setname(getName());
		model.setdescription(getDescription());
		currentCanvas = gui.getViewCanvas();
	}

	//The element's position is an X,Y coordinate on the Canvas
	public Point2D getPosition() {
		return position;
	}
	
	//This sets a new position of the element
	public void setPosition(Point2D pos) {
		setLayoutX(pos.getX());
		setLayoutY(pos.getY());
		position = pos;
	}


	public static void styleTextCombo(ComboBox<String> combo){
		combo.getStyleClass().addAll("shadowy","styled-select");
		combo.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override public ListCell<String> call(ListView<String> param) {
                final ListCell<String> cell = new ListCell<String>() {
                    {
                       super.getStyleClass().add("rich-blue");
                    }    
                    @Override public void updateItem(String item, 
                        boolean empty) {
                    		super.updateItem(item, empty);
                    		setText(item);
                            getStyleClass().add("mycell");
                        }
            };
            return cell;
        }
		});
	}
}
