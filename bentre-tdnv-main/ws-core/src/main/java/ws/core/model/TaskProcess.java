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
public class TaskProcess {
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@Id
	@Field(value = "_id")
	public ObjectId id;
	
	@Field(value = "creator")
	public UserOrganization creator;
	
	@Field(value = "percent")
	public int percent;
	
	@Field(value = "explain")
	public String explain;
	
	@Field(value="attachments")
	public LinkedList<TaskAttachment> attachments;
	
	public TaskProcess() {
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.id=new ObjectId();
		this.creator=new UserOrganization();
		this.percent=0;
		this.explain="";
		this.attachments=new LinkedList<TaskAttachment>();
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
