package ws.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document(collection = "tag")
@CompoundIndex(def = "{'name':1, 'creator':1}", name = "unique_primary", unique = true)
public class Tag {
	@Indexed
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@Id
	@Field(value = "_id")
	public ObjectId _id;
	
	@Indexed
	@Field(value = "name")
	public String name;
	
	@Field(value = "color")
	public String color;
	
	@Indexed
	@Field(value = "creator")
	public UserOrganization creator;
	
	@Indexed
	@Field(value = "taskIds")
	public List<String> taskIds;
	
	public Tag() {
		this._id=ObjectId.get();
		this.taskIds=new ArrayList<String>();
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
