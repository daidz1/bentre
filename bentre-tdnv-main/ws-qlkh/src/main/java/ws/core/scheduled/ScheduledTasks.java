package ws.core.scheduled;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
	protected Logger log = LogManager.getLogger(ScheduledTasks.class);
	
	@Scheduled(cron = "0 */5 * ? * *") /* 5 phut */
    public void scheduleTaskWithFixedEveryHour() {
		
	}
}
