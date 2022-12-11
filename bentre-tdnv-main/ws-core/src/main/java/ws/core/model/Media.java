package ws.core.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Document("media")
public class Media {
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
	@Field(value="category")
	private Category category;
	
	@Indexed
	@Field(value="ownerId")
	private String ownerId;
	
	@Field(value="name")
	private String name;
	
	@Field(value="path")
	private String path;
	
	@Field(value="type")
	private String type;
	
	@Field(value="size")
	private long size;
	
	@Indexed
	@Field(value="parentId")
	private String parentId;
	
	public Media() {
		this._id=new ObjectId();
		this.createdTime=new Date();
		this.updatedTime=new Date();
	}
	
	public enum Category{
		Folder, File, Link;
	}
}
