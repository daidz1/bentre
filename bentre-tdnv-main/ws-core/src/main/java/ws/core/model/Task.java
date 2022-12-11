package ws.core.model;

import java.util.Date;
import java.util.LinkedList;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ws.core.enums.TaskAssignmentType;

@Getter
@Setter
@ToString
@Document(collection = "task")
public class Task {
	@Indexed
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Indexed
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@Id
	@Field(value = "_id")
	public ObjectId _id;
	
	@Indexed
	@NotNull(message = "ownerTask không được trống")
	@Field(value = "ownerTask")
	public UserOrganization ownerTask;
	
	@Indexed
	@Field(value = "assistantTask")
	public UserOrganization assistantTask;
	
	@Indexed
	@NotNull(message = "assigneeTask không được trống")
	@Field(value = "assigneeTask")
	public UserOrganization assigneeTask;
	
	@Indexed
	@Field(value="followersTask")
	public LinkedList<UserOrganization> followersTask;
	
	@Indexed
	@NotBlank(message = "title không được trống")
	@Field(value = "title")
	public String title;
	
	@Indexed
	@NotBlank(message = "description không được trống")
	@Field(value = "description")
	public String description;
	
	@Indexed
	@Field(value = "priority")
	public int priority;
	
	@Indexed
	@Field(value = "endTime")
	public Date endTime;
	
	@Indexed
	@Field(value = "completedTime")
	public Date completedTime;
	
	@Indexed
	@Field(value = "pendingTime")
	public Date pendingTime;
	
	@Indexed
	@Field(value = "acceptedTime")
	public Date acceptedTime;
	
	@Indexed
	@Field(value = "reasonPending")
	public String reasonPending;
	
	@Indexed
	@Field(value = "parentId")
	public String parentId;
	
	@Indexed
	@Field(value = "docId")
	public String docId;
	
	@Indexed
	@Field(value = "countSubTask")
	public int countSubTask;
	
	@Indexed
	@Field(value="attachments")
	public LinkedList<TaskAttachment> attachments;
	
	@Indexed
	@Field(value="processes")
	public LinkedList<TaskProcess> processes;
	
	@Indexed
	@Field(value="comments")
	public LinkedList<TaskComment> comments;
	
	@Indexed
	@Field(value="reminds")
	public LinkedList<TaskRemind> reminds;
	
	@Indexed
	@Field(value="events")
	public LinkedList<TaskEvent> events;
	
	@Indexed
	@Field(value = "rating")
	public TaskRating rating;
	
	@Indexed
	@Field(value = "notifySoonExpire")
	public boolean notifySoonExpire=false;
	
	@Indexed
	@Field(value = "notifyHadExpire")
	public boolean notifyHadExpire=false;
	
	@Indexed
	@Field(value = "assignmentType")
	public String assignmentType;
	
	public Task() {
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this._id=new ObjectId();
		this.ownerTask=new UserOrganization();
		this.assigneeTask=new UserOrganization();
		this.followersTask=new LinkedList<UserOrganization>();
		this.attachments=new LinkedList<TaskAttachment>();
		this.processes=new LinkedList<TaskProcess>();
		this.comments=new LinkedList<TaskComment>();
		this.reminds=new LinkedList<TaskRemind>();
		this.events=new LinkedList<TaskEvent>();
		this.endTime=null;
		this.priority=1;
		this.parentId="";
		this.docId=null;
		this.countSubTask=0;
		this.completedTime=null;
		this.rating=null;
		/* Mặc định là giao cho cá nhân */
		this.assignmentType=TaskAssignmentType.User.getKey();
	}
	
	public String getId() {
		return _id.toHexString();
	}
	
	public long getCreatedTime() {
		return this.createdTime.getTime();
	}
	
	public long getUpdatedTime() {
		return this.updatedTime.getTime();
	}
	
	public Object getEndTime() {
		if(endTime!=null) {
			return this.endTime.getTime();
		}
		return 0;
	}
	
	public Object getCompletedTime() {
		if(completedTime!=null) {
			return this.completedTime.getTime();
		}
		return 0;
	}
	
	public Object getAcceptedTime() {
		if(acceptedTime!=null) {
			return this.acceptedTime.getTime();
		}
		return 0;
	}
	
	public boolean isAsssigmentTypeUser() {
		if(assignmentType.equalsIgnoreCase(TaskAssignmentType.User.getKey())) {
			return true;
		}
		return false;
	}
	
	public boolean isAsssigmentTypeOrganization() {
		if(assignmentType.equalsIgnoreCase(TaskAssignmentType.Organization.getKey())) {
			return true;
		}
		return false;
	}
}
