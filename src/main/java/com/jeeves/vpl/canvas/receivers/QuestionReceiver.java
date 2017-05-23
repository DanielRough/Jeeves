package com.jeeves.vpl.canvas.receivers;

import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.survey.questions.QuestionView;

public class QuestionReceiver extends ExternalReceiver implements ParentPane {

	public QuestionReceiver(double width, double height) {
		container.getChildren().add(elements);
		getChildren().add(container);

		captureRect.setHeight(height);
		captureRect.setWidth(width);
		captureRect.setOpacity(0.3);
		elements.setPrefWidth(width);
		elements.setFillWidth(false);
		elements.setAlignment(Pos.TOP_CENTER);

		setPickOnBounds(false);
		container.setPickOnBounds(false);
		elements.setPickOnBounds(false);
	}

	@Override
	public void addChildAtIndex(ViewElement child, int index) {

		QuestionView addedChild = (QuestionView) child;
		QuestionView parentQuestion = addedChild.getParentQuestion();
		if (parentQuestion != null) {
			int parentIndex = elements.getChildren().indexOf(parentQuestion);
			if (index <= parentIndex) {
				index = addedChild.oldIndex; // Reset it to add it back to its
												// old place
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Parent/Child Question Conflict");
				alert.setHeaderText(null);
				alert.setContentText("You can't have a parent question come after its child questions.");
				alert.showAndWait();
			}
		}

		List<QuestionView> childQuestions = addedChild.getChildQuestions();
		for (QuestionView parentchild : childQuestions) {
			int childIndex = elements.getChildren().indexOf(parentchild);
			if (childIndex < index || index == -1) {
				index = addedChild.oldIndex; // Can't have children coming
												// before it!
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Parent/Child Question Conflict");
				alert.setHeaderText(null);
				alert.setContentText("Can't have a parent question come after its child questions!");
				alert.showAndWait();

			}
		}

		super.addChildAtIndex(child, index);
		addedChild.setOldIndex(index);
		((QuestionView) child).addButtons();
	}
//
//	@Override
//	public void defineHandlers() {
//		super.defineHandlers();
////
////		EventHandler<MouseEvent> qreleasedHandler = event -> {
////			event.consume();
////			ViewElement question = (ViewElement)event.getSource();
////			if(question.getWasDragged()){
////				return;
////			}
////			System.out.println("NAY TWAS NOT DRAGGED");
////			addChildAtIndex(question,question.oldIndex);
////		};
////		addEventFilter(MouseEvent.MOUSE_RELEASED,qreleasedHandler);
//	}
	@Override
	public void addChildListeners() {
		elements.getChildren().addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> arg0) {
				arg0.next();
				if (arg0.wasAdded())
					captureRect.setHeight(captureRect.getHeight() + 66);
				else
					captureRect.setHeight(captureRect.getHeight() - 66);

			}

		});
	}

	public void addDummyView(Pane dummyView, int index) {
		container.getChildren().add(dummyView);
		captureRect.setHeight(captureRect.getHeight() + 66);
		dummyView.setMouseTransparent(true);
	}

	@Override
	public boolean isValidElement(ViewElement element) {
		if (element.getType() == ElementType.QUESTION)
			return true;
		return false;
	}

}
