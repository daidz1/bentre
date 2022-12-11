package ws.core.model;

import java.util.Date;
import java.util.LinkedList;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskComment {
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@Id
	@Field(value = "_id")
	public ObjectId id;
	
	@Field(value = "creator")
	public UserOrganization creator;
	
	@Field(value = "message")
	public String message;
	
	@Field(value = "parentId")
	public String parentId;
	
	@Field(value="attachments")
	public LinkedList<TaskAttachment> attachments;
	
	@Field(value="replies")
	public LinkedList<TaskComment> replies;
	
	public TaskComment() {
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.id=new ObjectId();
		this.creator=new UserOrganization();
		this.attachments=new LinkedList<TaskAttachment>();
		this.replies=new LinkedList<TaskComment>();
	}
	
	public String getId() {
		return id.toHexString();
	}
	
	public long getCreatedTime() {
		return this.createdTime.getTime();
	}
	
	public long getUpdatedTime() {
		return this.updatedTime.getTime();
	}
}
