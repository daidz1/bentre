package ws.core.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class TaskGroupUser {
	@Id
	@Field(value = "assigneeTask")
	public UserOrganization assigneeTask;
	
	@Field(value="totalTask")
	public int totalTask;
}
