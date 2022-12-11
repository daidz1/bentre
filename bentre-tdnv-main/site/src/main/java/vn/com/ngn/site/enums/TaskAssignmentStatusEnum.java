package vn.com.ngn.site.enums;

public enum TaskAssignmentStatusEnum {

	
	CHUAPHAN_CANBO("chuaphan_canbo","Chưa phân cán bộ"),
	DAPHAN_CANBO("daphan_canbo","Đã phân cán bộ");
	
	private String key;
	private String caption;
	
	private TaskAssignmentStatusEnum(String key,String caption) {
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
