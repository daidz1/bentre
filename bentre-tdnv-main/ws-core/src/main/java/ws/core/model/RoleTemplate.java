package ws.core.model;

import java.util.Date;
import java.util.LinkedList;

import javax.validation.constraints.NotBlank;

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
@Document(collection = "role")
public class RoleTemplate {
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
	@NotBlank(message = "name không được trống")
	@Field(value = "name")
	public String name;
	
	@Indexed
	@NotBlank(message = "description không được trống")
	@Field(value = "description")
	public String description;
	
	@Indexed
	@Field(value = "creatorId")
	public String creatorId;
	
	@Field(value="creatorName")
	public String creatorName;
	
	@Indexed
	@Field(value="permissionKeys")
	public LinkedList<String> permissionKeys;
	
	public RoleTemplate() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.permissionKeys=new LinkedList<String>();
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
