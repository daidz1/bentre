package ws.core.model.request;

import org.springframework.data.mongodb.core.mapping.Field;

public class ReqDocAttachment {
	@Field(value = "fileType")
	public String fileType=null;
	
	@Field(value = "fileName")
	public String fileName=null;
	
	@Field(value = "fileBase64")
	public byte[] fileBase64=null;
	
	public ReqDocAttachment() {
		
	}
}
