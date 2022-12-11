package ws.core.model.request;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqOrganizationImportUsers {
	@NotBlank(message = "organizationId không được trống")
	@Field(value="organizationId")
	public String organizationId;
	
	@NotEmpty(message = "userIds không được trống")
	@Field(value="userIds")
	public List<String> userIds;
	
	public ReqOrganizationImportUsers() {
		this.userIds=new ArrayList<String>();
	}
}
