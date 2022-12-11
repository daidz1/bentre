package ws.core.model.request;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

import ws.core.model.UserOrganization;

public class ReqTagCreate {
	@NotNull(message = "name không được trống")
	@Field(value = "name")
	public String name;
	
	@NotNull(message = "creator không được trống")
	@Field(value = "creator")
	public UserOrganization creator;
	
	@Field(value = "color")
	public String color;
	
	@Field(value = "taskIds")
	public List<String> taskIds=null;
	
	public ReqTagCreate() {
		taskIds=new ArrayList<String>();
	}
}
