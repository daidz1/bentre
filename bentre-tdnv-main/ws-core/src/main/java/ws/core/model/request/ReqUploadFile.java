package ws.core.model.request;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.web.multipart.MultipartFile;

public class ReqUploadFile {

	@NotNull(message = "The field of file is required")
	@Field(value = "file")
	public MultipartFile file=null;
	
	public ReqUploadFile() {

	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}
}
