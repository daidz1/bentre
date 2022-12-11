package ws.core.model.request;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqTaskReDo {
	@NotEmpty(message = "userId không được trống")
	@Field(value = "userId")
	public String userId;
	
	@NotEmpty(message = "organizationId không được trống")
	@Field(value = "organizationId")
	public String organizationId;
	
	@NotEmpty(message = "reason không được trống")
	@Field(value = "reason")
	public String reason;
	
	public ReqTaskReDo() {
		
	}
}
