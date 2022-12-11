package ws.core.model.filter;

import java.util.ArrayList;
import java.util.List;

import ws.core.model.UserOrganization;

public class UserFilter {
	public String _id=null;
	public String keySearch=null;
	public String creatorId;
	public List<String> userIds=null;
	public List<String> excludeUserIds=null;
	
	public List<String> organizationIds=null;
	public List<String> excludeOrganizationIds=null;
	
	public String organizationEmpty=null;
	
	public UserOrganization leader=null;
	
	public String username=null;
	public String activeCode=null;
	
	public UserFilter() {
		this.userIds=new ArrayList<String>();
		this.excludeUserIds=new ArrayList<String>();
		
		this.organizationIds=new ArrayList<String>();
		this.excludeOrganizationIds=new ArrayList<String>();
	}
}
