package ws.core.model;

import org.springframework.data.mongodb.core.index.Indexed;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LogRequestUserRequest {
	public Object token=null;
	
	@Indexed
	public Object userId=null;
	public Object username=null;
	public Object fullName=null;
	public Object email=null;
	public Object organizationIds=null;
	public Object accountDomino=null;
}
