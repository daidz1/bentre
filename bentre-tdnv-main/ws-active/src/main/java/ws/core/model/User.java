package ws.core.model;

import java.util.Date;
import java.util.LinkedList;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ws.core.model.fields.Config;

@Getter
@Setter
@ToString
@Document(collection = "user")
public class User {
	
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@Id
	@Field(value = "_id")
	public ObjectId id;
	
	@NotBlank(message = "username không được trống")
	@Field(value="username")
	public String username;
	
	@NotBlank(message = "email không được trống")
	@Field(value="email")
	public String email;
	
	@Field(value="phone")
	public String phone;
	
	@NotBlank(message = "password không được trống")
	@Field(value = "password")
	public String password;
	
	@Field(value = "fullName")
	public String fullName;
	
	@Field(value = "jobTitle")
	public String jobTitle;
	
	@Field(value="active")
	public boolean active;
	
	@Field(value = "lastDateLogin")
	public Date lastDateLogin;
	
	@Field(value = "lastIPLogin")
	public String lastIPLogin;

	@Field(value = "creatorId")
	public String creatorId;
	
	@Field(value="creatorName")
	public String creatorName;
	
	@Field(value="organizationIds")
	public LinkedList<String> organizationIds;
	
	@Field(value="leaders")
	public LinkedList<UserOrganization> leaders;
	
	@Field(value="accountDomino")
	public String accountDomino;
	
	@Field(value="activeCode")
	public String activeCode;
	
	@Field(value="config")
	public Config config;
	
	@Field(value="loginFail")
	public int loginFail;
	
	public User() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.organizationIds=new LinkedList<String>();
		this.phone="";
		this.jobTitle="";
		this.leaders=new LinkedList<UserOrganization>();
		this.accountDomino=null;
		this.config=new Config();
		this.loginFail=0;
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
