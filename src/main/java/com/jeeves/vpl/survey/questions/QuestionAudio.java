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
	public QuestionAudio() {
		super();
	}

	static Storage storage;
	static {
        InputStream googleCloudCrentials = FirebaseDB.class.getResourceAsStream(CLOUD_JSON);
        System.out.println("IS THIS HAPPPENIN");
		try {
			storage = StorageOptions.newBuilder().setProjectId("firebaseId")
					.setCredentials(ServiceAccountCredentials.fromStream(googleCloudCrentials))
					.build()
					.getService();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Oh no");
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
                   //  openFile(file);
                	 txtImage.setText(file.getAbsolutePath());
         		    imgImage.setImage(new Image(getClass().getResourceAsStream(getImagePath())));
             		Map<String, Object> audioOpts = new HashMap<String, Object>();
             		audioOpts.put("fullpath", file.getAbsolutePath());
             		audioOpts.put("audio", file.getName());
             			model.getparams().put("options",audioOpts);
             			 try {
//             				storage = StorageOptions.newBuilder().setProjectId("firebaseId")
//            						.setCredentials(ServiceAccountCredentials.fromStream(googleCloudCrentials))
//            						.build()
//            						.getService();
             				@SuppressWarnings("deprecation")
							BlobInfo blobInfo =
             				       storage.create(
             				           BlobInfo
             				               .newBuilder(bucket.getName(), file.getName())
             				               .setContentType("audio/*")
             				               .setBlobId(BlobId.of(bucket.getName(), file.getName()))
             				               // Modify access list to allow all users with link to read file
             				      //         .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
             				               .build(),
             				  			new FileInputStream(file));
             			// BlobId blobId = BlobId.of(bucket.getName(), "blob_name");
             		        // Add metadata to the blob
             		    //    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
             		        // Upload blob to GCS (same as Firebase Storage)
             		      //  Blob blob = storage.create(blobInfo, "Hello, Cloud Storage!".getBytes());
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

             				   // return the public download link
//             				   return blobInfo.getMediaLink();
                 }	}
		});		
		//qOptions.put("option" + Integer.toString(optcount++), opttext.getText());
	}

	@Override
	public String getImagePath() {
		return "/img/icons/audio.png";

	}

	@Override
	public String getLabel() {
		return "Play audio file to user";

	}

	@Override
	public int getQuestionType() {
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
//			handleAddOpt(paneChoiceOptsM,"A");
//			handleAddOpt(paneChoiceOptsM,"B");
		} catch (IOException e) {
		}		
	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		String audioName = "";
		if(opts != null) {
	//	String text = (String)textOpts.get("text");
	//	txtPresent.setText(text);

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
		    //System.out.println("No such object");
		    return;
		  }
		  PrintStream writeTo = System.out;
//		  File file = Files.createTempDir();
//		  file.getParentFile().mkdirs(); // correct!
//		  if (!file.exists()) {
//		      try {
//				file.createNewFile();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		  } 
//		  if (file != null) {
//		   
//				writeTo = new PrintStream(new FileOutputStream(file));
//			
//		  }
		  if (blob.getSize() < 1_000_000) {
		    // Blob is small read all its content in one request
		 //   byte[] content = blob.getContent();
		 //   ByteArrayInputStream bis = new ByteArrayInputStream(content);
		   // BufferedImage read = ImageIO.read(bis);
		    imgImage.setImage(new Image(getClass().getResourceAsStream(getImagePath())));
		    //writeTo.write(content);
		  } else {
		    // When Blob size is big or unknown use the blob's channel reader.
//		    try (ReadChannel reader = blob.reader()) {
//		      WritableByteChannel channel = Channels.newChannel(writeTo);
//		      ByteBuffer bytes = ByteBuffer.allocate(64 * 1024);
//		      while (reader.read(bytes) > 0) {
//		        bytes.flip();
//		        channel.write(bytes);
//		        bytes.clear();
//		      }
		//    }
		  }
		
		  //  writeTo.close();
		 //   imgImage.setImage(new Image(file.toURI().toString()));

		
	}

}
