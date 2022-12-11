package ws.core.model;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskRating {
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Field(value = "creator")
	public UserOrganization creator;
	
	@Field(value = "star")
	public int star;
	
	@Field(value = "comment")
	public String comment;
	
	public TaskRating() {
		this.createdTime=new Date();
		this.creator=new UserOrganization();
		this.star=0;
		this.comment="";
	}
	
	public long getCreatedTime() {
		return this.createdTime.getTime();
	}
}
