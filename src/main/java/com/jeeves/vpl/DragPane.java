package com.jeeves.vpl;

import javafx.scene.layout.Pane;

public class DragPane extends Pane {//implements ParentPane {

	public DragPane() {}
	
	public DragPane(double x, double y) {
		this.setPrefSize(x, y);
		this.setMouseTransparent(true);
		this.setPickOnBounds(false);
	}

	public void addChild(ViewElement child, double mouseX, double mouseY) {
		getChildren().add(child);
		child.setLayoutX(mouseX);
		child.setLayoutY(mouseY);
	}

	public void removeChild(ViewElement child) {
		getChildren().remove(child);
		requestLayout();
	}

}
