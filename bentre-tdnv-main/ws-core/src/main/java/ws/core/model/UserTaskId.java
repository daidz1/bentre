package ws.core.model;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.mongodb.core.mapping.Field;

public class UserTaskId {
	@Field(value = "userId")
	public String userId;
	
	@NotEmpty(message = "organizationId không được trống")
	@Field(value = "organizationId")
	public String organizationId;
}
