package ws.core.model.embeded;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class UserOrganizationExpand {
	@Indexed
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Indexed
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@Indexed
	@Field(value = "organizationId")
	public String organizationId;
	
	@Indexed
	@Field(value = "organizationName")
	public String organizationName;
	
	@Indexed
	@Field(value = "accountIOffice")
	public String accountIOffice;
	
	@Indexed
	@Field(value = "jobTitle")
	public String jobTitle;
	
	@Indexed
	@Field(value = "numberOrder")
	public long numberOrder;
	
	public UserOrganizationExpand() {
		this.createdTime=new Date();
		this.updatedTime=new Date();
	}
}
