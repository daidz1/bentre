package ws.core.model.request;

import java.util.LinkedList;

import javax.validation.constraints.Max;
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
public class ReqTaskProcessCreate {

	@NotEmpty(message = "taskId không được trống")
	@Field(value = "taskId")
	public String taskId;
	
	@NotNull(message = "creator không được trống")
	@Field(value = "creator")
	public UserOrganization creator;
	
	@Max(value = 100, message = "percent tối đa 100")
	@Field(value = "percent")
	public int percent;
	
	@NotEmpty(message = "explain không được trống")
	@Field(value = "explain")
	public String explain;
	
	@Field(value="addAttachments")
	public LinkedList<Attachment> addAttachments;
	
	public ReqTaskProcessCreate() {
		this.explain="";
		this.percent=0;
		this.addAttachments=new LinkedList<Attachment>();
	}
}
