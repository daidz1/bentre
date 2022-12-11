package vn.com.ngn.site.enums;

public enum TaskAssignmentTypeEnum {
	ORGANIZATION("Organization","Cơ quan / đơn vị"),
	USER("User","Cán bộ");
	
	private String key;
	private String caption;
	
	private TaskAssignmentTypeEnum(String key,String caption) {
		this.key = key;
		this.caption = caption;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}

}
