package ws.core.model.request;

import javax.validation.constraints.NotBlank;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqOrganizationRoleDelete {
	@NotBlank(message = "id vai trò không được trống")
	@Field(value = "id")
	public String id;
	
	@NotBlank(message = "organizationId không được trống")
	@Field(value = "organizationId")
	public String organizationId;
	
	public ReqOrganizationRoleDelete() {
	
	}
	
}
