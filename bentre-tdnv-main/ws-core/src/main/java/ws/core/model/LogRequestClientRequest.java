package ws.core.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LogRequestClientRequest {
	public Object ipaddress=null;
	public Object useragent=null;
	public Object location=null;
	public Object remote=null;
}
