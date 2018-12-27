package com.jeeves.vpl.canvas.receivers;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.uielements.UIElement;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

@SuppressWarnings("rawtypes")
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
	public void addChildAtIndex(ViewElement child, int index) {
		super.addChildAtIndex(child, index);
		Button editButton = new Button();
		editButton.setLayoutX(child.getBoundsInLocal().getMaxX()+10);
		child.setOnMouseEntered(handler->{child.getStyleClass().add("drop_shadow");
		});		
		
		child.setOnMouseExited(handler->{child.getStyleClass().remove("drop_shadow");
		});
		child.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent event) {
				if(event.isSecondaryButtonDown()){
					event.consume();
					((UIElement)child).update(childList);
				}
			}
			
		});

	}

	@Override
	public void addChildListeners() {
		ChangeListener<Number> updateListener1 = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				
				elements.setAlignment(Pos.TOP_CENTER);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						newElement = null;
						for (Pane childy : childList) {
							UIElement elem = (UIElement) childy;
							if (elem.getPreviouslyAdded() == false) {
								elem.setPreviouslyAdded(true);
								newElement = elem;
								break;
							}
						}
						if (newElement != null)
							newElement.update(childList); // Ask to edit text if we
													// haven't
					}
				});

			}
		};
		elements.heightProperty().addListener(updateListener1);
		
	}

	@Override
	public boolean isValidElement(ViewElement element) {
		if (element.getType() == ElementType.UIELEMENT)
			return true;
		return false;
	}

}
