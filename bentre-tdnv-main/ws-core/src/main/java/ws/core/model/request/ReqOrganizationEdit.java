package ws.core.model.request;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqOrganizationEdit {
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@NotBlank(message = "name không được trống")
	@Field(value = "name")
	public String name;
	
	@NotBlank(message = "description không được trống")
	@Field(value = "description")
	public String description;
	
	@Field(value="parentId")
	public String parentId;
	
	@Field(value="active")
	public boolean active;
	
	@Field(value="numberOrder")
	public int numberOrder;
	
	public ReqOrganizationEdit() {
		this.updatedTime=new Date();
		this.parentId="";
		this.active=true;
	}
	
	public long getUpdatedTime() {
		return this.updatedTime.getTime();
	}
	
	/*---------------------------------------*/
}
