package ws.core.model.request;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqAppMobi {
	@NotNull(message = "userId không được trống")
	@Field(value="userId")
	public String userId;
	
	@NotNull(message = "deviceId không được trống")
	@Field(value="deviceId")
	public String deviceId;
	
	@NotNull(message = "username không được trống")
	@Field(value="username")
	public String username;
	
	@NotNull(message = "fullName không được trống")
	@Field(value="fullName")
	public String fullName;
	
	@NotNull(message = "deviceName không được trống")
	@Field(value="deviceName")
	public String deviceName;
	
	@Field(value="longitute")
	public String longitute;
	
	@Field(value = "lagitute")
	public String lagitute;
	
	public ReqAppMobi() {
		
	}
}
