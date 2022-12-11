package ws.core.scheduled;

import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ws.core.service.TaskService;

@Component
public class ScheduledTasks {
	protected Logger log = LogManager.getLogger(ScheduledTasks.class);
	
	@Value("${task.notify.soon.expire.before-hour}")
	protected int soonExpireBeforeHour;
	
	@Autowired
	protected TaskService taskService;
	
	@Scheduled(cron = "0 */5 * ? * *") /* 5 phut */
    public void scheduleTaskWithFixedEveryHour() {
		try {
			Calendar soonExpireDate=Calendar.getInstance();
			soonExpireDate.setTime(new Date());
			soonExpireDate.add(Calendar.HOUR_OF_DAY, soonExpireBeforeHour);
			//taskService.notifySoonExpire(soonExpireDate.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			taskService.notifyHadExpire(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Scheduled(cron = "0 0 8 ? * *") /* 8h sáng mỗi ngày */
//    public void scheduleTaskWithFixedRate() {
//		try {
//			log.info("ScheduledTasks for analyticArticleDaily: "+new Date());
//			List<FCM> fcms=fcmRepository.findAll();
//			
//			List<String> tokens=new ArrayList<String>();
//			for (FCM fcm : fcms) {
//				tokens.add(fcm.fcm_id);
//			}
//			String title="Xoliz - Good morning";
//			String body="Have a nice day, Good luck!";
//			HashMap<String,String> datas=new HashMap<String,String>();
//			fcmService.SendMessage(tokens, title, body, datas);
//		} catch (FirebaseMessagingException e) {
//			e.printStackTrace();
//		}
//    }
}
