package ws.core.model;

import java.util.Date;

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
@Document(collection = "organization")
public class Organization {
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
	@Field(value = "leaderId")
	public String leaderId;
	
	@Field(value="leaderName")
	public String leaderName;
	
	@Field(value="path")
	public String path;
	
	@Indexed
	@Field(value="parentId")
	public String parentId;
	
	@Indexed
	@Field(value="active")
	public boolean active;
	
	@Indexed
	@Field(value="numberOrder")
	public int numberOrder;
	
	@Field(value="orgIdMysql")
	public String orgIdMysql;
	
	@Field(value="orgTypeMysql")
	public String orgTypeMysql;
	
	public Organization() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.parentId="";
		this.active=true;
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
