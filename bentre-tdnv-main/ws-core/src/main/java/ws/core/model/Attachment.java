package ws.core.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Attachment {
	@Field(value = "creator")
	public UserOrganization creator;
	
	@Field(value = "description")
	public String description=null;
	
	@Field(value = "fileType")
	public String fileType=null;
	
	@Field(value = "fileName")
	public String fileName=null;
	
	@Field(value = "fileBase64")
	public byte[] fileBase64=null;
	
	public Attachment() {

	}
}
