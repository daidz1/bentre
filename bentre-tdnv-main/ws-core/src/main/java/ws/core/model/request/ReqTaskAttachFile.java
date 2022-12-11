package ws.core.model.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ReqTaskAttachFile {
	@NotEmpty(message = "userId không được trống")
	@Field(value = "userId")
	public String userId;
	
	@NotEmpty(message = "fullName không được trống")
	@Field(value = "fullName")
	public String fullName;
	
	@NotEmpty(message = "organizationId không được trống")
	@Field(value = "organizationId")
	public String organizationId;
	
	@NotEmpty(message = "organizationName không được trống")
	@Field(value = "organizationName")
	public String organizationName;
	
	@NotNull(message = "file không được rỗng")
	@Field(value = "file")
	public MultipartFile file=null;
	
	@Field(value = "description")
	public String description=null;
	
	public ReqTaskAttachFile() {

	}
}
