package ws.core.model.request;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqTaskComplete {
	@NotEmpty(message = "userId không được trống")
	@Field(value = "userId")
	public String userId;
	
	@NotEmpty(message = "organizationId không được trống")
	@Field(value = "organizationId")
	public String organizationId;
	
	public ReqTaskComplete() {
		
	}
}
