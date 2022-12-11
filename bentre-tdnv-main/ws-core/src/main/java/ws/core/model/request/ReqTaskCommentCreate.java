package ws.core.model.request;

import java.util.LinkedList;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ws.core.model.Attachment;
import ws.core.model.UserOrganization;

@Getter
@Setter
@ToString
public class ReqTaskCommentCreate {

	@NotEmpty(message = "taskId không được trống")
	@Field(value = "taskId")
	public String taskId;
	
	@NotNull(message = "creator không được trống")
	@Field(value = "creator")
	public UserOrganization creator;
	
	@NotEmpty(message = "message không được trống")
	@Field(value = "message")
	public String message;
	
	@Field(value = "parentId")
	public String parentId;
	
	@Field(value="addAttachments")
	public LinkedList<Attachment> addAttachments;
	
	public ReqTaskCommentCreate() {
		this.parentId="";
		this.addAttachments=new LinkedList<Attachment>();
	}
}
