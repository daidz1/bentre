package ws.core.model;

import org.springframework.web.multipart.MultipartFile;

public class MyUploadForm {
	private String description;
	private String alt;
	private String name;
	private String parentId;
	private String type;
    private MultipartFile files;
 
    public MyUploadForm() {
    	this.description="";
    	this.alt="";
    	this.name="";
    	this.type="";
    }
    
    public String getDescription() {
        return description;
    }
 
    public void setDescription(String description) {
        this.description = description;
    }
 
    public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public MultipartFile getFile() {
        return files;
    }
 
    public void setFiles(MultipartFile files) {
        this.files = files;
    }
}
