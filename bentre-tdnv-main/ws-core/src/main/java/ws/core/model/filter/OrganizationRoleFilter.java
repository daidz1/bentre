package ws.core.model.filter;

import java.util.List;

public class OrganizationRoleFilter {
	public String _id=null;
	public String keySearch=null;
	public String creatorId;
	public List<String> userIds=null;
	public List<String> excludeUserIds=null;
	
	public List<String> organizationIds=null;
	public List<String> excludeOrganizationIds=null;
	
	public List<String> permissionKeys=null;
	public List<String> excludePermissionKeys=null;
	
	public OrganizationRoleFilter() {
		
	}
}
