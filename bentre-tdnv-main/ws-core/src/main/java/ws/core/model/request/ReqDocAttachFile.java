package ws.core.model.request;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ReqDocAttachFile {
	@NotNull(message = "file không được rỗng")
	@Field(value = "file")
	public MultipartFile file=null;
	
	
	public ReqDocAttachFile() {

	}
}
