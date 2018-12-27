package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.CLOUD_JSON;
import static com.jeeves.vpl.Constants.IMAGEPRESENT;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.io.Files;
import com.google.firebase.cloud.StorageClient;
import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseQuestion;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

public class QuestionImagePresent extends QuestionView{
	@FXML
	private TextField txtImage;
	@FXML
	private Button btnBrowse;
	@FXML
	private ImageView imgImage;
	public QuestionImagePresent(String label) throws Exception {
		this(new FirebaseQuestion(label));
	}


	public QuestionImagePresent(FirebaseQuestion data) {
		super(data);
	}
	static Storage storage;
	static {
        InputStream googleCloudCrentials = FirebaseDB.class.getResourceAsStream(CLOUD_JSON);
		try {
			storage = StorageOptions.newBuilder().setProjectId("firebaseId")
					.setCredentials(ServiceAccountCredentials.fromStream(googleCloudCrentials))
					.build()
					.getService();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addEventHandlers() {
		// the inputstream is closed by default, so we don't need to close it here
		  
		btnBrowse.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
		        final FileChooser fileChooser = new FileChooser();

				 Bucket bucket = StorageClient.getInstance().bucket();
				 File file = fileChooser.showOpenDialog(null);
                 if (file != null) {
                	 txtImage.setText(file.getAbsolutePath());
                	 imgImage.setImage(new Image(file.toURI().toString()));
             		Map<String, Object> imageOpts = new HashMap<String, Object>();
             		imageOpts.put("fullpath", file.getAbsolutePath());
             		imageOpts.put("image", file.getName());
             			model.getparams().put("options",imageOpts);
             			 try {
             				@SuppressWarnings("deprecation")
							BlobInfo blobInfo =
             				       storage.create(
             				           BlobInfo
             				               .newBuilder(bucket.getName(), file.getName())
             				               .setContentType("image/png")
             				               .setBlobId(BlobId.of(bucket.getName(), file.getName()))
             				               .build(),
             				  			new FileInputStream(file));
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} 
                 }	}
		});		
	}

	@Override
	public String getImagePath() {
		return "/img/icons/camera.png";

	}



	@Override
	public void loadOptions() {
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/OptionsImagePresent.fxml"));
		try {
			optionsPane = (Pane) surveyLoader.load();
			addEventHandlers();
		} catch (IOException e) {
		}		
	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		String imageName = "";
		if(opts != null) {

		if(opts.containsKey("image")) {
			txtImage.setText(opts.get("image").toString());
		imageName = opts.get("image").toString();
		}
		else
			return;
		}
		if(imgImage.getImage() != null)return;
		 Bucket bucket = StorageClient.getInstance().bucket();

		BlobId blobId = BlobId.of(bucket.getName(), imageName);
		Blob blob = storage.get(blobId);
		  if (blob == null) {
		    return;
		  }
		  if (blob.getSize() < 1_000_000) {
		    // Blob is small read all its content in one request
		    byte[] content = blob.getContent();
		    ByteArrayInputStream bis = new ByteArrayInputStream(content);
		    imgImage.setImage(new Image(bis));
		  }
		

	}


	@Override
	public String getQuestionType() {
		return "Present an image to the user";
	}

}

