package ws.core.model.request;

import java.util.ArrayList;
import java.util.Date;
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
public class ReqOrganizationRoleImportUsers {
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@NotBlank(message = "roleId không được trống")
	@Field(value="roleId")
	public String roleId;
	
	@NotEmpty(message = "userIds không được rỗng")
	@Field(value="userIds")
	public List<String> userIds;
	
	public ReqOrganizationRoleImportUsers() {
		this.updatedTime=new Date();
		this.userIds=new ArrayList<String>();
	}
}
