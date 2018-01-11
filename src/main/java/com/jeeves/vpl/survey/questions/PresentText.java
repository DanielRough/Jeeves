package com.jeeves.vpl.survey.questions;

import static com.jeeves.vpl.Constants.TEXTPRESENT;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.ChangeListener;

import com.google.cloud.storage.Storage;
import com.jeeves.vpl.firebase.FirebaseQuestion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

public class PresentText extends QuestionView{
	@FXML
	private TextArea txtPresent;
	public PresentText() {
		super();
	}


	public PresentText(FirebaseQuestion data) {
		super(data);
	}
	static Storage storage;
//	static {
//		  storage = StorageOptions.getDefaultInstance().getService();
//		}
	@Override
	public void addEventHandlers() {
		txtPresent.textProperty().addListener(change -> {
			Map<String, Object> textOpts = new HashMap<String, Object>();
			textOpts.put("text", txtPresent.getText());
			model.getparams().put("options",textOpts);
		});
//
//		btnBrowse.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent e) {
//		        final FileChooser fileChooser = new FileChooser();
//		        InputStream googleCloudCrentials = FirebaseDB.class.getResourceAsStream(CLOUD_JSON);
//
//				 Bucket bucket = StorageClient.getInstance().bucket();
//				 File file = fileChooser.showOpenDialog(null);
//                 if (file != null) {
//                   //  openFile(file);
//                	 txtImage.setText(file.getAbsolutePath());
//             			model.getparams().put("image",file.getName());
//             			 try {
//             				storage = StorageOptions.newBuilder().setProjectId("firebaseId")
//            						.setCredentials(ServiceAccountCredentials.fromStream(googleCloudCrentials))
//            						.build()
//            						.getService();
//             				@SuppressWarnings("deprecation")
//							BlobInfo blobInfo =
//             				       storage.create(
//             				           BlobInfo
//             				               .newBuilder(bucket.getName(), file.getName())
//             				               .setContentType("image/png")
//             				               // Modify access list to allow all users with link to read file
//             				      //         .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
//             				               .build(),
//             				  			new FileInputStream(file));
//             			// BlobId blobId = BlobId.of(bucket.getName(), "blob_name");
//             		        // Add metadata to the blob
//             		    //    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
//             		        // Upload blob to GCS (same as Firebase Storage)
//             		      //  Blob blob = storage.create(blobInfo, "Hello, Cloud Storage!".getBytes());
//						} catch (FileNotFoundException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						} catch (IOException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
//
//             				   // return the public download link
////             				   return blobInfo.getMediaLink();
//                 }	}
//		});		
		//qOptions.put("option" + Integer.toString(optcount++), opttext.getText());
	}

	@Override
	public String getImagePath() {
		return "/img/icons/textpresent.png";

	}

	@Override
	public String getLabel() {
		return "Present text to user";

	}

	@Override
	public int getQuestionType() {
		return TEXTPRESENT;

	}

	@Override
	public void loadOptions() {
		FXMLLoader surveyLoader = new FXMLLoader();
		surveyLoader.setController(this);
		surveyLoader.setLocation(getClass().getResource("/OptionsTextPresent.fxml"));
		try {
			optionsPane = (Pane) surveyLoader.load();
			addEventHandlers();
//			handleAddOpt(paneChoiceOptsM,"A");
//			handleAddOpt(paneChoiceOptsM,"B");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void showEditOpts(Map<String, Object> opts) {
		if(opts != null) {
			String text = (String)opts.get("text");
			txtPresent.setText(text);
		}
	}

}

