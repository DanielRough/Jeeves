package com.jeeves.vpl.canvas.receivers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.uielements.UIElement;

@SuppressWarnings("rawtypes")
public class ElementReceiver extends ExternalReceiver implements ParentPane {

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

	@Override
	public void addChildAtIndex(ViewElement child, int index) {
		super.addChildAtIndex(child, index);

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
							if (elem.previouslyAdded == false) {
								elem.previouslyAdded = true;
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
