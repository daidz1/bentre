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
public class ReqRoleTemplateEdit {
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@NotBlank(message = "name không được trống")
	@Field(value = "name")
	public String name;
	
	@NotBlank(message = "description không được trống")
	@Field(value = "description")
	public String description;
	
	@Field(value="permissionKeys")
	public LinkedList<String> permissionKeys;
	
	public ReqRoleTemplateEdit() {
		this.updatedTime=new Date();
		this.permissionKeys=new LinkedList<String>();
	}
	
	public long getUpdatedTime() {
		return this.updatedTime.getTime();
	}
	
	/*---------------------------------------*/
}
