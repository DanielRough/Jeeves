package com.jeeves.vpl.canvas.receivers;

import java.net.URL;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

class NewTimePane extends Pane{

	private Stage stage;
	@FXML private TextField txtHours;
	@FXML private TextField txtMins;
	
	public NewTimePane(){
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		URL location = this.getClass().getResource("/timepopup.fxml");
		fxmlLoader.setLocation(location);
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);	
			} catch (Exception e) {
				e.printStackTrace();
			}
	//	getStylesheets().add(ViewElement.class.getResource("ButtonsDemo.css").toExternalForm());
	
		txtHours.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent arg0) {
				String ch = arg0.getCharacter();
				int x = 0;
				try {
					x = Integer.parseInt(ch);
					int hours = Integer.parseInt(txtHours.getText() + x);
					if(hours > 23){
						arg0.consume();
						return;
					}
				} catch (NumberFormatException e) {
					arg0.consume();
					return;
				}

			}
			
		});
		txtHours.textProperty().addListener(listen->{
			int hours = Integer.parseInt(txtHours.getText());
			if(hours>2)
				txtMins.requestFocus();
		});
		txtMins.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent arg0) {
				if(txtMins.getText().length() == 2){
					arg0.consume();
					return;
				}
				String ch = arg0.getCharacter();
				int x = 0;
				try {
					x = Integer.parseInt(ch);
					int mins = Integer.parseInt(txtMins.getText() + x);
					if(mins > 59){
						arg0.consume();
						return;
					}
				} catch (NumberFormatException e) {
					arg0.consume();

					return;
				}				
			}
			
		});
	}
	
	public TextField getTxtHours(){
		return txtHours;
	}
	public TextField getTxtMins(){
		return txtMins;
	}
}
