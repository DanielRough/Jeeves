package com.jeeves.vpl;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class DateTimeCell extends TableCell<ScheduleItem,String>{ 
    private TextField textField;
    DateFormat df;
    @Override
    public void startEdit(){
        if (!isEmpty()) {
            super.startEdit();
            createTextField();
            setText(null);
            setGraphic(textField);
            textField.selectAll();
            textField.requestFocus();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem());
        setGraphic(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
		df = new SimpleDateFormat("dd/MM/yy HH:mm");
		df.setTimeZone(TimeZone.getDefault());
		df.setLenient(false);
        try {
        	if(item != null)
        		df.parse(item);
        	this.setTextFill(Color.BLACK);
        } catch (ParseException e) {
        	this.setTextFill(Color.RED);
        }
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(item);
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(item);
                setGraphic(null);
            }
        }
    }

    private void createTextField() {
        textField = new TextField(this.getText());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
        textField.focusedProperty().addListener(
            (ObservableValue<? extends Boolean> arg0, 
            Boolean arg1, Boolean arg2) -> {
                if (!arg2) {
                    commitEdit(textField.getText());
                }
        });
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)) {
                	commitEdit(textField.getText());
                    setText(getItem());
                    setGraphic(null);
                }
            }
        });

    }
}