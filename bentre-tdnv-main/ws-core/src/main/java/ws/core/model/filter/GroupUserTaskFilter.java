package ws.core.model.filter;

import ws.core.model.UserOrganization;

public class GroupUserTaskFilter {
	public String _id=null;
	public UserOrganization creator=null;
	public String keySearch=null;
	
	public String findOwners=null;
	public String findAssignees=null;
	public String findFollowers=null;
	
	public String assignmentType=null;
	
	public GroupUserTaskFilter() {
		
	}
	
}
