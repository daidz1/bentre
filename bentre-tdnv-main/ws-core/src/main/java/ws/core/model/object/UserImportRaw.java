package ws.core.model.object;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserImportRaw {
	private String username;
	private String fullname;
	private String jobTitle;
	private String officePhone;
	private String employeeID;
	private String emailAddress;
	private String description;
	private String organizationUnit;
	private String groups;
	private boolean enabled;
	private String password;
	
	private String userId;
	private String organizationId;
	private String organizationName;
	
	private boolean result;
	private String status;
	
	public UserImportRaw(){
		
	}
	
	public boolean isValid() {
		if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(fullname) && !StringUtils.isEmpty(emailAddress) && !StringUtils.isEmpty(organizationUnit)) {
			return true;
		}
		return false;
	}
}
