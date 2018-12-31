package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.CLOUD_JSON;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.cloud.StorageClient;
import com.jeeves.vpl.Constants;
import com.jeeves.vpl.firebase.FirebaseDB;
import com.jeeves.vpl.firebase.FirebaseQuestion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class QuestionImagePresent extends QuestionView{
	private static final String IMAGE = "image";
	@FXML
	private TextField txtImage;
	@FXML
	private Button btnBrowse;
	@FXML
	private ImageView imgImage;
	public QuestionImagePresent(String label) {
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
			System.exit(1);
		}
	}

	@Override
	public void addEventHandlers() {
		// the inputstream is closed by default, so we don't need to close it here
		  
		btnBrowse.setOnAction(e -> {
		        final FileChooser fileChooser = new FileChooser();

				 Bucket bucket = StorageClient.getInstance().bucket();
				 File file = fileChooser.showOpenDialog(null);
                 if (file != null) {
                	 txtImage.setText(file.getAbsolutePath());
                	 imgImage.setImage(new Image(file.toURI().toString()));
             		Map<String, Object> imageOpts = new HashMap<>();
             		imageOpts.put("fullpath", file.getAbsolutePath());
             		imageOpts.put(IMAGE, file.getName());
             			model.getparams().put("options",imageOpts);
             			 try {
             				       storage.create(
             				           BlobInfo
             				               .newBuilder(bucket.getName(), file.getName())
             				               .setContentType("image/png")
             				               .setBlobId(BlobId.of(bucket.getName(), file.getName()))
             				               .build(),
             				  			new FileInputStream(file));
						} catch (FileNotFoundException e1) {
							System.exit(1);
						} 
                 }	
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
			optionsPane = surveyLoader.load();
			addEventHandlers();
		} catch (IOException e) {
			System.exit(1);
		}		
	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		String imageName = "";
		if(opts != null) {

		if(opts.containsKey(IMAGE)) {
			txtImage.setText(opts.get(IMAGE).toString());
		imageName = opts.get(IMAGE).toString();
		}
		else
			return;
		}
		if(imgImage.getImage() != null) {
			return;
		}
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
	@Override
	public String getAnswerType() {
		return Constants.VAR_NONE;
	}
}

