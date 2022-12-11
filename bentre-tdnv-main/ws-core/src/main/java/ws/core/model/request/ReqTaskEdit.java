package ws.core.model.request;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ws.core.model.Attachment;
import ws.core.model.UserOrganization;
import ws.core.model.UserTaskId;

@Getter
@Setter
@ToString
public class ReqTaskEdit {
	@Field(value = "assigneeTask")
	public UserOrganization assigneeTask;
	
	@Field(value="addFollowersTask")
	public LinkedList<UserOrganization> addFollowersTask;
	
	@Field(value="deleteFollowersTask")
	public LinkedList<UserTaskId> deleteFollowersTask;
	
	@NotBlank(message = "title không được trống")
	@Field(value = "title")
	public String title;
	
	@NotBlank(message = "description không được trống")
	@Field(value = "description")
	public String description;
	
	@Field(value = "priority")
	public int priority;
	
	@Field(value = "endTime")
	public long endTime;
	
	@Field(value="addAttachments")
	public List<Attachment> addAttachments;
	
	@Field(value="deleteAttachments")
	public List<String> deleteAttachments;
	
	public ReqTaskEdit() {
		this.assigneeTask=null;
		this.addFollowersTask=new LinkedList<UserOrganization>();
		this.deleteFollowersTask=new LinkedList<UserTaskId>();
		this.endTime=0;
		this.priority=1;
		this.addAttachments=new ArrayList<Attachment>();
		this.deleteAttachments=new ArrayList<String>();
	}
}
