package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.DATE_FROM;
import static com.jeeves.vpl.Constants.DATE_TO;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public abstract class ClockTrigger extends Trigger{ // NO_UCD (use default)

	protected Stage dateStage;
//	protected long dateFrom;
//	protected long dateTo;
	protected Pane datePane;
	//protected NewDatePane newDatePane;
//	public ClockTrigger(FirebaseTrigger data) {
//		super(data);
//	}

	public void setDateFrom(long dateFrom){
		params.put(DATE_FROM, dateFrom);
	//	this.dateFrom = dateFrom;
		
	}
	public void setDateTo(long dateTo){
		params.put(DATE_TO, dateTo);
	//	this.dateTo = dateTo;
	}
	
	public void addListeners(){
		super.addListeners();
//			dateStage = new Stage(StageStyle.UNDECORATED);
//			//NewDatePane newDatePane = new NewDatePane();
//			//Scene scene = new Scene(newDatePane);
//			//dateStage.setScene(scene);
//			dateStage.setTitle("Edit dates");
//			dateStage.initModality(Modality.APPLICATION_MODAL);
//		dateStage = gui.getDateStage(); //Ugly, will change also
//
//		newDatePane = gui.getDatePane(); //Ugly, will change
//		newDatePane.getPckFrom().valueProperty().addListener(new ChangeListener<LocalDate>(){
//
//			@Override
//			public void changed(ObservableValue<? extends LocalDate> arg0,
//					LocalDate arg1, LocalDate arg2) {
//				setDateFrom(arg2.toEpochDay());
//			}
//			
//		});
//		newDatePane.getPckTo().valueProperty().addListener(new ChangeListener<LocalDate>(){
//
//			@Override
//			public void changed(ObservableValue<? extends LocalDate> arg0,
//					LocalDate arg1, LocalDate arg2) {
//				setDateTo(arg2.toEpochDay());
//			}
//			
//		});
//		newDatePane.getToggleGroup().selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
//
//			@Override
//			public void changed(ObservableValue<? extends Toggle> arg0,
//					Toggle arg1, Toggle arg2) {
//				if(arg2.equals(newDatePane.btnEveryday)){
//					setDateFrom(0);
//					setDateTo(0);
//				}
//				else{
//					
//				}
//			}
//			
//		});

	}
	public class CalendarEveryday extends Pane{

		@FXML private ImageView imgCalendar;
		
		public CalendarEveryday(){
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setController(this);
			URL location = this.getClass().getResource("/calevery.fxml");
			fxmlLoader.setLocation(location);
			try {
				Node root = (Node) fxmlLoader.load();
				getChildren().add(root);	
				imgCalendar.setImage(new Image("/img/icons/calenda.png"));

				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
//	public class CalendarFromTo extends Pane{
//		@FXML private Label lblFrom;
//		@FXML private Label lblTo;
//		@FXML private ImageView imgCalFrom;
//		@FXML private ImageView imgCalTo;
//		
//		public CalendarFromTo(){
//			FXMLLoader fxmlLoader = new FXMLLoader();
//			fxmlLoader.setController(this);
//			URL location = this.getClass().getResource("/calfromto.fxml");
//			fxmlLoader.setLocation(location);
//			try {
//				Node root = (Node) fxmlLoader.load();
//				getChildren().add(root);	
//				imgCalFrom.setImage(new Image("/img/icons/calenda.png"));
//				imgCalTo.setImage(new Image("/img/icons/calenda.png"));
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//		}
//		public void setCalDates(LocalDate from, LocalDate to){
//			 String fromstr = from.format(DateTimeFormatter.ofPattern("MMM dd"));
//			  String tostr = to.format(DateTimeFormatter.ofPattern("MMM dd"));
//			lblFrom.setText(fromstr);
//			lblTo.setText(tostr);
//		}
//	}
//	public class NewDatePane extends Pane{
//		private ToggleGroup group;
//		@FXML public RadioButton btnEveryday;
//		@FXML private RadioButton btnRange;
//		@FXML private DatePicker pckFrom;
//		@FXML private DatePicker pckTo;
//		
//		@FXML private Label lblError;
//		
//		
//		private Stage stage;
////		private Pane paneDate;
////		private CalendarFromTo fromtoDatePane;
////		private CalendarEveryday everydayPane;
//		private long dateFrom;
//		private long dateTo;
//		private ClockTrigger trigger;
//		
//		public DatePicker getPckFrom(){
//			return pckFrom;
//		}
//		public DatePicker getPckTo(){
//			return pckTo;
//		}
//		public ToggleGroup getToggleGroup(){
//			return group;
//		}
//		public void setParams(Stage stage,ClockTrigger trigger, long dateFrom, long dateTo){
//			this.stage = stage;
//			this.dateFrom = dateFrom;
//			this.dateTo = dateTo;
//			this.trigger = trigger;
//			setup();
//		}
//		public NewDatePane(){
//			FXMLLoader fxmlLoader = new FXMLLoader();
//			fxmlLoader.setController(this);
//			URL location = this.getClass().getResource("/CalendarDatePopup.fxml");
//			fxmlLoader.setLocation(location);
//			try {
//				Node root = (Node) fxmlLoader.load();
//				getChildren().add(root);	
//				group = new ToggleGroup();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//		}
//		
//		public void setup(){
//			btnEveryday.setToggleGroup(group);
//			btnRange.setToggleGroup(group);
////			fromtoDatePane = new CalendarFromTo();
////			everydayPane = new CalendarEveryday();
//			pckFrom.getEditor().setDisable(true);
//			pckTo.getEditor().setDisable(true);
//			group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
//
//				@Override
//				public void changed(ObservableValue<? extends Toggle> arg0,
//						Toggle arg1, Toggle arg2) {
//					if(arg2.equals(btnEveryday)){
//						pckFrom.setDisable(true);
//						pckTo.setDisable(true);
////						paneDate.getChildren().clear();
////						paneDate.getChildren().add(everydayPane); //Every day
//					}
//					else{
////						paneDate.getChildren().clear();
////						paneDate.getChildren().add(fromtoDatePane);
//						pckFrom.setDisable(false);
//						pckTo.setDisable(false);
//					}
//				}
//				
//			});
//			if(dateFrom != 0){
//				group.selectToggle(btnRange);
//				pckFrom.setValue(LocalDate.ofEpochDay(dateFrom));
//				pckTo.setValue(LocalDate.ofEpochDay(dateTo));
//			}
//			else
//				group.selectToggle(btnEveryday);
//			getPckFrom().valueProperty().addListener(new ChangeListener<LocalDate>(){
//				
//							@Override
//							public void changed(ObservableValue<? extends LocalDate> arg0,
//									LocalDate arg1, LocalDate arg2) {
//								trigger.setDateFrom(arg2.toEpochDay());
//							}
//							
//						});
//						getPckTo().valueProperty().addListener(new ChangeListener<LocalDate>(){
//				
//							@Override
//							public void changed(ObservableValue<? extends LocalDate> arg0,
//									LocalDate arg1, LocalDate arg2) {
//								trigger.setDateTo(arg2.toEpochDay());
//							}
//							
//						});
//						getToggleGroup().selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
//				
//							@Override
//							public void changed(ObservableValue<? extends Toggle> arg0,
//									Toggle arg1, Toggle arg2) {
//								if(arg2.equals(btnEveryday)){
//									trigger.setDateFrom(0);
//									trigger.setDateTo(0);
//								}
//								else{
//									
//								}
//							}
//							
//						});
//			
//		}
//		@FXML
//		public void closePane(Event e){
//			if(btnRange.isSelected()){
//				if(pckFrom.getValue() == null || pckTo.getValue() == null){
//					lblError.setVisible(true);
//					return;
//				}
//			//	LocalDate from = pckFrom.getValue();
//			//	LocalDate to = pckTo.getValue();
////				  String fromstr = from.format(DateTimeFormatter.ofPattern("MMM dd"));
////				  String tostr = to.format(DateTimeFormatter.ofPattern("MMM dd"));
//		//		  fromtoDatePane.setCalDates(from, to);
//
//			}
//			stage.hide();
//		}
//	}
}
