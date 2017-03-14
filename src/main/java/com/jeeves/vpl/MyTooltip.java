package com.jeeves.vpl;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class MyTooltip {
//
//	ArrayList<ImageView> infoIcons = new ArrayList<ImageView>();
//	ImageView infoIcon = createInfoIcon();
//
//	public ImageView createInfoIcon(){
//		ImageView infoIcon = new ImageView();
//		infoIcons.add(infoIcon);
//		infoIcon.setImage(new Image("/img/icons/information-icon.png"));
//		infoIcon.setFitHeight(14);
//		infoIcon.setFitWidth(14);
//		infoIcon.setOpacity(0);
//		infoIcon.setOnMouseEntered(event->infoIcon.setCursor(Cursor.HAND));
//		return infoIcon;
//	}
//	
//	public static void hackTooltipStartTiming(Tooltip tooltip) {
//	    try {
//	        Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
//	        fieldBehavior.setAccessible(true);
//	        Object objBehavior = fieldBehavior.get(tooltip);
//
//	        Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
//	        fieldTimer.setAccessible(true);
//	        Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);
//
//	        objTimer.getKeyFrames().clear();
//	        objTimer.getKeyFrames().add(new KeyFrame(new Duration(250)));
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//	}
//	
//	private VBox createBoxyBox(ViewElement elem){
//		Label newlable = new Label(elem.name.get());
//
//		HBox box = new HBox();
//		box.setSpacing(7);
//		VBox boxybox = new VBox();
//		boxybox.setSpacing(5);
//		boxybox.setOnMouseEntered(event->{infoIcons.forEach(info->info.setOpacity(0));infoIcon.setOpacity(100);});
//		boxybox.setOnMouseExited(event->{Point2D point = new Point2D(event.getSceneX(),event.getSceneY());
//											if(!boxybox.localToScene(boxybox.getBoundsInLocal()).contains(point)){infoIcon.setOpacity(0);}});
//		boxybox.prefWidthProperty().bind(paneFrame.widthProperty());		
//		box.setFillHeight(true);
//		box.getChildren().addAll(infoIcon,newlable);
//		boxybox.getChildren().addAll(box,elem);
//		box.setPadding(new Insets(0,0,0,-15));
//		Tooltip t = new Tooltip(elem.description);
//		hackTooltipStartTiming(t); //A wonderful wonderful method someone else made
//		Tooltip.install(
//			    infoIcon,
//			    t
//			);
//		newlable.setFont(Font.font("Calibri", FontWeight.NORMAL, 16));
//
//		return boxybox;
//	}
}
