package ws.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.enums.NotifyCaption;
import ws.core.model.Notify;
import ws.core.model.Task;
import ws.core.model.UserOrganization;
import ws.core.model.filter.TaskFilter;
import ws.core.model.request.ReqTaskCreate;
import ws.core.repository.NotifyRepository;
import ws.core.repository.TaskRepository;
import ws.core.repository.TaskRepositoryCustom;
import ws.core.util.DateTimeUtil;

@Service
public class TaskService {
	
	@Autowired
	protected TaskRepository taskRepository;
	
	@Autowired
	protected TaskRepositoryCustom taskRepositoryCustom;
	
	@Autowired
	protected NotifyRepository taskNotifyRepository;
	
	@Autowired
	protected FirebaseService firebaseService;
	
	public boolean validForCreate(ReqTaskCreate task) throws Exception{
		/* Check username */
//		User usercheck=null;
//		try {
//			usercheck=userRepository.findByUsername(task.username).get();
//		} catch (Exception e) {}
//		
//		if(usercheck!=null) {
//			throw new Exception("username đã tồn tại");
//		}
//		
//		/* check email */
//		usercheck=null;
//		try {
//			usercheck=userRepository.findByEmail(task.email).get();
//		} catch (Exception e) {}
//		
//		if(usercheck!=null) {
//			throw new Exception("email đã tồn tại");
//		}
		
		return true;
	}
	
	public boolean validForUpdate(Task task) throws Exception{
		
		return true;
	}
	
	public int plusSubTask(String parentId) {
		int current=0;
		try {
			Task taskUpdate=taskRepository.findById(new ObjectId(parentId)).get();
			taskUpdate.countSubTask=taskUpdate.countSubTask+1;
			taskRepository.save(taskUpdate);
			current=taskUpdate.countSubTask;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return current;
	}
	
	public int subSubTask(String parentId) {
		int current=0;
		try {
			Task taskUpdate=taskRepository.findById(new ObjectId(parentId)).get();
			if(taskUpdate.countSubTask>=1) {
				taskUpdate.countSubTask=taskUpdate.countSubTask-1;
				taskRepository.save(taskUpdate);
				current=taskUpdate.countSubTask;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return current;
	}
	
	public void notifySoonExpire(Date soonExpireDate) {
		try {
			TaskFilter taskFilter=new TaskFilter();
			taskFilter.soonExpireDate=soonExpireDate.getTime();
			List<Task> tasks=taskRepositoryCustom.findAll(taskFilter, 0, 0);
			for (Task task : tasks) {
				task.notifySoonExpire=true;
				taskRepository.save(task);
				
				Notify taskNotify=new Notify();
				taskNotify.creator=null;
				taskNotify.taskId=task.getId();
				taskNotify.action=NotifyCaption.NhiemVuSapQuaHan.getAction();
				taskNotify.title=NotifyCaption.NhiemVuSapQuaHan.getTitle()+" - "+DateTimeUtil.getDatetimeFormat().format(new Date());
				taskNotify.content=task.title;
				taskNotify.viewed=false;
				
				/* Thông báo trên firebase */
				String topic = "giaoviecvptw_";
				String title = taskNotify.title;
				String content = taskNotify.content;
				Map<String,String> data = new HashMap<String,String>();
				data.put("taskId", taskNotify.taskId);
				data.put("action", taskNotify.action);
				
				/* Danh sách các người có thể nhận thông báo */
				List<UserOrganization> userTasks=new ArrayList<UserOrganization>();
				userTasks.add(task.ownerTask);
				userTasks.add(task.assigneeTask);
				userTasks.addAll(task.followersTask);
							
				/* Thông báo cho người còn lại */
				List<String> checkNotified=new ArrayList<String>();
				for(UserOrganization userTask:userTasks) {
					/* Check thông tin đã thông báo chưa */
					String keyCheck=userTask.userId+"_"+userTask.organizationId;
					if(checkNotified.contains(keyCheck)==false) {
						taskNotify.reNewId();
						taskNotify.receiver=userTask;
						taskNotifyRepository.save(taskNotify);
						
						/* Thông báo trên firebase */
						try {
							topic = "giaoviecvptw_"+taskNotify.receiver.userId;
							firebaseService.sendToTopic(topic,title,content, data);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						checkNotified.add(keyCheck);
					}
				}
				/* End thông báo */
			}
			System.out.println("Done notifySoonExpire");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void notifyHadExpire(Date hadExpireDate) {
		try {
			TaskFilter taskFilter=new TaskFilter();
			taskFilter.hadExpireDate=hadExpireDate.getTime();
			List<Task> tasks=taskRepositoryCustom.findAll(taskFilter, 0, 0);
			for (Task task : tasks) {
				task.notifyHadExpire=true;
				taskRepository.save(task);
				
				Notify taskNotify=new Notify();
				taskNotify.creator=null;
				taskNotify.taskId=task.getId();
				taskNotify.action=NotifyCaption.NhiemVuDaQuaHan.getAction();
				taskNotify.title=NotifyCaption.NhiemVuDaQuaHan.getTitle()+" - "+DateTimeUtil.getDatetimeFormat().format(new Date());
				taskNotify.content=task.title;
				taskNotify.viewed=false;
				
				/* Thông báo trên firebase */
				String topic = "giaoviecvptw_";
				String title = taskNotify.title;
				String content = taskNotify.content;
				Map<String,String> data = new HashMap<String,String>();
				data.put("taskId", taskNotify.taskId);
				data.put("action", taskNotify.action);
				
				/* Danh sách các người có thể nhận thông báo */
				List<UserOrganization> userTasks=new ArrayList<UserOrganization>();
				userTasks.add(task.ownerTask);
				userTasks.add(task.assigneeTask);
				userTasks.addAll(task.followersTask);
			
				List<UserOrganization> userTasksDistinct=userTasks.stream().distinct().collect(Collectors.toList());
				
				/* Thông báo cho người còn lại */
				List<String> checkNotified=new ArrayList<String>();
				for(UserOrganization userTask:userTasksDistinct) {
					/* Check thông tin đã thông báo chưa */
					String keyCheck=userTask.userId+"_"+userTask.organizationId;
					if(checkNotified.contains(keyCheck)==false) {
						taskNotify.reNewId();
						taskNotify.receiver=userTask;
						taskNotifyRepository.save(taskNotify);
						
						/* Thông báo trên firebase */
						try {
							topic = "giaoviecvptw_"+taskNotify.receiver.userId;
							firebaseService.sendToTopic(topic,title,content, data);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						checkNotified.add(keyCheck);
					}
				}
				/* End thông báo */
			}
			System.out.println("Done notifyHadExpire");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
