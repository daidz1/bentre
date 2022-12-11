package vn.com.ngn.site.enums;

public enum TaskTypeEnum {
	DAGIAO("dagiao","Nhiệm vụ đã giao"),
	DUOCGIAO("duocgiao","Nhiệm vụ được giao"),
	THEODOI("theodoi","Nhiệm vụ cần theo dõi"),
	GIAOVIECTHAY("giaoviecthay","Nhiệm vụ đã giao thay"),
	THEODOITHAY("theodoithay","Nhiệm vụ được theo dõi thay");
	
	private String key;
	private String title;
	
	private TaskTypeEnum(String key,String title) {
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
