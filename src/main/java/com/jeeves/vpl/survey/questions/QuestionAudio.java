package com.jeeves.vpl.survey.questions;

//import static com.jeeves.vpl.Constants.CLOUD_JSON;
import static com.jeeves.vpl.Constants.AUDIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.cloud.StorageClient;
import com.jeeves.vpl.Constants;
import com.jeeves.vpl.firebase.FirebaseQuestion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class QuestionAudio extends QuestionView{
	final static Logger logger = LoggerFactory.getLogger(QuestionAudio.class);
	private static final String AUDIOSTR = "audio";
	@FXML
	private TextField txtImage;
	@FXML
	private Button btnBrowse;
	@FXML
	private ImageView imgImage;
	public QuestionAudio(String label) {
		this(new FirebaseQuestion(label));
	}

	Storage storage;
	Bucket bucket;
	static {

	}
	public QuestionAudio(FirebaseQuestion data) {
		super(data);
	}

	private void getStorage() {
		try {
			InputStream resource = new FileInputStream(Constants.FILEPATH);
			storage = StorageOptions.newBuilder().setProjectId("firebaseId")
					.setCredentials(ServiceAccountCredentials.fromStream(resource))
					.build()
					.getService();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	//@SuppressWarnings("deprecation")
	@Override
	public void addEventHandlers() {
		btnBrowse.setOnAction(e -> {
			if(storage == null) {
				getStorage();
			}

			final FileChooser fileChooser = new FileChooser();

			Bucket bucket = StorageClient.getInstance().bucket();
			File file = fileChooser.showOpenDialog(null);
			if (file != null) {
				txtImage.setText(file.getAbsolutePath());
				imgImage.setImage(new Image(getClass().getResourceAsStream(getImagePath())));
				Map<String, Object> audioOpts = new HashMap<>();
				audioOpts.put("fullpath", file.getAbsolutePath());
				audioOpts.put(AUDIOSTR, file.getName());
				model.getparams().put("options",audioOpts);
				try {
					BlobId blobId = BlobId.of(bucket.getName(), file.getName());
					BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("audio/*").build();
					Blob blob = storage.create(blobInfo,new FileInputStream(file));
					URL myUrl = blob.signUrl(14, TimeUnit.DAYS);
					System.out.println(myUrl);
				} catch (FileNotFoundException e1) {
					logger.error(e1.getMessage(),e1.fillInStackTrace());
				} 

			}	
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
			optionsPane = surveyLoader.load();
			addEventHandlers();
		} catch (IOException e) {
			logger.error(e.getMessage(),e.fillInStackTrace());
		}		
	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		String audioName = "";
		if(opts != null) {

			if(opts.containsKey(AUDIOSTR)) {
				txtImage.setText(opts.get(AUDIOSTR).toString());
				audioName = opts.get(AUDIOSTR).toString();
			}
			else
				return;
		}
		if(storage == null) {
			getStorage();
		}
		if(imgImage.getImage() != null)return;
		Bucket bucket = StorageClient.getInstance().bucket();

		BlobId blobId = BlobId.of(bucket.getName(), audioName);
		Blob blob = storage.get(blobId);
		if (blob == null) {
			return;
		}
		if (blob.getSize() < 1_000_000) {
			imgImage.setImage(new Image(getClass().getResourceAsStream(getImagePath())));
		}

	}

	@Override
	public String getAnswerType() {
		return Constants.VAR_NONE;
	}
}
