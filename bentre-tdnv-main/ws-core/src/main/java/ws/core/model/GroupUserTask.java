package ws.core.model;

import java.util.Date;
import java.util.LinkedList;

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
@Document(collection = "group_usertask")
public class GroupUserTask {
	@Indexed
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Indexed
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@Id
	@Field(value = "_id")
	public ObjectId _id;
	
	@Indexed
	@Field(value = "name")
	public String name;
	
	@Indexed
	@Field(value = "description")
	public String description;
	
	@Indexed
	@Field(value = "creator")
	public UserOrganization creator;
	
	@Indexed
	@Field(value = "assigneeTask")
	public UserOrganization assigneeTask;
	
	@Indexed
	@Field(value="followersTask")
	public LinkedList<UserOrganization> followersTask;
	
	@Indexed
	@Field(value = "sortBy")
	public int sortBy;
	
	@Indexed
	@Field(value = "assignmentType")
	public String assignmentType;
	
	public GroupUserTask() {
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this._id=new ObjectId();
		this.creator=null;
		this.assigneeTask=null;
		this.followersTask=new LinkedList<UserOrganization>();
		this.sortBy=99;
	}
	
	public String getId() {
		return _id.toHexString();
	}
	
	public long getCreatedTime() {
		return this.createdTime.getTime();
	}
	
	public long getUpdatedTime() {
		return this.updatedTime.getTime();
	}
}
