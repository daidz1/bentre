package ws.core.model;

import javax.validation.constraints.NotBlank;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserToOrganization {
	@NotBlank(message = "userId không được trống")
	@Field(value="userId")
	public String userId;
	
	@NotBlank(message = "organizationId không được trống")
	@Field(value="organizationId")
	public String organizationId;
	
	public UserToOrganization() {
		
	}
}
