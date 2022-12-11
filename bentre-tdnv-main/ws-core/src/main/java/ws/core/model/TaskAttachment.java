package ws.core.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskAttachment {
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@Id
	@Field(value = "_id")
	public ObjectId id;
	
	@Field(value = "creator")
	public UserOrganization creator;
	
	@Field(value = "description")
	public String description;
	
	@Field(value = "fileType")
	public String fileType;
	
	@Field(value = "fileName")
	public String fileName;
	
	@Field(value = "filePath")
	public String filePath;
	
	public TaskAttachment() {
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.id=new ObjectId();
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
