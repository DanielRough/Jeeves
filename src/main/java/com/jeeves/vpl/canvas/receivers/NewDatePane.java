package com.jeeves.vpl.canvas.receivers;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.jeeves.vpl.CalendarEveryday;
import com.jeeves.vpl.CalendarFromTo;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class NewDatePane extends Pane{
	private ToggleGroup group;
	@FXML public RadioButton btnEveryday;
	@FXML private RadioButton btnRange;
	@FXML private DatePicker pckFrom;
	@FXML private DatePicker pckTo;
	
	@FXML private Label lblError;
	
	
	private Stage stage;
	private Pane paneDate;
	private CalendarFromTo fromtoDatePane;
	private CalendarEveryday everydayPane;
	private long dateFrom;
	private long dateTo;
	
	public DatePicker getPckFrom(){
		return pckFrom;
	}
	public DatePicker getPckTo(){
		return pckTo;
	}
	public ToggleGroup getToggleGroup(){
		return group;
	}
	public void setParams(Stage stage, Pane paneDate, long dateFrom, long dateTo){
		this.stage = stage;
		this.paneDate = paneDate;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		setup();
	}
	public NewDatePane(){
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/datepopup.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);	
			group = new ToggleGroup();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public void setup(){
		btnEveryday.setToggleGroup(group);
		btnRange.setToggleGroup(group);
		fromtoDatePane = new CalendarFromTo();
		everydayPane = new CalendarEveryday();
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){

			@Override
			public void changed(ObservableValue<? extends Toggle> arg0,
					Toggle arg1, Toggle arg2) {
				if(arg2.equals(btnEveryday)){
					pckFrom.setDisable(true);
					pckTo.setDisable(true);
					paneDate.getChildren().clear();
					paneDate.getChildren().add(everydayPane); //Every day
				}
				else{
					paneDate.getChildren().clear();
					paneDate.getChildren().add(fromtoDatePane);
					pckFrom.setDisable(false);
					pckTo.setDisable(false);
				}
			}
			
		});
		if(dateFrom != 0){
			group.selectToggle(btnRange);
			pckFrom.setValue(LocalDate.ofEpochDay(dateFrom));
			pckTo.setValue(LocalDate.ofEpochDay(dateTo));
		}
		else
			group.selectToggle(btnEveryday);
		
	}
	@FXML
	public void closePane(Event e){
		if(btnRange.isSelected()){
			if(pckFrom.getValue() == null || pckTo.getValue() == null){
				lblError.setVisible(true);
				return;
			}
			LocalDate from = pckFrom.getValue();
			LocalDate to = pckTo.getValue();
			  String fromstr = from.format(DateTimeFormatter.ofPattern("MMM dd"));
			  String tostr = to.format(DateTimeFormatter.ofPattern("MMM dd"));
			  fromtoDatePane.setCalDates(from, to);

		}
		stage.hide();
	}
}
