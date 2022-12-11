package ws.core.model.request;

import java.util.Date;
import java.util.LinkedList;

import javax.validation.constraints.NotBlank;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqOrganizationRoleEdit {
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@NotBlank(message = "id vai trò không được trống")
	@Field(value = "id")
	public String id;
	
	@NotBlank(message = "name không được trống")
	@Field(value = "name")
	public String name;
	
	@NotBlank(message = "description không được trống")
	@Field(value = "description")
	public String description;
	
	@Field(value="permissionKeys")
	public LinkedList<String> permissionKeys;
	
	@Field(value="userIds")
	public LinkedList<String> userIds;
	
	@NotBlank(message = "organizationId không được trống")
	@Field(value = "organizationId")
	public String organizationId;
	
	public ReqOrganizationRoleEdit() {
		this.updatedTime=new Date();
		this.permissionKeys=new LinkedList<String>();
	}
	
	public long getUpdatedTime() {
		return this.updatedTime.getTime();
	}
}
