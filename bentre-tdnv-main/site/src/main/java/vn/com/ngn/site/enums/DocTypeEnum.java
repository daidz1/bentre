package vn.com.ngn.site.enums;

public enum DocTypeEnum {
	VANBANDEN("FrOfficialIn","Văn bản đến"),
	VANBANDI("FrOfficialOut","Văn bản đi"),
	;
	
	private String key;
	private String title;
	
	private DocTypeEnum(String key,String title) {
		this.key = key;
		this.title = title;
	}
	

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
