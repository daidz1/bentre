package ws.core.model.filter;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

public class OrganizationFilter {
	public ObjectId _id=null;
	public List<ObjectId> _ids=null;
	public String name=null;
	public String keySearch=null;
	public String creatorId;
	public String parentId=null;
	public String active=null;
	
	public OrganizationFilter() {
		this._ids=new ArrayList<ObjectId>();
	}
}
