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
@Document(collection = "app_mobi")
public class AppMobi {
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
	@Field(value="userId")
	public String userId;
	
	@Indexed
	@Field(value="deviceId")
	public String deviceId;
	
	@Indexed
	@Field(value="username")
	public String username;
	
	@Indexed
	@Field(value="fullName")
	public String fullName;
	
	@Indexed
	@Field(value="deviceName")
	public String deviceName;
	
	@Indexed
	@Field(value="longitute")
	public String longitute;
	
	@Indexed
	@Field(value = "lagitute")
	public String lagitute;
	
	@Indexed
	@Field(value = "active")
	public boolean active;
	
	public AppMobi() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.active=false;
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
