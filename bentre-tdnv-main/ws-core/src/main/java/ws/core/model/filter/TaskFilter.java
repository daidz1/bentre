package ws.core.model.filter;

import ws.core.enums.TaskAssignmentStatus;
import ws.core.enums.TaskAssignmentType;
import ws.core.enums.TaskCategory;
import ws.core.enums.TaskSubCategory;
import ws.core.model.UserOrganization;

public class TaskFilter {
	public String _id=null;
	public UserOrganization userTask=null;
	
	public TaskCategory taskCategory=null;
	public TaskSubCategory taskSubCategory=null;
	
	public TaskAssignmentType taskAssignmentType=null;
	public TaskAssignmentStatus taskAssignmentStatus=null;
	
	public String keySearch=null;
	public long fromDate=0;
	public long toDate=0;
	
	public long completedFromDate=0;
	public long completedToDate=0;
	
	public int priority=0;
	
	public String findAssistants=null;
	public String findOwners=null;
	public String findAssignees=null;
	public String findFollowers=null;
	public String parentId=null;
	public String docId=null;
	
	public long soonExpireDate=0;
	public long hadExpireDate=0;
	
	public TaskFilter() {
		
	}
	
}
