package ws.core.model;

import java.util.Date;
import java.util.LinkedList;

import javax.validation.constraints.NotNull;

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
@Document("organization_role")
@CompoundIndex(def = "{'name':1, 'organizationId':1}", name = "unique_primary", unique = true)
public class OrganizationRole {
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
	@NotNull(message = "name không được trống")
	@Field(value = "name")
	public String name;
	
	@Indexed
	@NotNull(message = "description không được trống")
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
	
	@Indexed
	@Field(value="userIds")
	public LinkedList<String> userIds;
	
	@Indexed
	@NotNull(message = "organizationId không được trống")
	@Field(value = "organizationId")
	public String organizationId;
	
	public OrganizationRole() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.permissionKeys=new LinkedList<String>();
		this.userIds=new LinkedList<String>();
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
