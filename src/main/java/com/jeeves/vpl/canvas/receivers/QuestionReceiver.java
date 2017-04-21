package com.jeeves.vpl.canvas.receivers;

import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.survey.questions.QuestionView;

public class QuestionReceiver extends Receiver implements ParentPane{
	private VBox elements;
	private int hoveredIndex = -1; // If we've dragged over a particular child
	private EventHandler<MouseDragEvent> mexited;
	private EventHandler<MouseDragEvent> mentered;
	//	private EventHandler<MouseDragEvent> mreleased;
	private EventHandler<MouseDragEvent> draggedhandler;
	private Pane dummyView;
	VBox container;
	public void addDummyView(Pane dummyView, int index){
		this.dummyView = dummyView;

		container.getChildren().add(dummyView);
		captureRect.setHeight(captureRect.getHeight() + 66);		
		dummyView.setMouseTransparent(true);

	}

	@Override
	public void defineHandlers(){
		super.defineHandlers();
		mreleased = event -> {
			if (isValidElement((ViewElement) event.getGestureSource())){
				QuestionView view = ((QuestionView) event.getGestureSource());
//				if(view.wasAdded() == false)
		//			captureRect.setHeight(captureRect.getHeight() + view.getHeight());
				view.setAddedFlag();
				addChild(view,event.getSceneX(),event.getSceneY());
				event.consume();

			}
		};
	}
	@Override
	public void addChild(ViewElement child, double x, double y) {
		hoveredIndex = 0;
		System.out.println("UMM");
		QuestionView newChild = (QuestionView) child;
		elements.getChildren().remove(child);
		childList.remove(child);
		Point2D point = elements.sceneToLocal(x, y);
		for (int i = 0; i < elements.getChildren().size()-1; i++) {
			Pane elem = ((Pane) elements.getChildren().get(i));
			double max = (elem.getLayoutY() + elem.getHeight());
			if (point.getY() < max) {
			//	hoveredIndex = i+1;
				break;
			}
			hoveredIndex = i+1;
		}
		getChildren().remove(child);
//		System.out.println("childlist size is " + childList.size());
//		System.out.println("hovered index is " + hoveredIndex);
//		System.out.println("actual children are " + elements.getChildren().size());
//		
		System.out.println("hovered index is " + hoveredIndex);
		if (hoveredIndex > -1) {
			childList.add(hoveredIndex, newChild);
			elements.getChildren().add(hoveredIndex,newChild);
		} 
		else{
			childList.add(newChild);
			elements.getChildren().add(newChild);
		}
		newChild.addButtons();
		newChild.parentPane = this;
		newChild.setManaged(true); // So it sits in the appropriate place
		newChild.setMouseTransparent(false);
				EventHandler<MouseEvent> removeHandler = new EventHandler<MouseEvent>() {
					public void handle(MouseEvent e) {
						e.consume();
						if (e.isSecondaryButtonDown()) {
							return;
						}
						System.out.println("AND REMOVED IT");
						newChild.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
						removeChild(newChild);
					}
				};
				System.out.println("I ADDED THIS AGAIN");
				newChild.setOnMousePressed(removeHandler);

		child.setOnMouseDragReleased(event -> {
			event.consume();
		//	captureRect.setHeight(captureRect.getHeight() + child.getHeight());
			captureRect.fireEvent(event);
		});
	}

	@Override
	public void removeChild(ViewElement child) {
		
		System.out.println("capture rect height REMOVED is now " + captureRect.getHeight());

		child.setOnMouseDragExited(null);
		child.setOnMouseDragReleased(null); 
		child.setOnMouseDragEntered(null);
		child.setManaged(false);
		elements.getChildren().remove(child);
		childList.remove(child);
	}

	@Override
	public boolean isValidElement(ViewElement element) {
		if (element.getType() == ElementType.QUESTION)
			return true;
		return false;
	}
	public QuestionReceiver(double width, double height){
	container = new VBox();
	elements = new VBox();

	container.getChildren().add(elements);
	getChildren().add(container);

	captureRect.setHeight(height);
	captureRect.setWidth(width);
	captureRect.setOpacity(0.3);
	elements.setPrefWidth(width);
	elements.setFillWidth(false);
	elements.setAlignment(Pos.TOP_CENTER);
	elements.getChildren().addListener(new ListChangeListener<Node>(){

		@Override
		public void onChanged(
				javafx.collections.ListChangeListener.Change<? extends Node> arg0) {
			arg0.next();
			if(arg0.wasAdded()){
				captureRect.setHeight(captureRect.getHeight() + 66);	
				System.out.println("GROWIN");
			}
			else{
				captureRect.setHeight(captureRect.getHeight() - 66);
				System.out.println("SHRINKING");

			}
		}
		
	});
	setPickOnBounds(false);
	container.setPickOnBounds(false);
	elements.setPickOnBounds(false);
	}
	}
 