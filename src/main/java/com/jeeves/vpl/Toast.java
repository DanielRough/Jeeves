package com.jeeves.vpl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public final class Toast
{
	final static Logger logger = LoggerFactory.getLogger(Toast.class);

    public static void makeText(Stage ownerStage, double xPos, double yPos, double length, String toastMsg)
    {
        Stage toastStage=new Stage();
        toastStage.initOwner(ownerStage);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(toastMsg);
        text.setFont(Font.font("Verdana", 24));
        HBox root = new HBox();
        root.setAlignment(Pos.CENTER);
        root.getChildren().add(text);
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2); -fx-padding: 10px " + (length-40-text.getBoundsInParent().getWidth()) + "px 10px 40px");
        root.setOpacity(0);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);
        toastStage.setX(xPos);
        toastStage.setY(yPos);
        toastStage.show();

        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(150), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 0.6)); 
        fadeInTimeline.getKeyFrames().add(fadeInKey1);   
        fadeInTimeline.setOnFinished(ae -> 
        
            new Thread(() -> {
                try
                {
                    Thread.sleep(1500);
                }
                catch (InterruptedException e)
                {
                    logger.error(e.getMessage(),e.fillInStackTrace());
                    Thread.currentThread().interrupt();
                }
                   Timeline fadeOutTimeline = new Timeline();
                    KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(500), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 0)); 
                    fadeOutTimeline.getKeyFrames().add(fadeOutKey1);   
                    fadeOutTimeline.setOnFinished(aeb -> toastStage.close()); 
                    fadeOutTimeline.play();
            }).start()
        ); 
        fadeInTimeline.play();
    }
}