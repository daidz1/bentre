package ws.core.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document(collection = "task_notification")
public class Notify {
	@Indexed
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Id
	@Field(value = "_id")
	public ObjectId _id;
	
	@Indexed
	@Field(value = "taskId")
	public String taskId;
	
	@Indexed
	@Field(value = "creator")
	public UserOrganization creator;
	
	@Indexed
	@Field(value = "receiver")
	public UserOrganization receiver;
	
	@Indexed
	@Field(value = "title")
	public String title;
	
	@Indexed
	@Field(value = "content")
	public String content;
	
	@Indexed
	@Field(value = "action")
	public String action;
	
	@Indexed
	@Field(value = "viewed")
	public boolean viewed;
	
	@Indexed
	@Field(value = "active")
	public boolean active;
	
	public Notify() {
		this.createdTime=new Date();
		this._id=new ObjectId();
		this.creator=new UserOrganization();
		this.receiver=new UserOrganization();
		this.title="";
		this.content="";
		this.action="";
		this.viewed=false;
		this.active=true;
	}
	
	public String getId() {
		return _id.toHexString();
	}
	
	public void reNewId() {
		this._id=new ObjectId();
	}
	
	public long getCreatedTime() {
		return this.createdTime.getTime();
	}
}
