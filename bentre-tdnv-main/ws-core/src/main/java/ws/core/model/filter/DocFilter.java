package ws.core.model.filter;

import lombok.ToString;
import ws.core.model.UserOrganizationCreator;

@ToString
public class DocFilter {
	public String _id=null;
	public String accountDomino=null;
	public String docCategory=null;
	
	public String findDocFroms=null;
	public String findNorNameBosses=null;
	public String findNorNameG3s=null;
	
	public String keySearch=null;
	public long fromDate=0;
	public long toDate=0;
	
	//public String creatorId=null;
	public UserOrganizationCreator docCreator;
	
	public String active=null;
	
	public DocFilter() {
		
	}
}
