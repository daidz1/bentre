package ws.core.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DocAttachment {
	@Indexed
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Indexed
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@Id
	@Field(value = "_id")
	public ObjectId id;
	
	@Indexed
	@Field(value = "fileType")
	public String fileType;
	
	@Indexed
	@Field(value = "fileName")
	public String fileName;
	
	@Indexed
	@Field(value = "filePath")
	public String filePath;
	
	@Indexed
	@Field(value = "idIOffice")
	public String idIOffice;
	
	public DocAttachment() {
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
