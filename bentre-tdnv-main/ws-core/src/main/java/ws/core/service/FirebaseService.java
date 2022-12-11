package ws.core.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FirebaseService {
protected Logger log = LogManager.getLogger(FirebaseService.class);
	
	@Value("${firebase.admin.sdk.json}")
	protected String adminSDK;
	
	@Value("${firebase.sent}")
	protected String sent;
	
	@SuppressWarnings("deprecation")
	public void InitFCMService() throws IOException {
		if (sent.equalsIgnoreCase("on") && FirebaseApp.getApps().isEmpty()) {
			System.out.println("adminSDK: "+adminSDK);
			FileInputStream serviceAccount =new FileInputStream(adminSDK);
			
			FirebaseOptions options = new FirebaseOptions.Builder()
			  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
			  .build();
			
			FirebaseApp.initializeApp(options);
			System.out.println("Init App Firebase");
		}
		
		if (sent.equalsIgnoreCase("on")) {
			System.out.println("FCM Name: "+FirebaseApp.getInstance().getName());
			log.info("FCM Name: "+FirebaseApp.getInstance().getName());
		}
	}
	
	public void sendToTopic(String topic, String title, String content, Map<String,String> data) throws FirebaseMessagingException {
		// [START send_to_topic]
		if(sent.equalsIgnoreCase("on")) {
			// The topic name can be optionally prefixed with "/topics/".
			Notification notification = Notification.builder().setTitle(title).setBody(content).build();
			Message message = Message.builder()
					.putAllData(data)
					.setTopic(topic)
					.setNotification(notification)
					.build();
			// Send a message to the devices subscribed to the provided topic.
			String response = FirebaseMessaging.getInstance().send(message);
			// Response is a message ID string.
			System.out.println("Successfully sent message: " + response);
		}
		// [END send_to_topic]
	}
}
