package com.jeeves.vpl;

public interface ParentPane {

	public void addChild(ViewElement child, double mouseX, double mouseY);
	public void removeChild(ViewElement child);
}
