package ws.core.model;

import javax.validation.constraints.NotNull;

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
@Document(collection = "permission")
public class Permission {
	@Id
	@Field(value = "_id")
	public ObjectId id;
	
	@Indexed(unique = true)
	@NotNull(message = "key không được trống")
	@Field(value = "key")
	public String key;
	
	@Indexed
	@NotNull(message = "name không được trống")
	@Field(value = "name")
	public String name;
	
	@Indexed
	@NotNull(message = "description không được trống")
	@Field(value = "description")
	public String description;
	
	@Indexed
	@Field(value="order")
	public int order;
	
	@Indexed
	@Field(value = "groupId")
	public String groupId;
	
	@Indexed
	@Field(value = "groupName")
	public String groupName;
	
	@Indexed
	@Field(value="groupOrder")
	public int groupOrder;
	
	public Permission() {
		this.id=ObjectId.get();
	}
	
	public String getId() {
		return id.toHexString();
	}
}
