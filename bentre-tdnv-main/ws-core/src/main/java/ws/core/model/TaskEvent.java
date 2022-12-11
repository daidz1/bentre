package ws.core.model;

import java.util.Date;
import java.util.LinkedHashMap;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskEvent {
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@Id
	@Field(value = "_id")
	public ObjectId id;
	
	@Field(value = "creator")
	public UserOrganization creator;
	
	@Field(value = "title")
	public String title;
	
	@Field(value = "descriptions")
	public LinkedHashMap<String, String> descriptions;
	
	@Field(value = "action")
	public String action;
	
	public TaskEvent() {
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.id=new ObjectId();
		this.creator=new UserOrganization();
		this.title="";
		this.descriptions=new LinkedHashMap<String, String>();
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
