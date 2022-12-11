package ws.core.model.filter;

import java.util.List;

import ws.core.model.UserOrganization;

public class TagFilter {
	public String _id=null;
	public UserOrganization creator=null;
	public String keySearch=null;
	public List<String> taskIds=null;
	public long fromDate=0;
	public long toDate=0;
	
	public SkipLimitFilter skipLimitFilter=null;
	
	public TagFilter() {
		
	}
}
