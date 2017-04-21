package com.jeeves.vpl;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.firebase.FirebaseElement;
import com.jeeves.vpl.firebase.FirebaseVariable;
/**
 * The main superclass that governs all the draggable elements in the visual
 * language
 * @author Daniel
 *
 */
@SuppressWarnings("rawtypes")

public abstract class ViewElement<T extends FirebaseElement> extends Pane{
	private ViewElement draggable; //Self-referencing class, hm...
	protected T model;
    public boolean isReadOnly = false;
    protected Main gui;
    protected EventHandler<MouseEvent> draggedHandler;
    protected EventHandler<MouseEvent> mainHandler;
    protected EventHandler<MouseEvent> sidebarElemHandler;
    protected EventHandler<MouseEvent> releasedHandler;
   // protected ViewCanvas currentCanvas; 
	private double initialHeight;
	protected ActionHolder parent;
	protected ElementType type;
	protected Point2D position = new Point2D(0, 0);
	protected String name;
	protected String description;
	public ParentPane parentPane;
	//public StringProperty name = new SimpleStringProperty();
//	public String description;
//	public String getName(){
//		return name.get();
//	}

	public abstract ViewElement<T> getInstance();
	public abstract void fxmlInit();
	public abstract Node[] getWidgets();
	public String getName(){
		return name;
	}
	public String getDescription(){
		return description;
	}
     public void setReadOnly(){
    	 isReadOnly = true;
    //	 removeEventHandler(MouseEvent.ANY,mainHandler);
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

	//Create a new ViewElement from just the class name
	public static ViewElement create(String name){
		try{
			return (ViewElement) Class.forName(name).getConstructor().newInstance();
		}
		catch(Exception e){
			e.printStackTrace();//better than null
		}
		return null;
		}

	public void setDraggable(ViewElement draggable){
		this.draggable = draggable;
	}

	public ViewElement getDraggable(){
		return draggable;
	}
	



	public ViewElement(Class<T> typeParameterClass){
		this.gui = Main.getContext();
	//	currentCanvas = gui.getViewCanvas();
		fxmlInit();
		try {
			//	 if(data.getname() == null)
				this.model = typeParameterClass.newInstance();
			} catch (Exception e){
				e.printStackTrace();
			}		//if(data.getname() != null) //Data is null if it's a new element{
		//setData(data);
		addListeners();	
		initEventHandlers();
	}
	//Nice new convenience methods
	public void addAllHandlers(){
		addEventHandler(MouseEvent.ANY,mainHandler);
		addEventHandler(MouseDragEvent.DRAG_DETECTED,draggedHandler);
		addEventHandler(MouseEvent.MOUSE_RELEASED,releasedHandler);
	}
	public void removeAllHandlers(){
		removeEventHandler(MouseEvent.ANY,mainHandler);
		removeEventHandler(MouseDragEvent.DRAG_DETECTED,draggedHandler);
		removeEventHandler(MouseEvent.MOUSE_RELEASED,releasedHandler);
	}
	//For whatever reason we want the element to stop responding in the normal way
	public void removeHander(){
		this.removeEventHandler(MouseEvent.ANY, mainHandler);
	}
//	public String getDescription(){
//		return description;
//	}
//	public void setName(String name){
//		this.name.setValue(name);
//	}
//	public void setDescription(String description){
//		this.description = description;
//	}

//	public double getInitHeight(){
//		return initialHeight;
//	}
//	public void setInitHeight(double height){
//		this.initialHeight = height;
//	}
//	
	public void setActionHolder(ActionHolder holder){
		this.parent = holder;
	}
	public ActionHolder getActionHolder(){
		return parent;
	}
//	public void setReceiver(IReceiver receiver){
//		this.receiver = receiver;
//	}
//	public IReceiver getReceiver(){
//		return receiver;
//	}

	public ElementType getType(){
		return type;
	}
	//DJRNEW
	public void setHandler(EventHandler<MouseEvent> handler){
		this.addEventHandler(MouseEvent.ANY, handler);
	}
	
	public double x;
	public double y;
	public double mouseX;
	public double mouseY;

	public void initEventHandlers(){
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
				draggable.getStyleClass().add("drop_shadow");
			//	draggable.currentCanvas = gui.getViewCanvas(); //I don't like this much
				setEffect(null);
			} else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
				draggable.setLayoutX(event.getSceneX());
				draggable.setLayoutY(event.getSceneY());
			} else if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
				setCursor(Cursor.HAND);
		     	getStyleClass().add("drop_shadow");
		     	gui.hideMenu();
			} 
			else if (event.getEventType().equals(MouseEvent.MOUSE_EXITED)){
				getStyleClass().remove("drop_shadow");			}
			else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
				
				draggable.getStyleClass().remove("drop_shadow");
				draggable.addAllHandlers();
			}
		}};

		mainHandler = new EventHandler<MouseEvent>() {
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
				//	Point2D canvasPoint = parentPane.sceneToLocal(new Point2D(event.getSceneX(),event.getSceneY()));
//DJR Receiver should be able to handle this itself
					//					if(getReceiver() != null && (!(getReceiver() instanceof ElementReceiver))){ //This does not apply to Element Receivers
//						getReceiver().removeChild(getInstance());

					System.out.println("Adding child at " + event.getSceneX() + "," + event.getSceneY());
					parentPane.addChild(getInstance(), event.getSceneX(), event.getSceneY());
					
				//	}
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
						parentPane.removeChild(getInstance());
						//DJR Receiver should be able to handle this itself

//						if(getInstance().getReceiver() != null){
//							getInstance().getReceiver().removeChild(getInstance()); //Make sure it's totally gotten rid of
//						}
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
		addEventHandler(MouseDragEvent.DRAG_DETECTED,draggedHandler);
		addEventHandler(MouseEvent.MOUSE_RELEASED,releasedHandler);	}
	
	public ViewElement(T data,Class<T> typeParameterClass) {
		this.gui = Main.getContext();
	//	this.model= data;
		fxmlInit();
		//if(data.getname() != null) //Data is null if it's a new element{
		setData(data);
		addListeners();
		initEventHandlers();
	//	this.typeParameterClass = typeParameterClass;
		 try {
		//	 if(data.getname() == null)
			this.model = typeParameterClass.newInstance();
		} catch (Exception e){
			e.printStackTrace();
		}
//		 DropShadow shadow = new DropShadow();
//		 shadow.setWidth(25);
//		 shadow.setHeight(25);
//		 shadow.setRadius(15);
//		 shadow.setSpread(0.8);
//		 shadow.setColor(Color.LIGHTBLUE);
		setPickOnBounds(false);
		//Note that these handlers depend on whether the element is read-only or not


	}


	protected void setData(T model){
		this.model = model;
	//	this.setName(model.getname());
	//	this.setDescription(model.getdescription());
		Point2D position = new Point2D(model.getxPos(),model.getyPos());
		setPosition(position);
		
	}
	
	protected void addListeners(){
		layoutXProperty().addListener(listener->model.setxPos((long)getLayoutX()));
		layoutYProperty().addListener(listener->{model.setyPos((long)getLayoutY());});
//		model.settype(getInstance().getClass().getName());
//		model.setname(getName());
//		model.setdescription(getDescription());
//		currentCanvas = gui.getViewCanvas();
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

}
