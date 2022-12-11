package ws.core.model;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserOrganization {
	@NotEmpty(message = "userId không được trống")
	@Field(value = "userId")
	public String userId;
	
	@NotEmpty(message = "fullName không được trống")
	@Field(value = "fullName")
	public String fullName;
	
	@NotEmpty(message = "organizationId không được trống")
	@Field(value = "organizationId")
	public String organizationId;
	
	@NotEmpty(message = "organizationName không được trống")
	@Field(value = "organizationName")
	public String organizationName;
	
	public boolean compareTo(UserOrganization userTaskOther) {
		if(!userId.equalsIgnoreCase(userTaskOther.userId))
			return false;
		if(!fullName.equalsIgnoreCase(userTaskOther.fullName))
			return false;
		if(!organizationId.equalsIgnoreCase(userTaskOther.organizationId))
			return false;
		if(!organizationName.equalsIgnoreCase(userTaskOther.organizationName))
			return false;
		return true;
	}
	
	public boolean valid() {
		if(userId==null || fullName==null || organizationId==null || organizationName==null)
			return false;
		return true;
	}
	
	public boolean validNotify() {
		if(userId==null || fullName==null)
			return false;
		return true;
	}
}
