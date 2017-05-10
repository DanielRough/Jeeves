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

public abstract class ExternalReceiver extends Receiver implements ParentPane{

	protected VBox elements;
	protected int hoveredIndex = -1; // If we've dragged over a particular child
	protected VBox container;
	public void addDummyView(Pane dummyView, int index){
		container.getChildren().add(dummyView);
		captureRect.setHeight(captureRect.getHeight() + 66);		
		dummyView.setMouseTransparent(true);

	}

	public ExternalReceiver(){
		elements = new VBox();
		container = new VBox();
		addChildListeners();
	}
	@Override
	public void defineHandlers(){
		super.defineHandlers();
		mreleased = event -> {
			if (isValidElement((ViewElement) event.getGestureSource())){
			//	QuestionView view = ((QuestionView) event.getGestureSource());
			//	view.setAddedFlag();
				addChild((ViewElement) event.getGestureSource(),event.getSceneX(),event.getSceneY());
				event.consume();

			}
		};

	}

	//Another method for when we KNOW the index it has to go to
	public void addChildAtIndex(ViewElement child, int index){
		if (index > -1) {
			childList.add(index, child);
			elements.getChildren().add(index,child);
		} 
		else{
			childList.add(child);
			elements.getChildren().add(child);
		}

		//	child.parentPane = this;
		child.setManaged(true); // So it sits in the appropriate place
		child.setMouseTransparent(false);
		EventHandler<MouseEvent> removeHandler = new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				e.consume();
				if (e.isSecondaryButtonDown()) {
					return;
				}
				child.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
				child.setOnMouseReleased(handler->{addChildAtIndex(child,elements.getChildren().indexOf(child));}); //Hopefully readds it automatically if it gets dragged out and released?

				removeChild(child);
			}
		};
		child.setOnMousePressed(removeHandler);
		child.setOnMouseDragReleased(event -> {
			event.consume();
			captureRect.fireEvent(event);
		});
	}
	@Override
	public void addChild(ViewElement child, double x, double y) {
		hoveredIndex = 0;
		Point2D point = elements.sceneToLocal(x, y);
		for (int i = 0; i < elements.getChildren().size(); i++) {
			Pane elem = ((Pane) elements.getChildren().get(i));
			double max = (elem.getLayoutY() + elem.getHeight());
			if (point.getY() < max) {
				break;
			}
			hoveredIndex = i+1;
		}
		addChildAtIndex(child, hoveredIndex);

	}

	@Override
	public void removeChild(ViewElement child) {
		child.setOnMouseDragExited(null);
		child.setOnMouseDragReleased(null); 
		child.setOnMouseDragEntered(null);
		child.setManaged(false);
		elements.getChildren().remove(child);
		childList.remove(child);
	}

	@Override
	public abstract boolean isValidElement(ViewElement element);
	
	public abstract void addChildListeners();

}
