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
@Document(collection = "log_request")
public class LogRequest {
	@Indexed
	@Field(value = "createdTime")
	public Date createdTime; 
	
	@Id
	@Field(value = "_id")
	public ObjectId id;
	
	@Indexed
	@Field(value = "addremote")
	public Object addremote;
	
	@Indexed
	@Field(value = "requestURL")
	public Object requestURL;
	
	@Indexed
	@Field(value = "method")
	public Object method;
	
	@Indexed
	@Field(value="protocol")
	public Object protocol;
	
	@Indexed
	@Field(value="parameters")
	public Object requestQuery;
	
	@Field(value="clientRequest")
	public LogRequestClientRequest clientRequest;
	
	@Field(value="userRequest")
	public LogRequestUserRequest userRequest;
	
	@Indexed
	@Field(value = "action")
	public Object action;
	
	@Indexed
	@Field(value = "access")
	public Object access;
	
	public LogRequest() {
		this.createdTime=new Date();
		this.id=ObjectId.get();
	}
	
	public String getId() {
		return id.toHexString();
	}
	
	public enum Action {
		Login("login", "Login"),
		Request("request", "Request");
		
		private String key;
		private String name;
		
		Action(String key, String name){
			this.key=key;
			this.name=name;
		}

		public String getKey() {
			return key;
		}

		public String getName() {
			return name;
		}
	}

	public enum Access {
		Admin("admin", "Admin"),
		Website("website", "Website");
		
		private String key;
		private String name;
		
		Access(String key, String name){
			this.key=key;
			this.name=name;
		}

		public String getKey() {
			return key;
		}

		public String getName() {
			return name;
		}
	}
	
	public long getCreatedTime() {
		return this.createdTime.getTime();
	}
}
