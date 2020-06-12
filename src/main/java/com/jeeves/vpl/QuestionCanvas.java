package com.jeeves.vpl;

import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.Pane;

import com.jeeves.vpl.canvas.receivers.ExternalReceiver;
import com.jeeves.vpl.canvas.uielements.UIElement;
import com.jeeves.vpl.survey.questions.Question;

public class QuestionCanvas extends ViewCanvas{

	public QuestionCanvas(double width, double height) {
//		addChildListeners();
//		container.getChildren().add(elements);
//		getChildren().add(container);
//
//		captureRect.setHeight(height);
//		captureRect.setWidth(width);
//		captureRect.setOpacity(0.3);
//		elements.setPrefWidth(width);
//		elements.setFillWidth(false);
//		elements.setAlignment(Pos.TOP_CENTER);
//
//		setPickOnBounds(false);
//		container.setPickOnBounds(false);
//		elements.setPickOnBounds(false);
	}

	@Override
	public void addEventHandlers() {
		super.addEventHandlers();
	}
	@Override
	protected void makeDragHandler() {
		 dragHandler = event -> {
			if (event.getEventType().equals(MouseDragEvent.MOUSE_DRAG_RELEASED)
					&& event.getGestureSource() instanceof ViewElement) {
					ViewElement dragged = (ViewElement) event.getGestureSource();
						addChild(dragged, event.getSceneX(), event.getSceneY());
					}
				
		};
	}
	@Override
	public void addChild(ViewElement child, double mouseX, double mouseY) {
		super.addChild(child, mouseX, mouseY);
	//	child.setParentPane(this);
	}
//	void addChildrenListener() {
//		getChildren().addListener((javafx.collections.ListChangeListener.Change<? extends Node> arg0)->{
//			while (arg0.next()) {
//				if (arg0.wasAdded()) {
//					List<?> addedlist = arg0.getAddedSubList();
//					Constants.getOpenProject().add((ViewElement<?>) addedlist.get(0),0);
//				} else if (arg0.wasRemoved()) {
//					List<?> removedlist = arg0.getRemoved();
//					Constants.getOpenProject().remove((ViewElement<?>) removedlist.get(0));
//
//				}
//			}
//		});
//	}
//	@Override
//	public void addChildAtIndex(ViewElement<?> child, int index) {
//
//		QuestionView addedChild = (QuestionView) child;
//		QuestionView parentQuestion = addedChild.getParentQuestion();
//		if (parentQuestion != null) {
//			int parentIndex = elements.getChildren().indexOf(parentQuestion);
//			if (index <= parentIndex) {
//				index = addedChild.getOldIndex(); // Reset it to add it back to its
//												// old place
//				Alert alert = new Alert(AlertType.INFORMATION);
//				alert.setTitle("Parent/Child Question Conflict");
//				alert.setHeaderText(null);
//				alert.setContentText("You can't have a parent question come after its child questions.");
//				alert.showAndWait();
//			}
//		}
//
//		List<QuestionView> childQuestions = addedChild.getChildQuestions();
//		for (QuestionView parentchild : childQuestions) {
//			int childIndex = elements.getChildren().indexOf(parentchild);
//			if (childIndex < index || index == -1) {
//				index = addedChild.getOldIndex(); // Can't have children coming
//												// before it!
//				Alert alert = new Alert(AlertType.INFORMATION);
//				alert.setTitle("Parent/Child Question Conflict");
//				alert.setHeaderText(null);
//				alert.setContentText("Can't have a parent question come after its child questions!");
//				alert.showAndWait();
//
//			}
//		}
//
//		super.addChildAtIndex(child, index);
//		addedChild.setOldIndex(index);
//		((QuestionView) child).addButtons();
//	}

//	@Override
//	public void addChildListeners() {
//		elements.getChildren().addListener((ListChangeListener.Change<? extends Node> arg0) ->{
//				arg0.next();
//				if (arg0.wasAdded())
//					captureRect.setHeight(captureRect.getHeight() + 55);
//				else
//					captureRect.setHeight(captureRect.getHeight() - 55);
//
//		});
//	}

//	public void addDummyView(Pane dummyView) {
//		container.getChildren().add(dummyView);
//		captureRect.setHeight(captureRect.getHeight() + 55);
//		dummyView.setMouseTransparent(true);
//	}

//	@Override
//	public boolean isValidElement(ViewElement<?> element) {
//		return (element.getType() == ElementType.QUESTION);
//	}

}
