package com.jeeves.vpl.firebase;

import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Daniel on 12/07/16.
 */
@SuppressWarnings("serial")
@IgnoreExtraProperties
public class FirebaseUI extends FirebaseElement implements Serializable {
	private String text;
	@Exclude
	private StringProperty textProperty = new SimpleStringProperty();

	@Exclude
	public StringProperty getMyTextProperty() {
		return textProperty;
	}

	public FirebaseUI() {}
	public FirebaseUI(String name) {
		this.name.setValue(name);
	}
	public String gettext() {
		return text;
	}

	public void settext(String text) {
		this.text = text;
		textProperty.set(text);
	}

}
