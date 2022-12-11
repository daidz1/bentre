package ws.core.model.filter;

import java.util.ArrayList;
import java.util.List;

import ws.core.enums.TaskCategory;
import ws.core.model.UserOrganization;

public class NotifyFilter {
	public UserOrganization creator=null;
	public UserOrganization receiver=null;
	public String viewed=null;
	public List<String> includeActions=null;
	public List<String> excludeActions=null;
	public TaskCategory taskCategory=null;
	public long fromDate=0, toDate=0;
	
	public NotifyFilter() {
		this.creator=new UserOrganization();
		this.receiver=new UserOrganization();
		this.includeActions=new ArrayList<String>();
		this.excludeActions=new ArrayList<String>();
	}
	
}
