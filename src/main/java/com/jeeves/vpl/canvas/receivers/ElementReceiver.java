package com.jeeves.vpl.canvas.receivers;

import com.jeeves.vpl.Constants.ElementType;

import java.util.List;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.uielements.UIElement;
import com.jeeves.vpl.firebase.FirebaseUI;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class ElementReceiver extends ExternalReceiver{

	UIElement newElement;

	public ElementReceiver(double width, double height) {

		elements.setPadding(new Insets(15, 0, 0, 0));
		getChildren().add(elements);
		captureRect.setHeight(height);
		captureRect.setWidth(width);
		captureRect.setOpacity(0.0);
		elements.setPrefWidth(width);
		elements.setFillWidth(false);
		elements.setSpacing(10);

		setPickOnBounds(false);
		elements.setPickOnBounds(false);

	}

	boolean elementdragged = false;

	@Override
	public void addChildAtIndex(ViewElement<?> child, int index) {
		super.addChildAtIndex(child, index);
		Button editButton = new Button();
		editButton.setLayoutX(child.getBoundsInLocal().getMaxX()+10);
		child.setOnMouseEntered(handler->child.getStyleClass().add("drop_shadow"));		
		
		child.setOnMouseExited(handler->child.getStyleClass().remove("drop_shadow"));
		child.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
				if(event.isSecondaryButtonDown()){
					event.consume();
					((UIElement)child).update(childList);
				}
			
		});

	}

	@Override
	public void addChildListeners() {
		getChildElements().addListener((javafx.collections.ListChangeListener.Change<? extends Node> arg0)->{
			arg0.next();
			if (arg0.wasAdded()) {
				ViewElement<?> added = (ViewElement<?>) arg0.getAddedSubList().get(0);
				FirebaseUI uiModel = (FirebaseUI) added.getModel();
				if(uiModel.gettext()!=null){
					Constants.getOpenProject().add(added);
				}
				else
					//We wait until we've set the text before we actually add it
					uiModel.getMyTextProperty().addListener(listener ->
						Constants.getOpenProject().add(added)
					);
			} else {
				List<ViewElement<?>> removed = (List<ViewElement<?>>) arg0.getRemoved();
				removed.forEach(elem ->
					Constants.getOpenProject().remove(elem)
					);
			}
		}
	);
		addHeightListener();
		
	}

	private void addHeightListener() {
		ChangeListener<Number> updateListener1 = (o, v0, v1)->{
			
			elements.setAlignment(Pos.TOP_CENTER);
			Platform.runLater(()-> {
					newElement = null;
					for (Pane childy : childList) {
						UIElement elem = (UIElement) childy;
						if (!elem.getPreviouslyAdded()) {
							elem.setPreviouslyAdded(true);
							newElement = elem;
							break;
						}
					}
					if (newElement != null)
						newElement.update(childList); // Ask to edit text if we
												// haven't
			});
	};
	elements.heightProperty().addListener(updateListener1);
	}
	@Override
	public boolean isValidElement(ViewElement<?> element) {
		return (element.getType() == ElementType.UIELEMENT);
	}

}
