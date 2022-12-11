package ws.core.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileLocationService {

	@Value("${ws.domain.api}")
	public String domainAPI;
	
	@Value("${attachment.upload.path}")
	public String pathStoreLocation;
	
	public enum Template{
		Demo("Demo.xlsx");
		
		private String filename;
		Template(String filename){
			this.filename=filename;
		}
		public String getFilename() {
			return filename;
		}
		public void setFilename(String filename) {
			this.filename = filename;
		}
	}
	
	private String folderAttachment="attachments";
	private String folderExport="exports";
	private String folderTemplate="templates";
	
	public String getPathAttachments() {
		return pathStoreLocation+File.separator+folderAttachment;
	}
	
	public String getPathExports() {
		return pathStoreLocation+File.separator+folderExport;
	}
	
	public String getPathTemplates() {
		return pathStoreLocation+File.separator+folderTemplate;
	}
	
	public String getPathTemplate(Template template) {
		return getPathTemplates()+File.separator+template.getFilename();
	}
}
