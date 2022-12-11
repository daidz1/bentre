package ws.core.model.request;

import java.util.LinkedList;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ws.core.model.UserOrganization;

@Getter
@Setter
@ToString
public class ReqGroupUserTask {
	@NotBlank(message = "name không được trống")
	@Field(value = "name")
	public String name;
	
	@Field(value = "description")
	public String description;
	
	@NotNull(message = "creator không được trống")
	@Field(value = "creator")
	public UserOrganization creator;
	
	@Field(value = "assigneeTask")
	public UserOrganization assigneeTask;
	
	@Field(value="followersTask")
	public LinkedList<UserOrganization> followersTask;
	
	@Field(value = "sortBy")
	public int sortBy;
	
	@NotNull(message = "assignmentType không được trống")
	@Field(value = "assignmentType")
	public String assignmentType;
	
	public ReqGroupUserTask() {
		this.creator=null;
		this.assigneeTask=null;
		this.followersTask=new LinkedList<UserOrganization>();
		this.sortBy=99;
	}
}
