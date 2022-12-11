package ws.core.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserOrganization {
	@Field(value = "userId")
	public String userId;
	
	@Field(value = "fullName")
	public String fullName;
	
	@NotNull(message = "organizationId không được trống")
	@Field(value = "organizationId")
	public String organizationId;
	
	@NotNull(message = "organizationName không được trống")
	@Field(value = "organizationName")
	public String organizationName;
	
	public UserOrganization() {

	}
	
	public UserOrganization(@NotEmpty(message = "userId không được trống") String userId,
			@NotEmpty(message = "organizationId không được trống") String organizationId) {
		super();
		this.userId = userId;
		this.organizationId = organizationId;
	}

	public boolean compareTo(UserOrganization userTaskOther) {
		if(userId!=null && !userId.equalsIgnoreCase(userTaskOther.userId))
			return false;
		if(fullName!=null && !fullName.equalsIgnoreCase(userTaskOther.fullName))
			return false;
		if(organizationId!=null && !organizationId.equalsIgnoreCase(userTaskOther.organizationId))
			return false;
		if(organizationName!=null && !organizationName.equalsIgnoreCase(userTaskOther.organizationName))
			return false;
		return true;
	}
	
	public boolean validAll() {
		if(userId==null || fullName==null || organizationId==null || organizationName==null)
			return false;
		return true;
	}
	
	public boolean validIds() {
		if(StringUtils.isEmpty(this.userId) || StringUtils.isEmpty(this.organizationId))
			return false;
		return true;
	}
	
	public boolean validUserId() {
		if(StringUtils.isEmpty(this.userId))
			return false;
		return true;
	} 
	
	public boolean validUser() {
		if(StringUtils.isEmpty(this.userId) || StringUtils.isEmpty(this.fullName))
			return false;
		return true;
	}
	
	public void validUserFields() throws Exception{
		if(StringUtils.isEmpty(this.userId))
			throw new Exception("userId không được rỗng");
		if(StringUtils.isEmpty(this.fullName))
			throw new Exception("fullName không được rỗng");
	}
	
	public boolean validOrganizationId() {
		if(StringUtils.isEmpty(this.organizationId))
			return false;
		return true;
	}
	
	public boolean validOrganization() {
		if(StringUtils.isEmpty(this.organizationId) || StringUtils.isEmpty(this.organizationName))
			return false;
		return true;
	}
	
	public void validOrganizationFields() throws Exception{
		if(StringUtils.isEmpty(this.organizationId))
			throw new Exception("organizationId không được rỗng");
		if(StringUtils.isEmpty(this.organizationName))
			throw new Exception("organizationName không được rỗng");
	}
	
	
	public void validUserOrganizationFields() throws Exception{
		if(StringUtils.isEmpty(this.userId))
			throw new Exception("userId không được rỗng");
		if(StringUtils.isEmpty(this.fullName))
			throw new Exception("fullName không được rỗng");
		if(StringUtils.isEmpty(this.organizationId))
			throw new Exception("organizationId không được rỗng");
		if(StringUtils.isEmpty(this.organizationName))
			throw new Exception("organizationName không được rỗng");
	}
	
	/* Dùng để kiểm tra đầu vào tối thiểu, cơ bản */
	public boolean validBasic() {
		if(StringUtils.isEmpty(this.organizationId) || StringUtils.isEmpty(this.organizationName))
			return false;
		return true;
	}
	
	/* Dùng để check điều kiện thông báo */
	public boolean validNotify() {
		if(StringUtils.isEmpty(this.userId))
			return false;
		return true;
	}
	
	@Hidden
	public String getText() {
		String text=null;
		if(StringUtils.isEmpty(this.fullName)==false && StringUtils.isEmpty(this.organizationName)==false) {
			text=this.fullName +" (" + this.organizationName +")";
		}else if(StringUtils.isEmpty(this.organizationName)==false) {
			text=this.organizationName;
		}
		return text;
	}
}
