package ws.core.model.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ws.core.model.UserOrganization;

@Getter
@Setter
@ToString
public class ReqTaskRemindCreate {

	@NotEmpty(message = "taskId không được trống")
	@Field(value = "taskId")
	public String taskId;
	
	@NotNull(message = "creator không được trống")
	@Field(value = "creator")
	public UserOrganization creator;
	
	@NotEmpty(message = "message không được trống")
	@Field(value = "message")
	public String message;
	
	public ReqTaskRemindCreate() {
		
	}
}
