package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.AUDIO;
import static com.jeeves.vpl.Constants.CLOUD_JSON;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
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

public class QuestionAudio extends QuestionView{
	@FXML
	private TextField txtImage;
	@FXML
	private Button btnBrowse;
	@FXML
	private ImageView imgImage;
	public QuestionAudio(String label)  throws Exception {
		this(new FirebaseQuestion(label));
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
	public QuestionAudio(FirebaseQuestion data) {
		super(data);
	}

	@Override
	public void addEventHandlers() {
		btnBrowse.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				final FileChooser fileChooser = new FileChooser();

				Bucket bucket = StorageClient.getInstance().bucket();
				File file = fileChooser.showOpenDialog(null);
				if (file != null) {
					txtImage.setText(file.getAbsolutePath());
					imgImage.setImage(new Image(getClass().getResourceAsStream(getImagePath())));
					Map<String, Object> audioOpts = new HashMap<String, Object>();
					audioOpts.put("fullpath", file.getAbsolutePath());
					audioOpts.put("audio", file.getName());
					model.getparams().put("options",audioOpts);
					try {
						@SuppressWarnings("deprecation")
						BlobInfo blobInfo =
						storage.create(
								BlobInfo
								.newBuilder(bucket.getName(), file.getName())
								.setContentType("audio/*")
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
		return "/img/icons/audio.png";
	}

	@Override
	public String getQuestionType() {
		return AUDIO;
	}

	@Override
	public void loadOptions() {
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/OptionsAudioPresent.fxml"));
		try {
			optionsPane = (Pane) surveyLoader.load();
			addEventHandlers();
		} catch (IOException e) {
		}		
	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		String audioName = "";
		if(opts != null) {

			if(opts.containsKey("audio")) {
				txtImage.setText(opts.get("audio").toString());
				audioName = opts.get("audio").toString();
			}
			else
				return;
		}
		if(imgImage.getImage() != null)return;
		Bucket bucket = StorageClient.getInstance().bucket();

		BlobId blobId = BlobId.of(bucket.getName(), audioName);
		Blob blob = storage.get(blobId);
		if (blob == null) {
			return;
		}
		PrintStream writeTo = System.out;
		if (blob.getSize() < 1_000_000) {
			imgImage.setImage(new Image(getClass().getResourceAsStream(getImagePath())));
		} else {

		}

	}


}
